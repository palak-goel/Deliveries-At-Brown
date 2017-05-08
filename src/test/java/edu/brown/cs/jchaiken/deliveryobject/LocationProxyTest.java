package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;

public class LocationProxyTest {
  @Test
  public void testNotNull() {
    assert new LocationProxy("test") != null;
  }

  @Test
  public void testGetLatitude() {
    Database.setUrl("data/test.sqlite3");
    assertEquals(new LocationProxy("/l/1").getLatitude(), 42.0, .1);
  }

  @Test
  public void testGetLongitude() {
    Database.setUrl("data/test.sqlite3");
    assertEquals(new LocationProxy("/l/2").getLongitude(), -71.3, .1);
  }

  @Test
  public void testBadPoint() {
    assert new LocationProxy("loc").getLatitude() == Double.MAX_VALUE;
  }

  @Test
  public void testGetName() {
    Database.setUrl("data/test.sqlite3");
    final Location l = LocationProxy.byLatLng(42.312, -70.231);
    assert l.getName().equals("name");
  }

  @Test
  public void testByLatLng() {
    Database.setUrl("data/test.sqlite3");
    final Location l = LocationProxy.byLatLng(42.312, -70.231);
    assert l.getId().equals("/l/3");
    assert l.getName().equals("name");
    final Location bad = LocationProxy.byLatLng(0, 0);
    assert bad == null;
  }

  @Test
  public void testGoodBox() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Location> box = LocationProxy.byBoundingBox(41, -73, 43,
        -69);
    assertEquals(box.size(), 5);
  }

  @Test
  public void testBadBox() {
    Database.setUrl("data/test.sqlite3");
    final Collection<Location> box = LocationProxy.byBoundingBox(0, 0, 0, 0);
    assert box.size() == 0;
  }
}
