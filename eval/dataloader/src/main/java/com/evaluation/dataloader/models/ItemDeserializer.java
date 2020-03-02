package com.evaluation.dataloader.models;

import java.lang.reflect.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/*
 * {"product":"UTX","quantity":82,"price":"$4.84","type":"S","industry":"Aerospace","origin":"TX"}
 */
public class ItemDeserializer implements JsonDeserializer<Item> {

	private static final Logger logger = LogManager.getLogger(ItemDeserializer.class);
	
	@Override
	public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)	throws JsonParseException {
		
		Item item = null;
		try {
			JsonObject jObject = json.getAsJsonObject();
			String product 	= jObject.get("product").getAsString();
			long quantity 	= jObject.get("quantity").getAsLong();
			String sPrice	= jObject.get("price").getAsString();
			String type		= jObject.get("type").getAsString();
			String industry	= jObject.get("industry").getAsString();
			String origin	= jObject.get("origin").getAsString();
			// TODO: use Locale ( have to define a locale, receive as property or ...
			long price  	= Long.parseLong(sPrice.replaceAll("[^\\d]",""));
		
			item = new Item(product,quantity,price,type,industry,origin);
		} catch (Exception e ) {
			logger.error("Json could be deserialized "+json.toString());
		}
		return item;
	}

}

