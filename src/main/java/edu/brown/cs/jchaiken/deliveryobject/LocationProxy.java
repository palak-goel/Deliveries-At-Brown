package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Proxy pattern class that interacts with the database to model a location.
 * @author jacksonchaiken
 *
 */
class LocationProxy extends DeliveryObjectProxy<Location> implements
    Location {

  LocationProxy(String id) {
    super(id);
  }

  @Override
  public double getLatitude() {
    check();
    if (super.getData() == null) {
      return Double.MAX_VALUE;
    }
    return super.getData().getLatitude();
  }

  @Override
  public double getLongitude() {
    check();
    if (super.getData() == null) {
      return Double.MAX_VALUE;
    }
    return super.getData().getLongitude();
  }

  private static final String CACHE_Q = "SELECT * FROM locations WHERE"
      + " id = ?";

  @Override
  protected void cache() throws SQLException {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(CACHE_Q)) {
      assert prep != null;
      prep.setString(1, super.getId());
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          double lat = rs.getDouble(2);
          double lng = rs.getDouble(3);
          Location loc = new LocationBean(super.getId(), lat, lng);
          super.setData(loc);
        }
      }
    }
  }

  private static final String LAT_LNG_Q = "SELECT id FROM locations WHERE"
      + " latitude = ? AND longitude = ?";

  /**
   * Returns a location from the database.
   * @param lat the latitude
   * @param lng the longitude
   * @return return the location, or null if it does not exist.
   */
  public static Location byLatLng(double lat, double lng) {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(LAT_LNG_Q)) {
      prep.setDouble(1, lat);
      prep.setDouble(2, lng);
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          return new LocationProxy(rs.getString(1));
        }
      }
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  private static final String BB_QUERY = "SELECT id FROM locations WHERE"
      + " latitude > ? AND longitude > ? AND latitude < ? AND longitude"
      + " < ?";

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
    Collection<Location> results = new HashSet<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(BB_QUERY)) {
      prep.setDouble(1, seLat);
      prep.setDouble(2, seLng);
      prep.setDouble(3, neLat);
      prep.setDouble(4, neLng);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          results.add(new LocationProxy(rs.getString(1)));
        }
      }
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
    return results;
  }

  private static final String ALL_L = "SELECT * FROM locations";

  /**
   * Returns a collection of all location's in the database.
   * @return the colleciton of locations.
   */
  public static Collection<Location> allLocations() {
    Collection<Location> locs = new HashSet<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ALL_L)) {
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          locs.add(new LocationProxy(rs.getString(1)));
        }
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return locs;
  }

  @Override
  public void addToDatabase() {
    check();
    super.getData().addToDatabase();
  }

  @Override
  public void deleteFromDatabase() {
    check();
    super.getData().deleteFromDatabase();
  }
}
