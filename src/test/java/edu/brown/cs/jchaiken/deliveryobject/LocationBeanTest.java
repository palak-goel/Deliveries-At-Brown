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
  }

  @Test
  public void testDeleteFromDatabase() {
    Database.setUrl("data/test.sqlite3");
    LocationProxy test = new LocationProxy("/l/10");
    assert test.getLatitude() == 40.0;
    test.deleteFromDatabase();
    LocationProxy check = new LocationProxy("/l/10");
    check.check();
    assert check.getData() == null;
  }
}
