package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;

public class LocationBeanTest {
  @Test
  public void testNotNull() {
    assertNotNull(new LocationBean("Id", 1,1));
  }

  @Test
  public void testGetLat() {
    Location x = new LocationBean("x", 2, 3);
    assert x.getLatitude() == 2;
  }

  @Test
  public void testGetLng() {
    Location x = new LocationBean("x", 2, 3);
    assert x.getLongitude() == 3;
  }

  @Test
  public void testAddToDatabase() {
    Database.setUrl("data/test.sqlite3");
    Location test = new LocationBean("/l/10", 40, -70);
    test.addToDatabase();
    Location l = Location.byId("/l/10");
    assertEquals(l.getLatitude(), 40.0, 1);
    assertEquals(l.getLongitude(), -70.0, 1);
    Location test2 = new LocationBean("/l/11", 41, -69);
    test2.addToDatabase();
    Location l2 = Location.byId("/l/11");
    assert l2.getLatitude() == 41.0;
    assert l2.getLongitude() == -69.0;
  }

  @Test
  public void testDeleteFromDatabase() {
    Database.setUrl("data/test.sqlite3");
    Location test = Location.byId("/l/10");
    Location test2 = Location.byId("/l/11");
    assert test.getLatitude() == 40.0;
    assertEquals(test2.getLatitude(), 41.0, .1);
    test2.deleteFromDatabase();
    test.deleteFromDatabase();
    LocationProxy check = new LocationProxy("/l/10");
    check.check();
    assert check.getData() == null;
  }
}
