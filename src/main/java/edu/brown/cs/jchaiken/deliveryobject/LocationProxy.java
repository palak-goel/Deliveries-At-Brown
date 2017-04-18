package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Proxy pattern class that interacts with the database to model a location.
 * @author jacksonchaiken
 *
 */
public class LocationProxy extends DeliveryObjectProxy<Location> implements
    Location {

  LocationProxy(String id) {
    super(id);
  }

  @Override
  public double getLatitude() {
    check();
    return super.getData().getLatitude();
  }

  @Override
  public double getLongitude() {
    check();
    return super.getData().getLongitude();
  }

  @Override
  protected void cache() throws SQLException {
    String query = "SELECT * FROM locations WHERE id = " + super.getId();
    List<List<Object>> results = Database.query(query);
    if (results.size() == 1) {
      List<Object> location = results.get(0);
      double lat = (double) location.get(1);
      double lng = (double) location.get(2);
      Location loc = new LocationBean(super.getId(), lat, lng);
      super.setData(loc);
    }
  }

  /**
   * Returns a location from the database.
   * @param lat the latitude
   * @param lng the longitude
   * @return return the location, or null if it does not exist.
   */
  public static Location byLatLng(double lat, double lng) {
    String query = "SELECT id FROM locations WHERE latitude = "
        + lat + " AND longitude = " + lng;
    List<List<Object>> results = Database.query(query);
    if (results.size() == 1) {
      List<Object> loc = results.get(0);
      Location location = new LocationProxy((String) loc.get(0));
      return location;
    }
    return null;
  }

  /**
   * Returns a collection of locations that are inside a bounding box.
   * @param seLat the south-eastern latitude
   * @param seLng the south-eastern longitude
   * @param neLat the north-eastern latitude
   * @param neLng the north-eastern longitude
   * @return Collection of locations
   */
  public static Collection<Location> byBoundingBox(double seLat, double seLng,
      double neLat, double neLng) {
    String query = "SELECT id FROM locations WHERE latitude > " + seLat
        + " AND longitude > " + seLng + " AND latitude < " + neLat
        + " AND longtiude < " + neLng;
    List<List<Object>> box = Database.query(query);
    Collection<Location> results = new HashSet<>();
    for (List<Object> point : box) {
      Location location = new LocationProxy((String) point.get(0));
      results.add(location);
    }
    return results;
  }

  @Override
  public void addToDatabase() {
    check();
    super.getData().addToDatabase();
  }
}
