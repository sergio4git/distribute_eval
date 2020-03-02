package com.evaluation.dataloader.models;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/*
 * {"product":"UTX","quantity":82,"price":"$4.84","type":"S","industry":"Aerospace","origin":"TX"}
 */
public class ItemDeserializerJackson extends StdDeserializer<Item> { 
	 
    public ItemDeserializerJackson() { 
        this(null); 
    } 
 
    public ItemDeserializerJackson(Class<?> vc) { 
        super(vc); 
    }
 
    @Override
    public Item deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        
        long quantity =  Long.parseLong(node.get("quantity").asText());
        
        String industry = node.get("industry").asText();
        String type = node.get("type").asText();
        String origin = node.get("origin").asText();
        String product = node.get("product").asText();
        String sPrice = node.get("price").asText();
		// TODO: use Locale ( have to define a locale, receive as property or ...
		long price  	= Long.parseLong(sPrice.replaceAll("[^\\d]",""));
 
        return new Item(product,quantity,price,type,industry,origin);
    }
}




