package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * Represents an order once it has been read in from the database. Order's can
 * be built manually using the OrderBuilder.
 * @author jacksonchaiken
 *
 */
final class OrderBean extends DeliveryObjectBean<Order> implements
    Order {
  private static final int SEVEN = 7;
  private User orderer;
  private User deliverer;
  private Location pickupL;
  private Location dropoffL;
  private List<String> items;
  private double price;
  private double fee;
  private Status status;
  private double pickupTime;
  private double dropoffTime;

  private OrderBean(String newId, User newOrderer, User newDeliverer,
      Location newPickup, Location newDropoff, double pickupT,
      double dropoffT) {
    super(newId);
    orderer = newOrderer;
    deliverer = newDeliverer;
    pickupL = newPickup;
    dropoffL = newDropoff;
    price = -1;
    fee = -1;
    pickupTime = pickupT;
    dropoffTime = dropoffT;
  }

  private void addStatus(Status newStatus) {
    status = newStatus;
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
  public void assignDeliverer(User newDeliverer) {
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
    //TODO set fee here
  }

  @Override
  public void chargeCustomer() {
    orderer.charge(price);
    deliverer.pay(fee);
  }

  @Override
  public void setOrderStatus(Status newStatus) {
    status = newStatus;
  }

  @Override
  public Status status() {
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

  private static final String addOrder = "INSERT INTO orders VALUES (?,?,?,?,?"
      + ",?,?,?,?)";
  private static final String addStatus = "INSERT INTO order_status VALUES (?,?)";

  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(addOrder)) {
      prep.setString(1, super.getId());
      prep.setString(2, orderer.getId());
      prep.setString(3, deliverer.getId());
      prep.setDouble(4, pickupTime);
      prep.setDouble(5, dropoffTime);
      prep.setString(6, pickupL.getId());
      prep.setString(SEVEN, dropoffL.getId());
      prep.setDouble(8, price);
      String itemString = "";
      for (int x = 0; x < items.size(); x++) {
        if (x + 1 == items.size()) {
          itemString += items.get(x);
        } else {
          itemString += items.get(x) + ",";
        }
      }
      prep.setString(9, itemString);
      prep.addBatch();
      prep.executeBatch();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection().prepareStatement(addStatus)) {
      prep.setString(1, super.getId());
      prep.setInt(2, status.ordinal());
      prep.addBatch();
      prep.executeBatch();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  private static final String orderRemove = "DELETE FROM orders WHERE id = ?";
  private static final String statusRemove = "DELETE FROM order_status WHERE order_id = ?";

  @Override
  public void removeFromDatabase() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(orderRemove)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection().prepareStatement(statusRemove)) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
  }

  /**
   * OrderBuilder offers a way to construct an order. If any of the order
   * elements are not set, the Builder returns null.
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
    private double pickupT;
    private double dropoffT;
    private Status status;
    private double price;

    OrderBuilder setId(String id) {
      idB = id;
      return this;
    }

    OrderBuilder setStatus(Status newStatus) {
      status = newStatus;
      return this;
    }

    
    OrderBuilder setOrderer(User orderer) {
      ordererB = orderer;
      return this;
    }

    OrderBuilder setDeliverer(User deliverer) {
      delivererB = deliverer;
      return this;
    }

    OrderBuilder setPickup(Location pickup) {
      pickupB = pickup;
      return this;
    }

    OrderBuilder setDropoff(Location dropoff) {
      dropoffB = dropoff;
      return this;
    }

    OrderBuilder setItems(List<String> items) {
      itemsB = items;
      return this;
    }

    OrderBuilder setPickupTime(double pickup) {
      pickupT = pickup;
      return this;
    }

    OrderBuilder setDropoffTime(double dropoff) {
      dropoffT = dropoff;
      return this;
    }

    OrderBuilder setPrice(double newPrice) {
      price = newPrice;
      return this;
    }

    Order build() {
      if (idB == null || ordererB == null || delivererB == null || pickupB
          == null || dropoffB == null || status == null) {
        return null;
      }
      OrderBean bean = new OrderBean(idB, ordererB, delivererB, pickupB,
          dropoffB, pickupT, dropoffT);
      bean.addItems(itemsB);
      bean.addStatus(status);
      bean.setPrice(price);
      return bean;
    }
  }
}
