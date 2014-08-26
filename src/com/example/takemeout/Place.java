package com.example.takemeout;

public class Place {
	private String rating = "", 
			priceLevel = "", 
			totalRating = "", 
			address = "", 
			picRef = "", 
			phoneNum = "", 
			name = "", 
			storeID = "";
	//private String lat, lon;
	
	public void setRating(String rating)
	{	this.rating = rating;	}
	
	public void setTotalRating(String total)
	{	this.totalRating = total;	}
	
	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public void setPrice(String price)
	{	
		this.priceLevel = price;	
		setPriceWord(priceLevel);
	}
	
	public Place(){
		
	}
	
	public Place(String name, String address, String phoneNum, String rating, String totalRating, String priceLevel, String picRef, String storeId) 
	{
		super();
		this.rating = rating;
		this.totalRating = totalRating;
		this.address = address;
		this.picRef = picRef;
		this.phoneNum = phoneNum;
		this.name = name;
		this.priceLevel = priceLevel;
		this.storeID = storeId;
		setPriceWord(priceLevel);
	}

	public void setAddress(String address)
	{	this.address = address;	}
	
	public void setPicREF(String picRef)
	{	this.picRef = picRef;	}
	
	public void setPhone(String phone)
	{	this.phoneNum = phone;	}
	
	public void setName(String name)
	{	this.name = name;	}
	
	public void setAll( String name, String address, String phoneNum, String rating, String totalRating, String priceLevel, String picRef)
	{
		this.rating = rating;
		this.totalRating = totalRating;
		this.address = address;
		this.picRef = picRef;
		this.phoneNum = phoneNum;
		this.name = name;
		this.priceLevel = priceLevel;
		setPriceWord(priceLevel);
	}
	
	
	public String getRating()
	{	return rating;	}
	
	public String getTotalRating()
	{	return totalRating;	}
	
	public String getPrice()
	{	return priceLevel;	}
	
	public String getAddress()
	{	return address;	}
	
	public String getPicRef()
	{	return picRef;	}
	
	public String getPhone()
	{	return phoneNum;	}

	public String getName()
	{	return name;	}
	
	public void setPriceWord(String price)
	{
		if(price.equals("0"))
		{
			this.priceLevel = "Free";
		}
		else if(price.equals("1"))
		{
			this.priceLevel = "Inexpensive";
		}
		else if(price.equals("2"))
		{
			this.priceLevel = "Moderate";
		}
		else if(price.equals("3"))
		{
			this.priceLevel = "Expensive";
		}
		else if(price.equals("4"))
		{
			this.priceLevel = "Very Expensive";
		}
		else 
		{
			this.priceLevel = "N/A";
		}
		
	}
	
}
