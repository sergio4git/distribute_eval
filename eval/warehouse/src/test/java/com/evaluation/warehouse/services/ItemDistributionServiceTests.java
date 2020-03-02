package com.evaluation.warehouse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.evaluation.warehouse.mocks.BasicDataReader;
import com.evaluation.warehouse.models.Item;
import com.evaluation.warehouse.models.ItemDistribution;
import com.evaluation.warehouse.models.ItemDistributionResponse;
import com.evaluation.warehouse.models.ItemWrapper;

@SpringBootTest
public class ItemDistributionServiceTests {

	private Logger logger = LogManager.getLogger();
	@Value("${distribution.acceptableDifference}")
	private int acceptableDifference;
	@Value("${distribution.acceptablePercentage}")
	private float acceptablePercentage;	
	
	@Autowired
	private ItemDistributionService itemDistributionService;
	@Autowired
	private BasicDataReader basicReader;
	
	//@Test
	public void testBasicDistributionZero() {
		assertEquals("Invalid number",itemDistributionService.getDistribution("Product 1",0).getMsg(),"Should return a number error message");
	}

	//@Test
	public void testBasicDistributionEmptyProduct() {
		assertEquals("Invalid product",itemDistributionService.getDistribution("",4).getMsg(),"Should return a product error message");
	}

	//@Test
	public void testBasicDistributionNullProduct() {
		assertEquals("Invalid product",itemDistributionService.getDistribution(null,4).getMsg(),"Should return a product error message");
	}

	//@Test
	public void testListBasicReader() {
		ItemWrapper itemWrapper = basicReader.getItemListByProduct("EMMS");
		assertEquals(270L,itemWrapper.getData().stream().mapToLong(item -> item.getQuantity()).sum(),"Should return the sum of the quantities");
	}
	
	//@Test
	public void testBasicDistributionEMMS() {
		int number = 2;
		ItemDistributionResponse idResponse = itemDistributionService.getDistribution("EMMS", number);
		assertEquals("Distributing EMMS between 2",itemDistributionService.getDistribution("EMMS",2).getMsg(),"Should return a valid message");
		assertEquals(number,idResponse.getListItemDistribution().size(),"List should have size "+number);

		long[] expectedArray1 = {37,18,50,30};
		long[] expectedArray2 = {37,18,49,31};
		
		List<Long> resultArray = idResponse.getListItemDistribution().get(0).getListItemPartition().stream().map(item -> item.getQuantity()).collect(Collectors.toList());
		assertEquals(Arrays.asList(expectedArray1),resultArray,"Should return the sum of the quantities divided by "+number);
		
		resultArray = idResponse.getListItemDistribution().get(1).getListItemPartition().stream().map(item -> item.getQuantity()).collect(Collectors.toList());
		assertEquals(Arrays.asList(expectedArray2),resultArray,"Should return the sum of the quantities divided by "+number);
	}
	
	/*
	 * Testing distribution of file 1 in number partitions and comparing the difference between the biggest and the smallest average price
	 * number = 2 : Total products 5154 acceptable 5118
	 * number = 3 : Total products 5154 acceptable 5083
	 * number = 4 : Total products 5154 acceptable 5012
	 * number = 5 : Total products 5154 acceptable 4942
	 * number = 6 : Total products 5154 acceptable 4844
	 * The naive approach is a good approximation for the first try.Some cases had quantity 1 and are not partitionable.
	 * Will create an adjust quantity method to shift quantities on cases where its possible to even out.
	 * acceptablePercentage = 0.05
	 * acceptableDifference = 50
	 * For testing. 
	 * test held for acceptableDifference = 10
	 * 
	 */
	//@Test
	public void testDistribution() {
		BasicDataReader basicReader = new BasicDataReader();
		ItemWrapper itemWrapper = basicReader.getItems();
		
		List<Item> li; 
		List<ItemDistribution> liR;
		Set<String> allProducts = itemWrapper.getData().stream().map(item -> item.getProduct()).collect(Collectors.toSet());

		int number = 2;
		int total = allProducts.size();

		int acceptable = 0;

		Comparator<ItemDistribution> idComparator = (ItemDistribution a,ItemDistribution b)  -> a.getAveragePriceDouble().compareTo(b.getAveragePriceDouble());
		for ( String product:allProducts ) {

			li = itemWrapper.getData().stream().filter(it -> it.getProduct().equals(product)).collect(Collectors.toList());
			liR = itemDistributionService.doDistribution(product,li, number);
			// sort items to see the difference between the 2 extremes
			Collections.sort(liR,idComparator);
			if ( Math.abs(liR.get(0).getAveragePrice()-liR.get(number-1).getAveragePrice()) > acceptableDifference ) {
				logger.info("Product "+product);
				logger.info(liR.get(0).getAveragePrice() +" "+liR.get(number-1).getAveragePrice());
			} else
				acceptable++;	
		}

		
		logger.info("Total products "+total+" acceptable "+acceptable);
		
		assertTrue(acceptable/total < acceptablePercentage);
	}
	
	//@Test
	// Gson takes 131 seconds to deserialize file 1 10000 times
	public void testLoadDataGson() {
		BasicDataReader basicReader = new BasicDataReader();
		long l = basicReader.testLoadDataGson();
		logger.info("Gson time is "+l);
		assertTrue(l > 0);
	}
	
	//@Test
	// Jackson takes 121 seconds to deserialize file 1 10000 times
	public void testLoadDataJackson() {
		BasicDataReader basicReader = new BasicDataReader();
		long l = basicReader.testLoadDataJackson();
		logger.info("Jackson time is "+l);
		assertTrue(l > 0);
	}
}
