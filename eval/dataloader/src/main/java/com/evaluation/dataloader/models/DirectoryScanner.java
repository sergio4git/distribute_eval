package com.evaluation.dataloader.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryScanner {

	private String basePath = "D:\\Temp\\UBS\\teste-fullstack\\massa";
	
	public List<String> getDatafiles() {
		List<String> listDatafile;
		String pattern ="^data_\\d.json$";
		try (Stream<Path> walk = Files.walk(Paths.get( basePath))) {
			listDatafile = walk.map(x -> x.getFileName().toString()).filter(f -> f.matches(pattern)).collect(Collectors.toList());
			 return listDatafile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
