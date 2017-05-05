package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import edu.brown.cs.jchaiken.database.Database;

/**
 * Top-level interface that represents a pickup or delivery location that is
 * also stored in the database.
 * 
 * @author jacksonchaiken
 *
 */
public interface Location extends DeliveryObject {

	/**
	 * Returns the Location's id.
	 * 
	 * @return the id.
	 */
	String getId();

	/**
	 * Returns the name.
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the latitude.
	 * 
	 * @return the latitude.
	 */
	double getLatitude();

	/**
	 * Returns the longitude.
	 * 
	 * @return longitude
	 */
	double getLongitude();

	/**
	 * Adds the location to the database.
	 */
	void addToDatabase();

	/**
	 * Deletes the location from the database.
	 */
	void deleteFromDatabase();

	/**
	 * Returns a location, given its id.
	 * 
	 * @param id
	 *            the location's id.
	 * @return the Location.
	 */
	static Location byId(String id) {
		return new LocationProxy(id);
	}

	/**
	 * Returns a new location, acting as a constructor for location bean.
	 * 
	 * @param id
	 *            the location's id, must be unique.
	 * @param latitude
	 *            the latitude.
	 * @param longitude
	 *            the longitude.
	 * @return the location object.
	 */
	static Location newLocation(double latitude, double longitude,
	    String name) {
	  Location loc = Location.byLatLng(latitude, longitude);
	  if (loc != null) {
	    return loc;
	  }
		LocationBean bean = new LocationBean(
		    IdGenerator.getNextId(), latitude, longitude, name);
		bean.addToDatabase();
		return bean;
	}

	static Location byName(String name) {
	  return LocationProxy.byName(name);
	}

	/**
	 * Returns a location, given its latitude and longitude.
	 * 
	 * @param lat
	 *            the latitude
	 * @param lng
	 *            the longitude
	 * @return the Location, or null if it is not in the database.
	 */
	static Location byLatLng(double lat, double lng) {
		return LocationProxy.byLatLng(lat, lng);
	}

	/**
	 * Returns a collection of locations that are inside a bounding box.
	 * 
	 * @param seLat
	 *            the south-eastern latitude
	 * @param seLng
	 *            the south-eastern longitude
	 * @param neLat
	 *            the north-eastern latitude
	 * @param neLng
	 *            the north-eastern longitude
	 * @return Collection of locations
	 */
	static Collection<Location> byBoundingBox(double seLat, double seLng, double neLat, double neLng) {
		return LocationProxy.byBoundingBox(seLat, seLng, neLat, neLng);
	}

	/**
	 * Returns a collection of all locations in the database.
	 * 
	 * @return the collection of locations.
	 */
	static Collection<Location> allLocations() {
		return LocationProxy.allLocations();
	}

	/**
	 * Class that provides unique ids for locations for proper database
	 * insertion and hashing.
	 *
	 * @author jacksonchaiken
	 *
	 */
	class IdGenerator {
		private static double counter = 0;
		/**
		 * Returns the next id.
		 * 
		 * @return the unique id.
		 */
		static String getNextId() {
		  if (counter == 0) {
		    counter = LocationProxy.checkCounter();
		  }
			counter++;
			return "/l/" + counter;
		}
	}
}
