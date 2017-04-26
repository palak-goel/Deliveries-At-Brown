package edu.brown.cs.mhasan3.rankers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import edu.brown.cs.mhasan3.CSVReader;

/**
 * Handles the commands associated with AutoCorrect.
 *
 * @author mhasan3
 *
 */

public class CommandCorrect {

  private boolean pref;
  private int led;
  private HashMap<String, Integer> frequency;
  private HashMap<String, Integer> bigram;
  private Trie trie;
  private Levenshtein lev;
  private boolean corpus;

  /**
   * Constructor sets up instance variables.
   */
  public CommandCorrect() {
    pref = true;
    corpus = false;
    led = 2;
    frequency = new HashMap<String, Integer>();
    bigram = new HashMap<String, Integer>();
    trie = new Trie();
    lev = new Levenshtein();
  }

  /**
   * Done for maps project.
   *
   * @param words
   *          words
   */
  public void addList(List<String> words) {
    for (int i = 0; i < words.size(); i++) {
      String wor = words.get(i);
      wor = wor.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
      trie.addWord(wor);

    }
    corpus = true;
  }

  /**
   * Finds the frequency of every two words.
   *
   * @param words
   *          of file
   */
  public void findBigrams(List<String> words) {
    for (int i = 0; i < words.size() - 1; i++) {
      String ar = words.get(i);
      ar += " ";
      ar += words.get(i + 1);
      if (!bigram.containsKey(ar)) {
        bigram.put(ar, 1);
      } else {
        bigram.put(ar, bigram.get(ar) + 1);
      }
    }
  }

  /**
   * Generates the suggestions for the ac command.
   *
   * @param inputs
   *          given in cli
   * @return suggestions
   */
  public List<String> combineResults(String[] inputs) {
    if (!corpus) {
      List<String> results = new ArrayList<String>();
      // System.out.println("not getting res");
      return results;
    } else {
      // System.out.println("getting res");
      List<String> results = new ArrayList<String>();
      int siz = inputs.length;
      if (siz <= 1) {
        return results;
      }
      String toCheck = inputs[siz - 1];
      toCheck = toCheck.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
      if (pref) {
        List<String> temp = this.checkPrefix(toCheck);
        results.addAll(temp);
      }
      List<String> temp = this.checkLED(toCheck);
      results.addAll(temp);
      Set<String> t = new HashSet<>();
      t.addAll(results);
      results.clear();
      results.addAll(t);
      if (inputs.length == 2) {
        ResultSorter sorter = new ResultSorter(frequency, bigram);
        sorter.single();
        sorter.setWord(toCheck);
        Collections.sort(results, sorter);
        Collections.reverse(results);
        List<String> finRes = new ArrayList<String>();
        for (int j = 0; j < results.size(); j++) {
          String output = results.get(j).trim();
          output.trim();
          if (j < 5) {
            // System.out.println(output);
            finRes.add(output);
          }
        }
        return finRes;
      } else {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < inputs.length - 1; i++) {
          builder.append(inputs[i]);
          if (i != inputs.length - 2) {
            builder.append(" ");
          }
        }
        String te = builder.toString();
        te = te.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
        te = te.replaceAll("\\s+", " ");
        te.trim();
        ResultSorter sorter = new ResultSorter(frequency, bigram);
        sorter.single();
        sorter.setWord(toCheck);
        Collections.sort(results, sorter);
        Collections.reverse(results);
        HashMap<String, Integer> noRepeat = new HashMap<String, Integer>();
        List<String> finRes = new ArrayList<String>();
        int sizRep = 0;
        for (int j = 0; j < results.size(); j++) {
          String output = te + " " + results.get(j).trim();
          output.trim();
          if (!noRepeat.containsKey(output)) {
            noRepeat.put(output, 1);
          } else {
            noRepeat.put(output, noRepeat.get(output) + 1);
            sizRep += 1;
          }
          if ((j < 5 + sizRep) && (noRepeat.get(output) == 1)) {
            // System.out.println(output);
            finRes.add(output);
          }
        }
        return finRes;
      }
    }
  }

  /**
   * Returns the hashmap for two words.
   *
   * @return map
   */
  public HashMap<String, Integer> getBigram() {
    return bigram;
  }

  /**
   * Returns the results for Levenshtein edit distance.
   *
   * @param inp
   *          input
   * @return LED
   */
  public List<String> checkLED(String inp) {
    List<String> res = new ArrayList<String>();
    for (String key : frequency.keySet()) {
      if (lev.findEditDistance(inp, key) <= led) {
        res.add(key);
      }
    }
    return res;
  }

  /**
   * Returns the results for prefix.
   *
   * @param inp
   *          input
   * @return prefix
   */
  public List<String> checkPrefix(String inp) {
    List<String> res = new ArrayList<String>();
    res = trie.traverseTrie(inp);
    return res;
  }

  /**
   * Returns the trie.
   *
   * @return trie
   */
  public Trie getTrie() {
    return trie;
  }

  /**
   * Returns the hashmap for words.
   *
   * @return words
   */
  public HashMap<String, Integer> getMap() {
    return frequency;
  }

  /**
   * Changes the prefix.
   *
   * @param inp
   *          words
   */
  public void prefixChange(String[] inp) {
    switch (inp[1]) {
    case "on":
      pref = true;
      break;
    case "off":
      pref = false;
      break;
    default:
      System.out.println("ERROR: Invalid Command");
      break;
    }
  }

  /**
   * Changes the levenshtein edit distance.
   *
   * @param inp
   *          input
   */
  public void ledChange(String[] inp) {
    int newLed = led;
    try {
      newLed = Integer.parseInt(inp[1]);
    } catch (NumberFormatException e) {
      System.out.println("ERROR: INVALID INPUT, NOT A NUMBER");
    }
    led = newLed;
  }

  /**
   * Returns whether prefix is on.
   *
   * @param inp
   *          command
   * @return boolean
   */
  public String prefixCheck(String[] inp) {
    String str;
    if (!pref) {
      str = "prefix" + " " + "off";
    } else {
      str = "prefix" + " " + "on";
    }
    return str;
  }

  /**
   * Returns the current Levenshtein edit distance.
   *
   * @param inp
   *          input
   * @return integer
   */
  public String ledCheck(String[] inp) {
    String str;
    str = "led" + " " + led;
    return str;
  }
}
