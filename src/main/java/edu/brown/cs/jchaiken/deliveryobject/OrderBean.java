package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import edu.brown.cs.jchaiken.database.Database;

/**
 * Represents an order once it has been read in from the database. Order's can
 * be built manually using the OrderBuilder.
 * @author jacksonchaiken
 *
 */
public final class OrderBean extends DeliveryObjectBean<Order> implements
    Order {
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
      String newPickup, String newDropoff, List<String> newItems, double 
      pickupT, double dropoffT, Status newStatus) {
    super(newId);
    status = newStatus;
    orderer = newOrderer;
    deliverer = newDeliverer;
    pickupL = newPickup;
    dropoffL = newDropoff;
    items = newItems;
    price = -1;
    fee = -1;
    pickupTime = pickupT;
    dropoffTime = dropoffT;
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
    // TODO Auto-generated method stub
    return dropoffL;
  }

  @Override
  public double getPrice() {
    // TODO Auto-generated method stub
    return price;
  }

  @Override
  public void setPrice(double newPrice) {
    // TODO Auto-generated method stub
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
    private static boolean tableBuilt = false;

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
      //TODO: add to database
      PreparedStatement prep;
      try {
        if (tableBuilt == false) {
          prep = Database.getConnection().prepareStatement("CREATE TABLE IF NOT"
              + " EXISTS orders (id TEXT, orderer_id TEXT, deliverer_id TEXT,"
              + " pickup_time REAL, dropoff_time REAL, pickup_location TEXT, dropoff_location TEXT,"
              + " PRIMARY KEY (id), FOREIGN KEY (orderer_id) REFERENCES users(id),"
              + " FOREIGN KEY (deliverer_id) REFERENCES users(id) ON DELETE CASCADE"
              + " ON UPDATE CASCADE);");
          prep.executeUpdate();
          prep = Database.getConnection().prepareStatement("CREATE TABLE IF"
              + " NOT EXISTS items (order_id TEXT, item TEXT, PRIMARY KEY"
              + " (order_id) ON DELETE CASCADE ON UPDATE CASCADE);");
          prep.executeUpdate();
          tableBuilt = true;
        }
        prep = Database.getConnection().prepareStatement("INSERT INTO orders"
            + " VALUES (?,?,?,?,?,?,?)");
        prep.setString(1, idB);
        prep.setString(2, ordererB.getId());
        prep.setString(3, delivererB.getId());
        prep.setDouble(4,  pickupT);
        prep.setDouble(5,  dropoffT);
        prep.setString(6, pickupB);
        prep.setString(7,  dropoffB);
        prep.addBatch();
        prep.executeBatch();
        prep = Database.getConnection().prepareStatement("INSERT INTO items VALUES (?, ?)");
        for (String item : itemsB) {
          prep.setString(1, idB);
          prep.setString(2, item);
          prep.addBatch();
        }
        prep.executeBatch();
      } catch (SQLException exc) {
        // TODO Auto-generated catch block
        exc.printStackTrace();
      }
      return new OrderBean(idB, ordererB, delivererB, pickupB, dropoffB,
          itemsB, pickupT, dropoffT, status);
    }
  }
}
