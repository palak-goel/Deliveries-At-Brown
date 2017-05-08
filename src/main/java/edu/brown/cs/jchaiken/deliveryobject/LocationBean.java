package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.brown.cs.jchaiken.database.Database;

/**
 * LocationBean models a location as a latitude longitude pair with an ID once
 * it has been read in from the database.
 * @author jacksonchaiken
 *
 */
class LocationBean extends DeliveryObjectBean<Location> implements Location {
  private final double lat;
  private final double lng;
  private final String name;

  /**
   * Creates a new Location Bean.
   * @param newId
   *          the location's id.
   * @param newLat
   *          the location's latitude.
   * @param newLng
   *          the location's longitude.
   */
  LocationBean(String newId, double newLat, double newLng, String newName) {
    super(newId);
    lat = newLat;
    lng = newLng;
    name = newName;
  }

  @Override
  public double getLatitude() {
    return lat;
  }

  @Override
  public double getLongitude() {
    return lng;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("INSERT INTO locations VALUES (?,?,?,?);")) {
      prep.setString(1, super.getId());
      prep.setDouble(2, lat);
      prep.setDouble(3, lng);
      prep.setString(4, name);
      prep.addBatch();
      prep.executeBatch();
    } catch (final SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
  }

  @Override
  public void deleteFromDatabase() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("DELETE FROM locations WHERE id = ?")) {
      prep.setString(1, super.getId());
      prep.executeUpdate();
    } catch (final SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
    DeliveryObjectProxy.getCache().invalidate(super.getId());
  }
}
