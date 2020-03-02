package com.evaluation.dataloader.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.evaluation.dataloader.models.DatafileInformation;
import com.evaluation.dataloader.models.DatafileMessage;
import com.evaluation.dataloader.services.ItemDataloaderService;

@RestController
@RequestMapping("/load")
public class ItemDataloaderController {

	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private ItemDataloaderService itemDataloaderService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/hello")
	public String hello()  {
		String response = restTemplate.getForObject("http://storage-service/store/hello", String.class);
		
		return response;
	}
	
	
	@GetMapping("/fileinfo")
	public DatafileInformation getDatafileInfo()  {
		DatafileInformation dataInfo = new DatafileInformation();
		dataInfo.setDatafiles(itemDataloaderService.getListDatafiles());
		
		return dataInfo;
	}
	
	@GetMapping("/fileread/{filename}")
	public DatafileMessage getDatafile(@PathVariable("filename") String filename)  {
		
		logger.info("Received request for "+filename);
		DatafileMessage datafileMessage = itemDataloaderService.loadFile(filename);
		
		return datafileMessage;
	}	
}
