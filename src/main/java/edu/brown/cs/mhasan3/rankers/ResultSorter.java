package edu.brown.cs.mhasan3.rankers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Regular Sort method for the results.
 *
 * @author mhasan3
 *
 */
public class ResultSorter implements Comparator<String>, Serializable {
  private static final long serialVersionUID = -2001941223932527041L;
  private String word;
  private String wordBefore;
  private HashMap<String, Integer> uni;
  private HashMap<String, Integer> big;
  private boolean length;

  /**
   * Constructor sets up instance variables.
   *
   * @param unigram
   *          from corpus
   * @param bigram
   *          from corpus
   */
  public ResultSorter(HashMap<String, Integer> unigram,
      HashMap<String, Integer> bigram) {
    word = null;
    wordBefore = null;
    uni = unigram;
    big = bigram;
    length = true;
  }

  /**
   * Sets the boolean for if only one word is corrected.
   */
  public void single() {
    length = false;
  }

  /**
   * Sets the boolean for if a sentence was inputed.
   */
  public void sentence() {
    length = true;
  }

  /**
   * Sets the input word to be corrected.
   *
   * @param inpWord
   *          input
   */
  public void setWord(String inpWord) {
    word = inpWord;
  }

  /**
   * Sets the word before the input word for use in bigrams.
   *
   * @param inpWordBefore
   *          input
   */
  public void setWordBefore(String inpWordBefore) {
    wordBefore = inpWordBefore;
  }

  /**
   * Sets the unigram hashmap.
   *
   * @param unigram
   *          passed in
   */
  public void setUni(HashMap<String, Integer> unigram) {
    uni = unigram;
  }

  /**
   * Sets the bigram hashmap.
   *
   * @param bigram
   *          passed in
   */
  public void setBig(HashMap<String, Integer> bigram) {
    big = bigram;
  }

  @Override
  public int compare(String o1, String o2) {
    final String[] one = o1.split(" ");
    final String[] two = o2.split(" ");
    if (one[0].equals(word) && one.length == 1) {
      return 1;
    }
    if (two[0].equals(word) && two.length == 1) {
      return -1;
    }
    if (length) {
      if (big.containsKey(wordBefore + " " + one[0])
          && !big.containsKey(wordBefore + " " + two[0])) {
        return 1;
      }
      if (!big.containsKey(wordBefore + " " + one[0])
          && big.containsKey(wordBefore + " " + two[0])) {
        return -1;
      }
      if (big.containsKey(wordBefore + " " + one[0])
          && big.containsKey(wordBefore + " " + two[0])) {
        if (big.get(wordBefore + " " + one[0]) > big
            .get(wordBefore + " " + two[0])) {
          return 1;
        }
        if (big.get(wordBefore + " " + one[0]) < big
            .get(wordBefore + " " + two[0])) {
          return -1;
        }
      }

    }
    if (uni.containsKey(one[0]) && !uni.containsKey(two[0])) {
      return 1;
    }
    if (!uni.containsKey(one[0]) && uni.containsKey(two[0])) {
      return -1;
    }
    if (uni.containsKey(one[0]) && uni.containsKey(two[0])) {
      if (uni.get(one[0]) > uni.get(two[0])) {
        return 1;
      }
      if (uni.get(one[0]) < uni.get(two[0])) {
        return -1;
      }
    }
    return two[0].compareTo(one[0]);

  }

}
