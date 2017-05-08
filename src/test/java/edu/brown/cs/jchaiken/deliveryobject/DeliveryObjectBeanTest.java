package edu.brown.cs.jchaiken.deliveryobject;

import org.junit.Test;

public class DeliveryObjectBeanTest {
  @Test
  public void testNotNull() {
    final DeliveryObjectBean<Location> test = new LocationBean("test", 0, 0,
        "");
    assert test != null;
  }

  @Test
  public void testGetId() {
    final DeliveryObjectBean<Location> test = new LocationBean("test", 0, 0,
        "");
    assert test.getId().equals("test");
  }

  @Test
  public void testEquals() {
    final DeliveryObjectBean<Location> test = new LocationBean("test", 0, 0,
        "");
    final DeliveryObjectBean<Location> test1 = new LocationBean("test", 0, 0,
        "");
    final DeliveryObjectBean<Location> test2 = new LocationBean("test2", 0, 0,
        "");
    assert test.equals(test1);
    assert !test.equals(1);
    assert !test.equals(test2);
    assert test2.equals(test2);
  }
}
