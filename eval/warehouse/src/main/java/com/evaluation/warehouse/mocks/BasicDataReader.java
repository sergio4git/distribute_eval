package com.evaluation.warehouse.mocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.evaluation.warehouse.models.Item;
import com.evaluation.warehouse.models.ItemDeserializer;
import com.evaluation.warehouse.models.ItemDeserializerJackson;
import com.evaluation.warehouse.models.ItemWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.GsonBuilder;

@Component
public class BasicDataReader {
	private Logger logger = LogManager.getLogger();
	private String basePath = "D:\\Temp\\UBS\\teste-fullstack\\massa\\";
	private ItemWrapper itemWrapper = null;

	private static ItemWrapper itemwWrapper;
	
	public BasicDataReader() {
		loadDataJackson("data_1.json");
	}

	private void loadDataGson(String fileName) {
		try(BufferedReader in = new BufferedReader(new FileReader(new File(basePath+fileName)))) {
			
			StringBuilder sb = (StringBuilder)  in.lines().collect(StringBuilder::new,StringBuilder::append, (a,b) -> a.append(b.toString()));

		    GsonBuilder gsonBldr = new GsonBuilder();
		    gsonBldr.registerTypeAdapter(Item.class, new ItemDeserializer());
		    itemWrapper = gsonBldr.create().fromJson(sb.toString(), ItemWrapper.class);
			
		} catch( NullPointerException e ) {
			logger.error("NullPointerException on file "+fileName);
			return;
		} catch (FileNotFoundException e1) {
			logger.error("File not found "+fileName);
			return;
		} catch (IOException e1) {
			logger.error("IOException on file "+fileName);
			return;
		}		
	}
	
	private void loadDataJackson(String fileName) {
		try(BufferedReader in = new BufferedReader(new FileReader(new File(basePath+fileName)))) {
			
			StringBuilder sb = (StringBuilder)  in.lines().collect(StringBuilder::new,StringBuilder::append, (a,b) -> a.append(b.toString()));

			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(Item.class, new ItemDeserializerJackson());
			objectMapper.registerModule(module);

			itemWrapper = objectMapper.readValue(sb.toString(), ItemWrapper.class);

		} catch( NullPointerException e ) {
			logger.error("NullPointerException on file "+fileName);
			return;
		} catch (FileNotFoundException e1) {
			logger.error("File not found "+fileName);
			return;
		} catch (IOException e1) {
			logger.error("IOException on file "+fileName);
			return;
		}			
	}
	
	public ItemWrapper getItems() {
		if ( itemWrapper != null ) 
			return itemWrapper;
		
		return null;
	}
	
	public Map<String,List<Item>> getItemMap() {
		if ( itemWrapper != null ) 
			return itemWrapper.getData().stream().collect(Collectors.groupingBy(Item::getProduct));
		
		return null;
	}
	
	public ItemWrapper getItemListByProduct(String product) {
		if ( itemWrapper != null ) {
			List<Item> newList = itemWrapper.getData().stream().filter(item -> item.getProduct().equals(product)).collect(Collectors.toList());
			itemWrapper.setData(newList);
			return itemWrapper;
		}
		
		return null;
	}
		
	public long testLoadDataGson() {
		try(BufferedReader in = new BufferedReader(new FileReader(new File("D:\\Temp\\UBS\\teste-fullstack\\massa\\data_1.json")))) {
			
			StringBuilder sb = (StringBuilder)  in.lines().collect(StringBuilder::new,StringBuilder::append, (a,b) -> a.append(b.toString()));

		    GsonBuilder gsonBldr = new GsonBuilder();
		    gsonBldr.registerTypeAdapter(Item.class, new ItemDeserializer());
		    
			Instant iStart = Instant.now();
			for ( long i = 0; i < 10000L; i++ ) {
				itemWrapper = gsonBldr.create().fromJson(sb.toString(), ItemWrapper.class);
			}
			Instant iEnd = Instant.now();
			return Duration.between(iStart, iEnd).toSeconds();
			
		} catch( NullPointerException e ) {
			return 0;
		} catch (FileNotFoundException e1) {
			return 0;
		} catch (IOException e1) {
			return 0;
		}		
	}
	
	public long testLoadDataJackson() {
		try(BufferedReader in = new BufferedReader(new FileReader(new File("D:\\Temp\\UBS\\teste-fullstack\\massa\\data_1.json")))) {
			
			StringBuilder sb = (StringBuilder)  in.lines().collect(StringBuilder::new,StringBuilder::append, (a,b) -> a.append(b.toString()));

			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(Item.class, new ItemDeserializerJackson());
			objectMapper.registerModule(module);
			
			Instant iStart = Instant.now();
			for ( long i = 0; i < 10000L; i++ ) {
				itemWrapper = objectMapper.readValue(sb.toString(), ItemWrapper.class);
			}
			Instant iEnd = Instant.now();
			return Duration.between(iStart, iEnd).toSeconds();
			
		} catch( NullPointerException e ) {
			return 0;
		} catch (FileNotFoundException e1) {
			return 0;
		} catch (IOException e1) {
			return 0;
		}		
	}
}
