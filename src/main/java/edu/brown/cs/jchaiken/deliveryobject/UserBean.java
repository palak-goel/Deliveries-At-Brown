package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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

  private UserBean(String newId, String newName, String newEmail,
      String newPaymentId) {
    super(newId);
    name = newName;
    email = newEmail;
    paymentId = newPaymentId;
    pastDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    pastOrders = Collections.synchronizedCollection(new HashSet<Order>());
    currDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
    currOrders = Collections.synchronizedCollection(new HashSet<Order>());
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
  public String getStripeId() {
    return paymentId;
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

    UserBuilder setPayment(String newPaymentId) {
      this.paymentId = newPaymentId;
      return this;
    }

    User build() {
      //insert into DB
      return new UserBean(id, name, email, paymentId);
    }
  }
}
