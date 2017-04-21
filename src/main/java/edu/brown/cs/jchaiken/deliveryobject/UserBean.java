package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * UserBean models a user after it has been read in from the database.
 * @author jacksonchaiken
 *
 */
final class UserBean extends DeliveryObjectBean<User> implements User,
    Serializable {
  private static final long serialVersionUID = -3254567965198728L;
  private String name;
  private Collection<Order> pastDeliveries;
  private Collection<Order> pastOrders;
  private Collection<Order> currDeliveries;
  private Collection<Order> currOrders;
  private String paymentId;
  private String cell;
  private final int password;
  private double distancePref;
  private double feePref;
  private double timePref;
  private List<Double> ordererRatings;
  private List<Double> delivererRatings;

  private AccountStatus status;
  private UserBean(String email, String newName, String newPaymentId, String
      cellNum, Integer newPass, AccountStatus newStatus) {
    super(email);
    password = newPass;
    name = newName;
    paymentId = newPaymentId;
    status = newStatus;
    pastDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    pastOrders = Collections.synchronizedCollection(new HashSet<Order>());
    currDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    currOrders = Collections.synchronizedCollection(new HashSet<Order>());
    cell = cellNum;
    distancePref = -1;
    feePref = -1;
    ordererRatings = new ArrayList<>();
    delivererRatings = new ArrayList<>();
    timePref = -1;
  }

  private void setOrdererRatings(List<Double> oRatings) {
    ordererRatings = oRatings;
  }

  private void setDelivererRatings(List<Double> dRatings) {
    delivererRatings = dRatings;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Collection<Order> pastDeliveries() {
    return pastDeliveries;
  }

  @Override
  public Collection<Order> currentDeliveries() {
    return currDeliveries;
  }

  @Override
  public Collection<Order> pastOrders() {
    return pastOrders;
  }

  @Override
  public Collection<Order> currentOrders() {
    return currOrders;
  }

  @Override
  public void addPastOrder(Order order) {
    pastOrders.add(order);
  }

  @Override
  public void addCurrentOrder(Order order) {
    currOrders.add(order);
  }

  @Override
  public void addCurrentDelivery(Order order) {
    currDeliveries.add(order);
  }

  @Override
  public void addPastDelivery(Order order) {
    pastDeliveries.add(order);
  }

  @Override
  public String getStripeId() {
    return paymentId;
  }

  @Override
  public String getCell() {
    return cell;
  }

  @Override
  public void setStripeId(String id) {
    paymentId = id;
  }


  @Override
  public void pay(double amount) {
    //TODO : Stripe stuff
  }

  @Override
  public void charge(double amount) {
    //TODO : Stripe stuff
  }

  private static final String USER_A = "INSERT INTO users VALUES (?,?,?,?,?)";
  private static final String STATUS_A =
      "INSERT INTO account_status VALUES (?,?)";

  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(USER_A)) {
      prep.setString(2, name);
      prep.setString(1, super.getId());
      prep.setString(3, cell);
      prep.setInt(4, password);
      prep.setString(5, paymentId);
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(STATUS_A)) {
      prep.setString(1, super.getId());
      prep.setInt(2, status.ordinal());
      prep.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static final String USER_R = "DELETE FROM users WHERE id = ?";
  private static final String STATUS_R =
      "DELETE FROM account_status WHERE user_id = ?";

  @Override
  public void removeFromDatabase() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(STATUS_R)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(USER_R)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * UserBuilder provides a way to build a User if it must be created for
   * the first time and it should be added to the database.
   * @author jacksonchaiken
   *
   */
  public static class UserBuilder {
    private String id = null;
    private String name = null;
    private String paymentId = null;
    private String cell = null;
    private Integer pass = null;
    private AccountStatus status = null;
    private List<Double> oRatings;
    private List<Double> dRatings;
    UserBuilder setStatus(AccountStatus stat) {
      status = stat;
      return this;
    }

    UserBuilder setId(String newId) {
      this.id = newId;
      return this;
    }

    UserBuilder setName(String newName) {
      this.name = newName;
      return this;
    }

    UserBuilder setPassword(Integer password) {
      pass = password;
      return this;
    }

    UserBuilder setPayment(String newPaymentId) {
      this.paymentId = newPaymentId;
      return this;
    }

    UserBuilder setCell(String cellNumber) {
      this.cell = cellNumber;
      return this;
    }

    UserBuilder setDelivererRatings(List<Double> ratings) {
      dRatings = ratings;
      return this;
    }

    UserBuilder setOrdererRatings(List<Double> ratings) {
      oRatings = ratings;
      return this;
    }

    User build() {
      if (id == null || name == null || paymentId == null || cell == null
          || pass == null || status == null || dRatings == null
          || oRatings == null) {
        throw new IllegalArgumentException("There are parameters that have"
            + " not been set");
      }
      UserBean user = new UserBean(id, name, paymentId, cell, pass, status);
      user.setDelivererRatings(dRatings);
      user.setOrdererRatings(oRatings);
      if (DeliveryObjectProxy.getCache().getIfPresent(id) == null) {
        DeliveryObjectProxy.getCache().put(id, user);
      }
      return user;

    }
  }

  @Override
  public AccountStatus status() {
    return status;
  }

  @Override
  public void setAccountStatus(AccountStatus newStatus) {
    status = newStatus;
  }

  @Override
  public void addDeliveryPreferences(double distance, double minimumFee,
        double maximumTime) {
    distancePref = distance;
    feePref = minimumFee;
    timePref = maximumTime;
  }

  @Override
  public double getDeliveryDistancePreference() {
    return distancePref;
  }

  @Override
  public double getDeliveryFeePreference() {
    return feePref;
  }

  @Override
  public double getDeliveryTimePreference() {
    return timePref;
  }

  @Override
  public double getDelivererRating() {
    double sum = 0;
    if (delivererRatings.size() == 0) {
      return -1;
    }
    for (double rating : delivererRatings) {
      sum += rating;
    }
    sum = sum / delivererRatings.size();
    return sum;
  }

  @Override
  public double getOrdererRating() {
    double sum = 0;
    if (ordererRatings.size() == 0) {
      return -1;
    }
    for (double rating : ordererRatings) {
      sum += rating;
    }
    sum = sum / ordererRatings.size();
    return sum;
  }

  @Override
  public void addOrdererRating(double rating) {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("INSERT INTO user_ratings VALUES(?,?,?)")) {
      prep.setString(1, super.getId());
      prep.setDouble(2, rating);
      prep.setString(3, "orderer");
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    ordererRatings.add(rating);
  }

  @Override
  public void addDelivererRating(double rating) {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("INSERT INTO user_ratings VALUES(?,?,?)")) {
      prep.setString(1, super.getId());
      prep.setDouble(2, rating);
      prep.setString(3, "deliverer");
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    delivererRatings.add(rating);
  }
}
