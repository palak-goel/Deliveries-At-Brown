package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;

/**
 * UserProxy models a User if it has not been read in from the database.
 * 
 * @author jacksonchaiken
 *
 */
class UserProxy extends DeliveryObjectProxy<User> implements User {

	UserProxy(String id) {
		super(id);
	}

	@Override
	public String getName() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().getName();
	}

	@Override
	public Collection<Order> pastDeliveries() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().pastDeliveries();
	}

	@Override
	public Collection<Order> currentDeliveries() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().currentDeliveries();
	}

	@Override
	public Collection<Order> pastOrders() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().pastOrders();
	}

	@Override
	public Collection<Order> currentOrders() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().currentOrders();
	}

	@Override
	public void addPastDelivery(Order order) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addPastDelivery(order);
	}

	@Override
	public void addPastOrder(Order order) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addPastOrder(order);
	}

	@Override
	public boolean validatePassword(String password) {
		check();
		if (super.getData() == null) {
			return false;
		}
		return super.getData().validatePassword(password);
	}

	@Override
	public String getWebId() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().getWebId();
	}

	@Override
	public void addCurrentOrder(Order order) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addCurrentOrder(order);
	}

	@Override
	public void addCurrentDelivery(Order order) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addCurrentDelivery(order);
	}

	@Override
	public String getStripeId() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().getStripeId();
	}

	@Override
	public String getCell() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().getCell();
	}

	@Override
	public void setStripeId(String id) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().setStripeId(id);
	}

	@Override
	public AccountStatus status() {
		check();
		if (super.getData() == null) {
			return null;
		}
		return super.getData().status();
	}

	@Override
	public void setAccountStatus(AccountStatus status) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().setAccountStatus(status);
	}

	private static final String CACHE_Q = "SELECT * FROM users WHERE id = ?";
	private static final String ORDER_Q = "SELECT id FROM orders" + " WHERE deliverer_id = ? OR orderer_id = ?";
	private static final String STATUS_Q = "SELECT * FROM account_status WHERE" + " user_id = ?";
	private static final String RATINGS_Q = "SELECT rating, user_type FROM user_ratings WHERE user_id = ?";

	@Override
	protected void cache() throws SQLException {
		try (PreparedStatement cachePrep = Database.getConnection().prepareStatement(CACHE_Q)) {
			cachePrep.setString(1, super.getId());
			try (ResultSet rs = cachePrep.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString(2);
					String cell = rs.getString(3);
					int pass = rs.getInt(4);
					String stripe = rs.getString(5);
					String url = rs.getString(6);
					UserBuilder bean = new UserBuilder();
					try (PreparedStatement statusPrep = Database.getConnection().prepareStatement(STATUS_Q)) {
						statusPrep.setString(1, super.getId());
						try (ResultSet status = statusPrep.executeQuery()) {
							if (status.next()) {
								bean.setStatus(AccountStatus.valueOf(status.getInt(2)));
							}
						}
					}
					List<Double> oRatings = new ArrayList<>();
					List<Double> dRatings = new ArrayList<>();
					try (PreparedStatement ratePrep = Database.getConnection().prepareStatement(RATINGS_Q)) {
						ratePrep.setString(1, super.getId());
						try (ResultSet ratings = ratePrep.executeQuery()) {
							while (ratings.next()) {
								if (ratings.getString(2).equals("orderer")) {
									oRatings.add(ratings.getDouble(1));
								} else {
									dRatings.add(ratings.getDouble(1));
								}
							}
						}
					}
					User newUser = bean.setCell(cell).setPayment(stripe).setId(super.getId()).setName(name)
							.setPersonalUrl(url).setPassword(pass).setDelivererRatings(dRatings)
							.setOrdererRatings(oRatings).build();
					try (PreparedStatement orderPrep = Database.getConnection().prepareStatement(ORDER_Q)) {
						orderPrep.setString(1, super.getId());
						orderPrep.setString(2, super.getId());
						try (ResultSet orders = orderPrep.executeQuery()) {
							while (orders.next()) {
								Order order = Order.byId(orders.getString(1));
								if (order.getDeliverer().getId().equals(super.getId())) {
									if (order.status() == OrderStatus.COMPLETED) {
										newUser.addPastDelivery(order);
									} else {
										newUser.addCurrentDelivery(order);
									}
								} else {
									if (order.status() == OrderStatus.COMPLETED) {
										newUser.addPastOrder(order);
									} else {
										newUser.addCurrentOrder(order);
									}
								}
							}
						}
					}
					assert newUser != null;
					super.setData(newUser);
				}
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
	}

	@Override
	public void pay(double amount) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().pay(amount);
	}

	@Override
	public void charge(double amount) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().charge(amount);
	}

	@Override
	public void addToDatabase() {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addToDatabase();
	}

	@Override
	public void removeFromDatabase() {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().removeFromDatabase();

	}

	private static final String VAL = "SELECT * FROM users WHERE id = ?" + " AND password = ?";

	/**
	 * Returns true or false if an email password combination exists.
	 * 
	 * @param email
	 *            the potential account email.
	 * @param password
	 *            the potential account's password.
	 * @return true or false whether it exists or not.
	 */
	public static boolean userValidator(String email, String password) {
		int hash = password.hashCode();
		try (PreparedStatement prep = Database.getConnection().prepareStatement(VAL)) {
			prep.setString(1, email);
			prep.setInt(2, hash);
			try (ResultSet rs = prep.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public void addDeliveryPreferences(double distance, double minimumFee, double maximumTime) {
		// TODO Auto-generated method stub
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addDeliveryPreferences(distance, minimumFee, maximumTime);

	}

	@Override
	public double getDeliveryDistancePreference() {
		check();
		if (super.getData() == null) {
			return -1;
		}
		return super.getData().getDeliveryDistancePreference();
	}

	@Override
	public double getDeliveryFeePreference() {
		check();
		if (super.getData() == null) {
			return -1;
		}
		return super.getData().getDeliveryFeePreference();

	}

	@Override
	public double getDeliveryTimePreference() {
		check();
		if (super.getData() == null) {
			return 5;
		}
		return super.getData().getDeliveryTimePreference();

	}

	@Override
	public double getDelivererRating() {
		check();
		if (super.getData() == null) {
			return 5;
		}
		return super.getData().getDelivererRating();
	}

	@Override
	public double getOrdererRating() {
		check();
		if (super.getData() == null) {
			return 5;
		}
		return super.getData().getOrdererRating();
	}

	@Override
	public void addOrdererRating(double rating) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addOrdererRating(rating);
	}

	@Override
	public void addDelivererRating(double rating) {
		check();
		if (super.getData() == null) {
			return;
		}
		super.getData().addDelivererRating(rating);
	}

	private static final String PASS_RESET = "UPDATE users SET password = ?" + " WHERE id = ?";

	/**
	 * Resets the password of a given user, updating the database and returning
	 * the new user.
	 * 
	 * @param id
	 *            the account id.
	 * @param oldPassword
	 *            The existing password.
	 * @param newPassword
	 *            the new password.
	 * @return The new user, or null if the old password is incorrect.
	 */
	protected static boolean resetPassword(String id, String oldPassword, String newPassword) {
		int pass = newPassword.hashCode();
		if (!User.byId(id).validatePassword(oldPassword)) {
			return false;
		}
		try (PreparedStatement prep = Database.getConnection().prepareStatement(PASS_RESET)) {
			prep.setInt(1, pass);
			prep.setString(2, id);
			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		// ensure we will get proper update in new bean
		DeliveryObjectProxy.getCache().invalidate(id);
		return true;
	}

	@Override
	public User setPendingUpdate() {
		super.getCache().invalidate(super.getId());
		return User.byId(super.getId());
	}

	private static final String EXIST_Q = "SELECT * FROM users, account_status"
			+ " WHERE users.id = ? AND account_status.status = 0";

	/**
	 * Returns true if the account already exists.
	 * 
	 * @param id
	 *            the id in question
	 * @return a boolean value tied to account status.
	 */
	protected static boolean accountExists(String id) {
		// check database
		try (PreparedStatement prep = Database.getConnection().prepareStatement(EXIST_Q)) {
			prep.setString(1, id);
			try (ResultSet rs = prep.executeQuery()) {
				if (rs.next()) {
					return true;
				}
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private static final String URL_Q = "SELECT id FROM users WHERE url = ?";

	protected static User byWebId(String id) {
		try (PreparedStatement prep = Database.getConnection().prepareStatement(URL_Q)) {
			prep.setString(1, id);
			try (ResultSet rs = prep.executeQuery()) {
				if (rs.next()) {
					return User.byId(rs.getString(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String NEW_P = "UPDATE users" + " SET password = ? WHERE id = ?";

	protected static boolean newPassword(String email, String password) {
		if (accountExists(email)) {
			try (PreparedStatement prep = Database.getConnection().prepareStatement(NEW_P)) {
				prep.setInt(1, password.hashCode());
				prep.setString(2, email);
				prep.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
}
