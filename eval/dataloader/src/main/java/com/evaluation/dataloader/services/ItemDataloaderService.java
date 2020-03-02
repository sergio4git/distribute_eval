package com.evaluation.dataloader.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.dataloader.models.DatafileInformation;
import com.evaluation.dataloader.models.DatafileMessage;
import com.evaluation.dataloader.models.DatafileReader;
import com.evaluation.dataloader.models.DirectoryScanner;

@Service
public class ItemDataloaderService {
	
	private Logger logger = LogManager.getLogger();

	@Autowired
	private RestTemplate restTemplate;
	
	public List<String> getListDatafiles() {
		DirectoryScanner directoryScanner = new DirectoryScanner();
		
		return directoryScanner.getDatafiles();
	}
	
	public void sendDatafileInformation(List<String> listFiles) {
		DatafileInformation datafileInformation = new DatafileInformation();
		datafileInformation.setDatafiles(listFiles);
		try {
			restTemplate.put("http://storage-service/store/files", datafileInformation);
		} catch ( Exception e ) {
			logger.error(e.getMessage());
		}
	}
	
	public DatafileMessage loadFile(String filename) {
		DatafileReader datafileReader = new DatafileReader();
		
		return datafileReader.readFile(filename);
	}
}
