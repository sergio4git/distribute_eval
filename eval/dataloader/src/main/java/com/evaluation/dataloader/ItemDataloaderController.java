package com.evaluation.dataloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.evaluation.dataloader.models.DatafileInformation;
import com.evaluation.dataloader.services.ItemDataloaderService;

@RestController
@RequestMapping("/load")
public class ItemDataloaderController {

	@Autowired
	private ItemDataloaderService itemDataloadService;
	
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
		dataInfo.setDatafiles(itemDataloadService.getListDatafiles());
		
		return dataInfo;
	}
	
}
