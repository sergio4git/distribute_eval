package com.evaluation.storage.models;

public class DatafileTracker {

	private String 	fileName;
	private long	bytesConsumed;
	private boolean	finished;
	
	public DatafileTracker() {
	}

	public DatafileTracker(String filename) {
		this.fileName = filename;
		finished = false;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	
}
