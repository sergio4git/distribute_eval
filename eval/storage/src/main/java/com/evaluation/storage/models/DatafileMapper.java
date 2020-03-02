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

	public void addFileList(List<String> listFiles) {
		listFiles.stream().forEach(file -> addTracker(file) );
	}
	
	public void addTracker(String filename) {
		if ( !datafileMapper.containsKey(filename) ) {
			DatafileTracker datafileTracker = new DatafileTracker(filename);
			datafileMapper.put(filename,datafileTracker);
		}
	}
	
	public void updateTracker(DatafileTracker datafileTracker) {
		String filename = datafileTracker.getFileName();
		if ( datafileMapper.containsKey(filename) ) {
			datafileMapper.replace(filename, datafileTracker);
		}
	}
}
