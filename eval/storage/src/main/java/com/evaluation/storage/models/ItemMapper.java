package com.evaluation.storage.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemMapper {
	
	Map<String,List<Item>> itemMap;
	
	public ItemMapper() {
		itemMap = new HashMap<String,List<Item>>();
	}

	public List<Item> getItems(String product) {
		return itemMap.get(product);
	}
	
	public long getSize() {
		return itemMap.size();
	}
	
	public void addItems(List<Item> listItems) {
		Map<String,List<Item>> newMap = listItems.stream().collect(Collectors.groupingBy(Item::getProduct));
        newMap.forEach((K, V) -> {
            itemMap.merge(K, V, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            });
        });
	}
}
