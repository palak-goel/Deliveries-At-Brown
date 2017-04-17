package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;

public interface User extends DeliveryObject {
  public String getId();

  public String getName();

  public String getEmail();

  public Collection<Order> pastDeliveries();

  public Collection<Order> currentDeliveries();

  public Collection<Order> pastOrders();

  public Collection<Order> currentOrders();

  public String getStripeId();

  public void setStripeId(String id);

  public void pay(double amount);

  public void charge(double amount);

  public static User byId(String id) {
    return new UserProxy(id);
  }

  public static User byEmail(String email) {
    return UserProxy.byEmail(email);
  }
}
