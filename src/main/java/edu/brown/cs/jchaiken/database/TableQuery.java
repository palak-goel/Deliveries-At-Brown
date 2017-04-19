package edu.brown.cs.jchaiken.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.jchaiken.deliveryobject.Location;
import edu.brown.cs.jchaiken.deliveryobject.LocationBean;
import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;

public class TableQuery {
	private static Connection conn;

	public TableQuery(String dbName) {
		setUpConnection(dbName);
	}

	public void setUpConnection(String dbName) {
		String urlToDB = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      urlToDB = "jdbc:sqlite:" + dbName;

	    } catch (ClassNotFoundException e) {
	      System.out.println("ERROR: class not found");
	    }
	    try {
	      conn = DriverManager.getConnection(urlToDB);
	      Statement stat = conn.createStatement();
	      stat.executeUpdate("PRAGMA foreign_keys = ON;");
	    } catch (SQLException e) {
	      System.out.println("ERROR: Database not found!");
	      if (conn != null) {
	        try {
	          conn.close();
	        } catch (SQLException e1) {
	          System.out.println("ERROR: couldn't close");
	        }
	      }
	    }
	}

	public void insertUser(User user) {
		PreparedStatement prep = null;
		try {
			prep = conn.prepareStatement(
					"INSERT INTO user(user_id, user_obj) VALUES(?, ?)"
					);

			prep.setString(1, user.getId());
			prep.setObject(2, user);
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateUser(String userId, User user) {
	
	}

	public User getUser(String userId) {
		PreparedStatement prep = null;
		User user = null;
		try {
			prep = conn.prepareStatement(
					"SELECT user_obj FROM user WHERE user_id = ?"
					);
			prep.setString(1, userId);
			ResultSet rs = prep.executeQuery();
			rs.next();

			byte[] buf = rs.getBytes(1);
			ObjectInputStream objectIn = null;

			if (buf != null) {
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			}

			user = (User) objectIn.readObject();
			prep.close();
		} catch (SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return user;
	}

	public void insertOrder(Order order) {
		PreparedStatement prep = null;
		try {
			prep = conn.prepareStatement(
					"INSERT INTO order(order_id, order_obj) VALUES(?, ?)"
					);
			prep.setString(1, order.getId());
			prep.setObject(2, order);
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateOrder(String orderId, Order order) {
		
	}

	public Order getOrder(String orderId) {
		PreparedStatement prep = null;
		Order order = null;
		try {
			prep = conn.prepareStatement(
					"SELECT order_obj FROM order WHERE order_id = ?"
					);
			prep.setString(1, orderId);
			ResultSet rs = prep.executeQuery();
			rs.next();
			
			byte[] buf = rs.getBytes(1);
			ObjectInputStream objectIn = null;
			
			if (buf != null) {
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			}

			order = (Order) objectIn.readObject();
			prep.close();
		} catch (SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return order;
	}
	
	public void insertLocation(Location loc) {
		PreparedStatement prep = null;
		String name = loc.getId();
		Double lat = loc.getLatitude();
		Double lon = loc.getLongitude();
		try {
			prep = conn.prepareStatement(
					"INSERT INTO location(location_name, lat, lon) VALUES(?, ?, ?)"
					);
			prep.setString(1, name);
			prep.setDouble(2, lat);
			prep.setDouble(2, lon);
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Location> getLocations() {
		PreparedStatement prep = null;
		List<Location> locations = new ArrayList<>();
		try {
			prep = conn.prepareStatement(
					"SELECT * FROM location"
					);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				String locationId = rs.getString(1);
				Double lat = rs.getDouble(2);
				Double lon = rs.getDouble(3);
				LocationBean lb = new LocationBean(locationId, lat, lon);
				locations.add(lb);
			}
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	public void insertOrderStatus(String id, int status) {
		PreparedStatement prep = null;
		try {
			prep = conn.prepareStatement(
					"INSERT INTO order_status(order_id, status) VALUES(?, ?)"
					);
			prep.setString(1, id);
			prep.setInt(2, status);
			prep.executeUpdate();
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getOrderByStatus(int status) {
		PreparedStatement prep = null;
		List<String> orderIDs = new ArrayList<>();
		try {
			prep = conn.prepareStatement(
					"SELECT * FROM order_status WHERE status = ?"
					);
			prep.setInt(1, status);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				String orderId = rs.getString(1);
				orderIDs.add(orderId);
			}
			prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderIDs;
	}
	
}
