package com.evaluation.warehouse.models;

import java.util.List;

import com.evaluation.warehouse.models.Item;

public class ItemWrapper {
	private List<Item> data;
	
	public ItemWrapper() {
		
	}

	public List<Item> getData() {
		return data;
	}

	public void setData(List<Item> data) {
		this.data = data;
	};
}

