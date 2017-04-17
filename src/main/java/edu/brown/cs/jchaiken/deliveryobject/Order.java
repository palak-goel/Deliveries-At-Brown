package edu.brown.cs.jchaiken.deliveryobject;

import java.util.List;

public interface Order extends DeliveryObject {

  public static enum ORDER_STATUS {
    UNASSIGNED,
    ASSIGNED,
    AT_LOCATION,
    HAVE_FOOD,
    DROPPED_OFF
  }

  public User getOrderer();

  public User getDeliverer();

  public void assignDeliverer(User deliverer);

  public List<String> getOrderItems();

  public String getPickupLocation();

  public String getDropoffLocation();

  public double getPrice();

  public void setPrice(double price);

  public void chargeCustomer();

  public void setOrderStatus(ORDER_STATUS status);

  public ORDER_STATUS status();
}
