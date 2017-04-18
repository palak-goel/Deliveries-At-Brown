package edu.brown.cs.jchaiken.projectcontrol;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Web socket class to handle live interactions between orders,
 * deliverers/requesters, and actual users.
 *
 * @author sumitsohani
 *
 */
public class OrderWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;

  private enum MESSAGE_TYPE {
    CONNECT, ORDER_TAKEN, ORDER_ADDED
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // TODO Add the session to the queue
    SESSIONS.add(session);
    // TODO Build the CONNECT message
    JsonObject msg = new JsonObject();
    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId);
    msg.add("payload", payload);
    msg.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    // TODO Send the CONNECT message
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

  // Added message ... update all tabs
  // Ticket taken message... update all tabs
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    System.out.println(message);
    JsonObject received = GSON.fromJson(message, JsonObject.class);
    assert received.get("type").getAsInt() == MESSAGE_TYPE.SCORE.ordinal();
    JsonObject payload = received.get("payload").getAsJsonObject();
    int id = payload.get("id").getAsInt();
    String text = payload.get("text").getAsString();
    int score = 0;
    JsonObject msg = new JsonObject();
    msg.addProperty("type", MESSAGE_TYPE.ORDER_ADDED.ordinal());
    JsonObject msg_p = new JsonObject();
    msg_p.addProperty("id", id);
    msg_p.addProperty("score", score);
    msg.add("payload", msg_p);
    // TODO Compute the player's score
    // TODO Send an UPDATE message to all users
    for (Session s : sessions) {
      s.getRemote().sendString(GSON.toJson(msg));
    }
  }
}
