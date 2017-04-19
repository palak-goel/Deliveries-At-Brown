package edu.brown.cs.jchaiken.projectcontrol;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class Manager {

  private static List<User> activeUsers;
  private static List<Order> pendingOrders;
  private static Map<User, Order> pendingMatches;
  private static final Gson GSON = new Gson();

  // Trigger handler
  static boolean select(User u, Order o) {
    activeUsers.remove(u);
    pendingOrders.remove(o);
    pendingMatches.put(u, o);
    return true;
  }

  void onQuitUser(User u) {
    Order o = pendingMatches.get(u);
    pendingOrders.add(o);
    // UNDO user transactions
    // Finish payment
  }

  void onQuitOrder(Order o) {
    pendingOrders.remove(o);
    // Handle order quitting
  }

  void onEntryUser(User u) {
    activeUsers.add(u);
  }

  static List<Order> rank(User u) {
    return Collections.<Order>emptyList();
  }

  static void addOrder(Order o) {
    pendingOrders.add(o);
  }

  static void removeOrder(Order o) {
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
