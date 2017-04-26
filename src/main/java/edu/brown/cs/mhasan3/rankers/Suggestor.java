package edu.brown.cs.mhasan3.rankers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.projectcontrol.Manager;

public class Suggestor {

  private Manager manage;
  private User user;

  public Suggestor(Manager m, User curr) {
    manage = m;
    user = curr;
  }

  public List<String> suggestItem() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
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

  public List<String> getList(PriorityQueue<SuggestionItem> q) {
    List<String> ans = new ArrayList<String>();
    while (!q.isEmpty()) {
      SuggestionItem tem = q.poll();
      ans.add(tem.getID());
    }
    return ans;
  }

  public List<String> suggestPickup() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = o.getPickupLocation().getId();
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

  public PriorityQueue<SuggestionItem> addQueue(
      Map<String, SuggestionItem> ma) {
    PriorityQueue<SuggestionItem> queue = new PriorityQueue<SuggestionItem>(
        new SuggestionQueue());
    for (SuggestionItem value : ma.values()) {
      queue.add(value);
    }
    return queue;
  }

  public List<String> suggestDropoff() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
    List<Order> p = new ArrayList<Order>(past);
    Map<String, SuggestionItem> map = new HashMap<String, SuggestionItem>();
    for (Order o : p) {
      String temp = o.getDropoffLocation().getId();
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

  public List<String> suggestUserTime() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
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

  public List<String> suggestPayment() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastOrders();
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

  public List<String> suggestDeliverTime() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastDeliveries();
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

  public List<String> suggestUserPayment() {
    List<String> res = new ArrayList<String>();
    Collection<Order> past = user.pastDeliveries();
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
