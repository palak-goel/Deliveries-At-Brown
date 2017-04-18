package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;

public class DeliveryObjectProxyTest {
  @Test
  public void testNotNull() {
    Database.setUrl("data/test.sqlite3");
    DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    assert test != null;
  }

  @Test
  public void checkCache() {
    Database.setUrl("data/test.sqlite3");
    DeliveryObjectProxy<Location> test = new LocationProxy("/l/5");
    test.check();
    assertEquals(test.getCache().size(), 1);
  }

  @Test
  public void clearCache() {
    Database.setUrl("data/test.sqlite3");
    DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    test.check();
    assert test.getData() != null;
    assertEquals(test.getCache().size(), 1);
    DeliveryObjectProxy.clearCache();
    assertEquals(test.getCache().size(), 0);
  }

  @Test
  public void testGetData() {
    Database.setUrl("data/test.sqlite3");
    DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    test.check();
    assert test.getData() != null;
  }

  @Test
  public void testEquals() {
    Database.setUrl("data/test.sqlite3");
    DeliveryObjectProxy<Location> test = new LocationProxy("/l/1");
    DeliveryObjectProxy<Location> test1 = new LocationProxy("/l/2");
    DeliveryObjectProxy<Location> test2 = new LocationProxy("/l/1");
    assert test.equals(test2);
    assert !test1.equals(test);
    assert !test.equals("hi");
    assert test.equals(test);
  }
}
