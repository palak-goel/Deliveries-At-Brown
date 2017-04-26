package edu.brown.cs.mhasan3.rankers;

import java.util.Comparator;

import edu.brown.cs.jchaiken.deliveryobject.Order;

public class RankerQueue implements Comparator<Order> {

  /**
   * Constructor for RankerQueue
   */
  public RankerQueue() {

  }

  @Override
  public int compare(Order o1, Order o2) {
    return Double.compare(o1.getRanking(), o2.getRanking());
  }

}
