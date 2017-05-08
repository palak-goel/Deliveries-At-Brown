package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;

public class OrderProxyTest {
  @Test
  public void testNotNull() {
    assert new OrderProxy("hey") != null;
  }

  @Test
  public void testGoodProxy() {
    Database.setUrl("data/test.sqlite3");
    final OrderProxy test = new OrderProxy("/o/1");
    // precache
    assert test.getData() == null;
    assert test.getOrderer().getId().equals("jackson_chaiken@brown.edu");
    assert test.getPickupTime() == 123;
    assert test.getData() != null;
    assert test.getOrderItems().size() == 1;
    assertEquals(test.status(), OrderStatus.COMPLETED);
    assertEquals(test.getOrderItems().get(0), "muffin");
  }

  @Test
  public void testBadProxy() {
    final OrderProxy test = new OrderProxy("2");
    assert test.getDeliverer() == null;
    assert test.getData() == null;
    assert test.getPrice() == Double.MAX_VALUE;
    assert test.getPickupLocation() == null;
    assert test.getDropoffLocation() == null;
    assert test.getOrderItems() == null;
  }

  @Test
  public void testByItem() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Order> muffins = OrderProxy.byItem("muffin");
    assert muffins.size() == 2;
    final Order muffin = muffins.iterator().next();
    assert muffin.getId().equals("/o/1") || muffin.getId().equals("/o/3");
  }

  @Test
  public void testByItemNoItem() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Order> muffins = OrderProxy.byItem("spicy");
    assert muffins.size() == 0;
  }

  @Test
  public void testByPickupLocationGood() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Order> orders = OrderProxy.byPickupLocation("/l/1");
    assertEquals(orders.size(), 2);
  }

  @Test
  public void testNoPickupLoc() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Order> orders = OrderProxy.byPickupLocation("hey");
    assert orders.size() == 0;
  }
}
