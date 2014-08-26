package com.example.takemeout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.*;

public class GooglePlaceAPI {

	final static String API_Key = "AIzaSyC3iATl0yMKagIqd3s_hOymF5YrRdOvXoA";
	private static String Radius = "4828";//about 3 miles
	final static String Type = "food";
	final static String Keyword = "food";

	final static String SearchURL = "https://maps.googleapis.com/maps/api/place/radarsearch/json" +
			"?location=%s,%s" +
			"&radius=%s" +
			"&types=%s" +
			"&opennow=true" +
			"&keyword=%s"+
			"&key=%s";
	
	final static String PlaceDetailURL = "https://maps.googleapis.com/maps/api/place/details/json?" +
			"placeid=%s" +
			"&key=%s";
	
	final static String NearbyPlaceURL =  "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
			"?location=%s,%s" +
			"&radius=%s" +
			"&types=%s" +
			"&opennow=true" +
			"&keyword=%s"+
			"&key=%s";
	
	final static String PlacePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%s" +
			"&maxheight=%s" +
			"&photoreference=%s" +
			"&key=%s";
	
	
	public static String getRadius() {
		return Radius;
	}

	public static void setRadius(String radius) {
		Radius = radius;
	}
	
	public static List<String> getListOfPlaceID(String lat, String lon) throws ParseException, IOException, JSONException
	{
		String radarSearchURL = String.format(SearchURL, 
				lat, 
				lon,
				Radius,
				Type,
				Keyword,
				API_Key
				);
		Log.i("getListOfPlaceID", radarSearchURL);
		String jsonResult = getJson(radarSearchURL);
		
		List<String> listOfID= new ArrayList<String>();
		
		JSONObject root = new JSONObject(jsonResult);
		JSONArray results = root.getJSONArray("results");
		
		for(int i = 0; i< results.length(); i++)
		{
			String placeID = results.getJSONObject(i).getString("place_id");//.getAsJsonObject().get("place_id").getAsString();
			listOfID.add(placeID);
			//Log.i("Generating ListOfID",placeID);
		}
		Log.i("getListOfPlaceID","Finished!");
		return listOfID;
	}

	public static Place getPlaceDetail(String placeid) throws IOException, JSONException
	{	
		String placeDetailURL = String.format(PlaceDetailURL, 
				placeid,
				API_Key
				);
		Log.i("getPlaceDetail",placeDetailURL);
		
		Log.i("getPlaceDetail","connect to the URL");
		String jsonResult = getJson(placeDetailURL);
		
		String rating, totalRating, priceLevel, address, picRef, phone, name;
		
		JSONObject root = new JSONObject(jsonResult);
		JSONObject results = root.getJSONObject("result");
		Log.i("getPlaceDetail","parsing data");
		try{address = results.getString("formatted_address");}
		catch(Exception e){address = "N/A";}
		
		try{rating = results.getString("rating");}
		catch(Exception e){rating = "-";}
		
		try{totalRating = results.getString("user_ratings_total");}
		catch(Exception e){totalRating = "N/A";}
		
		try{name = results.getString("name");}
		catch(Exception e){name = "N/A";}
		
		try{phone = results.getString("formatted_phone_number");}
		catch(Exception e){phone = "N/A";}
		
		try{priceLevel = results.getString("price_level");}
		catch(Exception e){priceLevel = "N/A";}
		
		try{picRef = results.getJSONArray("photos").getJSONObject(0).getString("photo_reference");}
		catch(Exception e){picRef = "na";}
		
		Log.i("PlaceAPI picURL", picRef);
		
		Place p = new Place(name, address, phone, rating, totalRating, priceLevel, picRef, placeid);
		
		return p;
	}
	
	public static String getJson(String URL) throws IOException
	{
		HttpGet request = new HttpGet(URL);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse) httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		String jsonResult = EntityUtils.toString(entity);
		return jsonResult;
	}
	
	
	public static Bitmap getPic(String picRef, int W, int H) throws IOException
	{
		W = W - 40;
		H = W;
		String placePhoto = String.format(PlacePhotoURL, 
				W,
				H,
				picRef,
				API_Key
				);
		URL placePhotoURL = new URL(placePhoto);
		HttpURLConnection connection = (HttpURLConnection) placePhotoURL.openConnection();
		connection.connect();
		InputStream iStream = connection.getInputStream();
		
		Bitmap bitmap = BitmapFactory.decodeStream(iStream);
		
		return bitmap;
	}
	
	
	
}
