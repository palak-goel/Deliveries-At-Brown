package edu.brown.cs.mhasan3.rankers;

import java.util.Comparator;

public class SuggestionQueue implements Comparator<SuggestionItem> {

  /**
   * Constructor for SuggestionQueue.
   */
  public SuggestionQueue() {

  }

  @Override
  public int compare(SuggestionItem o1, SuggestionItem o2) {
    return (-1) * Double.compare(o1.getRank(), o2.getRank());
  }

}
