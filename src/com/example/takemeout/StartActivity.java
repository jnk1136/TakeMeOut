package com.example.takemeout;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//Location Listener to get gps
public class StartActivity extends ActionBarActivity implements
		LocationListener {

	//global variables used
	public static int counter = 0;
	List<String> result;
	List<Place> listOfPlaces = new ArrayList<Place>();
	Button buttonNo;
	Button buttonYes;
	Button buttonInfo;
	TextView textDist;
	ImageView view;
	
	//location globals
	LocationManager locationManager;
	String provider;
	String lat, lon;

	//values for pic dimen.
	DisplayMetrics metrics;
	int picW;
	int picH;
	
	//datebase
	sqlDatabaseHelper sqlHelper;
	
	ProgressBar spinner;
	float radius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		//get global DB
		sqlHelper = ((MyApplication)getApplication()).sqlhelper;
		
		//get Radius
		radius = sqlHelper.getDist(1);
		Log.i("onCreate", "Distance to search for: " + Float.toString(radius));

		//set id for layout
		buttonNo = (Button) findViewById(R.id.butNo);
		buttonYes = (Button) findViewById(R.id.butYes);
		buttonInfo = (Button) findViewById(R.id.butInfo);
		view = (ImageView) this.findViewById(R.id.imageStore);
		spinner = (ProgressBar) findViewById(R.id.progressBar);
		textDist = (TextView) findViewById(R.id.textDist);
		
		textDist.setText("Searching Distance: "+Float.toString(round( (float)(radius/1609.34),2)) + " miles" );
		
		//used to get height and width of picture, so we know how big to make the google ones
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        view.getViewTreeObserver().removeOnPreDrawListener(this);
		        //set height and width
		        picW = view.getMeasuredWidth();
		        picH = view.getMeasuredHeight();
		        Log.i("Photo OnPost","Height: " + picH + " Width: " + picW);
		        return true;
		    }
		});

		//set false, to wait until content download
		buttonNo.setClickable(false);
		buttonYes.setClickable(false);
		buttonInfo.setClickable(false);

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Creating an empty criteria object
		Criteria criteria = new Criteria();

		// Getting the name of the provider that meets the criteria
		provider = locationManager.getBestProvider(criteria, false);

		if (provider != null && !provider.equals("")) {

			// Get the location from the given provider
			Location location = locationManager.getLastKnownLocation(provider);

			locationManager.requestLocationUpdates(provider, 20000, 1, this);

			if (location != null) {
				onLocationChanged(location);
				new GooglePlaceSearch().execute();
			} 
			else {
				Toast.makeText(getBaseContext(), "Location can't be retrieved, Using Test numbers.",
						Toast.LENGTH_SHORT).show();
				//finish();
				// For Testing, needs coordinate or wont work on emulator
			 	lat = "39.955456"; 
			 	lon = "-75.188114";
				new GooglePlaceSearch().execute();
				

			}
		} else {
			Toast.makeText(getBaseContext(), "No Provider Found",
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}
	
	//dialog box for when info is clicked
	DialogInterface.OnClickListener infoClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			//okay button
			case DialogInterface.BUTTON_POSITIVE:
				break;
			}
		}
	};
	
	//dialog box to re-traverse the list after it is exhausted
	DialogInterface.OnClickListener restartClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			//reset
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				counter = 0;
				new GoogleGetDetail().execute();
				break;
			//quit
			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked, do nothing
				finish();
				break;
			}
		}
	};

	//for when yes is clicked
	public void processYes(View view) {
		//get the last spot in the list of places
		Place p = listOfPlaces.get(listOfPlaces.size() - 1);

		//get current time
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss");
		Date now = new Date();
		String time = sdf.format(now);
		Log.i("processYes", "InsertData");

		//add to database
		sqlHelper.insertData(time, p);
		String address = p.getAddress();
		//pass address to google map intent
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?saddr=" + lat + ","
						+ lon + "&daddr=" + address));
		startActivity(intent);
		//exit to home
		finish();

	}

	//update the layout
	public void updateActivity(Place p) {
		Log.i("updateActivity", "starting function");
		//set id for layout
		TextView name = (TextView) this.findViewById(R.id.textStore);
		TextView rating = (TextView) this.findViewById(R.id.textRating);
		TextView price = (TextView) this.findViewById(R.id.textPrice);

		//set text for location
		name.setText(p.getName());
		price.setText("Price: " + p.getPrice());
		rating.setText("Rating: " + p.getRating() + "/5" + " ("
				+ p.getTotalRating() + ")");

		//after updates make button true
		buttonNo.setClickable(true);
		buttonYes.setClickable(true);
		buttonInfo.setClickable(true);
	}
	
	// When no is clicked
	public void nextStore(View view) throws IOException, JSONException {
		//make all buttton flase
		buttonNo.setClickable(false);
		buttonYes.setClickable(false);
		buttonInfo.setClickable(false);
		//turn on progressbar
		spinner.setVisibility(View.VISIBLE);

		//see if we still have content in list
		if (result.size() > counter) {
			new GoogleGetDetail().execute();
			counter++;
		} 
		//if list is exhausted
		else {
			//dialog box to restart
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"No other stores are open, would you like to see the list again? ")
					.setPositiveButton("Yes", restartClickListener)
					.setNegativeButton("No", restartClickListener).show();
		}
	}

	//when button info is clicked
	public void getInfo(View view) {
		Place p = listOfPlaces.get(listOfPlaces.size() - 1);
		//call dialog box to show info
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Address: " + p.getAddress() + "\n\nPhone: " + p.getPhone()
						+ "").setPositiveButton("Okay", infoClickListener)
				.show();
	}
	
	//rounding function for floats
	public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

	//do radarsearch to get list of businessID
	private class GooglePlaceSearch extends AsyncTask<Void, Integer, Void> {
		//initial progress dialod
		ProgressDialog spinner = new ProgressDialog(StartActivity.this);
		@Override
		protected void onPreExecute()
		{
			//show
			spinner.setMessage("Downloading");
			spinner.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Log.i("GooglePlaceSearch", "Getting Results");
				//call google api and se global result with radius from database
				result = GooglePlaceAPI.getListOfPlaceID(lat, lon, radius);
				Collections.shuffle(result);
				Log.i("GooglePlaceSearch", "Got Results");
				spinner.dismiss();
				//get the first store in the list and get details
				new GoogleGetDetail().execute();
			}
			catch (Exception e) {} 

			return null;
		}
	}

	// get all the detailed info on store
	private class GoogleGetDetail extends AsyncTask<Void, Integer, Place> {
		@Override
		protected void onPostExecute(Place p) {
			Log.i("onPostExecute", "About to update Activity");
			listOfPlaces.add(p);
			updateActivity(p);
		}
		@Override
		protected Place doInBackground(Void... params) {
			Log.i("DoInBackground", "Start doInBackground for detail");
			try {
				//get businessID
				String placeId = result.get(counter);
				Log.i("DoInBackground", placeId);
				Log.i("GoogleGetDetail", "Getting Place Info");
				//use ID for api call
				Place p = GooglePlaceAPI.getPlaceDetail(placeId);
				//get the photo with api call
				new GooglePlacePhoto().execute(p.getPicRef());
				return p;
			}
			catch (Exception e) {}
			
			return null;
		}
	}

	//get first photo of store
	private class GooglePlacePhoto extends AsyncTask<String, Integer, Bitmap> {
		
		@Override
		protected Bitmap doInBackground(String... p) {
			Bitmap image = null;
			try {
				//get first pic
				if (p[0] == "na") {
					//set to default pic if none
					image = BitmapFactory.decodeResource(getResources(),
							R.drawable.not_avail_pic);
				} 
				//get the picture with api call
				else {
					image = GooglePlaceAPI.getPic(p[0], picW, picH);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap b) {
			//when picture is loaded stop progress bar and picture
			spinner.setVisibility(View.INVISIBLE);
			view.setImageBitmap(b);
		}
	}

	@Override
	public void onBackPressed()
	{
		Log.i("OnBackPressed", "Back button pressed");
		finish();
	}
	
	//setting location
	@Override
	public void onLocationChanged(Location location) {
		// Setting Current Lat
		lat = String.valueOf(location.getLatitude());
		// Setting Current Long
		lon = String.valueOf(location.getLongitude());
	}
	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
