package com.evaluation.dataloader.models;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryScanner {

	private Logger logger = LogManager.getLogger();
	private String basePath;
	

	public DirectoryScanner() {
	}

	public DirectoryScanner(String basePath) {
		this.basePath = basePath;
	}
	
	public List<String> getDatafiles() {
		List<String> listDatafile;
		String pattern ="^data_\\d.json$";
		try (Stream<Path> walk = Files.walk(Paths.get(basePath))) {
			listDatafile = walk.map(x -> x.getFileName().toString()).filter(f -> f.matches(pattern)).collect(Collectors.toList());
			 return listDatafile;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
}
