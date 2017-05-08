package edu.brown.cs.mhasan3.rankers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

import edu.brown.cs.jchaiken.deliveryobject.Location;
import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.projectcontrol.Manager;

/**
 * Class describes the algorithm that is used to rank orders in a user's feed.
 *
 * @author shehryarhasan
 *
 */
public class Ranker {

	private final Manager manage;
	private final List<Order> orders;
	private final User user;

	/**
	 * Constructor takes in the manager and the current user.
	 *
	 * @param m
	 *            manager
	 * @param curr
	 *            user
	 */
	public Ranker(Manager m, User curr) {
		manage = m;
		orders = manage.getPendingOrders();
		user = curr;
	}

	/**
	 * Orders by the time the order was inputted.
	 *
	 * @return list
	 */
	public List<Order> orderByTime() {
		final PriorityQueue<Order> queue = new PriorityQueue<>(new RankerQueue());
		for (final Order o : orders) {
			final double tim = o.getDropoffTime();
			o.setRanking(tim);
			queue.add(o);
		}
		return this.returnAnswer(queue);
	}

	/**
	 * Orders by the tip input only.
	 *
	 * @return ordered list
	 */
	public List<Order> orderByPrice() {
		final PriorityQueue<Order> queue = new PriorityQueue<>(new RankerQueue());
		for (final Order o : orders) {
			final double tip = o.getPrice();
			o.setRanking(tip);
			queue.add(o);
		}
		final List<Order> q = this.returnAnswer(queue);
		return q;
	}

	/**
	 * Orders by the distance only.
	 *
	 * @return ordered list
	 */
	public List<Order> orderByDistance() {
		final PriorityQueue<Order> queue = new PriorityQueue<>(new RankerQueue());
		for (final Order o : orders) {
			final Location pickLoc = o.getPickupLocation();
			final double la1 = pickLoc.getLatitude();
			final double lg1 = pickLoc.getLongitude();
			final Location dropLoc = o.getDropoffLocation();
			final double la2 = dropLoc.getLatitude();
			final double lg2 = dropLoc.getLongitude();
			final double res = this.haversineFormula(la1, lg1, la2, lg2);
			o.setRanking(res);
			queue.add(o);
		}
		final List<Order> q = this.returnAnswer(queue);
		return q;
	}

	/**
	 * Method is called to return the ranked list.
	 *
	 * @return List of Orders
	 */
	public List<Order> rank() {
		final PriorityQueue<Order> queue = new PriorityQueue<>(new RankerQueue());
		for (final Order o : orders) {
			final double time = this.considerTime(o); // this has to be fixed.
			final double dist = this.considerDistance(o);
			final double stars = this.considerStars(o);
			final double payment = this.considerPayment(o);
			final double history = this.considerHistory(o);
			// System.out.println(time);
			// System.out.println(dist);
			// System.out.println(stars);
			// System.out.println(payment);
			// System.out.println(history);
			final double weightedAverage = this.weightedAverage(time, dist, stars, payment, history);
			// System.out.println("avg" + weightedAverage);
			o.setRanking(weightedAverage);
			queue.add(o);
		}
		return this.returnAnswer(queue);
	}

	/**
	 * Helper method that turns a queue into an Arraylist.
	 *
	 * @param q
	 *            queue
	 * @return list
	 */
	public List<Order> returnAnswer(PriorityQueue<Order> q) {
		final List<Order> ans = new ArrayList<>();
		while (!q.isEmpty()) {
			final Order tem = q.poll();
			ans.add(tem);
		}
		return ans;
	}

	/**
	 * Finds the weighted average of all of the individual components.
	 *
	 * @param t
	 *            time
	 * @param d
	 *            distance
	 * @param s
	 *            stars
	 * @param pay
	 *            payment
	 * @param his
	 *            history
	 * @return double for average
	 */
	public double weightedAverage(double t, double d, double s, double pay, double his) {
		final double ans = t + d + (3 * s) + (5 * pay) + his;
		return ans;
	}

	/**
	 * Considers the time of the order.
	 *
	 * @param curr
	 *            Order
	 * @return double for average
	 */

	public double considerTime(Order curr) {
		final double userTime = user.getDeliveryTimePreference();
		// System.out.println("user" + userTime);
		final double currTime = (double) System.currentTimeMillis() / (double) 1000;
		final double endTime = curr.getDropoffTime();
		// System.out.println("curr" + currTime);
		// System.out.println("end" + endTime);
		// currTime =
		final double timeDiffSecs = endTime - currTime;
		final double timeDiffMins = timeDiffSecs / 60;
		final double res = Math.abs(userTime - timeDiffMins);
		return res;
	}

	/**
	 * Considers the distance that the user will have to walk to deliver the
	 * order.
	 *
	 * @param curr
	 *            Order
	 * @return double for average
	 */
	public double considerDistance(Order curr) {
		final double userDist = user.getDeliveryDistancePreference();
		final Location pickLoc = curr.getPickupLocation();
		final double la1 = pickLoc.getLatitude();
		final double lg1 = pickLoc.getLongitude();
		final Location dropLoc = curr.getDropoffLocation();
		final double la2 = dropLoc.getLatitude();
		final double lg2 = dropLoc.getLongitude();
		final double res = this.haversineFormula(la1, lg1, la2, lg2);
		final double rem = Math.abs(userDist - res);
		return rem;
	}

	/**
	 * Uses the Haversine Formula to find the distance from start to end.
	 *
	 * @param lat1
	 *            latitude
	 * @param lng1
	 *            longitude
	 * @param lat2
	 *            latitude
	 * @param lng2
	 *            longitude
	 * @return distance
	 */
	public double haversineFormula(Double lat1, Double lng1, Double lat2, Double lng2) {
		double res = 0.0;
		final double radius = 6378.137;
		final double dLat = Math.toRadians(lat2 - lat1);
		final double dLon = Math.toRadians(lng2 - lng1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		final double a = Math.pow(Math.sin(dLat / 2), 2)
				+ Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		final double c = 2 * Math.asin(Math.sqrt(a));
		res = radius * c;
		return res;
	}

	/**
	 * Considers the Payment that the orderer would receieve.
	 *
	 * @param curr
	 *            Order
	 * @return double for average
	 */

	public double considerPayment(Order curr) {
		final double currentPay = curr.getPrice();
		final double desiredPay = user.getDeliveryFeePreference();
		final double res = desiredPay - currentPay;
		return res;
	}

	/**
	 * Considers the requestor's star rating.
	 *
	 * @param curr
	 *            Order
	 * @return double for average
	 */
	public double considerStars(Order curr) {
		final User u = curr.getOrderer();
		double stars = u.getOrdererRating();
		stars = 5.0 - stars;
		return stars;
	}

	/**
	 * Considers whether the orderer and the deliverer have a history of
	 * interactions.
	 *
	 * @param curr
	 *            Order
	 * @return double for average
	 */
	public double considerHistory(Order curr) {
		final User u = curr.getOrderer();
		final Collection<Order> past = u.pastDeliveries();
		final List<Order> p = new ArrayList<>(past);
		double res = 5.0;
		for (final Order o : p) {
			if (o.getOrderer().getId().equals(u.getId()) && res > 0.0) {
				res = res - 1.0;
			}
		}
		return res;
	}
}
