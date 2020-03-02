package com.evaluation.storage.models;

public class DatafileTracker {

	private String 	filename;
	private long	bytesConsumed;
	private boolean	finished;
	
	public DatafileTracker() {
	}

	public DatafileTracker(String filename) {
		this.filename = filename;
		finished = false;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String fileName) {
		this.filename = fileName;
	}

	public long getBytesConsumed() {
		return bytesConsumed;
	}

	public void setBytesConsumed(long bytesConsumed) {
		this.bytesConsumed = bytesConsumed;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public void update(long	bytesConsumed,boolean finished) {
		this.bytesConsumed += bytesConsumed;
		this.finished = finished;
	}
}
