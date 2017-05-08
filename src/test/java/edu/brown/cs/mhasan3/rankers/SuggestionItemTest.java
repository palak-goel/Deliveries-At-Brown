package edu.brown.cs.mhasan3.rankers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuggestionItemTest {

  @Test
  public void testBasicFunctionality() {
    final SuggestionItem ite = new SuggestionItem("Item");
    assertEquals(ite.getId(), "Item");
    assertEquals((int) ite.getRank(), 0);
  }

  @Test
  public void testSetterGetterFunctionality() {
    final SuggestionItem ite = new SuggestionItem("Item");
    ite.setRank(100.0);
    assertEquals(ite.getId(), "Item");
    assertEquals((int) ite.getRank(), 100);
  }

}
