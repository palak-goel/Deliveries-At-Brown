package edu.brown.cs.mhasan3.rankers;

public class SuggestionItem {

  private final String ident;
  private double rank;

  public SuggestionItem(String id) {
    ident = id;
    rank = 0;
  }

  public void setRank(double tem) {
    rank = tem;
  }

  public String getID() {
    return ident;
  }

  public double getRank() {
    return rank;
  }

}
