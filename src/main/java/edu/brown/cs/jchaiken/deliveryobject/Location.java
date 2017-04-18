package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Collection;

/**
 * Top-level interface that represents a pickup or delivery location that is
 * also stored in the database.
 * @author jacksonchaiken
 *
 */
public interface Location extends DeliveryObject {

  /**
   * Returns the Location's id.
   * @return the id.
   */
  String getId();

  /**
   * Returns the latitude.
   * @return the latitude.
   */
  double getLatitude();

  /**
   * Returns the longitude.
   * @return longitude
   */
  double getLongitude();

  /**
   * Adds the location to the database.
   */
  void addToDatabase();

  /**
   * Returns a location, given its id.
   * @param id the location's id.
   * @return the Location.
   */
  static Location byId(String id) {
    return new LocationProxy(id);
  }

  /**
   * Returns a location, given its latitude and longitude.
   * @param lat the latitude
   * @param lng the longitude
   * @return the Location, or null if it is not in the database.
   */
  static Location byLatLng(double lat, double lng) {
    return LocationProxy.byLatLng(lat, lng);
  }

  /**
   * Returns a collection of locations that are inside a bounding box.
   * @param seLat the south-eastern latitude
   * @param seLng the south-eastern longitude
   * @param neLat the north-eastern latitude
   * @param neLng the north-eastern longitude
   * @return Collection of locations
   */
  static Collection<Location> byBoundingBox(double seLat, double seLng, double
      neLat, double neLng) {
    return LocationProxy.byBoundingBox(seLat, seLng, neLat, neLng);
  }

  /**
   * Class that provides unique ids for locations for proper
   * database insertion and hashing.
   * @author jacksonchaiken
   *
   */
  class IdGenerator {
    private static double counter = 0;

    private IdGenerator() {
    }

    /**
     * Returns the next id.
     * @return the unique id.
     */
    static String getNextId() {
      counter++;
      return "/l/" + counter;
    }
  }
}
