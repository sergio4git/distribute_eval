package com.evaluation.warehouse.models;

import java.util.ArrayList;
import java.util.List;

import com.evaluation.warehouse.models.Item;
import com.evaluation.warehouse.models.ItemPartition;

public class ItemDistribution {
	private String 	product;
	private int number;
	private long total;
	private long quantity;
	private double averagePrice;
	
	private List<ItemPartition> listItemPartition;

	
	public ItemDistribution() {
		listItemPartition = new ArrayList<ItemPartition>();
	}
	
	public ItemDistribution(Item item,int number) {
		product = item.getProduct();
		this.number = number;
		ItemPartition itemPartition = new ItemPartition(item.getQuantity(),item.getPrice(),number);
		
		listItemPartition = new ArrayList<ItemPartition>();
		listItemPartition.add(itemPartition);
	}
	
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<ItemPartition> getListItemPartition() {
		return listItemPartition;
	}

	public void setListItemPartition(List<ItemPartition> listItemPartition) {
		this.listItemPartition = listItemPartition;
	}

	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	// Decoration to set decimal place back
	public Double getAveragePriceDouble() {
		return averagePrice/100;
	}
	
	public void addItem(int quantity,long price) {
		ItemPartition it = new ItemPartition(quantity,price,number);
		listItemPartition.add(it);
	}
	
	public void accept(Item item) {
		ItemPartition ItemPartition = new ItemPartition(item.getQuantity(),item.getPrice(),number);
		listItemPartition.add(ItemPartition);
	}
	
	public ItemDistribution combine(ItemDistribution itemDistribution) {
		this.listItemPartition.addAll(itemDistribution.getListItemPartition());
		return this;
	}
	
	/*
	 * Update the quantities
	 */
	public void updateQuantities(long[] newQuantities) {
		for ( int i = 0; i < newQuantities.length ; i ++) 
			listItemPartition.get(i).updateQuantityVolume(newQuantities[i]);
	}
	
	public void updateTotalAverage() {
		total = listItemPartition.stream().mapToLong(ip ->ip.getVolume()).sum();
		quantity = listItemPartition.stream().mapToLong(ip ->ip.getQuantity()).sum();
		try {
			averagePrice = total/quantity;
		} catch (Exception e) {
			averagePrice = 0;
		}
	}
}

