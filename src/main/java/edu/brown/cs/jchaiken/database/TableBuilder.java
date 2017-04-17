package edu.brown.cs.jchaiken.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableBuilder {

  /**
   * Builds the users table if it does not exist.
   */
  public void buildUsers() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS users (id TEXT, name TEXT, email TEXT,"
          + " cell TEXT, password INT, stripe_id TEXT, PRIMARY KEY (id));")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Builds the items table if it does not exist.
   */
  public void buildItems() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS items ("
        + "order_id TEXT, item TEXT, PRIMARY KEY (order_id));")) {
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
        + " dropoff_location TEXT, PRIMARY KEY (id), FOREIGN KEY (orderer_id)"
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
}
