package com.example.takemeout;

//class to hold Data, I dont think it is used
public class Data {
	private String time;
	private Place place;
	
	public Data(String time, Place p)
	{
		this.place = p;
		this.time = time;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Place getPlace() {
		return place;
	}
	public void setPlace(Place place) {
		this.place = place;
	}
	
	
	
}
