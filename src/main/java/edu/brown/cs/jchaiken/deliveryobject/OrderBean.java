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
public final class OrderBean extends DeliveryObjectBean<Order> implements
    Order {
  private static final int SEVEN = 7;
  private User orderer;
  private User deliverer;
  private String pickupL;
  private String dropoffL;
  private List<String> items;
  private double price;
  private double fee;
  private Status status;
  private double pickupTime;
  private double dropoffTime;

  private OrderBean(String newId, User newOrderer, User newDeliverer,
      String newPickup, String newDropoff, double pickupT, double dropoffT) {
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
  public String getPickupLocation() {
    return pickupL;
  }

  @Override
  public String getDropoffLocation() {
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

  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "INSERT INTO orders VALUES (?,?,?,?,?,?,?)")) {
      prep.setString(1, super.getId());
      prep.setString(2, orderer.getId());
      prep.setString(3, deliverer.getId());
      prep.setDouble(4,  pickupTime);
      prep.setDouble(5,  dropoffTime);
      prep.setString(6, pickupL);
      prep.setString(SEVEN,  dropoffL);
      prep.addBatch();
      prep.executeBatch();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "INSERT INTO items VALUES (?, ?)")) {
      for (String item : items) {
        prep.setString(1, super.getId());
        prep.setString(2, item);
        prep.addBatch();
      }
      prep.executeBatch();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * OrderBuilder offers a way to construct an order if it is not yet in the
   * database. Also inserts it in the database.
   * @author jacksonchaiken
   *
   */
  public static class OrderBuilder {
    private User ordererB;
    private User delivererB;
    private String pickupB;
    private String dropoffB;
    private List<String> itemsB;
    private String idB;
    private double pickupT;
    private double dropoffT;
    private Status status;

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

    OrderBuilder setPickup(String pickup) {
      pickupB = pickup;
      return this;
    }

    OrderBuilder setDropoff(String dropoff) {
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

    Order build() {
      OrderBean bean = new OrderBean(idB, ordererB, delivererB, pickupB,
          dropoffB, pickupT, dropoffT);
      bean.addItems(itemsB);
      bean.addStatus(status);
      return bean;
    }
  }
}
