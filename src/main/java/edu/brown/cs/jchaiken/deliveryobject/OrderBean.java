package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import edu.brown.cs.jchaiken.database.Database;

/**
 * Represents an order once it has been read in from the database. Order's can
 * be built manually using the OrderBuilder.
 *
 * @author jacksonchaiken
 *
 */
public final class OrderBean extends DeliveryObjectBean<Order>
    implements Order {
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;
  private final User orderer;
  private User deliverer;
  private final Location pickupL;
  private final Location dropoffL;
  private List<String> items;
  private Double price;
  private OrderStatus status;
  private final Double pickupTime;
  private final Double dropoffTime;
  private double ranking;
  private boolean inDb = false;
  private String phone;

  private OrderBean(String newId, User newOrderer, User newDeliverer,
      Location newPickup, Location newDropoff, double pickupT,
      double dropoffT) {
    super(newId);
    orderer = newOrderer;
    deliverer = newDeliverer;
    pickupL = newPickup;
    dropoffL = newDropoff;
    price = -1.0;
    pickupTime = pickupT;
    dropoffTime = dropoffT;
    ranking = -1;
  }

  private void addPrice(double newPrice) {
    this.price = newPrice;
  }

  private void setDbStatus(Boolean db) {
    inDb = db;
  }

  private void addOrderStatus(OrderStatus newOrderStatus) {
    status = newOrderStatus;
  }

  private void addPhone(String newPhone) {
    phone = newPhone;
  }

  private void addItems(List<String> newItems) {
    items = newItems;
  }

  @Override
  public User getOrderer() {
    return orderer;
  }

  @Override
  public User getDeliverer() {
    return deliverer;
  }

  @Override
  public String getPhone() {
    return phone;
  }

  @Override
  public void assignDeliverer(User newDeliverer) {
    if (inDb && Database.getConnection() != null) {
      try (PreparedStatement prep = Database.getConnection().prepareStatement(
          "UPDATE orders SET deliverer_id = ? WHERE id = ?")) {
        prep.setString(1, newDeliverer.getId());
        prep.setString(2, super.getId());
        prep.executeUpdate();
      } catch (final SQLException e) {
        e.printStackTrace();
      }
    }
    deliverer = newDeliverer;
  }

  @Override
  public List<String> getOrderItems() {
    return items;
  }

  @Override
  public Location getPickupLocation() {
    return pickupL;
  }

  @Override
  public Location getDropoffLocation() {
    return dropoffL;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public void setPrice(double newPrice) {
    price = newPrice;
    if (inDb && Database.getConnection() != null) {
      try (PreparedStatement prep = Database.getConnection()
          .prepareStatement("UPDATE orders SET price = ? WHERE id = ?")) {
        prep.setDouble(1, newPrice);
        prep.setString(2, super.getId());
        prep.executeUpdate();
      } catch (final SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void chargeCustomer() {
    orderer.charge(price, items, pickupL.getName(), dropoffL.getName());
  }

  @Override
  public void setOrderStatus(OrderStatus newStatus) {
    if (inDb && Database.getConnection() != null) {
      try (PreparedStatement prep = Database.getConnection()
          .prepareStatement("INSERT INTO order_status VALUES(?, ?, ?)")) {
        prep.setString(1, super.getId());
        prep.setInt(2, newStatus.ordinal());
        prep.setLong(3, System.currentTimeMillis());
        prep.executeUpdate();
      } catch (final SQLException e) {
        e.printStackTrace();
      }
    }
    status = newStatus;
  }

  @Override
  public OrderStatus status() {
    return status;
  }

  @Override
  public double getPickupTime() {
    return pickupTime;
  }

  @Override
  public double getDropoffTime() {
    return dropoffTime;
  }

  private static final String ORDER_ADD
      = "INSERT INTO orders VALUES (?,?,?,?,?,?,?,?,?)";
  private static final String STATUS_ADD
      = "INSERT INTO order_status VALUES (?,?,?)";
  private static final String ITEM_ADD = "INSERT INTO items VALUES (?,?)";

  @Override
  public void addToDatabase() {
    System.out.println("Adding order to db");
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ORDER_ADD)) {
      prep.setString(1, super.getId());
      prep.setString(2, orderer.getId());
      if (deliverer != null) {
        prep.setString(3, deliverer.getId());
      }
      if (pickupTime == null) {
        prep.setDouble(4, -1);
      } else {
        prep.setDouble(4, pickupTime);
      }
      if (dropoffTime == null) {
        prep.setDouble(5, -1);
      } else {
        prep.setDouble(5, dropoffTime);
      }
      prep.setString(6, pickupL.getId());
      prep.setString(SEVEN, dropoffL.getId());
      if (price == null) {
        prep.setDouble(EIGHT, -1);
      } else {
        prep.setDouble(EIGHT, price);
      }
      if (phone == null) {
        prep.setString(NINE, "");
      } else {
        prep.setString(NINE, phone);
      }
      prep.executeUpdate();
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ITEM_ADD)) {
      for (final String item : items) {
        prep.setString(1, super.getId());
        prep.setString(2, item);
        prep.addBatch();
      }
      prep.executeBatch();
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(STATUS_ADD)) {
      prep.setString(1, super.getId());
      prep.setInt(2, status.ordinal());
      prep.setLong(3, System.currentTimeMillis());
      prep.executeUpdate();
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    inDb = true;
    orderer.setPendingUpdate();
    if (deliverer != null) {
      deliverer.setPendingUpdate();
    }
  }

  private static final String ORDER_REM = "DELETE FROM orders WHERE id = ?";
  private static final String STATUS_REM
      = "DELETE FROM order_status WHERE order_id = ?";
  private static final String ITEM_REM = "DELETE FROM items WHERE order_id = ?";

  @Override
  public void removeFromDatabase() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(STATUS_REM)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ITEM_REM)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ORDER_REM)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    orderer.setPendingUpdate();
    deliverer.setPendingUpdate();
    inDb = false;
  }

  /**
   * OrderBuilder offers a way to construct an order. If any of the order
   * elements are not set, the Builder returns null.
   *
   * @author jacksonchaiken
   *
   */
  public static class OrderBuilder {
    private User ordererB;
    private User delivererB;
    private Location pickupB;
    private Location dropoffB;
    private List<String> itemsB;
    private String idB;
    private String phone;
    private double pickupT;
    private double dropoffT;
    private OrderStatus status;
    private double price;
    private boolean db = false;

    /**
     * Sets the id of the order.
     * @param id the orders id.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setId(String id) {
      idB = id;
      return this;
    }

    /**
     * Sets the status of the order.
     * @param newOrderStatus the new status.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setOrderStatus(OrderStatus newOrderStatus) {
      status = newOrderStatus;
      return this;
    }

    /**
     * Sets the orderer.
     * @param orderer the user ordering.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setOrderer(User orderer) {
      ordererB = orderer;
      return this;
    }

    /**
     * Sets the deliverer of the order.
     * @param deliverer the deliverer.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setDeliverer(User deliverer) {
      delivererB = deliverer;
      return this;
    }

    /**
     * Sets the pickup location of the order.
     * @param pickup the pickup location.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setPickup(Location pickup) {
      pickupB = pickup;
      return this;
    }

    /**
     * Sets the dropoff location of the order.
     * @param dropoff the dropoff location.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setDropoff(Location dropoff) {
      dropoffB = dropoff;
      return this;
    }

    /**
     * Sets the items of the order.
     * @param items the list of items.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setItems(List<String> items) {
      itemsB = items;
      return this;
    }

    /**
     * Sets the pickup time of the order.
     * @param pickup the pickup time.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setPickupTime(double pickup) {
      pickupT = pickup;
      return this;
    }

    /**
     * Sets the dropoff time of the order.
     * @param dropoff the dropoff request time.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setDropoffTime(double dropoff) {
      dropoffT = dropoff;
      return this;
    }

    /**
     * Sets the price of the order.
     * @param newPrice the orders price.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setPrice(double newPrice) {
      price = newPrice;
      return this;
    }

    /**
     * Sets the whether the order has been added to the database or not.
     * Affects behavior of object once it becomes an Order.
     * @param newStatus the db status.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setDbStatus(boolean newStatus) {
      db = newStatus;
      return this;
    }

    /**
     * Sets the phone number of the restaurant.
     * @param newPhone the new phone number.
     * @return the new OrderBuilder object
     */
    public OrderBuilder setPhone(String newPhone) {
      phone = newPhone;
      return this;
    }

    /**
     * Constructs an Order on the given parameters.
     * If ID is null a unique id will be generated.
     * @return the order.
     */
    public Order build() {
      if (idB == null) {
        idB = getNextId();
      }
      final OrderBean bean = new OrderBean(idB, ordererB, delivererB, pickupB,
          dropoffB, pickupT, dropoffT);
      bean.addItems(itemsB);
      bean.addOrderStatus(status);
      bean.addPrice(price);
      bean.setDbStatus(db);
      bean.addPhone(phone);
      return bean;
    }
  }

  @Override
  public double getRanking() {
    return ranking;
  }

  @Override
  public void setRanking(double rank) {
    ranking = rank;
  }

  private static int counter = 0;

  /**
   * Returns the next id for an order.
   *
   * @return the id.
   */
  protected static String getNextId() {
    if (counter == 0) {
      counter = OrderProxy.checkCounter();
    }
    counter++;
    return "/o/" + counter;
  }

}
