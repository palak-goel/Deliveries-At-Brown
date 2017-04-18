package edu.brown.cs.jchaiken.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A thread-safe database implementation that allows the user to statically
 * query the database.
 * @author jacksonchaiken
 *
 */
public final class Database {
  private static String url;
  private static ThreadLocal<Connection> conn;

  private Database() {
  }

  /**
   * Returns a connection to the database.
   * @return the connection
   */
  public static Connection getConnection() {
    if (conn == null || conn.get() == null) {
      updateConn();
    }
    return conn.get();
  }

  private static synchronized void updateConn() {
    if (conn == null) {
      conn = new ThreadLocal<Connection>();
    }
    try {
      Class.forName("org.sqlite.JDBC");
      String newUrl = "jdbc:sqlite:" + url;
      conn.set(DriverManager.getConnection(newUrl));
      try (Statement stat = conn.get().createStatement()) {
        stat.executeUpdate("PRAGMA foreign_keys = ON;");
      }
    } catch (SQLException | ClassNotFoundException exc) {
      conn.set(null);
    }
  }

  private static void closeConnection() {
    if (getConnection() != null) {
      try {
        getConnection().close();
      } catch (SQLException exc) {
        exc.printStackTrace();
      }
      conn.set(null);
    }
  }


  /**
   * Updates the database's current url.
   * @param newUrl the new file path the database.
   */
  public static void setUrl(String newUrl) {
    if (url != null && !url.equals(newUrl)) {
      closeConnection();
    }
    url = newUrl;
  }

  /**
   * Returns the current url.
   * @return the url.
   */
  public static String getUrl() {
    return url;
  }
}
