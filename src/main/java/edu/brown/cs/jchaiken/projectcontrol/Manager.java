package edu.brown.cs.jchaiken.projectcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;

import edu.brown.cs.jchaiken.deliveryobject.Location;
import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;
import edu.brown.cs.jchaiken.deliveryobject.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

/**
 * A "controller" class for the application which handles the main user
 * interaction with the webpage. There is only one such instance of this class
 * (most methods are static) so data structures need to be concurrent.
 *
 * @author sumitsohani
 *
 */
public class Manager {

	private static List<User> activeDeliverers = Collections.synchronizedList(new ArrayList<>());
	private static List<Order> pendingOrders = Collections.synchronizedList(new ArrayList<>());
	private static Map<String, Order> ordMap = Collections.synchronizedMap(new HashMap<>());
	private static Map<User, Order> pendingMatches = Collections.synchronizedMap(new HashMap<>());
	private static final Gson GSON = new Gson();
	private static final int MAX_CACHE = 50000;
	private static Cache<User, Order> completedOrder = CacheBuilder.newBuilder().maximumSize(MAX_CACHE)
			.expireAfterAccess(125, TimeUnit.MINUTES).build();
	private static Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, String> widToJid = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Constructor for Manager.
	 */
	public Manager() {
	}

	public static List<Session> allSessions() {
		return new ArrayList<>(sessionMap.values());
	}

	public static String getUserJid(String wid) {
		return widToJid.get(wid);
	}

	public static void saveSession(String id, Session session) {
		System.out.println("SAVING SESH");
		System.out.println(id);
		System.out.println((String) session.attribute("webId"));
		System.out.println("---");
		sessionMap.put(id, session);
		widToJid.put(session.attribute("webId"), id);
	}

	public static Session getSession(String id) {
		return sessionMap.get(id);
	}

	public static Order getOrder(String id) {
		return ordMap.get(id);
	}

	public List<Order> getPendingOrders() {
		return new ArrayList<>(pendingOrders);
	}

	// Trigger handler
	static synchronized boolean select(User u, Order o) {
		activeDeliverers.remove(u);
		pendingOrders.remove(o);
		pendingMatches.put(u, o);
		return true;
	}

	synchronized void onQuitUser(User u) {
		Order o = pendingMatches.get(u);
		pendingOrders.add(o);
		// UNDO user transactions
		// Finish payment
	}

	synchronized void onQuitOrder(Order o) {
		pendingOrders.remove(o);
		// Handle order quitting
	}

	synchronized void onEntryUser(User u) {
		activeDeliverers.add(u);
	}

	synchronized static List<Order> rank(User u) {
		return Collections.<Order>emptyList();
	}

	public synchronized static void addOrder(Order o) {
		pendingOrders.add(o);
	}

	synchronized static void removeOrder(Order o) {
		pendingOrders.remove(o);
	}

	static void checkStatus() {
		Collection<Order> os = pendingMatches.values();
		for (Order o : os) {
			// Look at time of order
			// Do some sort of check based on that, possibly message order
			// o.message()?
		}
	}

	public static class OrderMaker implements Route {
		@Override
		public Object handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			try {
				double pLat = Double.parseDouble(qm.value("pickupLat"));
				double pLon = Double.parseDouble(qm.value("pickupLon"));
				double dLat = Double.parseDouble(qm.value("dropoffLat"));
				double dLon = Double.parseDouble(qm.value("dropoffLon"));
				String item = qm.value("item");
				double time = Double.parseDouble(qm.value("time"));
				double price = Double.parseDouble(qm.value("price"));
				OrderBuilder builder = new OrderBuilder();
				User curr = User.byWebId(req.session().attribute("webId"));
				Location pickup = Location.newLocation(pLat, pLon);
				Location dropoff = Location.newLocation(dLat, dLon);
				Order o = builder.setOrderer(curr).setPickup(pickup).setDropoff(dropoff).setPrice(price)
						.setDropoffTime(time).setItems(Arrays.asList(item)).build();
				o.addToDatabase();
				ordMap.put(o.getId(), o);
				OrderWebSocket.sendAddOrder(o);
				System.out.println("ORDER WEB ID");
				System.out.println(o.getOrderer().getWebId());
				System.out.println(curr.getWebId());
				/*
				 * Map<String, Object> variables = new
				 * ImmutableMap.Builder<String, Object>() .put("pickupLoc",
				 * qm.value("pickup")).put("dropoffLoc",
				 * qm.value("dropoff")).put("price", price) .put("time",
				 * time).put("item", item).build();
				 */
				// Not used
			} catch (Exception e) {
			}
			return new Object();
		}
	}
}
