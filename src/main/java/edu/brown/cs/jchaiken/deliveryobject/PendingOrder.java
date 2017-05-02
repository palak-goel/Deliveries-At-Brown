package edu.brown.cs.jchaiken.deliveryobject;

import edu.brown.cs.jchaiken.deliveryobject.OrderBean.OrderBuilder;

public class PendingOrder {

	private User orderer;
	private String pickup;
	private String dropoff;
	private String item;
	private double price;
	private double time;

	// Time
	public PendingOrder(User orderer, String pickup, String dropoff, String item, double price, double time) {
		this.orderer = orderer;
		this.pickup = pickup;
		this.dropoff = dropoff;
		this.item = item;
		this.price = price;
		this.time = time;
	}

	public Order makeOrder(User deliverer, double pickupT, double dropoffT) {
		return new OrderBuilder().build();
	}
}
