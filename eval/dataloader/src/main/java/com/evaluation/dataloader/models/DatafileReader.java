package com.evaluation.dataloader.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DatafileReader {

	private Logger logger = LogManager.getLogger();
	
	// basic test read - not to be used
	public DatafileMessage readFile(String filename) {
		DatafileMessage datafileMessage = new DatafileMessage();
		datafileMessage.setFilename(filename);
		datafileMessage.setSuccess(false);

		try(BufferedReader in = new BufferedReader(new FileReader(new File(filename)))) {
			
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
	
	/*
	 * If this way is acceptable, I need to check if file is LF or CR+LF
	 */
	public DatafileMessage readFile(String basePath,String filename,long skipBytes,int readLines) {
		DatafileMessage datafileMessage = new DatafileMessage();
		datafileMessage.setFilename(filename);

		try(BufferedReader in = new BufferedReader(new FileReader(new File(basePath+filename)))) {
			
			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(Item.class, new ItemDeserializerJackson());
			objectMapper.registerModule(module);
			
			long lines = 0;
			long bytesConsumed = 0;
			
			/* What is validar Item duplicado? reject or accept?
			*  need to change to Set if reject
			*/
			List<Item> listItem = new ArrayList<Item>();
			String s;

			if ( skipBytes == 0) {
				if ( (s = in.readLine()) != null ) {
					lines++;
					bytesConsumed += s.length()+1;
					int arStart = s.indexOf("[");
					readItem(s.substring(arStart+1),listItem,objectMapper);
				}
			} else
				in.skip(skipBytes-1);

			while ((s = in.readLine()) != null && lines < readLines ) {
				lines++;
				bytesConsumed += s.length()+1;
				readItem(s,listItem,objectMapper);
			}
			
			datafileMessage.setBytesConsumed(bytesConsumed);
			datafileMessage.setListItems(listItem);
			datafileMessage.setSuccess(true);
			datafileMessage.setFinished(s == null);
			datafileMessage.setMessage("File loaded");
			return datafileMessage;
			
		} catch( NullPointerException e ) {
			logger.error("NullPointerException on file "+filename);
			datafileMessage.setMessage("NullPointerException on file "+filename);
			datafileMessage.setSuccess(false);
			return datafileMessage;
		} catch (FileNotFoundException e1) {
			logger.error("File not found "+filename);
			datafileMessage.setMessage("File not found "+filename);
			datafileMessage.setSuccess(false);
			return datafileMessage;
		} catch (IOException e1) {
			logger.error("IOException on file "+filename);
			datafileMessage.setMessage("IOException on file "+filename);
			datafileMessage.setSuccess(false);
			return datafileMessage;
		}	
	}
	
	private void readItem(String s, List<Item> listItem,ObjectMapper objectMapper) {
		int lastIndex;
		try {
			if ( s.endsWith("]}") )
				lastIndex = s.length()-2;
			else
				lastIndex = s.length()-1;
			
			Item item = objectMapper.readValue(s.substring(0, lastIndex), Item.class);
			listItem.add(item);
		} catch (Exception e) {
			if ( !s.isEmpty())
				logger.error(e.toString());
		}
	}
}
