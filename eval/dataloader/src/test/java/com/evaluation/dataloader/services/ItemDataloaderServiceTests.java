package com.evaluation.dataloader.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.evaluation.dataloader.services.ItemDataloaderService;

@SpringBootTest
public class ItemDataloaderServiceTests {

	@Autowired
	private ItemDataloaderService itemDataLoaderService;
	
	@Test
	public void testGetListDatafiles() {
		assertEquals(4,itemDataLoaderService.getListDatafiles().size(),"Should return 4 files");
	}
	
	@Test
	public void testBasicLoadfile() {
		assertEquals(true,itemDataLoaderService.loadFile("data_1.json",12000).isSuccess(),"Should have read");
	}
	
	@Test
	public void testBasicLoadfileNew() {
		assertEquals(true,itemDataLoaderService.loadFile("data_1.json",12000).isSuccess(),"Should have read");
	}
}
