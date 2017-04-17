package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;


/**
 * UserBean models a user after it has been read in from the database.
 * @author jacksonchaiken
 *
 */
public final class UserBean extends DeliveryObjectBean<User> implements User {
  private String name;
  private Collection<Order> pastDeliveries;
  private Collection<Order> pastOrders;
  private Collection<Order> currDeliveries;
  private Collection<Order> currOrders;
  private String email;
  private String paymentId;
  private String cell;
  private final int password;

  private UserBean(String newId, String newName, String newEmail,
      String newPaymentId, String cellNum, int newPass) {
    super(newId);
    password = newPass;
    name = newName;
    email = newEmail;
    paymentId = newPaymentId;
    pastDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    pastOrders = Collections.synchronizedCollection(new HashSet<Order>());
    currDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    currOrders = Collections.synchronizedCollection(new HashSet<Order>());
    cell = cellNum;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getEmail() {
    return email;
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


  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
          "INSERT INTO users VALUES (?,?,?,?,?,?)")) {
      prep.setString(1, super.getId());
      prep.setString(2, name);
      prep.setString(3, email);
      prep.setString(4, cell);
      prep.setInt(5, password);
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
    private String id;
    private String name;
    private String email;
    private String paymentId;
    private String cell;
    private int pass;

    UserBuilder setId(String newId) {
      this.id = newId;
      return this;
    }

    UserBuilder setName(String newName) {
      this.name = newName;
      return this;
    }

    UserBuilder setEmail(String newEmail) {
      this.email = newEmail;
      return this;
    }

    UserBuilder setPassword(String password) {
      pass = Objects.hash(password);
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

    User build() {
      return new UserBean(id, name, email, paymentId, cell, pass);
    }
  }
}
