package com.evaluation.warehouse.models;

import java.util.List;

public class ItemDistributionResponse {
	private String msg;
	private List<ItemDistribution> listItemDistribution;
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<ItemDistribution> getListItemDistribution() {
		return listItemDistribution;
	}
	public void setListItemDistribution(List<ItemDistribution> listItemDistribution) {
		this.listItemDistribution = listItemDistribution;
	}

	
}
