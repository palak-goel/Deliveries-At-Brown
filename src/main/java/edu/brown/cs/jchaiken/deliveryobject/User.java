package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;
import java.util.List;

/**
 * Top-level interface that allows for other clients to interact with User
 * objects without knowing their underlying state.
 * @author jacksonchaiken
 *
 */
public interface User extends DeliveryObject {

  /**
   * Represents whether an account is still valid or if it has been
   * deleted.
   * @author jacksonchaiken
   *
   */
  enum AccountStatus {
    ACTIVE,
    CLOSED;
    public static AccountStatus valueOf(int status) {
      switch (status) {
        case 0:
          return AccountStatus.ACTIVE;
        case 1:
          return AccountStatus.CLOSED;
        default:
          return null;
      }
    }
  }


  /**
   * UserBuilder provides a way to build a User if it must be created for
   * the first time and it should be added to the database.
   * @author jacksonchaiken
   *
   */
  class UserBuilder {
    private String id = null;
    private String name = null;
    private String paymentId = null;
    private String cell = null;
    private Integer pass = null;
    private AccountStatus status = null;
    private List<Double> oRatings;
    private List<Double> dRatings;
    private String webId = null;

    public UserBuilder setStatus(AccountStatus stat) {
      status = stat;
      return this;
    }

    public UserBuilder setPersonalUrl(String url) {
      webId = url;
      return this;
    }

    public UserBuilder setId(String newId) {
      this.id = newId;
      return this;
    }

    public UserBuilder setName(String newName) {
      this.name = newName;
      return this;
    }

    public UserBuilder setPassword(Integer password) {
      pass = password;
      return this;
    }

    public UserBuilder setPayment(String newPaymentId) {
      this.paymentId = newPaymentId;
      return this;
    }

    public UserBuilder setCell(String cellNumber) {
      this.cell = cellNumber;
      return this;
    }

    public UserBuilder setDelivererRatings(List<Double> ratings) {
      dRatings = ratings;
      return this;
    }

    public UserBuilder setOrdererRatings(List<Double> ratings) {
      oRatings = ratings;
      return this;
    }

    public User build() {
      if (id == null || name == null || paymentId == null || cell == null
          || pass == null || status == null || dRatings == null
          || oRatings == null) {
        throw new IllegalArgumentException("There are parameters that have"
            + " not been set");
      }
      UserBean user = new UserBean(id, name, paymentId, cell, pass, status);
      user.setDelivererRatings(dRatings);
      user.setOrdererRatings(oRatings);
      if (webId != null) {
        user.setWebId(webId);
      }
      if (DeliveryObjectProxy.getCache().getIfPresent(id) == null) {
        DeliveryObjectProxy.getCache().put(id, user);
      }
      return user;
    }
  }

  /**
   * Returns the account's status.
   * @return the account's status
   */
  AccountStatus status();

  /**
   * Sets the accounts status and updates in the database.
   * @param status new status
   */
  void setAccountStatus(AccountStatus status);

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
   * Adds a past delivery to the user. Does not alter database.
   * @param order the order to add.
   */
  void addPastDelivery(Order order);

  /**
   * Adds a past order to the user. Does not alter database.
   * @param order the order to add.
   */
  void addPastOrder(Order order);

  /**
   * Adds a current order to the user. Does not alter database.
   * @param order the order to add.
   */
  void addCurrentOrder(Order order);

  /**
   * Adds a past delivery to the user.  Does not alter database.
   * @param order the order to add.
   */
  void addCurrentDelivery(Order order);

  /**
   * Adds a user to the database.
   */
  void addToDatabase();

  /**
   * Removes a user from the database.
   */
  void removeFromDatabase();

  /**
   * Sets the user's delivery preferences for effective order ranking.
   * @param distance the maximum distance they are willing to travel.
   * @param minimumFee the minimum fee they wish to receive from the delivery
   * @param maximumTime the most time they want to spend on the delivery
   */
  void addDeliveryPreferences(double distance, double minimumFee, double
      maximumTime);

  /**
   * Returns the User's distance preference, or -1 if it has not been set.
   * @return the distance preference.
   */
  double getDeliveryDistancePreference();

  /**
   * Returns the User's free preference, or -1 if it has not been set.
   * @return the preference.
   */
  double getDeliveryFeePreference();

  /**
   * Validates a password, returning true if the input is correct.
   * @param password the password to check.
   * @return true or false, depending on the passwords correctness.
   */
  boolean validatePassword(String password);

  /**
   * Returns the maximum amount of time the user wants to spend, or -1 if it
   * has not been set.
   * @return the preference.
   */
  double getDeliveryTimePreference();

  /**
   * Returns the deliverer's weighted average of ratings (out of 5).
   * @return the rating
   */
  double getDelivererRating();

  /**
   * Returns the orderer's weighted average of ratings (out of 5).
   * @return the rating
   */
  double getOrdererRating();

  /**
   * Adds a rating to the orderer average. Also adds to database.
   * @param rating the rating out of 5.
   */
  void addOrdererRating(double rating);

  /**
   * Adds a rating to the deliverer average. Also adds to database.
   * @param rating the rating out of 5.
   */
  void addDelivererRating(double rating);

  /**
   * Forces a user to be re-read in from the database when being cached by
   * removing it from the cache. Use the returned User to access changes.
   * Reflects changes to orders (new order, deleted order).
   * @return The new user object.
   */
  User setPendingUpdate();

  /**
   * Returns the User's web id.
   * @return the web id.
   */
  String getWebId();

  /**
   * Returns a user based on their id.
   * @param id the user id.
   * @return the user.
   */
  static User byId(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    return new UserProxy(id);
  }

  /**
   * Returns true if a given ID is already attached to a user.
   * @param id the id in question.
   * @return true or false, if it exists.
   */
  static boolean accountExists(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    return UserProxy.accountExists(id);
  }

  /**
   * Resets a User's password, if the oldPassword matches.
   * @param id the user's id.
   * @param oldPassword the existing password.
   * @param newPassword the new password.
   * @return true, if the old passwords matched and reset occurred.
   *  False otherwise.
   */
  static boolean resetPassword(String id, String oldPassword,
      String newPassword) {
    if (id == null || newPassword == null) {
      throw new IllegalArgumentException("Parameters are null");
    }
    return UserProxy.resetPassword(id, oldPassword, newPassword);
  }

  /**
   * Returns true or false if the email/password combination matches
   * an existing user's information.
   * @param email the user's email
   * @param password the user's password.
   * @return a boolean depending on whether the user exists.
   */
  static boolean userValidator(String email, String password) {
    if (email == null || password == null) {
      throw new IllegalArgumentException("Email or password null");
    }
    return UserProxy.userValidator(email, password);
  }

  /**
   * Returns a user based on their unique URL IDs.
   * @param id the User's web id.
   * @return the User.
   */
  static User byWebId(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null");
    }
    return UserProxy.byWebId(id);
  }

  static boolean newPassword(String id, String newPassword) {
    if (id == null || newPassword == null) {
      throw new IllegalArgumentException("parameters null");
    }
    return UserProxy.newPassword(id, newPassword);
  }
}
