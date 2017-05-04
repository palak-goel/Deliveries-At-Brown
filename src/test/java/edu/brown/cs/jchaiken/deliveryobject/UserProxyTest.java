package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;

public class UserProxyTest {
  @Test
  public void testNotNull() {
    assert new UserProxy("hey") != null;
  }

  @Test
  public void testGoodProxy() {
    Database.setUrl("data/test.sqlite3");
    UserProxy test = new UserProxy("palak_goel@brown.edu");
    assert test.getCell() != null;
    assert test.getId().equals("palak_goel@brown.edu");
    assert test.getStripeId().equals("palak");
    assert test.getName().equals("Palak Goel");
  }

  @Test
  public void testBadProxy() {
    Database.setUrl("data/test.sqlite3");
    UserProxy test = new UserProxy("d@b");
    assert test.getName() == null;
    assert test.getStripeId() == null;
    assert test.getCell() == null;
    assert test.getId().equals("d@b");
  }

  @Test
  public void testGoodProxyHistory() {
    Database.setUrl("data/test.sqlite3");
    UserProxy test = new UserProxy("palak_goel@brown.edu");
    assertEquals(test.currentOrders().size(), 0);
    assertEquals(test.currentDeliveries().size(), 0);
    assertEquals(test.pastOrders().size(), 1);
    assertEquals(test.pastDeliveries().size(), 1);
  }

  @Test
  public void testBadProxyHistory() {
    Database.setUrl("data/test.sqlite3");
    UserProxy test = new UserProxy("test");
    assert test.pastDeliveries() == null;
    assert test.pastOrders() == null;
    assert test.currentDeliveries() == null;
    assert test.currentOrders() == null;
  }

  @Test
  public void testAccountStatus() {
    Database.setUrl("data/test.sqlite3");
    assert User.byId("jackson_chaiken@brown.edu").status()
        == AccountStatus.ACTIVE;
  }

  @Test
  public void testRatings() {
    Database.setUrl("data/test.sqlite3");
    assertEquals(User.byId("jackson_chaiken@brown.edu").getDelivererRating(), 5.0,.1);
    assert User.byId("jackson_chaiken@brown.edu").getOrdererRating() == 4.25;
    assert User.byId("malik_shehyrar_hasan@brown.edu").getOrdererRating() == -1;
    assertEquals(User.byId("palak_goel@brown.edu").getOrdererRating(), 4.75, .1);
  }

  @Test
  public void testRatingsNotInDb() {
    Database.setUrl("data/test.sqlite3");
    assert User.byId("").getDelivererRating() == -1;
    assert User.byId("").getOrdererRating() == -1;
  }
}
