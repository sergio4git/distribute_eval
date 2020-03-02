package com.evaluation.dataloader.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evaluation.dataloader.models.DirectoryScanner;

@Service
public class ItemDataloaderService {

	public List<String> getListDatafiles() {
		DirectoryScanner directoryScanner = new DirectoryScanner();
		
		return directoryScanner.getDatafiles();
	}
}
