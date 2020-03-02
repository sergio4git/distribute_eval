package com.evaluation.dataloader.models;

import java.util.ArrayList;
import java.util.List;

public class DatafileMessage {

	private String filename;
	private long bytesConsumed;
	private List<Item> listItems;
	private boolean finished;
	
	public DatafileMessage() {
	}

	public DatafileMessage(String filename) {
		this.filename = filename;
		this.listItems = new ArrayList<Item>();
		this.finished = false;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getBytesConsumed() {
		return bytesConsumed;
	}

	public void setBytesConsumed(long bytesConsumed) {
		this.bytesConsumed = bytesConsumed;
	}

	public List<Item> getListItems() {
		return listItems;
	}

	public void setListItems(List<Item> listItems) {
		this.listItems = listItems;
	}
	

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void update(List<Item> listItems,long bytesConsumed) {
		this.listItems.clear();
		this.listItems.addAll(listItems);
		this.bytesConsumed += bytesConsumed;
	}
}
