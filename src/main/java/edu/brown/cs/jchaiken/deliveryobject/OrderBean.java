package edu.brown.cs.jchaiken.deliveryobject;

import java.util.List;

public class OrderBean extends DeliveryObjectBean<Order> implements Order {
  private User orderer;
  private User deliverer;
  private String pickup;
  private String dropoff;
  private List<String> items;
  private double price;
  private double fee;
  private ORDER_STATUS status;

  private OrderBean(String newId, User newOrderer, User newDeliverer, String newPickup, String newDropoff, List<String> newItems) {
    super(newId);
    status = ORDER_STATUS.UNASSIGNED;
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
  public void setOrderStatus(ORDER_STATUS newStatus) {
    status = newStatus;
  }

  @Override
  public ORDER_STATUS status() {
    // TODO Auto-generated method stub
    return null;
  }
}
