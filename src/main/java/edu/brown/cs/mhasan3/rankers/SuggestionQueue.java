package edu.brown.cs.mhasan3.rankers;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Describes the comparator for the queue for Suggestion items.
 *
 * @author shehryarhasan
 *
 */
public class SuggestionQueue implements Comparator<SuggestionItem>,
    Serializable {
  private static final long serialVersionUID = -1579786484003000132L;

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
