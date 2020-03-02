package com.evaluation.warehouse.models;

public class ItemPartition {
	private long 	quantity;
	private long 	price;
	private long 	remainder;
	private long	volume;
	
	public ItemPartition(long quantity, long price,int number) {
		this.price 		= price;
		this.quantity 	= quantity/number;
		this.remainder 	= quantity%number;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getRemainder() {
		return remainder;
	}

	public void setRemainder(int remainder) {
		this.remainder = remainder;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}
	
	public void updateQuantityVolume(long quantity) {
		this.quantity = quantity;
		volume = price*quantity;
	}
}

