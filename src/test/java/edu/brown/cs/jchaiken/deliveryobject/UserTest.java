package edu.brown.cs.jchaiken.deliveryobject;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;

public class UserTest {

  @Test
  public void testAccountStatus() {
    assert AccountStatus.valueOf(0).ordinal() == AccountStatus.ACTIVE.ordinal();
    assert AccountStatus.valueOf(1).ordinal() == AccountStatus.CLOSED.ordinal();
    assert AccountStatus.CLOSED.ordinal() == 1;
    assert AccountStatus.ACTIVE.ordinal() == 0;
  }

  @Test
  public void testGoodIdNotNull() {
    assert User.byId("hey") != null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNulLId() {
    User.byId(null);
  }

  @Test
  public void testGoodValidator() {
    Database.setUrl("data/test.sqlite3");
    assert User.userValidator("jackson_chaiken@brown.edu", "password");
  }

  @Test
  public void testBadValidator() {
    assert !User.userValidator("test", "test");
    Database.setUrl("data/test.sqlite3");
    assert !User.userValidator("test2", "test2");
  }
}
