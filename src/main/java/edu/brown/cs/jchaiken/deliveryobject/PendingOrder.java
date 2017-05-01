package edu.brown.cs.jchaiken.deliveryobject;

import java.util.List;

import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

public class PendingOrder {

	private User orderer;
	private Location pickup;
	private Location dropoff;
	private List<String> items;
	private double price;

	public PendingOrder(User orderer, Location pickup, Location dropoff, List<String> items, double price) {
		this.orderer = orderer;
		this.pickup = pickup;
		this.dropoff = dropoff;
		this.items = items;
		this.price = price;
	}

	public Order makeOrder(User deliverer, double pickupT, double dropoffT) {
		return new OrderBuilder().build();
	}
}
