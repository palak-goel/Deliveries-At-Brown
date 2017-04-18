package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * LocationBean models a location as a latitude longitude pair with an ID
 * once it has been read in from the database.
 * @author jacksonchaiken
 *
 */
public class LocationBean extends DeliveryObjectBean<Location> implements
    Location {
  private double lat;
  private double lng;

  LocationBean(String newId, double newLat, double newLng) {
    super(newId);
    lat = newLat;
    lng = newLng;
  }

  @Override
  public double getLatitude() {
    // TODO Auto-generated method stub
    return lat;
  }

  @Override
  public double getLongitude() {
    // TODO Auto-generated method stub
    return lng;
  }

  @Override
  public void addToDatabase() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "INSERT INTO locations VALUES (?,?,?);")) {
      prep.setString(1, super.getId());
      prep.setDouble(2, lat);
      prep.setDouble(3, lng);
      prep.addBatch();
      prep.executeBatch();
    } catch (SQLException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
  }
}
