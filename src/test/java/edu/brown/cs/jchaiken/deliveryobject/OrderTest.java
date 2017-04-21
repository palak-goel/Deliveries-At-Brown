package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;

public class OrderTest {
  @Test
  public void testStatus() {
    assert OrderStatus.valueOf(0) == OrderStatus.COMPLETED;
    assert OrderStatus.valueOf(-1) == null;
    assertEquals(OrderStatus.valueOf(1).ordinal(), 3);
  }

  @Test
  public void testIdGenerator() {
    String lastId = Order.IdGenerator.getNextId();
    for (int x = 0; x < 10000; x++) {
      String newId = Order.IdGenerator.getNextId();
      assert !newId.equals(lastId);
      lastId = newId;
    }
  }

  @Test
  public void testById() {
    Database.setUrl("data/test.sqlite3");
    Order test = Order.byId("/o/1");
    assert test.getDropoffTime() == 456.0;
    assert test.getDeliverer().getId().equals("palak_goel@brown.edu");
  }

  @Test
  public void testBadId() {
    Database.setUrl("data/test.sqlite3");
    Order test = Order.byId("bad");
    assert test.getDeliverer() == null;
    assert test.getPrice() == Double.MAX_VALUE;
  }

  @Test
  public void testByPickupLocation() {
    Database.setUrl("data/test.sqlite3");
    Collection<Order>  orders = Order.byPickupLocation("/l/4");
    assert orders.size() == 1;
  }

  @Test
  public void testByItem() {
    Database.setUrl("data/test.sqlite3");
    assertEquals(Order.byItem("muffin").size(), 2);
    assert Order.byItem("gum").size() == 1;
  }
}
