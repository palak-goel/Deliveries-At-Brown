package edu.brown.cs.jchaiken.projectcontrol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jchaiken.deliveryobject.Location;
import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.mhasan3.messaging.Sender;
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

	private static List<Order> pendingOrders = Collections.synchronizedList(new ArrayList<>());
	private static Map<String, Order> jidToOrder = Collections.synchronizedMap(new HashMap<>());
	private static final Gson GSON = new Gson();
	private static Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, String> widToJid = Collections.synchronizedMap(new HashMap<>());
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
	private static Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());

	/**
	 * Constructor for Manager.
	 */
	public Manager() {
	}

	/**
	 * Sets a user to be active.
	 * 
	 * @param jid
	 *            the job id.
	 */
	public static void setActiveUser(String jid) {
		try {
			System.out.println("ACTIVE UfSERSSSS");
			System.out.println("webId");
			String wid = sessionMap.get(jid).attribute("webId");
			System.out.println(wid);
			System.out.println("HERE-");
			activeUsers.add(wid);
			for (String s : activeUsers) {
				System.out.println(s);
			}
			System.out.println("-");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns if an id is active.
	 * 
	 * @param jid
	 *            the id in question.
	 * @return true, false depending on activity.
	 */
	public static boolean isActiveUser(String jid) {
		String wid = sessionMap.get(jid).attribute("webId");
		System.out.println(wid);
		return activeUsers.contains(wid);
	}

	/**
	 * removes an active user.
	 * 
	 * @param jid
	 *            the id of the user to remove.
	 */
	public static void removeActiveUser(String jid) {
		String wid = sessionMap.get(jid).attribute("webId");
		activeUsers.remove(wid);
	}

	/**
	 * Returns a list of all sessions.
	 * 
	 * @return the list of sessions.
	 */
	public static List<Session> allSessions() {
		return new ArrayList<>(sessionMap.values());
	}

	/**
	 * Returns a user's job id.
	 * 
	 * @param wid
	 *            the user's web id.
	 * @return the job id.
	 */
	public static String getUserJid(String wid) {
		return widToJid.get(wid);
	}

	/**
	 * Saves a session by adding it to the map.
	 * 
	 * @param id
	 *            the user's web id.
	 * @param session
	 *            the spark session.
	 */
	public static void saveSession(String id, Session session) {
		System.out.println("Saving Session");
		sessionMap.put(id, session);
		widToJid.put(session.attribute("webId"), id);
	}

	/**
	 * Returns a session given the user's web id.
	 * 
	 * @param id
	 *            the user's web id.
	 * @return the session.
	 */
	public static Session getSession(String id) {
		return sessionMap.get(id);
	}

	private static final long TIME = 1000L;

	/**
	 * Returns a list of all pending orders.
	 * 
	 * @return the list of orders
	 */
	public List<Order> getPendingOrders() {
		final List<Order> os = new ArrayList<>();
		for (final Order o : new ArrayList<>(pendingOrders)) {
			if ((double) System.currentTimeMillis() / (double) TIME <= o.getDropoffTime()) {
				os.add(o);
			} else {
				OrderWebSocket.sendTimeout(o);
				removeOrder(o);
			}
		}
		return os;
	}

	/**
	 * Returns an empty list for ranking.
	 * 
	 * @param u
	 *            the user.
	 * @return an empty list for ranking.
	 */
	static synchronized List<Order> rank(User u) {
		return Collections.<Order>emptyList();
	}

	/**
	 * Adds an order to manager.
	 * 
	 * @param o
	 *            the order to add.
	 */
	public static synchronized void addOrder(Order o) {
		pendingOrders.add(o);
		System.out.println("ADDING ORDER IN addOrder()");
		System.out.println(widToJid.get(o.getOrderer().getWebId()));
		setActiveUser(widToJid.get(o.getOrderer().getWebId()));
		System.out.println("ACTIVEUSERS");
		for (final String s : activeUsers) {
			System.out.println(s);
		}
	}

	static synchronized boolean removeOrder(Order o) {
		removeActiveUser(widToJid.get(o.getOrderer().getWebId()));
		jidToOrder.remove(widToJid.get(o.getOrderer().getWebId()));
		return pendingOrders.remove(o);
	}

	private static final int DIV = 1000;

	/**
	 * Builds an order upon being submitted by client.
	 *
	 */
	public static class OrderMaker implements Route {
		@Override
		public Object handle(Request req, Response res) {
			final QueryParamsMap qm = req.queryMap();
			System.out.println("HERE");
			final double pLat = Double.parseDouble(qm.value("pickupLat"));
			final double pLon = Double.parseDouble(qm.value("pickupLon"));
			final double dLat = Double.parseDouble(qm.value("dropoffLat"));
			final double dLon = Double.parseDouble(qm.value("dropoffLon"));
			final String item = qm.value("item");
			Date time = new Date();
			try {
				time = DATE_FORMAT.parse(qm.value("time"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date cur = new Date();
			try {
				cur = DATE_FORMAT.parse(DATE_FORMAT.format(cur));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			final Calendar uT = Calendar.getInstance();
			uT.setTime(time);
			c.set(Calendar.HOUR_OF_DAY, uT.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, uT.get(Calendar.MINUTE));
			c.set(Calendar.SECOND, 0);
			if (time.before(cur)) {
				c.add(Calendar.DATE, 1);
			}
			time = c.getTime();
			final double price = Double.parseDouble(qm.value("price"));
			final OrderBuilder builder = new OrderBuilder();
			final User curr = User.byWebId(req.session().attribute("webId"));
			final Location pickup = Location.newLocation(pLat, pLon, qm.value("pickup"));
			final Location dropoff = Location.newLocation(dLat, dLon, qm.value("dropoff"));
			builder.setOrderer(curr).setPickup(pickup).setDropoff(dropoff).setPrice(price)
					.setDropoffTime((double) time.getTime() / (double) DIV).setItems(Arrays.asList(item))
					.setOrderStatus(OrderStatus.UNASSIGNED).setPhone(qm.value("phone"));
			if (qm.value("submit").equals("true")) {
				final Order o = builder.build();
				System.out.println("ADDING TO QUEUE");
				o.addToDatabase();
				OrderWebSocket.sendAddOrder(o);
				return GSON.toJson(ImmutableMap.of("id", o.getId()));
			} else {
				final Order o = builder.setId("").build();
				jidToOrder.put(req.session().id(), o);
			}
			return new Object();
		}
	}

	/**
	 * Returns the list of pending orders to the server.
	 *
	 */
	public static class PendingOrders implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			final Order o = jidToOrder.get(arg0.session().id());
			final Map<String, Object> res = new HashMap<>();
			if (o == null) {
				res.put("stored", "false");
				return GSON.toJson(res);
			}
			res.put("stored", "true");
			res.put("pickupLat", o.getPickupLocation().getLatitude());
			res.put("pickupLon", o.getPickupLocation().getLongitude());
			res.put("dropoffLat", o.getDropoffLocation().getLatitude());
			res.put("dropoffLon", o.getDropoffLocation().getLongitude());
			res.put("pickup", o.getPickupLocation().getName());
			res.put("dropoff", o.getDropoffLocation().getName());
			res.put("item", o.getOrderItems().get(0));
			res.put("time", DATE_FORMAT.format(new Date((long) DIV * (int) o.getDropoffTime())));
			res.put("price", o.getPrice());
			res.put("submit", true);
			return GSON.toJson(res);
		}
	}

	/**
	 * Handler to complete an order.
	 */
	public static class CompletedOrder implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			final QueryParamsMap qm = arg0.queryMap();
			final Order o = Order.byId(qm.value("id"));
			final double price = Double.parseDouble(qm.value("price"));
			o.setOrderStatus(Order.OrderStatus.COMPLETED);
			o.getOrderer().addPastOrder(o);
			o.getOrderer().charge(o.getPrice() + price + 3, o.getOrderItems(), o.getPickupLocation().getName(),
					o.getDropoffLocation().getName());
			o.getDeliverer().addPastDelivery(o);
			Manager.removeActiveUser(Manager.getUserJid(o.getOrderer().getWebId()));
			Manager.removeActiveUser(Manager.getUserJid(o.getDeliverer().getWebId()));
			OrderWebSocket.completeOrderRequester(widToJid.get(o.getOrderer().getWebId()));
			final Sender sender = new Sender(o.getOrderer().getCell());
			sender.updateMessage("complete", o);
			sender.sendMessage();
			return "";
		}
	}

	/**
	 * Provides users ratings upon post request.
	 *
	 */
	public static class Rating implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			final QueryParamsMap qm = arg0.queryMap();
			final String role = qm.value("role");
			final double score = Double.parseDouble(qm.value("rating"));
			final Order o = Order.byId(qm.value("id"));
			if (role.equals("deliverer")) {
				o.getOrderer().addOrdererRating(score);
			} else {
				o.getDeliverer().addDelivererRating(score);
			}
			return "";
		}
	}
}
