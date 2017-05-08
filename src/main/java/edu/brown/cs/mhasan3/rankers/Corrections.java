package edu.brown.cs.mhasan3.rankers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
   * Capitalizes the first letter of the String.
   *
   * @param m
   *          the String to be capitalized
   * @return The capitalized string
   */

  public String capitalize(String m) {
    final String[] n = m.split(" ");
    for (int i = 0; i < n.length; i++) {
      String tem = n[i];
      if (tem.length() > 1) {
        tem = tem.substring(0, 1).toUpperCase(Locale.getDefault())
            + tem.substring(1);
        n[i] = tem;
      }
    }
    final StringBuilder builder = new StringBuilder();
    for (final String s : n) {
      if (builder.length() > 0) {
        builder.append(" ");
      }
      builder.append(s);
    }
    final String r1 = builder.toString();
    return r1;
  }

  /**
   * Gives a list of suggestions for the place that we want auto corrected.
   *
   * @param str
   *          string to be auto corrected
   * @param u
   *          the user being corrected for
   * @return the suggestions
   * @throws SQLException
   *           e
   */

  // CHANGES - SHOULD BE GET NAME OF SOME SORT, NOT GET ID
  // MAYBE ADD ALL LOCATIONS TO THE DATABASE
  public List<String> correctPlace(String[] str, User u) throws SQLException {
    final CommandCorrect corr = new CommandCorrect();
    final Collection<Order> past = u.pastDeliveries();
    final List<Order> p = new ArrayList<>(past);
    final List<String> old = new ArrayList<>();
    for (final Order o : p) {
      old.add(o.getDropoffLocation().getName()); // change
      old.add(o.getPickupLocation().getName());
    }
    corr.addList(old);
    final List<String> res = corr.combineResults(str);
    final List<String> las = new ArrayList<>();
    for (final String r : res) {
      final String temp = this.capitalize(r);
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
   * @return the suggestions
   * @throws SQLException
   *           e
   */

  // TO CHANGE - GET ACCESS TO ALL ITEMS IN DATABASE(WOULD HELP)ß
  public List<String> correctItems(String[] str, User u) throws SQLException {
    final CommandCorrect corr = new CommandCorrect();
    final Collection<Order> past = u.pastDeliveries();
    final List<Order> p = new ArrayList<>(past);
    final List<String> old = new ArrayList<>();
    for (final Order o : p) {
      final List<String> temp = o.getOrderItems();
      for (final String s : temp) {
        old.add(s);
      }
    }
    corr.addList(old);
    final List<String> res = corr.combineResults(str);
    final List<String> las = new ArrayList<>();
    for (final String r : res) {
      final String temp = this.capitalize(r);
      las.add(temp);
    }
    return las;
  }

}
