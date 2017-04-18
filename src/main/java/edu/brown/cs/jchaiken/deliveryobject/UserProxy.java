package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.UserBean.UserBuilder;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * UserProxy models a User if it has not been read in from the database.
 * @author jacksonchaiken
 *
 */
public class UserProxy extends DeliveryObjectProxy<User> implements User {

  UserProxy(String id) {
    super(id);
  }

  @Override
  public String getName() {
    check();
    return super.getData().getName();
  }

  @Override
  public String getEmail() {
    check();
    return super.getData().getEmail();

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

  @Override
  protected void cache() throws SQLException {
    String query = "SELECT * FROM users WHERE id = " + super.getId();
    List<List<Object>> results = Database.query(query);
    if (results.size() == 1) {
      List<Object> user = results.get(0);
      String name = (String) user.get(1);
      String email = (String) user.get(2);
      String cell = (String) user.get(3);
      String spark = (String) user.get(4);
      UserBuilder bean = new UserBuilder();
      User newUser = bean.setCell(cell)
          .setEmail(email)
          .setPayment(spark)
          .setId(super.getId())
          .setName(name)
          .build();
      String orderQuery = "SELECT id, status, orderer_id, deliverer_id FROM"
          + " orders WHERE orderer_id = " + super
          .getId() + " OR deliverer_id = " + super.getId();
      List<List<Object>> orders = Database.query(orderQuery);
      for (List<Object> order : orders) {
        if (!order.get(1).equals("COMPLETED")) {
          //is an orderer
          if (order.get(2).equals(super.getId())) {
            newUser.addPastOrder(new OrderProxy((String) order.get(0)));
          } else {
            //is deliverer
            newUser.addPastDelivery(new OrderProxy((String) order.get(0)));
          }
        } else {
          if (order.get(2).equals(super.getId())) {
            newUser.addCurrentOrder(new OrderProxy((String) order.get(0)));
          } else {
            newUser.addCurrentDelivery(new OrderProxy((String) order.get(0)));
          }
        }
      }
      super.setData(newUser);
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

  /**
   * Returns a User based on the email entered.
   * @param email the user's email.
   * @return the User, or null if it does not exist.
   */
  public static User byEmail(String email) {
    String emailQuery = String.format("SELECT id FROM users WHERE email = %s",
        email);
    List<List<Object>> users = Database.query(emailQuery);
    if (users != null && users.size() == 1) {
      List<Object> result = users.get(0);
      return new UserProxy((String) result.get(0));
    }
    return null;
  }

  /**
   * Returns true or false if an email password combination exists.
   * @param email the potential account email.
   * @param password the potential account's password.
   * @return true or false whether it exists or not.
   */
  public static boolean userValidator(String email, String password) {
    int hash = Objects.hash(password);
    String query = String.format("SELECT * FROM users WHERE email = %s AND"
        + " password = %s", email, hash);
    List<List<Object>> users = Database.query(query);
    if (users != null && users.size() == 1) {
      return true;
    }
    return false;
  }
}
