package edu.brown.cs.jchaiken.projectcontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A "controller" class for the application which handles the main user
 * interaction with the webpage. There is only one such instance of this class
 * (most methods are static) so data structures need to be concurrent.
 *
 * @author sumitsohani
 *
 */
public class Manager {

  private static List<User> activeUsers;
  private static List<Order> pendingOrders;
  private static Map<User, Order> pendingMatches;
  private static final Gson GSON = new Gson();
  private static final int MAX_CACHE = 50000;
  private static Cache<User, Order> completedOrder;

  /**
   * Constructor for Manager.
   */
  public Manager() {
    activeUsers = Collections.synchronizedList(new ArrayList<>());
    pendingOrders = Collections.synchronizedList(new ArrayList<>());
    pendingMatches = Collections.synchronizedMap(new HashMap<>());
    completedOrder = CacheBuilder.newBuilder().maximumSize(MAX_CACHE)
        .expireAfterAccess(125, TimeUnit.MINUTES).build();
  }

  // Trigger handler
  static synchronized boolean select(User u, Order o) {
    activeUsers.remove(u);
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
    activeUsers.add(u);
  }

  synchronized static List<Order> rank(User u) {
    return Collections.<Order>emptyList();
  }

  synchronized static void addOrder(Order o) {
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

  private static class OrderHandler implements Route {
    @Override
    public String handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String u = qm.value("user");
      User user = null; // get from User global map and String u
      List<Order> ords = rank(user);
      Map<String, Object> variables = ImmutableMap.of("order", ords);
      return GSON.toJson(variables);
    }
  }

  private static class LoginHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      // Todo
      return "";
    }
  }

}
