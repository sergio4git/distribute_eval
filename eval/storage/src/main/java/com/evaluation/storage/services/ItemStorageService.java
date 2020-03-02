package com.evaluation.storage.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.evaluation.storage.models.Item;
import com.evaluation.storage.models.ItemMapper;

@Service
public class ItemStorageService {
	private ItemMapper itemMapper; 
	private Logger logger = LogManager.getLogger();
	
	public ItemStorageService() {
		itemMapper = new ItemMapper();
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
}
