package com.evaluation.storage.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatafileMapper {

	private Map<String,DatafileTracker> trackerMap;
	
	public DatafileMapper() {
		super();
		trackerMap = new HashMap<String,DatafileTracker>();
	}

	
	
	public Map<String, DatafileTracker> getTrackerMap() {
		return trackerMap;
	}



	public void addFileList(List<String> listFiles) {
		listFiles.stream().forEach(file -> addTracker(file) );
	}
	
	public void addTracker(String filename) {
		if ( !trackerMap.containsKey(filename) ) {
			DatafileTracker datafileTracker = new DatafileTracker(filename);
			trackerMap.put(filename,datafileTracker);
		}
	}
	
	public void updateTracker(DatafileMessage datafileMessage) {
		String filename = datafileMessage.getFilename();
		trackerMap.get(filename).update(datafileMessage.getBytesConsumed(),datafileMessage.isFinished());
	}
	
	public boolean accept(DatafileMessage datafileMessage) {
		String filename = datafileMessage.getFilename();
		
		// refuse if file doesnt exist
		if ( trackerMap.containsKey(filename)) {
			DatafileTracker datafileTracker = trackerMap.get(filename);
			
			// refuse if file is finished
			if ( datafileTracker.isFinished() )
				return false;

			return true;
		}
		
		return false;
	}
}
