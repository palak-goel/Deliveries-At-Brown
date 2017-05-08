package edu.brown.cs.mhasan3.rankers;

import java.util.HashMap;

/**
 * Defines a node of the trie.
 *
 * @author mhasan3
 *
 */
public class TrieNode {
  private final HashMap<Character, TrieNode> children;
  private boolean end;
  private String word;
  private final char character;

  /**
   * Constructor sets up instance variables.
   *
   * @param curr
   *          character.
   */
  public TrieNode(char curr) {
    children = new HashMap<>();
    end = false;
    character = curr;
    word = "";
  }

  /**
   * Sets the end of the word.
   */
  public void setEnd() {
    end = true;
  }

  /**
   * returns the HashMap of children.
   *
   * @return children
   */
  public HashMap<Character, TrieNode> getList() {
    return children;
  }

  /**
   * Sets the word.
   *
   * @param wor
   *          word
   */
  public void setWord(String wor) {
    word = wor;
  }

  /**
   * Returns the word.
   *
   * @return word
   */
  public String getWord() {
    return word;
  }

  /**
   * Gets the character.
   *
   * @return character
   */
  public char getCharacter() {
    return character;
  }

  /**
   * Gets the end.
   *
   * @return end
   */
  public boolean getEnd() {
    return end;
  }

  /**
   * Adds the child.
   *
   * @param c
   *          child
   */
  public void addChild(char c) {
    final TrieNode temp = new TrieNode(c);
    children.put(c, temp);
  }

  /**
   * Checks if the child is present.
   *
   * @param c
   *          child
   * @return key.
   */
  public boolean checkChild(char c) {
    return children.containsKey(c);
  }

  /**
   * Returns the child.
   *
   * @param c
   *          char
   * @return child
   */
  public TrieNode getChild(char c) {
    return children.get(c);
  }
}
