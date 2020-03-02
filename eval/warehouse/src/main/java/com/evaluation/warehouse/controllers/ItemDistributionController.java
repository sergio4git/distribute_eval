package com.evaluation.warehouse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.evaluation.warehouse.models.ItemWrapper;
import com.evaluation.warehouse.services.ItemDistributionService;

@RestController
@RequestMapping("/distribute")
public class ItemDistributionController {
	
	@Autowired
	private ItemDistributionService itemDistributionService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private ItemWrapper itemWrapper;
	
	@GetMapping("/hello")
	public String hello()  {
		String response = restTemplate.getForObject("http://storage-service/store/hello", String.class);
		
		return response;
	}

/*	
	@GetMapping("/hello")
	public ItemWrapper getItems(@PathVariable("product") String product)  {
		itemWrapper = new ItemWrapper();
		itemWrapper.setData(itemStorageService.getItems(product));
		return itemWrapper;
	}
*/
}
