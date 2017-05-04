package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;
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
  enum OrderStatus {
    UNASSIGNED,
    ASSIGNED,
    AT_LOCATION,
    HAVE_FOOD,
    COMPLETED;

    public static OrderStatus valueOf(int status) {
      switch (status) {
        case 0:
          return OrderStatus.UNASSIGNED;
        case 1:
          return OrderStatus.ASSIGNED;
        case 2:
          return OrderStatus.AT_LOCATION;
        case 3:
          return OrderStatus.HAVE_FOOD;
        case 4:
          return OrderStatus.COMPLETED;
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
   * Sets the order's status. Also updates the status in the database.
   * @param status the status.
   */
  void setOrderStatus(OrderStatus status);

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
  OrderStatus status();

  /**
   * Adds an order to the database.
   */
  void addToDatabase();

  /**
   * Removes order from database.
   */
  void removeFromDatabase();

  /**
   * Returns the orders ranking, if it has been set.
   * @return the double assigned
   */
  double getRanking();

  /**
   * Sets the orders ranking.
   * @param rank the ranking weight.
   */
  void setRanking(double rank);

  /**
   * Returns the phone number of the pickup location.
   * @return the phone number
   */
  String getPhone();

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
  static Collection<Order> byPickupLocation(String pickup) {
    return OrderProxy.byPickupLocation(pickup);
  }

  /**
   * Returns orders that contain a given item.
   * @param item the item to find.
   * @return the list of orders
   */
  static Collection<Order> byItem(String item) {
    return OrderProxy.byItem(item);
  }

  /**
   * Returns a collection of orders with the given status.
   * @param status the status in question
   * @return the collection of orders.
   */
  static Collection<Order> byStatus(OrderStatus status) {
    return OrderProxy.byStatus(status);
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
