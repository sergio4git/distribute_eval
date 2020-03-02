package com.evaluation.storage.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.storage.models.DatafileInformation;
import com.evaluation.storage.models.DatafileMapper;
import com.evaluation.storage.models.DatafileMessage;
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
	
	public boolean loadFile(String filename) {
		try {
			logger.info("sent request to load " + filename);
			DatafileMessage  dfmResponse = restTemplate.getForObject("http://dataloader-service/load/fileread/"+filename,DatafileMessage.class);
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
	
	public boolean loadData() {
		boolean succeeded = true;
		for ( String filename: datafileMapper.getDatafileMapper().keySet()) {
				logger.info("Requesting load for "+filename);
				if (!loadFile(filename) ) {
					// also remove or invalidate tracker
					logger.warn("Error loading "+filename);
					succeeded = false;
				}
		}
		
		return succeeded;
	}
	
}
