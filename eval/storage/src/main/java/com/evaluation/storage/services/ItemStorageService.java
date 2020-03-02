package com.evaluation.storage.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.evaluation.storage.models.DatafileMapper;
import com.evaluation.storage.models.Item;
import com.evaluation.storage.models.ItemMapper;

@Service
public class ItemStorageService {
	private ItemMapper itemMapper; 
	private DatafileMapper datafileMapper;
	
	private Logger logger = LogManager.getLogger();
	
	public ItemStorageService() {
		itemMapper = new ItemMapper();
		datafileMapper = new DatafileMapper();
	}
	
	public List<Item> getItems(String product) {
		return itemMapper.getItems(product);
	}

	public boolean addItems(List<Item> list) {
		try {
			itemMapper.addItems(list);
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
		return true;	
	}
	
	public boolean addFiles(List<String> listFiles) {
		try {
			datafileMapper.addFileList(listFiles);
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
		return true;	
	}
	
	
}
