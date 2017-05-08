package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;

public class DeliveryObjectProxyTest {
  @Test
  public void testNotNull() {
    Database.setUrl("data/test.sqlite3");
    final DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    assert test != null;
  }

  @Test
  public void checkCache() {
    Database.setUrl("data/test.sqlite3");
    final DeliveryObjectProxy<Location> test = new LocationProxy("/l/5");
    test.check();
    assertEquals(DeliveryObjectProxy.getCache().size(), 1);
  }

  @Test
  public void clearCache() {
    Database.setUrl("data/test.sqlite3");
    final DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    test.check();
    assert test.getData() != null;
    assertEquals(DeliveryObjectProxy.getCache().size(), 1);
    DeliveryObjectProxy.clearCache();
    assertEquals(DeliveryObjectProxy.getCache().size(), 0);
  }

  @Test
  public void testGetData() {
    Database.setUrl("data/test.sqlite3");
    final DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    test.check();
    assert test.getData() != null;
  }

  @Test
  public void testEquals() {
    Database.setUrl("data/test.sqlite3");
    final DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    final DeliveryObjectProxy<Location> test1 = new LocationProxy("/l/2");
    final DeliveryObjectProxy<Location> test2 = new LocationProxy("/l/1");
    assert test.equals(test2);
    assert !test1.equals(test);
    assert !test.equals("hi");
    assert test.equals(test);
  }
}
