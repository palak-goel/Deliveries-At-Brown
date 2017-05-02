package edu.brown.cs.jchaiken.deliveryobject;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.deliveryobject.Order.OrderStatus;
import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

public class OrderBeanTest {
	@Test
	public void testNotNull() {
		OrderBuilder builder = new OrderBuilder();
		Order newOrder = builder.setId("hey").setDeliverer(User.byId("user")).setOrderer(User.byId("user2"))
				.setDropoff(Location.byId("/l/1")).setPickup(Location.byId("/l/2")).setItems(new ArrayList<String>())
				.setDropoffTime(100).setPickupTime(150).setOrderStatus(OrderStatus.COMPLETED).setPrice(100).build();
		assert newOrder != null;
	}

	/*
	 * @Test (expected = IllegalArgumentException.class) public void
	 * testBuildException() { OrderBuilder builder = new OrderBuilder();
	 * builder.build(); }
	 */

	@Test
	public void testGetters() {
		OrderBuilder builder = new OrderBuilder();
		Order newOrder = builder.setId("hey").setDeliverer(User.byId("user")).setOrderer(User.byId("user2"))
				.setDropoff(Location.byId("/l/1")).setPickup(Location.byId("/l/2")).setItems(new ArrayList<String>())
				.setDropoffTime(100).setPickupTime(150).setOrderStatus(OrderStatus.COMPLETED).setPrice(100).build();
		assert newOrder != null;
		assert newOrder.getDeliverer().getId().equals("user");
		assert newOrder.getOrderer().getId().equals("user2");
		assert newOrder.getDropoffLocation().getId().equals("/l/1");
		assert newOrder.getPickupLocation().getId().equals("/l/2");
		assert newOrder.getPrice() == 100;
		assert newOrder.status() == OrderStatus.COMPLETED;
	}

	@Test
	public void testAddAndRemoveDb() {
		Database.setUrl("data/test.sqlite3");
		OrderBuilder builder = new OrderBuilder();
		Order newOrder = builder.setId("hey").setDeliverer(User.byId("jackson_chaiken@brown.edu"))
				.setOrderer(User.byId("palak_goel@brown.edu")).setDropoff(Location.byId("/l/1"))
				.setPickup(Location.byId("/l/2")).setItems(new ArrayList<String>()).setDropoffTime(100)
				.setPickupTime(150).setOrderStatus(OrderStatus.COMPLETED).setPrice(100).build();
		newOrder.addToDatabase();
		// now retrieve record
		Order test = Order.byId("hey");
		assert test.getDeliverer().getId().equals("jackson_chaiken@brown.edu");
		assertEquals(test.status(), OrderStatus.COMPLETED);
		assertEquals(test.getOrderItems().size(), 0);
		test.removeFromDatabase();
		DeliveryObjectProxy.clearCache();
		assert Order.byId("hey").getDeliverer() == null;
	}
}
