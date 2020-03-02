package com.evaluation.storage.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evaluation.storage.models.DatafileInformation;
import com.evaluation.storage.models.DatafileMapper;
import com.evaluation.storage.models.DatafileMessage;
import com.evaluation.storage.models.ItemWrapper;
import com.evaluation.storage.services.ItemStorageService;

@RestController
@RequestMapping("/store")
public class ItemStorageController {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private ItemStorageService itemStorageService;
	
	private ItemWrapper itemWrapper;

	@GetMapping("/hello")
	public String hello()  {
		return "Hello from Storage Service";
	}
	
	@GetMapping("/list/{product}")
	public ItemWrapper getItems(@PathVariable("product") String product)  {
		itemWrapper = new ItemWrapper();
		itemWrapper.setData(itemStorageService.getItems(product));
		return itemWrapper;
	}

	@GetMapping("/mapper")
	public DatafileMapper getmapper()  {
		
		return itemStorageService.getDatafileMapper();
	}
	
	@GetMapping("/load")
	public void loadData()  {
		itemStorageService.requestFileInfo();
		itemStorageService.loadData();
	}
}
