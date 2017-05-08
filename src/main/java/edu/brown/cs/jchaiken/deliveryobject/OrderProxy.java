package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

/**
 * Models an order if it has not been read in from the database yet.
 *
 * @author jacksonchaiken
 *
 */
class OrderProxy extends DeliveryObjectProxy<Order> implements Order {
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;

  OrderProxy(String newId) {
    super(newId);
  }

  private static final String CACHE_QUERY = "SELECT * FROM orders WHERE id = ?";
  private static final String STATUS_QUERY
      = "SELECT status FROM order_status WHERE order_id = ?";
  private static final String ITEM_QUERY
      = "SELECT item FROM items WHERE order_id = ?";

  @Override
  protected void cache() throws SQLException {
    try (PreparedStatement cachePrep = Database.getConnection()
        .prepareStatement(CACHE_QUERY)) {
      cachePrep.setString(1, super.getId());
      try (ResultSet cacheSet = cachePrep.executeQuery()) {
        if (cacheSet.next()) {
          User orderer = null;
          User deliverer = null;
          if (cacheSet.getString(2) != null) {
            orderer = User.byId(cacheSet.getString(2));
          }
          if (cacheSet.getString(3) != null) {
            deliverer = User.byId(cacheSet.getString(3));
          }
          final double pickupTime = cacheSet.getDouble(4);
          final double dropoffTime = cacheSet.getDouble(5);
          final Location pickupLoc = Location.byId(cacheSet.getString(6));
          final Location dropoffLoc = Location.byId(cacheSet.getString(SEVEN));
          final double price = cacheSet.getDouble(EIGHT);
          final String phone = cacheSet.getString(9);
          final OrderBuilder builder = new OrderBuilder();
          builder.setId(super.getId()).setOrderer(orderer)
              .setDeliverer(deliverer).setPickupTime(pickupTime)
              .setDropoffTime(dropoffTime).setDropoff(dropoffLoc)
              .setPickup(pickupLoc).setPrice(price).setPhone(phone);
          try (PreparedStatement statusPrep = Database.getConnection()
              .prepareStatement(STATUS_QUERY)) {
            statusPrep.setString(1, super.getId());
            try (ResultSet statusSet = statusPrep.executeQuery()) {
              int maxStatus = -1;
              while (statusSet.next()) {
                maxStatus = maxStatus < statusSet.getInt(1)
                    ? statusSet.getInt(1) : maxStatus;
              }
              final OrderStatus status = OrderStatus.valueOf(maxStatus);
              builder.setOrderStatus(status);
            }
          }
          final List<String> dbItems = new ArrayList<>();
          try (PreparedStatement itemPrep = Database.getConnection()
              .prepareStatement(ITEM_QUERY)) {
            itemPrep.setString(1, super.getId());
            try (ResultSet itemSet = itemPrep.executeQuery()) {
              while (itemSet.next()) {
                dbItems.add(itemSet.getString(1));
              }
            }
          }
          builder.setItems(dbItems);
          builder.setDbStatus(true);
          final Order newOrder = builder.build();
          assert newOrder != null;
          super.setData(newOrder);
        }
      }
    }
  }

  @Override
  public User getOrderer() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getOrderer();
  }

  @Override
  public User getDeliverer() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getDeliverer();
  }

  @Override
  public void assignDeliverer(User deliverer) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().assignDeliverer(deliverer);
  }

  @Override
  public List<String> getOrderItems() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getOrderItems();
  }

  @Override
  public Location getPickupLocation() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getPickupLocation();
  }

  @Override
  public Location getDropoffLocation() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getDropoffLocation();
  }

  @Override
  public double getPrice() {
    check();
    if (super.getData() == null) {
      return Double.MAX_VALUE;
    }
    return super.getData().getPrice();
  }

  @Override
  public void setPrice(double price) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setPrice(price);
  }

  @Override
  public void chargeCustomer() {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().chargeCustomer();
  }

  @Override
  public void setOrderStatus(OrderStatus status) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setOrderStatus(status);
  }

  @Override
  public OrderStatus status() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().status();
  }

  @Override
  public double getPickupTime() {
    check();
    if (super.getData() == null) {
      return Double.MAX_VALUE;
    }
    return super.getData().getPickupTime();
  }

  @Override
  public double getDropoffTime() {
    check();
    if (super.getData() == null) {
      return Double.MAX_VALUE;
    }
    return super.getData().getDropoffTime();
  }

  @Override
  public void addToDatabase() {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().addToDatabase();
  }

  @Override
  public void removeFromDatabase() {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().removeFromDatabase();
  }

  private static final String ITEM_SEARCH
      = "SELECT * FROM items WHERE item = ?";

  /**
   * Returns a list of orders that contain a certain item.
   *
   * @param item
   *          the item to search for.
   * @return the list of orders with that item.
   */
  public static Collection<Order> byItem(String item) {
    final Collection<Order> orders = new HashSet<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ITEM_SEARCH)) {
      prep.setString(1, item);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          orders.add(new OrderProxy(rs.getString(1)));
        }
      }
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    return orders;
  }

  private static final String PICKUP_SEARCH
      = "SELECT * FROM orders WHERE pickup_location = ?";

  /**
   * Returns a list of orders from a given pickup location.
   *
   * @param pickup
   *          the location id where orders are picked up.
   * @return the list of orders.
   */
  public static Collection<Order> byPickupLocation(String pickup) {
    final Collection<Order> orders = new ArrayList<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(PICKUP_SEARCH)) {
      prep.setString(1, pickup);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          orders.add(new OrderProxy(rs.getString(1)));
        }
      }
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    return orders;
  }

  private static final String ALL_STATUS
      = "SELECT * FROM order_status WHERE status = ?";

  /**
   * Returns a collection of orders with a given status.
   *
   * @param status
   *          the orders' status.
   * @return the orders with that status.
   */
  public static Collection<Order> byStatus(OrderStatus status) {
    final Collection<Order> results = new HashSet<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ALL_STATUS)) {
      prep.setInt(1, status.ordinal());
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          results.add(new OrderProxy(rs.getString(1)));
        }
      }
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return results;
  }

  @Override
  public double getRanking() {
    check();
    if (super.getData() == null) {
      return -1;
    }
    return super.getData().getRanking();
  }

  @Override
  public void setRanking(double rank) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setRanking(rank);
  }

  @Override
  public String getPhone() {
    check();
    if (super.getData() == null) {
      return null;
    }
    return super.getData().getPhone();
  }

  private static final String COUNTER_Q = "SELECT COUNT(id) FROM orders";

  protected static int checkCounter() {
    if (Database.getConnection() != null) {
      try (PreparedStatement prep = Database.getConnection()
          .prepareStatement(COUNTER_Q)) {
        try (ResultSet rs = prep.executeQuery()) {
          return rs.getInt(1);
        }
      } catch (final SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return 0;
  }
}
