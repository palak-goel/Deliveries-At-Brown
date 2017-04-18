package edu.brown.cs.jchaiken.deliveryobject;

import java.util.List;

/**
 * Top-level interface that allows for other classes to interact with
 * Orders without knowing the underlying state.
 * @author jacksonchaiken
 *
 */
public interface Order extends DeliveryObject {

  /**
   * Various statuses that Orders can take on.
   * @author jacksonchaiken
   *
   */
  enum Status {
    UNASSIGNED,
    ASSIGNED,
    AT_LOCATION,
    HAVE_FOOD,
    COMPLETED;

    public static Status valueOf(int status) {
      switch (status) {
        case 0:
          return Status.UNASSIGNED;
        case 1:
          return Status.ASSIGNED;
        case 2:
          return Status.AT_LOCATION;
        case 3:
          return Status.HAVE_FOOD;
        case 4:
          return Status.COMPLETED;
        default:
          return null;
      }
    }
  }

  /**
   * Returns the user that ordered the order.
   * @return the user who ordered.
   */
  User getOrderer();

  /**
   * Returns the deliverer.
   * @return the deliverer who accepted, or null if none assigned.
   */
  User getDeliverer();

  /**
   * Assigns a user to be the deliverer.
   * @param deliverer the user.
   */
  void assignDeliverer(User deliverer);

  /**
   * Returns the items in the order.
   * @return the items.
   */
  List<String> getOrderItems();

  /**
   * Returns the pickup location.
   * @return the location
   */
  Location getPickupLocation();

  /**
   * Returns the dropoff location.
   * @return the location.
   */
  Location getDropoffLocation();

  /**
   * Returns the orders price.
   * @return the price, or -1 if it has not been set.
   */
  double getPrice();

  /**
   * Sets the price.
   * @param price the new price.
   */
  void setPrice(double price);

  /**
   * Charges the orderer and pays deliverer if order complete.
   */
  void chargeCustomer();

  /**
   * Sets the order's status.
   * @param status the status.
   */
  void setOrderStatus(Status status);

  /**
   * Gets the pickup time of the order.
   * @return the time the order was picked up.
   */
  double getPickupTime();

  /**
   * Gets the time the order was dropped off (if it has been).
   * @return the time, or null otherwise.
   */
  double getDropoffTime();

  /**
   * Returns the order status.
   * @return status
   */
  Status status();

  /**
   * Adds an order to the database.
   */
  void addToDatabase();

  /**
   * Removes order from database;
   */
  void removeFromDatabase();

  /**
   * Returns an order based on its id.
   * @param id the order's id.
   * @return the order.
   */
  static Order byId(String id) {
    return new OrderProxy(id);
  }

  /**
   * Returns a list of orders with a given pickup location.
   * @param pickup the pickup location.
   * @return a list of orders.
   */
  static List<Order> byPickupLocation(String pickup) {
    return OrderProxy.byPickupLocation(pickup);
  }

  /**
   * Returns orders that contain a given item.
   * @param item the item to find.
   * @return the list of orders
   */
  static List<Order> byItem(String item) {
    return OrderProxy.byItem(item);
  }

  /**
   * IdGenerator for unique hashing and database ids.
   * @author jacksonchaiken
   *
   */
  class IdGenerator {
    private static int counter = 0;

    private IdGenerator() {
    }

    /**
     * Returns the next id for an order.
     * @return the id.
     */
    static String getNextId() {
      counter++;
      return "/o/" + counter;
    }
  }
}
