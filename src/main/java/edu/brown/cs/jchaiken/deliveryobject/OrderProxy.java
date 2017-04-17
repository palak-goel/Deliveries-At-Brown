package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import edu.brown.cs.jchaiken.database.Database;

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
  protected void cache() throws SQLException {
    //TODO do database stuff
    String query = "SELECT * FROM orders WHERE id = " + super.getId();
    List<List<Object>> results = Database.query(query);
    if (results.size() == 1) {
      List<Object> order = results.get(0);
      String ordererId = (String) order.get(1);
      String delivererId = (String) order.get(2);
      double pickupT = (double) order.get(3);
      double dropoffT = (double) order.get(4);
      List<String> items = Arrays.asList(((String) order.get(5)).split(","));
      String pickupL = (String) order.get(6);
      String dropoffL = (String) order.get(7);
    }
  }

  @Override
  public User getOrderer() {
    check();
    return super.getData().getOrderer();
  }

  @Override
  public User getDeliverer() {
    check();
    return super.getData().getDeliverer();
  }

  @Override
  public void assignDeliverer(User deliverer) {
    check();
    super.getData().assignDeliverer(deliverer);
  }

  @Override
  public List<String> getOrderItems() {
    check();
    return super.getData().getOrderItems();
  }

  @Override
  public String getPickupLocation() {
    check();
    return super.getData().getPickupLocation();
  }

  @Override
  public String getDropoffLocation() {
    check();
    return super.getData().getDropoffLocation();
  }

  @Override
  public double getPrice() {
    check();
    return super.getData().getPrice();
  }

  @Override
  public void setPrice(double price) {
    check();
    super.getData().setPrice(price);
  }

  @Override
  public void chargeCustomer() {
    check();
    super.getData().chargeCustomer();
  }

  @Override
  public void setOrderStatus(Status status) {
    check();
    super.getData().setOrderStatus(status);
  }

  @Override
  public Status status() {
    check();
    return super.getData().status();
  }

  @Override
  public double getPickupTime() {
    check();
    return super.getData().getPickupTime();
  }

  @Override
  public double getDropoffTime() {
    check();
    return super.getData().getDropoffTime();
  }
}
