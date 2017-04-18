package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Models an order if it has not been read in from the database yet.
 * @author jacksonchaiken
 *
 */
class OrderProxy extends DeliveryObjectProxy<Order> implements Order {
  private static final int SEVEN = 7;

  OrderProxy(String newId) {
    super(newId);
  }

  private static final String cacheQuery = "SELECT * FROM orders WHERE id = ?";
  private static final String statusQ = "SELECT * FROM order_status WHERE order_id = ?";
  @Override
  protected void cache() throws SQLException {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(cacheQuery)) {
      prep.setString(1, super.getId());
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          User orderer = User.byId(rs.getString(2));
          User deliverer = User.byId(rs.getString(3));
          double pickupTime = rs.getDouble(4);
          double dropoffTime = rs.getDouble(5);
          Location pickupLoc = Location.byId(rs.getString(6));
          Location dropoffLoc = Location.byId(rs.getString(SEVEN));
          double price = rs.getDouble(8);
          List<String> items = Arrays.asList(rs.getString(9).split(","));
          OrderBuilder builder = new OrderBuilder();
          builder.setId(super.getId())
              .setOrderer(orderer)
              .setDeliverer(deliverer)
              .setPickupTime(pickupTime)
              .setDropoffTime(dropoffTime)
              .setDropoff(dropoffLoc)
              .setPickup(pickupLoc)
              .setItems(items)
              .setPrice(price);
          try (PreparedStatement prep2 = Database.getConnection().prepareStatement(statusQ)) {
            prep.setString(1, super.getId());
            try (ResultSet rs2 = prep.executeQuery()) {
              if (rs.next()) {
                Status status = Status.valueOf(rs.getInt(2));
                builder.setStatus(status);
              }
            }
          }
          Order newOrder = builder.build();
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
  public void setOrderStatus(Status status) {
    check();
    if (super.getData() == null) {
      return;
    }
    super.getData().setOrderStatus(status);
  }

  @Override
  public Status status() {
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
    super.getData().addToDatabase();
  }

  @Override
  public void removeFromDatabase() {
    check();
    super.getData().removeFromDatabase();
  }

  private static final String itemQuery = "SELECT id FROM orders WHERE"
      + " item LIKE '%?%'";


  /**
   * Returns a list of orders that contain a certain item.
   * @param item the item to search for.
   * @return the list of orders with that item.
   */
  public static List<Order> byItem(String item) {
    List<Order> orders = new ArrayList<>();
    try (PreparedStatement prep = Database.getConnection().prepareStatement(itemQuery)) {
      prep.setString(1, item);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          orders.add(new OrderProxy(rs.getString(1)));
        }
      }
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    return orders;
  }

  private static final String pickupQuery = "SELECT id FROM orders WHERE pickup_location = ?";

  /**
   * Returns a list of orders from a given pickup location.
   * @param pickup the location id where orders are picked up.
   * @return the list of orders.
   */
  public static List<Order> byPickupLocation(String pickup) {
    List<Order> orders = new ArrayList<>();
    try (PreparedStatement prep = Database.getConnection().prepareStatement(pickupQuery)) {
      prep.setString(1, pickup);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          orders.add(new OrderProxy(rs.getString(1)));
        }
      }
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    return orders;
  }
}
