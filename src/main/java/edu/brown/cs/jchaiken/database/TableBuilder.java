package edu.brown.cs.jchaiken.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableBuilder {

  /**
   * Builds the users table if it does not exist.
   */
  public void buildUsers() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS users (id TEXT, name TEXT,"
          + " cell TEXT, password INT, stripe_id TEXT, PRIMARY KEY (id));")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Builds the order table if it does not exist.
   */
  public void buildOrders() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT"
        + " EXISTS orders (id TEXT, orderer_id TEXT, deliverer_id TEXT,"
        + " pickup_time REAL, dropoff_time REAL, pickup_location TEXT,"
        + " dropoff_location TEXT, price REAL, items TEXT, PRIMARY KEY (id),"
        + " FOREIGN KEY (orderer_id)"
        + " REFERENCES users(id), FOREIGN KEY (deliverer_id) REFERENCES"
        + " users(id) ON DELETE CASCADE ON UPDATE CASCADE);")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Builds the locations table if it does not exist.
   */
  public void buildLocations() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS locations (id TEXT, latitude"
            + " REAL, longitude REAL, PRIMARY KEY (id));")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Builds the order status table if it does not exist.
   */
  public void buildOrderStatus() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS order_status (order_id TEXT, status INT,"
        + " FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE);")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }
  
  public void buildUserSerializable() {
	  try (PreparedStatement prep = Database.getConnection().prepareStatement(
		 "CREATE TABLE IF NOT EXISTS user (user_id TEXT, user_obj BLOB,"
		 + " PRIMARY KEY (user_id));")) {
		  prep.executeUpdate();
	  } catch (SQLException e) {
		e.printStackTrace();
	}
  }
  
  public void buildOrderSerializable() {
	  try (PreparedStatement prep = Database.getConnection().prepareStatement(
		 "CREATE TABLE IF NOT EXISTS order (order_id TEXT, order_obj BLOB,"
		 + " PRIMARY KEY (order_id));")) {
		  prep.executeUpdate();
	  } catch (SQLException e) {
		e.printStackTrace();
	}
  }
  
  public void buildLocation() {
	  try (PreparedStatement prep = Database.getConnection().prepareStatement(
		 "CREATE TABLE IF NOT EXISTS location (location_name TEXT, "
		 + " lat REAL, lon REAL"
		 + " PRIMARY KEY (location_name));")) {
		  prep.executeUpdate();
	  } catch (SQLException e) {
		e.printStackTrace();
	}
  }
  
  
}
