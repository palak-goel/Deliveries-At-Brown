package edu.brown.cs.jchaiken.deliveryobject;

import java.util.List;

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
  private String pickup;
  private String dropoff;
  private List<String> items;
  private double price;
  private double fee;
  private Status status;

  private OrderBean(String newId, User newOrderer, User newDeliverer,
      String newPickup, String newDropoff, List<String> newItems) {
    super(newId);
    status = Status.UNASSIGNED;
    orderer = newOrderer;
    deliverer = newDeliverer;
    pickup = newPickup;
    dropoff = newDropoff;
    items = newItems;
    price = -1;
    fee = -1;
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
    return pickup;
  }

  @Override
  public String getDropoffLocation() {
    // TODO Auto-generated method stub
    return dropoff;
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
    //set fee here
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

    OrderBuilder setId(String id) {
      idB = id;
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

    Order build() {
      return new OrderBean(idB, ordererB, delivererB, pickupB, dropoffB,
          itemsB);
    }
  }
}
