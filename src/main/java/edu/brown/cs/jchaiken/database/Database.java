package edu.brown.cs.jchaiken.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe database implementation that allows the user to statically
 * query the database.
 * @author jacksonchaiken
 *
 */
public final class Database {
  private static ConcurrentHashMap<String, PreparedStatement> statements
      = new ConcurrentHashMap<>();
  
  private static String url;
  private static ThreadLocal<Connection> conn;
  
  private Database() {
  }

  /**
   * Returns a connection to the database.
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
   * Querys the database for the given search. If the prepared statement for
   * the query already exists, that prepared statement is reused to reduce
   * overhead.
   * @param queryString the string of the query
   * @return A list of rows, containing the results
   */
  public static List<List<Object>> query(String queryString) {
    PreparedStatement prep;
    if (statements.containsKey(queryString)) {
      prep = statements.get(queryString);
    } else {
      try {
        prep = getConnection().prepareStatement(queryString);
        statements.put(queryString, prep);
      } catch (SQLException exc) {
        exc.printStackTrace();
        return null;
      }
    }
    try {
      ResultSet rs = prep.executeQuery();
      ResultSetMetaData rmd = rs.getMetaData();
      List<List<Object>> results = new ArrayList<>();
      int cols = rmd.getColumnCount();
      while (rs.next()) {
        List<Object> row = new ArrayList<>();
        for (int x = 0; x < cols; x++) {
          switch (rmd.getColumnTypeName(x)) {
            case "NULL":
              break;
            case "INTEGER":
              row.add(rs.getInt(x));
              break;
            case "REAL":
              row.add(rs.getDouble(x));
              break;
            case "TEXT":
              row.add(rs.getString(x));
              break;
            default:
              break;
          }
          results.add(row);
        }
      }
      return results;
    } catch (SQLException exc) {
      exc.printStackTrace();
      return null;
    }
  }

  /**
   * Updates the database's current url.
   * @param newUrl the new file path the database.
   */
  public static void setUrl(String newUrl) {
    if (url != null && !url.equals(url)) {
      closeConnection();
    }
    url = newUrl;
  }

  public static String getUrl() {
    return url;
  }
}
