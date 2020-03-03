package com.evaluation.storage.services;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.storage.models.DatafileInformation;
import com.evaluation.storage.models.DatafileMapper;
import com.evaluation.storage.models.DatafileMessage;
import com.evaluation.storage.models.DatafileTracker;
import com.evaluation.storage.models.Item;
import com.evaluation.storage.models.ItemMapper;

@Service
public class ItemStorageService {
	private ItemMapper itemMapper; 
	private DatafileMapper datafileMapper;
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private RestTemplate restTemplate;
	
	public ItemStorageService() {
		itemMapper = new ItemMapper();
		datafileMapper = new DatafileMapper();
	}
	
	public ItemMapper getItemMapper() {
		return itemMapper;
	}

	public void setItemMapper(ItemMapper itemMapper) {
		this.itemMapper = itemMapper;
	}

	public DatafileMapper getDatafileMapper() {
		return datafileMapper;
	}

	public void setDatafileMapper(DatafileMapper datafileMapper) {
		this.datafileMapper = datafileMapper;
	}

	public List<Item> getItems(String product) {
		// TODO: put this in a proper initialization place. There are endpoints for dataloader service to send an alive signal if it was down
		if ( itemMapper.getSize() == 0 ) {
			requestFileInfo();
			loadData();
		}
		return itemMapper.getItems(product);
	}

	public boolean addItems(List<Item> list) {
		try {
			itemMapper.addItems(list);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;	
	}
	
	public boolean addFiles(List<String> listFiles) {
		try {
			datafileMapper.addFileList(listFiles);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;	
	}
	
	public boolean processMessage(DatafileMessage datafileMessage) {
		
		try {
			if ( datafileMapper.accept(datafileMessage) ) {
				if ( addItems(datafileMessage.getListItems())) {
					datafileMapper.updateTracker(datafileMessage);
					return true;
				}
			} else 
				return false;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}		
		
		return true;
	}
	
	public void requestFileInfo() {
		try {
			DatafileInformation  dfiResponse = restTemplate.getForObject("http://dataloader-service/load/fileinfo",DatafileInformation.class);
			logger.info("Received "+dfiResponse.getDatafiles().size());
			addFiles(dfiResponse.getDatafiles());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public boolean loadFile(String filename,long bytesConsumed) {
		try {
			logger.info("sent request to load " + filename);
			DatafileMessage  dfmResponse = restTemplate.getForObject("http://dataloader-service/load/fileread/"+filename+"/"+bytesConsumed,DatafileMessage.class);
			logger.info("Received response for "+dfmResponse.getFilename());
			if ( dfmResponse.isSuccess())
				return processMessage(dfmResponse);
			else {
				logger.warn(dfmResponse.getMessage());
				return false;
			}
				
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}
	
	//TODO: put this in a separate class
	public boolean loadData() {
		boolean succeeded = true;
		
		List<DatafileTracker> listTracker = datafileMapper.getTrackerMap().values().stream().filter(ft -> !ft.isFinished() ).collect(Collectors.toList());
		while (!listTracker.isEmpty()) {
		for ( DatafileTracker filetracker: listTracker) {
			logger.info("Requesting load for "+filetracker.getFilename());
			if (!loadFile(filetracker.getFilename(),filetracker.getBytesConsumed()) ) {
				// also remove or invalidate tracker
				//TODO: create an endpoint in case a file was in error but was fixed afterwards.
				// have to consider if bytesConsumed can be also reset easily
				// need to work on this a bit more using the messages to differentiate between file error and service error
				// filetracker.setFinished(true);
				logger.warn("Error loading "+filetracker.getFilename());
				succeeded = false;
			}
		}
		listTracker = datafileMapper.getTrackerMap().values().stream().filter(ft -> !ft.isFinished() ).collect(Collectors.toList());
		}
		
		return succeeded;
	}
	
}
