package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;

/**
 * Top-level interface that allows for other clients to interact with User
 * objects without knowing their underlying state.
 * @author jacksonchaiken
 *
 */
public interface User extends DeliveryObject {

  /**
   * Returns the User's id.
   * @return the id.
   */
  String getId();

  /**
   * Returns the User's name.
   * @return the name.
   */
  String getName();

  /**
   * Return's the User's cell phone number.
   * @return the number.
   */
  String getCell();

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
   * Adds a past delivery to the user.
   * @param order the order to add.
   */
  void addPastDelivery(Order order);

  /**
   * Adds a past order to the user.
   * @param order the order to add.
   */
  void addPastOrder(Order order);

  /**
   * Adds a current order to the user.
   * @param order the order to add.
   */
  void addCurrentOrder(Order order);

  /**
   * Adds a past delivery to the user.
   * @param order the order to add.
   */
  void addCurrentDelivery(Order order);

  /**
   * Adds a user to the database.
   */
  void addToDatabase();

  /**
   * Returns a user based on their id.
   * @param id the user id.
   * @return the user.
   */
  static User byId(String id) {
    return new UserProxy(id);
  }

  /**
   * Returns true or false if the email/password combination matches
   * an existing user's information.
   * @param email the user's email
   * @param password the user's password.
   * @return a boolean depending on whether the user exists.
   */
  static boolean userValidator(String email, String password) {
    return UserProxy.userValidator(email, password);
  }
}
