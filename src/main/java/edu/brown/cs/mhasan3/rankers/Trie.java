package edu.brown.cs.mhasan3.rankers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Creates a Trie.
 *
 * @author mhasan3
 *
 */

public class Trie {
  private TrieNode root;
  private int size;
  private Levenshtein calc;

  /**
   * Constructor sets up instance variables.
   */
  public Trie() {
    root = new TrieNode('0');
    size = 1;
    calc = new Levenshtein();
  }

  /**
   * Adds a word to the trie.
   *
   * @param wor
   *          word
   * @return word
   */
  public boolean addWord(String wor) {
    TrieNode current = root;
    boolean newWord = false;
    for (int i = 0; i < wor.length(); i++) {
      char c = (wor.charAt(i));
      if (!current.checkChild(c)) {
        newWord = true;
        current.addChild(c);
        size++;
      }
      current = current.getChild(c);
    }
    if (!current.getEnd()) {
      current.setEnd();
      current.setWord(wor);
      newWord = true;
    }
    return newWord;
  }

  /**
   * Gets the size of the trie.
   *
   * @return size
   */
  public int getSize() {
    return size;
  }

  /**
   * Gets the LED of the trie.
   *
   * @param x
   *          led
   */
  public void changeLED(int x) {
    calc.updateLED(x);
  }

  /**
   * traverses the trie to find completions of words.
   *
   * @param word
   *          word
   * @return word
   */
  public List<String> traverseTrie(String word) {
    List<String> results = new ArrayList<String>();
    TrieNode curr = root;
    int dep = 0;
    for (int i = 0; i < word.length(); i++) {
      if (curr.checkChild(word.charAt(i))) {
        dep++;
        curr = curr.getChild(word.charAt(i));
      }
    }
    if (dep != word.length()) {
      return results;
    } else {

      Stack<TrieNode> stack = new Stack<TrieNode>();
      stack.add(curr);
      while (!stack.isEmpty()) {
        TrieNode nod = stack.pop();
        if (nod.getWord() != "") {
          results.add(nod.getWord());
        }
        HashMap<Character, TrieNode> ma = nod.getList();
        if (ma.size() != 0) {
          stack.addAll(ma.values());
        }
      }

      return results;
    }
  }

}
