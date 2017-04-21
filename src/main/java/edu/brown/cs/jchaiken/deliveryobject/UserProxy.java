package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;
import edu.brown.cs.jchaiken.deliveryobject.UserBean.UserBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * UserProxy models a User if it has not been read in from the database.
 * @author jacksonchaiken
 *
 */
class UserProxy extends DeliveryObjectProxy<User> implements User {

  UserProxy(String id) {
    super(id);
  }

  @Override
  public String getName() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getName();
  }


  @Override
  public Collection<Order> pastDeliveries() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().pastDeliveries();
  }

  @Override
  public Collection<Order> currentDeliveries() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().currentDeliveries();
  }

  @Override
  public Collection<Order> pastOrders() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().pastOrders();
  }

  @Override
  public Collection<Order> currentOrders() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().currentOrders();
  }


  @Override
  public void addPastDelivery(Order order) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addPastDelivery(order);
  }

  @Override
  public void addPastOrder(Order order) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addPastOrder(order);
  }

  @Override
  public void addCurrentOrder(Order order) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addCurrentOrder(order);
  }

  @Override
  public void addCurrentDelivery(Order order) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addCurrentDelivery(order);
  }

  @Override
  public String getStripeId() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getStripeId();
  }


  @Override
  public String getCell() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getCell();
  }

  @Override
  public void setStripeId(String id) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setStripeId(id);
  }


  @Override
  public AccountStatus status() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().status();
  }

  @Override
  public void setAccountStatus(AccountStatus status) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setAccountStatus(status);
  }

  private static final String CACHE_Q =  "SELECT * FROM users WHERE id = ?";
  private static final String ORDER_Q = "SELECT * FROM orders, order_status"
      + " WHERE orders.deliverer_id = ? OR orders.orderer_id = ?";
  private static final String STATUS_Q = "SELECT * FROM account_status WHERE"
      + " user_id = ?";
  private static final String RATINGS_Q
      =  "SELECT rating, user_type FROM user_ratings WHERE user_id = ?";
  private static final int TEN = 10;
  @Override
  protected void cache() throws SQLException {
    try (PreparedStatement cachePrep = Database.getConnection()
        .prepareStatement(CACHE_Q)) {
      cachePrep.setString(1, super.getId());
      try (ResultSet rs = cachePrep.executeQuery()) {
        if (rs.next()) {
          String name = rs.getString(2);
          String cell = rs.getString(3);
          int pass = rs.getInt(4);
          String stripe = rs.getString(5);
          UserBuilder bean = new UserBuilder();
          try (PreparedStatement statusPrep = Database.getConnection()
              .prepareStatement(STATUS_Q)) {
            statusPrep.setString(1, super.getId());
            try (ResultSet status = statusPrep.executeQuery()) {
              if (status.next()) {
                bean.setStatus(AccountStatus.valueOf(status.getInt(2)));
              }
            }
          }
          List<Double> oRatings = new ArrayList<>();
          List<Double> dRatings = new ArrayList<>();
          try (PreparedStatement ratePrep = Database.getConnection()
              .prepareStatement(RATINGS_Q)) {
            ratePrep.setString(1, super.getId());
            try (ResultSet ratings = ratePrep.executeQuery()) {
              while (ratings.next()) {
                if (ratings.getString(2).equals("orderer")) {
                  oRatings.add(ratings.getDouble(1));
                } else {
                  dRatings.add(ratings.getDouble(1));
                }
              }
            }
          }
          bean.setDelivererRatings(dRatings);
          bean.setOrdererRatings(oRatings);
          User newUser = bean.setCell(cell)
              .setPayment(stripe)
              .setId(super.getId())
              .setName(name)
              .setPassword(pass)
              .build();
          try (PreparedStatement orderPrep =  Database.getConnection()
              .prepareStatement(ORDER_Q)) {
            orderPrep.setString(1, super.getId());
            orderPrep.setString(2, super.getId());
            try (ResultSet orders = orderPrep.executeQuery()) {
              while (orders.next()) {
                if (OrderStatus.valueOf(orders.getInt(TEN))
                    == OrderStatus.COMPLETED) {
                  //is completed order
                  if (orders.getString(2).equals(super.getId())) {
                    newUser.addPastOrder(new OrderProxy(
                        orders.getString(1)));
                  } else {
                    //completed delivery
                    newUser.addPastDelivery(new OrderProxy(
                        orders.getString(1)));
                  }
                } else {
                  //in progress order
                  if (orders.getString(2).equals(super.getId())) {
                    newUser.addCurrentOrder(new OrderProxy(
                        orders.getString(1)));
                  } else {
                    //in progress delivery
                    newUser.addCurrentDelivery(new OrderProxy(
                        orders.getString(1)));
                  }
                }
              }
            }
          }
          assert newUser != null;
          super.setData(newUser);
        }
      }
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  @Override
  public void pay(double amount) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().pay(amount);
  }

  @Override
  public void charge(double amount) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().charge(amount);
  }

  @Override
  public void addToDatabase() {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addToDatabase();
  }

  @Override
  public void removeFromDatabase() {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().removeFromDatabase();

  }

  private static final String VAL = "SELECT * FROM users WHERE id = ?"
      + " AND password = ?";
  /**
   * Returns true or false if an email password combination exists.
   * @param email the potential account email.
   * @param password the potential account's password.
   * @return true or false whether it exists or not.
   */
  public static boolean userValidator(String email, String password) {
    int hash = password.hashCode();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(VAL)) {
      prep.setString(1, email);
      prep.setInt(2, hash);
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          return true;
        }
      }
    } catch (SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
    return false;
  }

  @Override
  public void addDeliveryPreferences(double distance, double minimumFee,
      double maximumTime) {
    // TODO Auto-generated method stub
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addDeliveryPreferences(distance, minimumFee, maximumTime);

  }

  @Override
  public double getDeliveryDistancePreference() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getDeliveryDistancePreference();
  }

  @Override
  public double getDeliveryFeePreference() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getDeliveryFeePreference();

  }

  @Override
  public double getDeliveryTimePreference() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getDeliveryTimePreference();

  }

  @Override
  public double getDelivererRating() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getDelivererRating();
  }

  @Override
  public double getOrdererRating() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getOrdererRating();
  }

  @Override
  public void addOrdererRating(double rating) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addOrdererRating(rating);
  }

  @Override
  public void addDelivererRating(double rating) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addDelivererRating(rating);
  }
}
