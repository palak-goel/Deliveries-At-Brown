package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;

/**
 * Top-level interface that allows for other clients to interact with User
 * objects without knowing their underlying state.
 * @author jacksonchaiken
 *
 */
public interface User extends DeliveryObject {

  String getId();

  /**
   * Returns the User's name.
   * @return the name.
   */
  String getName();

  /**
   * Return's the User's email.
   * @return the email.
   */
  String getEmail();

  /**
   * Returns the User's past deliveries.
   * @return the past deliveries.
   */
  Collection<Order> pastDeliveries();

  /**
   * Returns the User's current deliveries.
   * @return the current deliveries.
   */
  Collection<Order> currentDeliveries();

  /**
   * Returns the User's past orders.
   * @return the past orders.
   */
  Collection<Order> pastOrders();

  /**
   * Returns the User's current orders.
   * @return the current orders.
   */
  Collection<Order> currentOrders();

  /**
   * Returns the Stripe Id.
   * @return the stripe id.
   */
  String getStripeId();

  /**
   * Set's the stripe id.
   * @param id the new id.
   */
  void setStripeId(String id);

  /**
   * Pay's the amount to the user.
   * @param amount the payment.
   */
  void pay(double amount);

  /**
   * Charges the amount to the user.
   * @param amount the charge.
   */
  void charge(double amount);

  /**
   * Returns a user based on their id.
   * @param id the user id.
   * @return the user.
   */
  static User byId(String id) {
    return new UserProxy(id);
  }

  /**
   * Returns a user by their email, if it exists.
   * @param email the potential user's email.
   * @return the user, or null.
   */
  static User byEmail(String email) {
    return UserProxy.byEmail(email);
  }
}
