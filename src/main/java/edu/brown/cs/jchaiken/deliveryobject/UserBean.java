package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import edu.brown.cs.jchaiken.database.Database;

/**
 * UserBean models a user after it has been read in from the database.
 *
 * @author jacksonchaiken
 *
 */
final class UserBean extends DeliveryObjectBean<User> implements User {
	private final String name;
	transient private Collection<Order> pastDeliveries;
	transient private Collection<Order> pastOrders;
	transient private Collection<Order> currDeliveries;
	transient private Collection<Order> currOrders;
	private String paymentId;
	private final String cell;
	transient private final int password;
	private double distancePref;
	private double feePref;
	private double timePref;
	private List<Double> ordererRatings;
	private List<Double> delivererRatings;
	private AccountStatus status;
	transient private String webId;
	transient private static Set<String> webIds = Collections.synchronizedSet(new HashSet<String>());

	UserBean(String email, String newName, String newPaymentId, String cellNum, Integer newPass,
			AccountStatus newStatus) {
		super(email);
		password = newPass;
		name = newName;
		paymentId = newPaymentId;
		status = newStatus;
		pastDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
		pastOrders = Collections.synchronizedCollection(new HashSet<Order>());
		currDeliveries = Collections.synchronizedCollection(new HashSet<Order>());
		currOrders = Collections.synchronizedCollection(new HashSet<Order>());
		cell = cellNum;
		distancePref = -1;
		feePref = -1;
		ordererRatings = new ArrayList<>();
		delivererRatings = new ArrayList<>();
		timePref = -1;
	}

	void setOrdererRatings(List<Double> oRatings) {
		ordererRatings = oRatings;
	}

	void setDelivererRatings(List<Double> dRatings) {
		delivererRatings = dRatings;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<Order> pastDeliveries() {
		return pastDeliveries;
	}

	@Override
	public Collection<Order> currentDeliveries() {
		return currDeliveries;
	}

	@Override
	public Collection<Order> pastOrders() {
		return pastOrders;
	}

	@Override
	public Collection<Order> currentOrders() {
		return currOrders;
	}

	@Override
	public void addPastOrder(Order order) {
		pastOrders.add(order);
	}

	@Override
	public void addCurrentOrder(Order order) {
		currOrders.add(order);
	}

	@Override
	public void addCurrentDelivery(Order order) {
		currDeliveries.add(order);
	}

	@Override
	public void addPastDelivery(Order order) {
		pastDeliveries.add(order);
	}

	@Override
	public String getStripeId() {
		return paymentId;
	}

	@Override
	public String getCell() {
		return cell;
	}

	@Override
	public void setStripeId(String id) {
		paymentId = id;
	}

	@Override
	public void pay(double amount) {
		// TODO : Stripe stuff
	}

	private static final int CONVERSION = 100;

	@Override
	public void charge(double amount, List<String> items, String pickup, String destination) {
		// TODO : Stripe stuff
		final Map<String, Object> chargeParams = new HashMap<>();
		StringBuffer list = new StringBuffer();
		for (final String item : items) {
			list.append(item + ", ");
		}
		chargeParams.put("amount", (int) (amount * CONVERSION));
		chargeParams.put("currency", "usd");
		chargeParams.put("customer", paymentId);
		chargeParams.put("description",
				"You ordered: " + list.toString() + " from " + pickup + " to be delivered to " + destination);
		chargeParams.put("receipt_email", super.getId());
		Charge charge;
		try {
			charge = Charge.create(chargeParams);
			assert charge.getPaid();
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
				| APIException e) {
			e.printStackTrace();
		}
	}

	private static final String USER_A = "INSERT INTO users VALUES (?,?,?,?,?,?)";
	private static final String STATUS_A = "INSERT INTO account_status VALUES (?,?)";

	@Override
	public void addToDatabase() {
		try (PreparedStatement prep = Database.getConnection().prepareStatement(USER_A)) {
			prep.setString(2, name);
			prep.setString(1, super.getId());
			prep.setString(3, cell);
			prep.setInt(4, password);
			prep.setString(5, paymentId);
			prep.setString(6, getWebId());
			prep.executeUpdate();
		} catch (final SQLException exc) {
			exc.printStackTrace();
		}
		try (PreparedStatement prep = Database.getConnection().prepareStatement(STATUS_A)) {
			prep.setString(1, super.getId());
			prep.setInt(2, status.ordinal());
			prep.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	private static final String USER_R = "DELETE FROM users WHERE id = ?";
	private static final String STATUS_R = "DELETE FROM account_status WHERE user_id = ?";

	@Override
	public void removeFromDatabase() {
		try (PreparedStatement prep = Database.getConnection().prepareStatement(STATUS_R)) {
			prep.setString(1, super.getId());
			prep.executeUpdate();
		} catch (final SQLException exc) {
			exc.printStackTrace();
		}
		try (PreparedStatement prep = Database.getConnection().prepareStatement(USER_R)) {
			prep.setString(1, super.getId());
			prep.executeUpdate();
		} catch (final SQLException exc) {
			exc.printStackTrace();
		}
	}

	@Override
	public AccountStatus status() {
		return status;
	}

	private static final String STATUS_UPDATE = "UPDATE account_status SET status = ? WHERE user_id = ?";

	@Override
	public void setAccountStatus(AccountStatus newStatus) {
		try (PreparedStatement prep = Database.getConnection().prepareStatement(STATUS_UPDATE)) {
			prep.setInt(1, status.ordinal());
			prep.setString(2, super.getId());
			prep.executeUpdate();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		status = newStatus;
	}

	@Override
	public void addDeliveryPreferences(double distance, double minimumFee, double maximumTime) {
		distancePref = distance;
		feePref = minimumFee;
		timePref = maximumTime;
	}

	@Override
	public double getDeliveryDistancePreference() {
		return distancePref;
	}

	@Override
	public double getDeliveryFeePreference() {
		return feePref;
	}

	@Override
	public double getDeliveryTimePreference() {
		return timePref;
	}

	@Override
	public double getDelivererRating() {
		double sum = 0;
		if (delivererRatings.size() == 0) {
			return 5;
		}
		for (final double rating : delivererRatings) {
			sum += rating;
		}
		sum = sum / delivererRatings.size();
		return sum;
	}

	@Override
	public double getOrdererRating() {
		double sum = 0;
		if (ordererRatings.size() == 0) {
			return 5;
		}
		for (final double rating : ordererRatings) {
			sum += rating;
		}
		sum = sum / ordererRatings.size();
		return sum;
	}

	@Override
	public void addOrdererRating(double rating) {
		try (PreparedStatement prep = Database.getConnection()
				.prepareStatement("INSERT INTO user_ratings VALUES(?,?,?)")) {
			prep.setString(1, super.getId());
			prep.setDouble(2, rating);
			prep.setString(3, "orderer");
		} catch (final SQLException exc) {
			exc.printStackTrace();
		}
		ordererRatings.add(rating);
	}

	@Override
	public void addDelivererRating(double rating) {
		try (PreparedStatement prep = Database.getConnection()
				.prepareStatement("INSERT INTO user_ratings VALUES(?,?,?)")) {
			prep.setString(1, super.getId());
			prep.setDouble(2, rating);
			prep.setString(3, "deliverer");
		} catch (final SQLException exc) {
			exc.printStackTrace();
		}
		delivererRatings.add(rating);
	}

	@Override
	public boolean validatePassword(String check) {
		if (check.hashCode() == password) {
			return true;
		}
		return false;
	}

	@Override
	public User setPendingUpdate() {
		DeliveryObjectProxy.getCache().invalidate(super.getId());
		return User.byId(super.getId());
	}

	@Override
	public String getWebId() {
		if (webId == null) {
			webId = UserBean.getNextWebId();
		}
		return webId;
	}

	protected void setWebId(String newId) {
		webId = newId;
	}

	private static final int TEN = 10;
	private static final int LENGTH_1 = 15;
	private static int idLength = LENGTH_1;
	private static double maxSize = Math.pow(TEN, LENGTH_1);

	private static String getNextWebId() {
		StringBuffer id = new StringBuffer();
		checkId();
		for (int x = 0; x < idLength; x++) {
			final int rand = ThreadLocalRandom.current().nextInt(0, TEN);
			id.append(String.valueOf(rand));
		}
		if (webIds.contains(id.toString())) {
			return UserBean.getNextWebId();
		}
		webIds.add(id.toString());
		return id.toString();
	}

	private static final String ID_Q = "SELECT url FROM users";
	private static final double RANGE = .1;

	private static void checkId() {
		if (webIds.size() == 0) {
			try (PreparedStatement prep = Database.getConnection().prepareStatement(ID_Q)) {
				try (ResultSet rs = prep.executeQuery()) {
					while (rs.next()) {
						final String id = rs.getString(1);
						if (id != null && id.length() == idLength) {
							webIds.add(rs.getString(1));
						}
					}
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		if (Math.abs(webIds.size() - maxSize) < RANGE) {
			idLength++;
			maxSize = Math.pow(TEN, idLength);
			webIds = Collections.synchronizedSet(new HashSet<>());
		}
	}
}
