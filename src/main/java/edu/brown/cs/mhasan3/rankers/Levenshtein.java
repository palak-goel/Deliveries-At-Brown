package edu.brown.cs.mhasan3.rankers;

import java.util.Locale;

/**
 * Class describes the Levenshtein edit distance function.
 *
 * @author mhasan3
 *
 */
public class Levenshtein {

  private int currLED;

  /**
   * Constructor sets up instance variables.
   */
  public Levenshtein() {
    currLED = 0;
  }

  /**
   * Updates the LED.
   *
   * @param n
   *          int.
   */
  public void updateLED(int n) {
    currLED = n;
  }

  /**
   * Returns the LED.
   *
   * @return LED.
   */
  public int getLED() {
    return currLED;
  }

  /**
   * Returns if the LED is off.
   *
   * @return boolean
   */
  public boolean off() {
    if (currLED == 0) {
      return true;
    }
    return false;
  }

  /**
   * Finds the edit distance between two words.
   *
   * @param one
   *          string
   * @param two
   *          string
   * @return integer
   */
  public int findEditDistance(String one, String two) {
    one = one.toLowerCase(Locale.getDefault());
    two = two.toLowerCase(Locale.getDefault());
    int dist = 0;
    int subs = 0;
    final String str1 = " ".concat(one);
    final String str2 = " ".concat(two);
    final int m = str1.length();
    final int n = str2.length();
    final int[][] arr = new int[m][n];
    for (int i = 0; i < n; i++) {
      arr[0][i] = i;
    }
    for (int i = 0; i < m; i++) {
      arr[i][0] = i;
    }
    for (int i = 1; i < m; i++) {
      for (int j = 1; j < n; j++) {
        if (str1.charAt(i) == str2.charAt(j)) {
          subs = 0;
        } else {
          subs = 1;
        }
        arr[i][j] = Math.min(arr[i - 1][j - 1] + subs,
            Math.min(arr[i - 1][j] + 1, arr[i][j - 1] + 1));
      }
    }
    dist = arr[m - 1][n - 1];
    return dist;
  }
}
