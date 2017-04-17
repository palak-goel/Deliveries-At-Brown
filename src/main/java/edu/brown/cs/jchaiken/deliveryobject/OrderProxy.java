package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Models an order if it has not been read in from the database yet.
 * @author jacksonchaiken
 *
 */
public class OrderProxy extends DeliveryObjectProxy<Order> implements Order {

  OrderProxy(String newId) {
    super(newId);
  }

  @Override
  protected void cache(Connection connection) throws SQLException {
    //TODO do database stuff
    //TODO create new bean
    //TODO set data to it
  }

  @Override
  public User getOrderer() {
    cache();
    return super.getData().getOrderer();
  }

  @Override
  public User getDeliverer() {
    cache();
    return super.getData().getDeliverer();
  }

  @Override
  public void assignDeliverer(User deliverer) {
    cache();
    super.getData().assignDeliverer(deliverer);
  }

  @Override
  public List<String> getOrderItems() {
    cache();
    return super.getData().getOrderItems();
  }

  @Override
  public String getPickupLocation() {
    cache();
    return super.getData().getPickupLocation();
  }

  @Override
  public String getDropoffLocation() {
    cache();
    return super.getData().getDropoffLocation();
  }

  @Override
  public double getPrice() {
    cache();
    return super.getData().getPrice();
  }

  @Override
  public void setPrice(double price) {
    cache();
    super.getData().setPrice(price);
  }

  @Override
  public void chargeCustomer() {
    cache();
    super.getData().chargeCustomer();
  }

  @Override
  public void setOrderStatus(Status status) {
    cache();
    super.getData().setOrderStatus(status);
  }

  @Override
  public Status status() {
    cache();
    return super.getData().status();
  }
}
