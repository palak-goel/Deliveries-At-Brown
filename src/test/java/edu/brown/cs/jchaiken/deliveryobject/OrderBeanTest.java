package edu.brown.cs.jchaiken.deliveryobject;

import java.util.ArrayList;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.Status;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

public class OrderBeanTest {
  @Test
  public void testNotNull() {
    OrderBuilder builder = new OrderBuilder();
    Order newOrder = builder.setId("hey")
        .setDeliverer(User.byId("user"))
        .setOrderer(User.byId("user2"))
        .setDropoff(Location.byId("/l/1"))
        .setPickup(Location.byId("/l/2"))
        .setItems(new ArrayList<String>())
        .setDropoffTime(100)
        .setPickupTime(150)
        .setStatus(Status.COMPLETED)
        .setPrice(100)
        .build();
    assert newOrder != null;
  }

  @Test
  public void testGetters() {
    OrderBuilder builder = new OrderBuilder();
    Order newOrder = builder.setId("hey")
        .setDeliverer(User.byId("user"))
        .setOrderer(User.byId("user2"))
        .setDropoff(Location.byId("/l/1"))
        .setPickup(Location.byId("/l/2"))
        .setItems(new ArrayList<String>())
        .setDropoffTime(100)
        .setPickupTime(150)
        .setStatus(Status.COMPLETED)
        .setPrice(100)
        .build();
    assert newOrder != null;
    assert newOrder.getDeliverer().getId().equals("user");
    assert newOrder.getOrderer().getId().equals("user2");
    assert newOrder.getDropoffLocation().getId().equals("/l/1");
    assert newOrder.getPickupLocation().getId().equals("/l/2");
    assert newOrder.getPrice() == 100;
    assert newOrder.status() == Status.COMPLETED;
  }

  @Test
  public void testBadBuilder() {
    OrderBuilder builder = new OrderBuilder();
    Order newOrder = builder.build();
    assert newOrder == null;
  }

  @Test
  public void testAddToDb() {
    Database.setUrl("data/test.sqlite3");
    OrderBuilder builder = new OrderBuilder();
    Order newOrder = builder.setId("hey")
        .setDeliverer(User.byId("user"))
        .setOrderer(User.byId("user2"))
        .setDropoff(Location.byId("/l/1"))
        .setPickup(Location.byId("/l/2"))
        .setItems(new ArrayList<String>())
        .setDropoffTime(100)
        .setPickupTime(150)
        .setStatus(Status.COMPLETED)
        .setPrice(100)
        .build();
    newOrder.addToDatabase();
    //now retrieve record
    Order test = Order.byId("hey");
    assert test.getDeliverer().getId().equals("user");
    assert test.status() == Status.COMPLETED;
  }

  @Test
  public void testDbRemove() {
    //remove the bad record we just created
    Database.setUrl("data/test.sqlite3");
    Order test = Order.byId("hey");
    test.removeFromDatabase();
    //check it was removed
    Order check = Order.byId("hey");
    assert check.getDeliverer() == null;
  }
}
