package edu.brown.cs.mhasan3.rankers;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.User;

public class SuggestorTest {

  @Test
  public void test() {
    Database.setUrl("data/test.sqlite3");
    final User user1 = User.byId("/u/1");
    final Suggestor suggest = new Suggestor(user1);
    System.out.println(suggest.suggestItem());
    // assertEquals(suggestItem.size(), 3);
    // assertEquals(rank.rank().get(1).getId(), "/o/2");

  }

}
