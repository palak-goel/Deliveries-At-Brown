package edu.brown.cs.mhasan3.rankers;

import java.io.Serializable;
import java.util.Comparator;

import edu.brown.cs.jchaiken.deliveryobject.Order;

/**
 * Class describes comparator for priority queue.
 *
 * @author shehryarhasan
 *
 */
public class RankerQueue implements Comparator<Order>, Serializable {
  private static final long serialVersionUID = 732680044738249301L;

  /**
   * Constructor for RankerQueue.
   */
  public RankerQueue() {

  }

  @Override
  public int compare(Order o1, Order o2) {
    return Double.compare(o1.getRanking(), o2.getRanking());
  }

}
