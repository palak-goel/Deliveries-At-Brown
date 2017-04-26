package edu.brown.cs.mhasan3.rankers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;

/**
 * Class is used to generate autocorrect suggestions.
 * 
 * @author shehryarhasan
 *
 */
public class Corrections {

  /**
   * Constructor.
   */
  public Corrections() {

  }

  /**
   * Capitalizes the first letter of the String
   * 
   * @param String
   *          to be capitalized
   * @return The capitalized string
   */

  public String capitalize(String m) {
    String[] n = m.split(" ");
    for (int i = 0; i < n.length; i++) {
      String tem = n[i];
      if (tem.length() > 1) {
        tem = tem.substring(0, 1).toUpperCase() + tem.substring(1);
        n[i] = tem;
      }
    }
    StringBuilder builder = new StringBuilder();
    for (String s : n) {
      if (builder.length() > 0) {
        builder.append(" ");
      }
      builder.append(s);
    }
    String r1 = builder.toString();
    return r1;
  }

  /**
   * Gives a list of suggestions for the place that we want auto corrected.
   * 
   * @param str:
   *          string to be auto corrected
   * @return List<String> Suggestions
   * @throws SQLException
   *           e
   */

  // CHANGES - SHOULD BE GET NAME OF SOME SORT, NOT GET ID
  // MAYBE ADD ALL LOCATIONS TO THE DATABASE
  public List<String> correctPlace(String[] str, User u) throws SQLException {
    CommandCorrect corr = new CommandCorrect();
    Collection<Order> past = u.pastDeliveries();
    List<Order> p = new ArrayList<Order>(past);
    List<String> old = new ArrayList<String>();
    for (Order o : p) {
      old.add(o.getDropoffLocation().getId()); // change
      old.add(o.getPickupLocation().getId());
    }
    corr.addList(old);
    List<String> res = corr.combineResults(str);
    List<String> las = new ArrayList<String>();
    for (String r : res) {
      String temp = this.capitalize(r);
      las.add(temp);
    }
    return las;
  }

  /**
   * Returns suggestions for item names.
   * 
   * @param str
   *          string
   * @param u
   *          user
   * @return List<String> Suggestions
   * @throws SQLException
   *           e
   */

  // TO CHANGE - GET ACCESS TO ALL ITEMS IN DATABASE
  public List<String> correctItems(String[] str, User u) throws SQLException {
    CommandCorrect corr = new CommandCorrect();
    Collection<Order> past = u.pastDeliveries();
    List<Order> p = new ArrayList<Order>(past);
    List<String> old = new ArrayList<String>();
    for (Order o : p) {
      List<String> temp = o.getOrderItems();
      for (String s : temp) {
        old.add(s);
      }
    }
    corr.addList(old);
    List<String> res = corr.combineResults(str);
    List<String> las = new ArrayList<String>();
    for (String r : res) {
      String temp = this.capitalize(r);
      las.add(temp);
    }
    return las;
  }

}
