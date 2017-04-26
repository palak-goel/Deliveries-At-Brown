package edu.brown.cs.mhasan3.rankers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuggestionItemTest {

  @Test
  public void testBasicFunctionality() {
    SuggestionItem ite = new SuggestionItem("Item");
    assertEquals(ite.getID(), "Item");
    assertEquals((int) ite.getRank(), 0);
  }

  @Test
  public void testSetterGetterFunctionality() {
    SuggestionItem ite = new SuggestionItem("Item");
    ite.setRank(100.0);
    assertEquals(ite.getID(), "Item");
    assertEquals((int) ite.getRank(), 100);
  }

}
