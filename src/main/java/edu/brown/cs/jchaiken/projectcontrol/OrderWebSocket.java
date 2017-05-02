package edu.brown.cs.jchaiken.projectcontrol;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;

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
	private static Map<Integer, User> socketidUser = new ConcurrentHashMap<>();

	private enum MESSAGE_TYPE {
		CONNECT, ORDER_TAKEN, ORDER_ADDED
	}

	@OnWebSocketConnect
	public void connected(Session session) throws IOException {
		// TODO Add the session to the queue
		SESSIONS.add(session);
		// TODO Build the CONNECT message
		JsonObject msg = new JsonObject();
		msg.addProperty("id", nextId);
		msg.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
		// TODO Send the CONNECT message
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

	// Added message ... update all tabs
	// Ticket taken message... update all tabs
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		System.out.println(message);
		JsonObject received = GSON.fromJson(message, JsonObject.class);
		JsonObject msg = new JsonObject();
		if (received.get("type").getAsInt() == MESSAGE_TYPE.CONNECT.ordinal()) {
			int id = received.get("id").getAsInt();
			String uId = Manager.getSession(received.get("jsessionid").getAsString()).attribute("webId");
			User u = User.byWebId(uId);
			socketidUser.put(id, u);
			msg.addProperty("type", MESSAGE_TYPE.ORDER_ADDED.ordinal());
			JsonObject msg_p = new JsonObject();
			msg_p.addProperty("id", id);
			// msg_p.addProperty("order", o.toString());
			msg.add("payload", msg_p);
		} else if (received.get("type").getAsInt() == MESSAGE_TYPE.ORDER_TAKEN.ordinal()) {
			JsonObject payload = received.get("payload").getAsJsonObject();
			int id = payload.get("id").getAsInt();
			String ord = payload.get("order").getAsString();
			String usr = payload.get("user").getAsString();
			Order o = null; // get from map
			User u = null; // get from map
			Manager.select(u, o);
			msg.addProperty("type", MESSAGE_TYPE.ORDER_TAKEN.ordinal());
			JsonObject msg_p = new JsonObject();
			msg_p.addProperty("id", id);
			msg_p.addProperty("order", o.toString());
			msg_p.addProperty("user", u.toString());
			msg.add("payload", msg_p);
		} else {
			// raise error
		}
		for (Session s : SESSIONS) {
			s.getRemote().sendString(GSON.toJson(msg));
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

}
