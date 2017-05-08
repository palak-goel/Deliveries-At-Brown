package edu.brown.cs.mhasan3.rankers;

/**
 * Class describes an item that can be suggested for the user making an order or
 * setting their preferences.
 *
 * @author shehryarhasan
 *
 */
public class SuggestionItem {

  private final String ident;
  private double rank;

  /**
   * Constructor takes in an id for the item.
   *
   * @param id
   *          name of item
   */
  public SuggestionItem(String id) {
    ident = id;
    rank = 0;
  }

  /**
   * Sets the Rank of the item.
   *
   * @param tem
   *          Double for new rank
   */
  public void setRank(double tem) {
    rank = tem;
  }

  /**
   * Returns the name of the item.
   *
   * @return item
   */
  public String getId() {
    return ident;
  }

  /**
   * Returns the rank of the item.
   *
   * @return Double for the rank.
   */
  public double getRank() {
    return rank;
  }

}
