package com.evaluation.storage.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.evaluation.storage.models.Item;

@SpringBootTest
public class ItemStorageServicesTests {

	@Autowired
	private ItemStorageService itemStorageService;
	
	@Test
	public void testGetProductNull() {
		List<Item> list = itemStorageService.getItems(null);
		assertNull(list);
	}

	//{"product":"HNP","quantity":7,"price":"$1.75","type":"3XL","industry":"Electric Utilities: Central","origin":"TX"}
	@Test
	public void testAddNullEmpty() {

		assertFalse(itemStorageService.addItems(null),"Should not insert");
		
		List<Item> list = new ArrayList<Item>();
		assertTrue(itemStorageService.addItems(list),"Should insert with no problems");
	}
	
	//{"product":"HNP","quantity":7,"price":"$1.75","type":"3XL","industry":"Electric Utilities: Central","origin":"TX"}
	@Test
	public void testAddOne() {
		Item item = new Item("HNP",7,175,"3XL","Electric Utilities: Central","TX");
		List<Item> list = new ArrayList<Item>();
		list.add(item);
		assertTrue(itemStorageService.addItems(list),"Should insert with no problems");
		
		list = itemStorageService.getItems("HNP");
		
		assertEquals(175L,list.get(0).getPrice(),"Should have inseerted above item");
	}
	
	//{"product":"EMMS","quantity":61,"price":"$7.45","type":"2XL","industry":"Broadcasting","origin":"LA"}
	//{"product":"TSNU","quantity":65,"price":"$1.32","type":"XL","industry":"Meat/Poultry/Fish","origin":"NY"}
	//{"product":"EMMS","quantity":36,"price":"$5.29","type":"3XL","industry":"Broadcasting","origin":"MN"}
	//{"product":"HNP","quantity":7,"price":"$1.75","type":"3XL","industry":"Electric Utilities: Central","origin":"TX"}
	@Test
	public void testAdd2Overlap() {
		List<Item> list = new ArrayList<Item>();

		Item item = new Item("HNP",7,175,"3XL","Electric Utilities: Central","TX");
		list.add(item);
		item = new Item("EMMS",36,529,"3XL","Broadcasting","MN");
		list.add(item);
		assertTrue(itemStorageService.addItems(list),"Should insert with no problems");

		list.clear();
		item = new Item("TSNU",65,132,"XL","Meat/Poultry/Fish","NY");
		list.add(item);
		item = new Item("EMMS",61,745,"2XL","Broadcasting","LA");
		list.add(item);
		assertTrue(itemStorageService.addItems(list),"Should insert with no problems");
		
		list = itemStorageService.getItems("HNP");
		assertEquals(175L,list.get(0).getPrice(),"Should have inseerted above item");

		list = itemStorageService.getItems("TSNU");
		assertEquals(132L,list.get(0).getPrice(),"Should have inseerted above item");

		list = itemStorageService.getItems("EMMS");
		assertEquals(2L,list.size(),"Should have inseerted 2 items on map");
	}
	
	@Test
	public void testRealLoad() {
		itemStorageService.requestFileInfo();
		assertEquals(4,itemStorageService.getDatafileMapper().getTrackerMap().size(),"Should have inseerted 4 files on map");
	}
}
