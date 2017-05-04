package edu.brown.cs.jchaiken.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class that builds the table if the one given does not hold
 * any of them.
 * @author jacksonchaiken
 *
 */
public class TableBuilder {

  /**
   * Builds the users table if it does not exist.
   */
  public void buildUsers() {
    try (PreparedStatement prep = Database.getConnection().prepareStatement(
        "CREATE TABLE IF NOT EXISTS users (id TEXT, name TEXT,"
          + " cell TEXT, password INT, stripe_id TEXT, url TEXT,"
          + " PRIMARY KEY (id));")) {
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
        + " dropoff_location TEXT, price REAL, pickup_phone TEXT,"
        + " PRIMARY KEY (id),"
        + " FOREIGN KEY (orderer_id)"
        + " REFERENCES users(id), FOREIGN KEY (deliverer_id) REFERENCES"
        + " users(id), FOREIGN KEY (pickup_location) REFERENCES locations(id),"
        + " FOREIGN KEY (dropoff_location) REFERENCES locations(id)"
        + " ON DELETE CASCADE ON UPDATE CASCADE);")) {
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
        "CREATE TABLE IF NOT"
        + " EXISTS items (order_id TEXT, item TEXT, FOREIGN KEY (order_id)"
        + " REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE);")) {
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
            + " REAL, longitude REAL, name TEXT, PRIMARY KEY (id));")) {
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
        + " time REAL, "
        + " FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON"
        + " UPDATE CASCADE);")) {
      prep.executeUpdate();
    } catch (SQLException exc) {
      exc.printStackTrace();
    }
  }

  /**
   * Builds the location table.
   */
  public void buildLocation() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("CREATE TABLE IF NOT EXISTS location (location_name"
            + " TEXT, lat REAL, lon REAL PRIMARY KEY (location_name));")) {
      prep.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds the account status table.
   */
  public void buildAccountStatus() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("CREATE TABLE IF NOT EXISTS account_status (user_id"
            + " TEXT UNIQUE, status INT, FOREIGN KEY (user_id) REFERENCES users(id)"
            + " ON DELETE CASCADE ON UPDATE CASCADE);")
        ) {
      prep.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds the ratings table in the database.
   */
  public void buildRatingsTable() {
    try (PreparedStatement prep = Database.getConnection()
        .prepareStatement("CREATE TABLE IF NOT EXISTS user_ratings (user_id"
            + " TEXT, rating REAL, user_type TEXT, FOREIGN KEY (user_ID)"
            + " REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE);")) {
      prep.executeUpdate();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
