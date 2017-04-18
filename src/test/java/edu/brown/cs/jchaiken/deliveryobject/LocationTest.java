package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;

public class LocationTest {
  @Test
  public void testNotNull() {
    Location l = Location.byId("loc");
    assert l != null;
  }

  @Test
  public void testIdGenerator() {
    String lastId = Location.IdGenerator.getNextId();
    for (int x = 0; x < 10000; x++) {
      String id = Location.IdGenerator.getNextId();
      assert !lastId.equals(id);
      lastId = id;
    }
  }

  @Test
  public void testById() {
    Database.setUrl("data/test.sqlite3");
    Location l = Location.byId("/l/1");
    assert l.getLatitude() == 42.0;
  }

  @Test
  public void testByLatLng() {
    assert Location.byLatLng(42.4, -72.0).getId().equals("/l/5");
    assert Location.byLatLng(0, 0) == null;
  }

  @Test
  public void testBoundingBox() {
    Collection<Location> box = Location.byBoundingBox(30, -80, 50, -50);
    assert box.size() == 5;
  }
}
