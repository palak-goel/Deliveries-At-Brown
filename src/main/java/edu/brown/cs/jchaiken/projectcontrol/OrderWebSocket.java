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

	private enum MESSAGE_TYPE {
		CONNECT, ADD_ORDER, REMOVE_ORDER, REQUESTED, GET_ORDERS, DELIVERED, COMPLETED, TIMED_OUT;
	}

	@OnWebSocketConnect
	public void connected(Session session) throws IOException {
		SESSIONS.add(session);
		JsonObject msg = new JsonObject();
		msg.addProperty("id", nextId);
		msg.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
		session.getRemote().sendString(GSON.toJson(msg));
		nextId++;
	}

	/**
	 * Closing the socket.
	 *
	 * @param session
	 *            The session.
	 * @param statusCode
	 *            The status code.
	 * @param reason
	 *            The reason.
	 */
	@OnWebSocketClose
	public void closed(Session session, int statusCode, String reason) {
		SESSIONS.remove(session);
	}

	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		System.out.println(message);
		JsonObject received = GSON.fromJson(message, JsonObject.class);
		if (received.get("type").getAsInt() == MESSAGE_TYPE.GET_ORDERS.ordinal()) {
			sendOrders(session);
		} else if (received.get("type").getAsInt() == MESSAGE_TYPE.CONNECT.ordinal()) {
			System.out.println("adding to map");
			socketidUser.put(received.get("jid").getAsString(), session);
		} else if (received.get("type").getAsInt() == MESSAGE_TYPE.COMPLETED.ordinal()) {

		} else {
			System.out.println("ticket picked up");
			String id = received.get("id").getAsString();
			Order o = Order.byId(id);
			if (!Manager.removeOrder(o)) {
				session.getRemote().sendString(
						GSON.toJson(ImmutableMap.of("error", "CLAIMED", "type", MESSAGE_TYPE.DELIVERED.ordinal())));
				return;
			}
			Manager.addOrder(o);
			if (o.getDropoffTime() < (System.currentTimeMillis() / 1000L)) {
				session.getRemote().sendString(
						GSON.toJson(ImmutableMap.of("error", "TIME", "type", MESSAGE_TYPE.DELIVERED.ordinal())));
				sendRemoveOrder(o);
				Session req = socketidUser.get(Manager.getUserJid(o.getOrderer().getWebId()));
				req.getRemote().sendString(
						GSON.toJson(ImmutableMap.of("error", "TIME", "type", MESSAGE_TYPE.REQUESTED.ordinal())));
				return;
			}
			String jid = received.get("jid").getAsString();
			User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
			o.assignDeliverer(u);
			Map<String, Object> m = new HashMap<>();
			m.put("type", MESSAGE_TYPE.DELIVERED.ordinal());
			m.put("pLat", o.getPickupLocation().getLatitude());
			m.put("pLng", o.getPickupLocation().getLongitude());
			m.put("dLat", o.getDropoffLocation().getLatitude());
			m.put("dLng", o.getDropoffLocation().getLongitude());
			m.put("dcell", o.getDeliverer().getCell());
			m.put("rcell", o.getOrderer().getCell());
			session.getRemote().sendString(GSON.toJson(m));
			String reqId = o.getOrderer().getWebId();
			System.out.println(o.getOrderer().getName());
			System.out.println("---");
			System.out.println(reqId);
			try {
				for (spark.Session s : Manager.allSessions()) {
					String z = s.attribute("webId");
					System.out.println(z);
					System.out.println(socketidUser.keySet());
					double dlat = received.get("dLat").getAsDouble();
					double dlng = received.get("dLng").getAsDouble();
					double dropLat = o.getDropoffLocation().getLatitude();
					double dropLng = o.getDropoffLocation().getLongitude();
					double pickupLat = o.getPickupLocation().getLatitude();
					double pickupLng = o.getPickupLocation().getLongitude();
					if (reqId.equals(s.attribute("webId"))) {
						Manager.setActiveUser(Manager.getUserJid(o.getOrderer().getWebId()));
						Manager.setActiveUser(Manager.getUserJid(o.getDeliverer().getWebId()));
						System.out.println("Found requested id");
						String y = Manager.getUserJid(reqId);
						System.out.println(y);
						Map<String, Object> msg = ImmutableMap.<String, Object>builder()
								.put("type", MESSAGE_TYPE.REQUESTED.ordinal()).put("name", u.getName())
								.put("phone", u.getCell()).put("delivLat", dlat).put("delivLng", dlng)
								.put("dropLat", dropLat).put("dropLng", dropLng).put("pickLat", pickupLat)
								.put("pickLng", pickupLng).build();
						socketidUser.get(y).getRemote().sendString(GSON.toJson(msg));
						sendRemoveOrder(o);
						// Manager.setActiveUser(jid);
						// Manager.setActiveUser(y);
						Sender sender = new Sender(o.getOrderer().getCell());
						sender.updateMessage("confirm", o);
						sender.sendMessage();
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void sendMsg() {
		try {
			JsonObject msg = new JsonObject();
			msg.addProperty("hi", "hello");
			for (Session s : SESSIONS) {
				s.getRemote().sendString(GSON.toJson(msg));
			}
		} catch (IOException e) {

		}
	}

	public static void sendAddOrder(Order o) {
		Manager.addOrder(o);
		List<Order> orders = MGR.getPendingOrders();
		try {
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("orders", orders);
			List<String> start = new ArrayList<>();
			List<String> end = new ArrayList<>();
			for (Order os : orders) {
				start.add(os.getPickupLocation().getName());
				end.add(os.getDropoffLocation().getName());
			}
			toServer.put("pickup", start);
			toServer.put("dropoff", end);
			toServer.put("type", MESSAGE_TYPE.ADD_ORDER.ordinal());
			String msg = GSON.toJson(toServer);
			for (Session s : SESSIONS) {
				s.getRemote().sendString(msg);
			}
		} catch (IOException e) {

		}
	}

	public static void sendRemoveOrder(Order o) {
		Manager.removeOrder(o);
		List<Order> orders = MGR.getPendingOrders();
		try {
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("orders", orders);
			List<String> start = new ArrayList<>();
			List<String> end = new ArrayList<>();
			for (Order os : orders) {
				start.add(os.getPickupLocation().getName());
				end.add(os.getDropoffLocation().getName());
			}
			toServer.put("pickup", start);
			toServer.put("dropoff", end);
			toServer.put("type", MESSAGE_TYPE.ADD_ORDER.ordinal());
			String msg = GSON.toJson(toServer);
			for (Session s : SESSIONS) {
				s.getRemote().sendString(msg);
			}
		} catch (IOException e) {

		}
	}

	public static void sendOrders(Session s) {
		List<Order> orders = MGR.getPendingOrders();
		try {
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("orders", orders);
			List<String> start = new ArrayList<>();
			List<String> end = new ArrayList<>();
			for (Order o : orders) {
				start.add(o.getPickupLocation().getName());
				end.add(o.getDropoffLocation().getName());
			}
			toServer.put("pickup", start);
			toServer.put("dropoff", end);
			toServer.put("type", MESSAGE_TYPE.ADD_ORDER.ordinal());
			String msg = GSON.toJson(toServer);
			s.getRemote().sendString(msg);
		} catch (IOException e) {

		}
	}

	public static void sendOrders(String jid, List<Order> orders) {
		Session s = socketidUser.get(jid);
		try {
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("orders", orders);
			List<String> start = new ArrayList<>();
			List<String> end = new ArrayList<>();
			for (Order o : orders) {
				start.add(o.getPickupLocation().getName());
				end.add(o.getDropoffLocation().getName());
			}
			toServer.put("pickup", start);
			toServer.put("dropoff", end);
			toServer.put("type", MESSAGE_TYPE.ADD_ORDER.ordinal());
			String msg = GSON.toJson(toServer);
			s.getRemote().sendString(msg);
		} catch (IOException e) {

		}
	}

	public static void completeOrderRequester(String requesterJid) {
		try {
			Session s = socketidUser.get(requesterJid);
			s.getRemote().sendString(GSON.toJson(ImmutableMap.of("type", MESSAGE_TYPE.COMPLETED.ordinal())));
		} catch (IOException e) {

		}
	}

	public static void sendTimeout(Order o) {
		Session req = socketidUser.get(Manager.getUserJid(o.getOrderer().getWebId()));
		try {
			req.getRemote().sendString(
					GSON.toJson(ImmutableMap.of("error", "TIME", "type", MESSAGE_TYPE.REQUESTED.ordinal())));
		} catch (IOException e) {
		}
	}

}
