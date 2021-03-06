package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import edu.brown.cs.jchaiken.database.Database;

/**
 * Proxy pattern class that interacts with the database to model a location.
 *
 * @author jacksonchaiken
 *
 */
class LocationProxy extends DeliveryObjectProxy<Location> implements Location {

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
          final double lat = rs.getDouble(2);
          final double lng = rs.getDouble(3);
          final String name = rs.getString(4);
          final Location loc = new LocationBean(super.getId(), lat, lng, name);
          super.setData(loc);
        }
      }
    }
  }

  private static final String LAT_LNG_Q = "SELECT id FROM locations WHERE"
      + " latitude = ? AND longitude = ?";

  /**
   * Returns a location from the database.
   *
   * @param lat
   *          the latitude
   * @param lng
   *          the longitude
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
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  private static final String BB_QUERY = "SELECT id FROM locations WHERE"
      + " latitude > ? AND longitude > ? AND latitude < ? AND longitude"
      + " < ?";

  /**
   * Returns a collection of locations that are inside a bounding box.
   *
   * @param seLat
   *          the south-eastern latitude
   * @param seLng
   *          the south-eastern longitude
   * @param neLat
   *          the north-eastern latitude
   * @param neLng
   *          the north-eastern longitude
   * @return Collection of locations
   */
  public static Collection<Location> byBoundingBox(double seLat, double seLng,
      double neLat, double neLng) {
    final Collection<Location> results = new HashSet<>();
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
    } catch (final SQLException exc) {
      exc.printStackTrace();
    }
    return results;
  }

  private static final String BY_N = "SELECT id FROM locations WHERE name = ?";

  /**
   * Returns the location with a given name, if it exists.
   * @param name
   *          the locations name.
   * @return the location, or null if it does not exist.
   */
  public static Location byName(String name) {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(BY_N)) {
      prep.setString(1, name);
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          return new LocationProxy(rs.getString(1));
        }
      }
    } catch (final SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private static final String ALL_L = "SELECT * FROM locations";

  /**
   * Returns a collection of all location's in the database.
   * @return the colleciton of locations.
   */
  public static Collection<Location> allLocations() {
    final Collection<Location> locs = new HashSet<>();
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement(ALL_L)) {
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          locs.add(new LocationProxy(rs.getString(1)));
        }
      }
    } catch (final SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return locs;
  }

  @Override
  public void addToDatabase() {
    check();
    if (super.getData() != null) {
      super.getData().addToDatabase();
    }
  }

  @Override
  public void deleteFromDatabase() {
    check();
    if (super.getData() != null) {
      super.getData().deleteFromDatabase();
    }
  }

  @Override
  public String getName() {
    check();
    if (super.getData() != null) {
      return super.getData().getName();
    }
    return null;
  }

  private static final String COUNTER_Q = "SELECT COUNT(id) FROM locations";

  protected static int checkCounter() {
    if (Database.getConnection() != null) {
      try (PreparedStatement prep = Database.getConnection()
          .prepareStatement(COUNTER_Q)) {
        try (ResultSet rs = prep.executeQuery()) {
          return rs.getInt(1);
        }
      } catch (final SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return 0;
  }
}
