package com.evaluation.dataloader.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DatafileReader {

	private Logger logger = LogManager.getLogger();
	private String basePath = "D:\\Temp\\UBS\\teste-fullstack\\massa\\";
	
	// basic test read - not to be used
	public DatafileMessage readFile(String filename) {
		DatafileMessage datafileMessage = new DatafileMessage();
		datafileMessage.setFilename(filename);
		datafileMessage.setSuccess(false);

		try(BufferedReader in = new BufferedReader(new FileReader(new File(basePath+filename)))) {
			
			StringBuilder sb = (StringBuilder)  in.lines().collect(StringBuilder::new,StringBuilder::append, (a,b) -> a.append(b.toString()));

			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(Item.class, new ItemDeserializerJackson());
			objectMapper.registerModule(module);

			ItemWrapper itemWrapper = objectMapper.readValue(sb.toString(), ItemWrapper.class);
			// this doesnt consider lf/cr+lf
			datafileMessage.setBytesConsumed(sb.length());
			datafileMessage.setListItems(itemWrapper.getData());
			datafileMessage.setSuccess(true);
			datafileMessage.setFinished(true);
			datafileMessage.setMessage("File loaded");
			return datafileMessage;
			
		} catch( NullPointerException e ) {
			logger.error("NullPointerException on file "+filename);
			datafileMessage.setMessage("NullPointerException on file "+filename);
			return datafileMessage;
		} catch (FileNotFoundException e1) {
			logger.error("File not found "+filename);
			datafileMessage.setMessage("File not found "+filename);
			return datafileMessage;
		} catch (IOException e1) {
			logger.error("IOException on file "+filename);
			datafileMessage.setMessage("IOException on file "+filename);
			return datafileMessage;
		}	
	}
}
