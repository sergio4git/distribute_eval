package com.evaluation.storage.models;

/*
 * {"product":"UTX","quantity":82,"price":"$4.84","type":"S","industry":"Aerospace","origin":"TX"}
 */
public class Item {
	private String 	product;
	private long 	quantity;

	/*
	 * Not sure how to represent price.
	 * double will give rounding errors, BigDecimal will slow things down.
	 * long is fast...
	 * Locale.US?
	 * 
	 */
	private long 	price;
	private String 	type;
	private String 	industry;
	private String 	origin;
	
	public Item() {
	}

	public Item(String product, long quantity, long price, String type, String industry, String origin) {
		super();
		this.product = product;
		this.quantity = quantity;
		this.price = price;
		this.type = type;
		this.industry = industry;
		this.origin = origin;
	}



	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	/*
	 * Not sure what the criteria to decide if an item is duplicate or not
	 * Do I have to check all the properties or are the other properties dependent on product?
	 * Might have to include other properties in the comparison.
	 * In that case it would be better to compare the original json String
	 * Using product,price,quantity,origins as comparison
	 * Need a business definition for this and what validar duplicates mean
	 *   
	 */
	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof Item) )
			return false;
		
		Item oItem = (Item )o;
		if ( !this.product.equals(oItem.product) )
			return false;
		if ( this.price != oItem.price )
			return false;
		if ( this.quantity != oItem.quantity )
			return false;
		if ( !this.origin.equals(oItem.origin) )
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(product);
		sb.append(":-:");
		sb.append(price);
		sb.append(":+:");
		sb.append(quantity);
		sb.append(":*:");
		sb.append(origin);
		
		return sb.toString().hashCode();
	}
}

