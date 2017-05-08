package edu.brown.cs.jchaiken.projectcontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.mhasan3.messaging.Sender;

/**
 * Web socket class to handle live interactions between orders,
 * deliverers/requesters, and actual users.
 *
 * @author sumitsohani
 *
 */
@WebSocket
public class OrderWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;
  private static final Manager MGR = new Manager();
  private static Map<String, Session> socketidUser = new ConcurrentHashMap<>();

  /**
   * MessageType describes various messages sent via the socket.
   *
   */
  private enum MessageType {
    CONNECT,
    ADD_ORDER,
    REMOVE_ORDER,
    REQUESTED,
    GET_ORDERS,
    DELIVERED,
    COMPLETED,
    TIMED_OUT;
  }

  /**
   * Adds a session and sends a connected message.
   * @param session the new session.
   * @throws IOException if an error occurs
   */
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    SESSIONS.add(session);
    final JsonObject msg = new JsonObject();
    msg.addProperty("id", nextId);
    msg.addProperty("type", MessageType.CONNECT.ordinal());
    session.getRemote().sendString(GSON.toJson(msg));
    nextId++;
  }

  /**
   * Closing the socket.
   *
   * @param session
   *          The session.
   * @param statusCode
   *          The status code.
   * @param reason
   *          The reason.
   */
  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    SESSIONS.remove(session);
  }

  private static final long TIME = 1000L;
  /**
   * Handles various messages being sent back and forth.
   * @param session the session sending the message.
   * @param message the message.
   * @throws IOException if an error occurs
   */
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    System.out.println(message);
    final JsonObject received = GSON.fromJson(message, JsonObject.class);
    if (received.get("type").getAsInt() == MessageType.GET_ORDERS.ordinal()) {
      sendOrders(session);
    } else if (received.get("type").getAsInt() == MessageType.CONNECT
        .ordinal()) {
      System.out.println("adding to map");
      socketidUser.put(received.get("jid").getAsString(), session);
    } else if (received.get("type").getAsInt() == MessageType.COMPLETED
        .ordinal()) {
      System.out.println("completed");
    } else {
      System.out.println("ticket picked up");
      final String id = received.get("id").getAsString();
      final Order o = Order.byId(id);
      if (!Manager.removeOrder(o)) {
        session.getRemote().sendString(GSON.toJson(ImmutableMap.of("error",
            "CLAIMED", "type", MessageType.DELIVERED.ordinal())));
        return;
      }
      // Manager.addOrder(o);
      if (o.getDropoffTime()
          < ((double) System.currentTimeMillis() / (double) TIME)) {
        session.getRemote().sendString(GSON.toJson(ImmutableMap.of("error",
            "TIME", "type", MessageType.DELIVERED.ordinal())));
        sendRemoveOrder(o);
        final Session req = socketidUser
            .get(Manager.getUserJid(o.getOrderer().getWebId()));
        req.getRemote().sendString(GSON.toJson(ImmutableMap.of("error", "TIME",
            "type", MessageType.REQUESTED.ordinal())));
        return;
      }
      final String jid = received.get("jid").getAsString();
      final User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
      o.assignDeliverer(u);
      final Map<String, Object> m = new HashMap<>();
      m.put("type", MessageType.DELIVERED.ordinal());
      m.put("pLat", o.getPickupLocation().getLatitude());
      m.put("pLng", o.getPickupLocation().getLongitude());
      m.put("dLat", o.getDropoffLocation().getLatitude());
      m.put("dLng", o.getDropoffLocation().getLongitude());
      m.put("dcell", o.getDeliverer().getCell());
      m.put("rcell", o.getOrderer().getCell());
      session.getRemote().sendString(GSON.toJson(m));
      final String reqId = o.getOrderer().getWebId();
      System.out.println(o.getOrderer().getName());
      System.out.println("---");
      System.out.println(reqId);
      try {
        for (final spark.Session s : Manager.allSessions()) {
          final String z = s.attribute("webId");
          System.out.println(z);
          System.out.println(socketidUser.keySet());
          final double dlat = received.get("dLat").getAsDouble();
          final double dlng = received.get("dLng").getAsDouble();
          final double dropLat = o.getDropoffLocation().getLatitude();
          final double dropLng = o.getDropoffLocation().getLongitude();
          final double pickupLat = o.getPickupLocation().getLatitude();
          final double pickupLng = o.getPickupLocation().getLongitude();
          if (reqId.equals(s.attribute("webId"))) {
            Manager
                .setActiveUser(Manager.getUserJid(o.getOrderer().getWebId()));
            Manager
                .setActiveUser(Manager.getUserJid(o.getDeliverer().getWebId()));
            System.out.println("Found requested id");
            final String y = Manager.getUserJid(reqId);
            System.out.println(y);
            final Map<String, Object> msg = ImmutableMap
                .<String, Object>builder()
                .put("type", MessageType.REQUESTED.ordinal())
                .put("name", u.getName()).put("phone", u.getCell())
                .put("delivLat", dlat).put("delivLng", dlng)
                .put("dropLat", dropLat).put("dropLng", dropLng)
                .put("pickLat", pickupLat).put("pickLng", pickupLng).build();
            socketidUser.get(y).getRemote().sendString(GSON.toJson(msg));
            sendRemoveOrder(o);
            // Manager.setActiveUser(jid);
            // Manager.setActiveUser(y);
            final Sender sender = new Sender(o.getOrderer().getCell());
            sender.updateMessage("confirm", o);
            sender.sendMessage();
            return;
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * Sends a message to the sessions.
   */
  public static void sendMsg() {
    try {
      final JsonObject msg = new JsonObject();
      msg.addProperty("hi", "hello");
      for (final Session s : SESSIONS) {
        s.getRemote().sendString(GSON.toJson(msg));
      }
    } catch (final IOException e) {

    }
  }

  /**
   * Sends a new order to every session.
   * @param o the order to send.
   */
  public static void sendAddOrder(Order o) {
    Manager.addOrder(o);
    final List<Order> orders = MGR.getPendingOrders();
    try {
      final Map<String, Object> toServer = new HashMap<>();
      toServer.put("orders", orders);
      final List<String> start = new ArrayList<>();
      final List<String> end = new ArrayList<>();
      for (final Order os : orders) {
        start.add(os.getPickupLocation().getName());
        end.add(os.getDropoffLocation().getName());
      }
      toServer.put("pickup", start);
      toServer.put("dropoff", end);
      toServer.put("type", MessageType.ADD_ORDER.ordinal());
      final String msg = GSON.toJson(toServer);
      for (final Session s : SESSIONS) {
        s.getRemote().sendString(msg);
      }
    } catch (final IOException e) {

    }
  }

  /**
   * Sends a message to remove an order.
   * @param o the order to remove.
   */
  public static void sendRemoveOrder(Order o) {
    Manager.removeOrder(o);
    final List<Order> orders = MGR.getPendingOrders();
    try {
      final Map<String, Object> toServer = new HashMap<>();
      toServer.put("orders", orders);
      final List<String> start = new ArrayList<>();
      final List<String> end = new ArrayList<>();
      for (final Order os : orders) {
        start.add(os.getPickupLocation().getName());
        end.add(os.getDropoffLocation().getName());
      }
      toServer.put("pickup", start);
      toServer.put("dropoff", end);
      toServer.put("type", MessageType.ADD_ORDER.ordinal());
      final String msg = GSON.toJson(toServer);
      for (final Session s : SESSIONS) {
        s.getRemote().sendString(msg);
      }
    } catch (final IOException e) {

    }
  }

  /**
   * Sends all orders to a session.
   * @param s the session receiving the orders.
   */
  public static void sendOrders(Session s) {
    final List<Order> orders = MGR.getPendingOrders();
    try {
      final Map<String, Object> toServer = new HashMap<>();
      toServer.put("orders", orders);
      final List<String> start = new ArrayList<>();
      final List<String> end = new ArrayList<>();
      for (final Order o : orders) {
        start.add(o.getPickupLocation().getName());
        end.add(o.getDropoffLocation().getName());
      }
      toServer.put("pickup", start);
      toServer.put("dropoff", end);
      toServer.put("type", MessageType.ADD_ORDER.ordinal());
      final String msg = GSON.toJson(toServer);
      s.getRemote().sendString(msg);
    } catch (final IOException e) {

    }
  }

  /**
   * Sends orders to a session given the jid and a list of orders.
   * @param jid the job id.
   * @param orders the list of orders.
   */
  public static void sendOrders(String jid, List<Order> orders) {
    final Session s = socketidUser.get(jid);
    try {
      final Map<String, Object> toServer = new HashMap<>();
      toServer.put("orders", orders);
      final List<String> start = new ArrayList<>();
      final List<String> end = new ArrayList<>();
      for (final Order o : orders) {
        start.add(o.getPickupLocation().getName());
        end.add(o.getDropoffLocation().getName());
      }
      toServer.put("pickup", start);
      toServer.put("dropoff", end);
      toServer.put("type", MessageType.ADD_ORDER.ordinal());
      final String msg = GSON.toJson(toServer);
      s.getRemote().sendString(msg);
    } catch (final IOException e) {

    }
  }

  /**
   * Sends an order complete message to the requester.
   * @param requesterJid the requester's job id.
   */
  public static void completeOrderRequester(String requesterJid) {
    try {
      final Session s = socketidUser.get(requesterJid);
      s.getRemote().sendString(GSON
          .toJson(ImmutableMap.of("type", MessageType.COMPLETED.ordinal())));
    } catch (final IOException e) {

    }
  }

  /**
   * Sends a timeout message for an expired order.
   * @param o the timed out order.
   */
  public static void sendTimeout(Order o) {
    final Session req = socketidUser
        .get(Manager.getUserJid(o.getOrderer().getWebId()));
    try {
      req.getRemote().sendString(GSON.toJson(ImmutableMap.of("error", "TIME",
          "type", MessageType.REQUESTED.ordinal())));
    } catch (final IOException e) {
    }
  }

}
