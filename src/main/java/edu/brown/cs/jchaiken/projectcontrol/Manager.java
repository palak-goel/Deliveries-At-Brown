package edu.brown.cs.jchaiken.projectcontrol;

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

	public static void setActiveUser(String jid) {
		activeUsers.add(jid);
	}

	public static boolean isActiveUser(String jid) {
		return activeUsers.contains(jid);
	}

	public static void removeActiveUser(String jid) {
		activeUsers.remove(jid);
	}

	public static List<Session> allSessions() {
		return new ArrayList<>(sessionMap.values());
	}

	public static String getUserJid(String wid) {
		return widToJid.get(wid);
	}

	public static void saveSession(String id, Session session) {
		System.out.println("Saving Session");
		sessionMap.put(id, session);
		widToJid.put(session.attribute("webId"), id);
	}

	public static Session getSession(String id) {
		return sessionMap.get(id);
	}

	public List<Order> getPendingOrders() {
		List<Order> os = new ArrayList<>();
		for (Order o : new ArrayList<>(pendingOrders)) {
			if (System.currentTimeMillis() / 1000L <= o.getDropoffTime()) {
				os.add(o);
			} else {
				OrderWebSocket.sendTimeout(o);
				removeOrder(o);
			}
		}
		return os;
	}

	synchronized static List<Order> rank(User u) {
		return Collections.<Order>emptyList();
	}

	public synchronized static void addOrder(Order o) {
		pendingOrders.add(o);
		activeUsers.add(widToJid.get(o.getOrderer().getWebId()));
		System.out.println("ACTIVEUSERS");
		for (String s : activeUsers) {
			System.out.println(s);
		}
	}

	synchronized static boolean removeOrder(Order o) {
		activeUsers.remove(widToJid.get(o.getOrderer().getWebId()));
		jidToOrder.remove(widToJid.get(o.getOrderer().getWebId()));
		return pendingOrders.remove(o);
	}

	public static class OrderMaker implements Route {
		@Override
		public Object handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			System.out.println("HERE");
			try {
				double pLat = Double.parseDouble(qm.value("pickupLat"));
				double pLon = Double.parseDouble(qm.value("pickupLon"));
				double dLat = Double.parseDouble(qm.value("dropoffLat"));
				double dLon = Double.parseDouble(qm.value("dropoffLon"));
				String item = qm.value("item");
				Date time = DATE_FORMAT.parse(qm.value("time"));
				Date cur = new Date();
				cur = DATE_FORMAT.parse(DATE_FORMAT.format(cur));
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				Calendar uT = Calendar.getInstance();
				uT.setTime(time);
				c.set(Calendar.HOUR_OF_DAY, uT.get(Calendar.HOUR_OF_DAY));
				c.set(Calendar.MINUTE, uT.get(Calendar.MINUTE));
				c.set(Calendar.SECOND, 0);
				if (time.before(cur)) {
					c.add(Calendar.DATE, 1);
				}
				time = c.getTime();
				double price = Double.parseDouble(qm.value("price"));
				OrderBuilder builder = new OrderBuilder();
				User curr = User.byWebId(req.session().attribute("webId"));
				Location pickup = Location.newLocation(pLat, pLon, qm.value("pickup"));
				Location dropoff = Location.newLocation(dLat, dLon, qm.value("dropoff"));
				builder.setOrderer(curr).setPickup(pickup).setDropoff(dropoff).setPrice(price)
						.setDropoffTime(time.getTime() / 1000).setItems(Arrays.asList(item))
						.setOrderStatus(OrderStatus.UNASSIGNED).setPhone(qm.value("phone"));
				if (qm.value("submit").equals("true")) {
					Order o = builder.build();
					System.out.println("ADDING TO QUEUE");

					o.addToDatabase();
					OrderWebSocket.sendAddOrder(o);
					return GSON.toJson(ImmutableMap.of("id", o.getId()));
				} else {
					Order o = builder.setId("").build();
					jidToOrder.put(req.session().id(), o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Object();
		}
	}

	public static class PendingOrders implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			Order o = jidToOrder.get(arg0.session().id());
			Map<String, Object> res = new HashMap<>();
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
			res.put("time", DATE_FORMAT.format(new Date((long) 1000 * (int) o.getDropoffTime())));
			res.put("price", o.getPrice());
			res.put("submit", true);
			return GSON.toJson(res);
		}
	}

	public static class CompletedOrder implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			QueryParamsMap qm = arg0.queryMap();
			Order o = Order.byId(qm.value("id"));
			double price = Double.parseDouble(qm.value("price"));
			o.setOrderStatus(Order.OrderStatus.COMPLETED);
			o.getOrderer().addPastOrder(o);
			o.getOrderer().charge(o.getPrice() + price + 7.60, o.getOrderItems().get(0),
					o.getPickupLocation().getName(), o.getDropoffLocation().getName());
			o.getDeliverer().addPastDelivery(o);
			Manager.removeActiveUser(Manager.getUserJid(o.getOrderer().getWebId()));
			Manager.removeActiveUser(Manager.getUserJid(o.getDeliverer().getWebId()));
			OrderWebSocket.completeOrderRequester(widToJid.get(o.getOrderer().getWebId()));
			Sender sender = new Sender(o.getOrderer().getCell());
			sender.updateMessage("complete", o);
			sender.sendMessage();
			return "";
		}
	}

	public static class Rating implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			QueryParamsMap qm = arg0.queryMap();
			String role = qm.value("role");
			double score = Double.parseDouble(qm.value("rating"));
			Order o = Order.byId(qm.value("id"));
			if (role.equals("deliverer")) {
				o.getOrderer().addOrdererRating(score);
			} else {
				o.getDeliverer().addDelivererRating(score);
			}
			return "";
		}
	}
}
