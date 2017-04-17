package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.UserBean.UserBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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
    cache();
    return super.getData().getName();
  }

  @Override
  public String getEmail() {
    cache();
    return super.getData().getEmail();

  }

  @Override
  public Collection<Order> pastDeliveries() {
    cache();
    return super.getData().pastDeliveries();
  }

  @Override
  public Collection<Order> currentDeliveries() {
    cache();
    return super.getData().currentDeliveries();
  }

  @Override
  public Collection<Order> pastOrders() {
    cache();
    return super.getData().pastOrders();
  }

  @Override
  public Collection<Order> currentOrders() {
    cache();
    return super.getData().currentOrders();
  }

  @Override
  public String getStripeId() {
    cache();
    return super.getData().getStripeId();
  }

  @Override
  public void setStripeId(String id) {
    cache();
    super.getData().setStripeId(id);
  }

  @Override
  protected void cache(Connection connection) throws SQLException {
    //TODO: read in user from DB
  }

  @Override
  public void pay(double amount) {
    // TODO Auto-generated method stub
  }

  @Override
  public void charge(double amount) {
    // TODO Auto-generated method stub
  }

  /**
   * Returns a User based on the email entered.
   * @param email the user's email.
   * @return the User, or null if it does not exist.
   */
  public static User byEmail(String email) {
    String emailQuery = String.format("SELECT * FROM users WHERE email = %s",
        email);
    List<List<Object>> users = Database.query(emailQuery);
    assert users.size() == 1;
    List<Object> result = users.get(0);
    UserBuilder builder = new UserBuilder();
    //TODO: result stuff
    return builder.build();
  }

}
