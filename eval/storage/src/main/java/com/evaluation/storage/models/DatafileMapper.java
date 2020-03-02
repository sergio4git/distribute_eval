package com.evaluation.storage.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatafileMapper {

	private Map<String,DatafileTracker> datafileMapper;
	
	public DatafileMapper() {
		super();
		datafileMapper = new HashMap<String,DatafileTracker>();
	}

	
	
	public Map<String, DatafileTracker> getDatafileMapper() {
		return datafileMapper;
	}



	public void addFileList(List<String> listFiles) {
		listFiles.stream().forEach(file -> addTracker(file) );
	}
	
	public void addTracker(String filename) {
		if ( !datafileMapper.containsKey(filename) ) {
			DatafileTracker datafileTracker = new DatafileTracker(filename);
			datafileMapper.put(filename,datafileTracker);
		}
	}
	
	public void updateTracker(DatafileMessage datafileMessage) {
		String filename = datafileMessage.getFilename();
		datafileMapper.get(filename).update(datafileMessage.getBytesConsumed(),datafileMessage.isFinished());
	}
	
	public boolean accept(DatafileMessage datafileMessage) {
		String filename = datafileMessage.getFilename();
		
		// refuse if file doesnt exist
		if ( datafileMapper.containsKey(filename)) {
			DatafileTracker datafileTracker = datafileMapper.get(filename);
			
			// refuse if file is finished
			if ( datafileTracker.isFinished() )
				return false;

			return true;
		}
		
		return false;
	}
}
