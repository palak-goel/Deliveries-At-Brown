package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.UserBean.UserBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

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
    return super.getData().getName();
  }


  @Override
  public Collection<Order> pastDeliveries() {
    check();
    return super.getData().pastDeliveries();
  }

  @Override
  public Collection<Order> currentDeliveries() {
    check();
    return super.getData().currentDeliveries();
  }

  @Override
  public Collection<Order> pastOrders() {
    check();
    return super.getData().pastOrders();
  }

  @Override
  public Collection<Order> currentOrders() {
    check();
    return super.getData().currentOrders();
  }


  @Override
  public void addPastDelivery(Order order) {
    check();
    super.getData().addPastDelivery(order);
  }

  @Override
  public void addPastOrder(Order order) {
    check();
    super.getData().addPastOrder(order);
  }

  @Override
  public void addCurrentOrder(Order order) {
    check();
    super.getData().addCurrentOrder(order);
  }

  @Override
  public void addCurrentDelivery(Order order) {
    check();
    super.getData().addCurrentDelivery(order);
  }

  @Override
  public String getStripeId() {
    check();
    return super.getData().getStripeId();
  }


  @Override
  public String getCell() {
    check();
    return super.getData().getCell();
  }

  @Override
  public void setStripeId(String id) {
    check();
    super.getData().setStripeId(id);
  }

  private static final String cacheQuery =  "SELECT * FROM users WHERE id = ?";
  private static final String orderQuery = "SELECT * FROM orders, order_status"
      + " WHERE orders.deliverer_id = ? OR orders.orderer_id = ?";

  @Override
  protected void cache() throws SQLException {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(cacheQuery)) {
      prep.setString(1, super.getId());
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          String name = rs.getString(2);
          String cell = rs.getString(3);
          String stripe = rs.getString(4);
          UserBuilder bean = new UserBuilder();
          User newUser = bean.setCell(cell)
              .setPayment(stripe)
              .setId(super.getId())
              .setName(name)
              .build();
          try (PreparedStatement prep2 =  Database.getConnection().prepareStatement(orderQuery)) {
            prep.setString(1, super.getId());
            prep.setString(2, super.getId());
            try (ResultSet rq = prep.executeQuery()) {
              while (rq.next()) {
                if (rq.getString(10).equals("COMPLETED")) {
                  //is completed order
                  if (rq.getString(2).equals(super.getId())) {
                    newUser.addPastOrder(new OrderProxy(rs.getString(1)));
                  } else {
                    //completed delivery
                    newUser.addPastDelivery(new OrderProxy(rs.getString(1)));
                  }
                } else {
                  //in progress order
                  if (rq.getString(2).equals(super.getId())) {
                    newUser.addCurrentOrder(new OrderProxy(rs.getString(1)));
                  } else {
                    //in progress delivery
                    newUser.addCurrentDelivery(new OrderProxy(rs.getString(1)));
                  }
                }
              }
            }
          }
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
    super.getData().pay(amount);
  }

  @Override
  public void charge(double amount) {
    check();
    super.getData().charge(amount);
  }

  @Override
  public void addToDatabase() {
    check();
    super.getData().addToDatabase();
  }

  private static final String valQuery = "SELECT * FROM users WHERE email = ? AND"
      + " password = ?";

  /**
   * Returns true or false if an email password combination exists.
   * @param email the potential account email.
   * @param password the potential account's password.
   * @return true or false whether it exists or not.
   */
  public static boolean userValidator(String email, String password) {
    int hash = Objects.hash(password);
    try (PreparedStatement prep = Database.getConnection().prepareStatement(valQuery)) {
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
}
