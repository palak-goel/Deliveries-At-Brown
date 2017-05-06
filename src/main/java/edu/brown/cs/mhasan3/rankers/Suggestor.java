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

  private User user;

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
   * @return List<String> of items
   */
  public List<String> suggestItem() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    // System.out.println(past);
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      List<String> items = o.getOrderItems();
      for (String s : items) {
        if (!map.containsKey(s)) {
          SuggestionItem temp = new SuggestionItem(s);
          map.put(s, temp);
        } else {
          SuggestionItem ite = map.get(s);
          ite.setRank(ite.getRank() + 1);
          map.put(s, ite);
        }
      }
    }
    PriorityQueue<SuggestionItem> vals = this.addQueue(map);
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
    List<String> ans = new ArrayList<String>();
    while (!q.isEmpty()) {
      SuggestionItem tem = q.poll();
      ans.add(tem.getID());
    }
    return ans;
  }

  /**
   * Suggests pickup locations.
   * 
   * @return List<String> of locations.
   */
  public List<String> suggestPickup() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = o.getPickupLocation().getName();
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
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
    PriorityQueue<SuggestionItem> queue = new PriorityQueue<SuggestionItem>(
        new SuggestionQueue());
    for (SuggestionItem value : ma.values()) {
      queue.add(value);
    }
    return queue;
  }

  /**
   * Suggests dropoff locations.
   * 
   * @return List<String> locations
   */

  public List<String> suggestDropoff() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = o.getDropoffLocation().getName();
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
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
   * @return List<String> of times
   */

  public List<String> suggestUserTime() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      Double start = o.getPickupTime();
      Double end = o.getDropoffTime();
      Double diff = (end - start) / (60 * 1000);
      String temp = Double.toString(diff);
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;
  }

  /**
   * Suggests a payment amount for the deliverer.
   * 
   * @return List<String> Payments
   */

  public List<String> suggestPayment() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = Double.toString(o.getPrice());
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;
  }

  /**
   * Suggests a timeframe that a deliverer should desire.
   * 
   * @return List<String> of times
   */

  public List<String> suggestDeliverTime() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastDeliveries();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      Double start = o.getPickupTime();
      Double end = o.getDropoffTime();
      Double diff = (end - start) / (60 * 1000);
      String temp = Double.toString(diff);
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;
  }

  /**
   * Suggests how much of a payment a deliverer should want.
   * 
   * @return List<String> of payments
   */

  public List<String> suggestUserPayment() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastDeliveries();
    if (past == null) {
      return res;
    }
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = Double.toString(o.getPrice()); // change to something
                                                   // specific
      if (!map.containsKey(temp)) {
        map.put(temp, new SuggestionItem(temp));
      } else {
        SuggestionItem an = map.get(temp);
        an.setRank(an.getRank() + 1);
        map.put(temp, an);
      }
    }
    res = this.getList(this.addQueue(map));
    return res;

  }
}
