package com.evaluation.warehouse.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.warehouse.mocks.BasicDataReader;
import com.evaluation.warehouse.models.Item;
import com.evaluation.warehouse.models.ItemDistribution;
import com.evaluation.warehouse.models.ItemDistributionResponse;
import com.evaluation.warehouse.models.ItemPartition;
import com.evaluation.warehouse.models.ItemWrapper;
import com.google.gson.Gson;

@Service
public class ItemDistributionService {
	private final String InvalidNumberMessage="Invalid number";
	private final String InvalidProductMessage="Invalid product";
	
	private Logger logger = LogManager.getLogger();
	
	private BasicDataReader basicReader;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${distribution.acceptableDifference}")
	private int acceptableDifference;
	
	public ItemDistributionResponse getDistribution(String product,int number) {
		
		ItemDistributionResponse idResponse = new ItemDistributionResponse();
		
		if ( number < 1  ) {
			idResponse.setMsg(InvalidNumberMessage);
			return idResponse;
		}
		if (product == null || product.isEmpty() ) {
			idResponse.setMsg(InvalidProductMessage);
			return idResponse;
			
		}
		
		//basicReader = new BasicDataReader();
		idResponse.setMsg("Distributing "+product+" between "+ number);
		//ItemWrapper itemWrapper = basicReader.getItemListByProduct(product);
		try {
			ItemWrapper itemWrapper = restTemplate.getForObject("http://storage-service/store/list/"+product, ItemWrapper.class);
			idResponse.setListItemDistribution(doDistribution(product,itemWrapper.getData(),number));
		} catch (Exception e ) {
			logger.error(e.toString());
			idResponse.setMsg(e.toString());
		}
		
		return idResponse;
	}
	
	public List<ItemDistribution> doDistribution(String product,List<Item> sourceItems,int number) {
		List<ItemDistribution> listDistribution = new ArrayList<ItemDistribution>(number);
		
		ItemDistribution itemBaseline = new ItemDistribution();
		itemBaseline.setProduct(product);
		itemBaseline.getListItemPartition().addAll(sourceItems.stream().map(item -> new ItemPartition(item.getQuantity(),item.getPrice(),number)).collect(Collectors.toList()));
		long[] arrayQuantity = itemBaseline.getListItemPartition().stream().mapToLong(item -> item.getQuantity()).toArray();
		long[] arrayRemainder = itemBaseline.getListItemPartition().stream().mapToLong(item -> item.getRemainder()).toArray();
		int nOrder = itemBaseline.getListItemPartition().size();
		
		long[][] arrayRoundRobin = getRoundRobin(arrayQuantity,arrayRemainder,number,nOrder);
					
		// Create [number] deep copies of itemBaseline, and assign the array
		Gson gson = new Gson();
		for ( int i = 0; i < number ; i++ ) {
			ItemDistribution itemD = gson.fromJson(gson.toJson(itemBaseline), ItemDistribution.class);
			itemD.updateQuantities(arrayRoundRobin[i]);
			itemD.updateTotalAverage();
			listDistribution.add(itemD);
		}
		
		Comparator<ItemDistribution> idComparator = (ItemDistribution a,ItemDistribution b)  -> a.getAveragePriceDouble().compareTo(b.getAveragePriceDouble());
		Collections.sort(listDistribution,idComparator);
		
		if ( Math.abs(listDistribution.get(0).getAveragePrice()-listDistribution.get(number-1).getAveragePrice()) > acceptableDifference ) {
			adjustDistribution(listDistribution);
		}
		
		return listDistribution;
	}

	/*
	 *  The example distributes every order ( or item ) individually
	 *  From the baseline, ill do a round robin assignment of the remainder among the ItemDistribution elements
	 *  Starting with 0 and going around putting 1 in each , circling around.
	 *  This works reasonably if there are not huge differences in prices over the several items, and it doesnt 
	 *  take price into account. So I can use whatever representation of price I wish without causing big rounding erors.
	 *  If there are huge differences in price then another approach that targets matching the final quantity
	 *  has to be used instead.
	 *  Another problem will be distributing items where quantity < number 
	 *   
	 */
	private long[][] getRoundRobin(long[] arrayQuantity,long[] arrayRemainder,int number,int nOrder) {
		long[][] roundRobinArray = new long[number][nOrder];

		for ( int i = 0 ; i < number ; i++ )
			for ( int j = 0 ; j < nOrder ; j++ )
			roundRobinArray[i][j] = arrayQuantity[j];
		
		int iStart = 0;
		for ( int i = 0; i < nOrder ; i++ )
			for( long j = arrayRemainder[i]; j > 0 ; j-- ) {
				roundRobinArray[iStart++][i]++;
				iStart = iStart % number;
			}
	
		return roundRobinArray;
	}

	private void adjustDistribution(List<ItemDistribution> listDistribution) {
		logger.info("adjustDistribution to be implemented for product "+listDistribution.get(0).getProduct());
	}
}
