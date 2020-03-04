package com.evaluation.warehouse.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		ItemWrapper itemWrapper = null;
		try {
			itemWrapper = restTemplate.getForObject("http://storage-service/store/list/"+product, ItemWrapper.class);
			idResponse.setListItemDistribution(doDistribution(product,itemWrapper.getData(),number));
		} catch (Exception e ) {
			logger.error(e.toString());
			if ( itemWrapper == null )
				idResponse.setMsg("Error on storage service/discovery service.");
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
		long productTotal = 0;
		for ( int i = 0; i < number ; i++ ) {
			ItemDistribution itemD = gson.fromJson(gson.toJson(itemBaseline), ItemDistribution.class);
			itemD.updateQuantities(arrayRoundRobin[i]);
			itemD.updateTotalAverage();
			productTotal += itemD.getTotal();
			listDistribution.add(itemD);
		}
		
		Double distributionMean = (double) (productTotal/listDistribution.size());
		
		Comparator<ItemDistribution> idComparator = (ItemDistribution a,ItemDistribution b)  -> a.getAveragePriceDouble().compareTo(b.getAveragePriceDouble());
		Collections.sort(listDistribution,idComparator);
		
		/*
		 * Average price should not be the criteria for redistributing.
		 * Imagine you have distA = {price:10, quantity:2} and distB = {price:10, quantity:1}
		 * average price is the same but total is way different 
		 * Disabling this call until there is a decent solution 
		 */
		//if ( Math.abs(listDistribution.get(0).getAveragePrice()-listDistribution.get(number-1).getAveragePrice()) > acceptableDifference ) {
		//	adjustDistribution(listDistribution,distributionMean,idComparator);
		//}
		
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

	/*
	 * Strategy is to get the mean total for all the distributions. Then calc the difference between the biggest and the mean and the smallest and the mean
	 * Use the smallest difference value V and find the biggest list of items/quantities from the biggest distribution that is smaller than V
	 * Then transfer those quantities to the smallest. Finding that list is a time consuming combinatorial problem.
	 * sort the full list and repeat. 
	 */
	private void adjustDistribution(List<ItemDistribution> listDistribution,Double distributionMean,Comparator<ItemDistribution> idComparator) {
		logger.info(() -> "adjustDistribution to be implemented for product "+listDistribution.get(0).getProduct());
		int number = listDistribution.size();
		Collections.sort(listDistribution,idComparator);
		Double diffB = listDistribution.get(0).getTotal() - distributionMean;
		Double diffS = listDistribution.get(number-1).getTotal() - distributionMean;
		List<ItemPartition> listTransfer = getCombination(listDistribution.get(0),diffB>diffS?diffS:diffB);
		// if list is empty cant redisbrute
		if ( !listTransfer.isEmpty() ) {
			
		}
			
	}
	
	private List<ItemPartition> getCombination(ItemDistribution itemD,Double targetTotal) {
		List<ItemPartition> listTransfer = new ArrayList<ItemPartition>();

		/* 
		 * Using Long
		 */
		List<Long> listCandidatesLong = new ArrayList<Long>();
		itemD.getListItemPartition().stream().forEach(it -> addtoListLong(it,listCandidatesLong));
		Map<Long,List<Long>> mapCombinations = new HashMap<Long,List<Long>>();
		generateAll(listCandidatesLong,mapCombinations);
		List<Long> listCombination = mapCombinations.entrySet().stream().filter(e -> e.getKey() <= targetTotal).sorted().findFirst().get().getValue();

		return listTransfer;
	}
	
	public List<ItemPartition> getCombinationFlat(ItemDistribution itemD,Double targetTotal) {
		List<ItemPartition> listTransfer = new ArrayList<ItemPartition>();

		/*
		 * Using ItemPartition
		 */
		List<ItemPartition> listCandidatesFlat = new ArrayList<ItemPartition>();
		itemD.getListItemPartition().stream().forEach(it -> flattenToListLong(it,listCandidatesFlat));
		Map<Long,List<ItemPartition>> mapItemPCombinations = new HashMap<Long,List<ItemPartition>>();
		generateAllFlat(listCandidatesFlat,mapItemPCombinations);
		
		return listTransfer;
	}
	
	private void addtoListLong(ItemPartition itemP,List<Long> listLong) {
		for ( int i = 0 ; i < itemP.getQuantity() ; i++ )
			listLong.add(itemP.getPrice());
	}
	
	private void flattenToListLong(ItemPartition itemP,List<ItemPartition> listFlatPartition) {
		for ( int i = 0 ; i < itemP.getQuantity() ; i++ ) 
			listFlatPartition.add(itemP);
	}
	
	/*
	 * brute forcing all the possible combinations...
	 * works, but takes a lot of time
	 */
	public void generateAll(List<Long> listCandidates,Map<Long,List<Long>> sumCan) {
		if ( listCandidates.isEmpty() )
			return;
		
		sumCan.put(listCandidates.stream().reduce(0L,Long::sum),listCandidates);
		
		for ( int i = 0 ; i < listCandidates.size() ; i++) {
			List<Long> listSubCandidates = new ArrayList<Long>();
			listSubCandidates.addAll(listCandidates);
			listSubCandidates.remove(i);
			generateAll(listSubCandidates,sumCan);
		}
	}
	
	/*
	 * brute forcing all the possible combinations...
	 * works, but takes a lot of time
	 */
	public void generateAllFlat(List<ItemPartition> listCandidates,Map<Long,List<ItemPartition>> sumCan) {
		if ( listCandidates.isEmpty() )
			return;
		
		sumCan.put(listCandidates.stream().map(ip -> ip.getPrice()).reduce(0L,Long::sum),listCandidates);
		
		for ( int i = 0 ; i < listCandidates.size() ; i++) {
			List<ItemPartition> listSubCandidates = new ArrayList<ItemPartition>();
			listSubCandidates.addAll(listCandidates);
			listSubCandidates.remove(i);
			generateAllFlat(listSubCandidates,sumCan);
		}
	}
}
