package com.evaluation.storage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evaluation.storage.models.ItemWrapper;
import com.evaluation.storage.services.ItemStorageService;

@RestController
@RequestMapping("/store")
public class ItemStorageController {
	
	@Autowired
	private ItemStorageService itemStorageService;
	
	private ItemWrapper itemWrapper;

	@GetMapping("/hello")
	public String hello()  {
		return "Hello from Storage Service";
	}
	
	@GetMapping("/get/{product}")
	public ItemWrapper getItems(@PathVariable("product") String product)  {
		itemWrapper = new ItemWrapper();
		itemWrapper.setData(itemStorageService.getItems(product));
		return itemWrapper;
	}

}
