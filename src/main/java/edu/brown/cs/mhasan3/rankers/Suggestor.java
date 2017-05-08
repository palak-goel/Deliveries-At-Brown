package edu.brown.cs.mhasan3.rankers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;

/**
 * Class describes the generation of suggestions for users inputting data.
 *
 * @author shehryarhasan
 *
 */

// TODO - POSSIBLY CHANGE ALL INDIVIDUAL USERS TO AN OVERALL HISTORY?
public class Suggestor {

  private final User user;

  /**
   * Constructor takes in the current user.
   *
   * @param curr
   *          Current User
   */
  public Suggestor(User curr) {
    user = curr;
  }

  /**
   * Provides suggestions for the items that a user might want.
   *
   * @return the list of items
   */
  public List<String> suggestItem() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    // System.out.println(past);
    final List<Order> p = new ArrayList<>(past);
    final Map<String, SuggestionItem> map = new HashMap<>();
    for (final Order o : p) {
      final List<String> items = o.getOrderItems();
      for (final String s : items) {
        if (!map.containsKey(s)) {
          final SuggestionItem temp = new SuggestionItem(s);
          map.put(s, temp);
        } else {
          final SuggestionItem ite = map.get(s);
          ite.setRank(ite.getRank() + 1);
          map.put(s, ite);
        }
      }
    }
    final PriorityQueue<SuggestionItem> vals = this.addQueue(map);
    res = this.getList(vals);
    return res;
  }

  /**
   * Returns a list from a priority queue.
   *
   * @param q
   *          queue
   * @return list of strings
   */
  public List<String> getList(PriorityQueue<SuggestionItem> q) {
    final List<String> ans = new ArrayList<>();
    while (!q.isEmpty()) {
      final SuggestionItem tem = q.poll();
      ans.add(tem.getId());
    }
    return ans;
  }

  /**
   * Suggests pickup locations.
   *
   * @return suggested list of locations.
   */
  public List<String> suggestPickup() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    final List<Order> p = new ArrayList<>(past);
    final Map<String, SuggestionItem> map = new HashMap<>();
    for (final Order o : p) {
      final String temp = o.getPickupLocation().getName();
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        final SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;
  }

  /**
   * Helper Method that adds items to a priority queue from a HashMap.
   *
   * @param ma
   *          HashMap
   * @return priority queue
   */
  public PriorityQueue<SuggestionItem> addQueue(
      Map<String, SuggestionItem> ma) {
    final PriorityQueue<SuggestionItem> queue = new PriorityQueue<>(
        new SuggestionQueue());
    for (final SuggestionItem value : ma.values()) {
      queue.add(value);
    }
    return queue;
  }

  /**
   * Suggests dropoff locations.
   *
   * @return the suggested locations
   */

  public List<String> suggestDropoff() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    final List<Order> p = new ArrayList<>(past);
    final Map<String, SuggestionItem> map = new HashMap<>();
    for (final Order o : p) {
      final String temp = o.getDropoffLocation().getName();
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        final SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;
  }

  /**
   * Suggests a time for the user to input.
   *
   * @return suggested times
   */

  public List<String> suggestUserTime() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    return timeHelper(past);
  }

  private List<String> timeHelper(Collection<Order> past) {
    List<Order> p = new ArrayList<>(past);
    Map<String, SuggestionItem> map = new HashMap<>();
    for (final Order o : p) {
      final Double start = o.getPickupTime();
      final Double end = o.getDropoffTime();
      final Double diff = (end - start) / (60 * 1000);
      final String temp = Double.toString(diff);
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        final SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    return this.getList(this.addQueue(map));
  }
  /**
   * Suggests a timeframe that a deliverer should desire.
   *
   * @return suggested timeframes
   */

  public List<String> suggestDeliverTime() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastDeliveries();
    if (past == null) {
      return res;
    }
    return timeHelper(past);
  }

  /**
   * Suggests a payment amount for the deliverer.
   *
   * @return suggested payment amounts
   */

  public List<String> suggestPayment() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    return paymentHelper(past);
  }

  private List<String> paymentHelper(Collection<Order> past) {
    List<Order> p = new ArrayList<>(past);
    Map<String, SuggestionItem> map = new HashMap<>();
    for (final Order o : p) {
      final String temp = Double.toString(o.getPrice()); // change to something
      // specific
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        final SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    return this.getList(this.addQueue(map));
  }
  /**
   * Suggests how much of a payment a deliverer should want.
   *
   * @return suggested payments
   */
  public List<String> suggestUserPayment() {
    List<String> res = new ArrayList<>();
    final Collection<Order> past = user.pastDeliveries();
    if (past == null) {
      return res;
    }
    return paymentHelper(past);
  }
}
