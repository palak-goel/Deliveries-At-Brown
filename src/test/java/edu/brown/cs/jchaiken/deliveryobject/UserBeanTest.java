package edu.brown.cs.jchaiken.deliveryobject;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;
import edu.brown.cs.jchaiken.deliveryobject.User.UserBuilder;

public class UserBeanTest {
  @Test
  public void testNotNull() {
    UserBuilder builder = new UserBuilder();
    assert builder != null;
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setCell("")
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .build();
    assert user != null;
  }

  @Test (expected = IllegalArgumentException.class)
  public void testBuilderException() {
    UserBuilder builder = new UserBuilder();
    User user = builder.build();
  }

  @Test
  public void testGetters() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setCell("")
        .setPassword(1)
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .build();
    assert user.getId().equals("id");
    assert user.getName().equals("name");
    assert user.getStripeId().equals("payment");
  }

  @Test
  public void testPay() {
    //TODO: stripe stuff
  }

  @Test
  public void testCharge() {
    //TODO: stripe stuff
  }

  @Test
  public void testPastDeliveries() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setCell("")
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .build();
    for (int x = 0; x < 1000; x++) {
      user.addPastDelivery(Order.byId(String.valueOf(x)));
    }
    assert user.pastDeliveries().size() == 1000;
  }

  @Test
  public void testPastOrders() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setCell("")
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .build();
    for (int x = 0; x < 1000; x++) {
      user.addPastOrder(Order.byId(String.valueOf(x)));
    }
    assert user.pastOrders().size() == 1000;
  }

  @Test
  public void testCurrOrders() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setCell("")
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    for (int x = 0; x < 1000; x++) {
      user.addCurrentOrder(Order.byId(String.valueOf(x)));
    }
    assert user.currentOrders().size() == 1000;
  }

  @Test
  public void testCurrDeliveriess() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setCell("")
        .setPayment("payment")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    for (int x = 0; x < 1000; x++) {
      user.addCurrentDelivery(Order.byId(String.valueOf(x)));
    }
    assert user.currentDeliveries().size() == 1000;
  }

  @Test
  public void testStatus() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setPayment("payment")
        .setCell("")
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setStatus(AccountStatus.ACTIVE)
        .build();
    assert user.status() == AccountStatus.ACTIVE;
  }

  @Test
  public void testSetAccountStatus() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setPayment("payment")
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setCell("")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    assert user.status() == AccountStatus.ACTIVE;
    user.setAccountStatus(AccountStatus.CLOSED);
    assert user.status() == AccountStatus.CLOSED;
  }

  @Test
  public void testAddAndRemove() {
    Database.setUrl("data/test.sqlite3");
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setPayment("payment")
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setCell("")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    assert user.getCell().equals("");
    user.addToDatabase();
    DeliveryObjectProxy.clearCache();
    UserProxy check = new UserProxy("id");
    assert check.getCell() != null;
    //examine db to verify test (it works!)
    user.removeFromDatabase();
  }

  @Test
  public void testRatings() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setPayment("payment")
        .setDelivererRatings(Arrays.asList(new Double[]{4.0,5.0,4.0,5.0}))
        .setOrdererRatings(Arrays.asList(new Double[]{4.0}))
        .setCell("")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    assert user.getDelivererRating() == 4.5;
    assert user.getOrdererRating() == 4.0;
  }

  @Test
  public void testPreferencesSetandGet() {
    UserBuilder builder = new UserBuilder();
    User user = builder.setCell("")
        .setId("id")
        .setName("name")
        .setPassword(1)
        .setPayment("payment")
        .setDelivererRatings(new ArrayList<Double>())
        .setOrdererRatings(new ArrayList<Double>())
        .setCell("")
        .setStatus(AccountStatus.ACTIVE)
        .build();
    user.addDeliveryPreferences(4.0, 5.0, 6.0);
    assert user.getDeliveryDistancePreference() == 4.0;
    assert user.getDeliveryFeePreference() == 5.0;
    assert user.getDeliveryTimePreference() == 6.0;
  }
}
