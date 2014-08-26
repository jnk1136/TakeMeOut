package com.example.takemeout;

import java.io.IOException;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends ActionBarActivity implements
		LocationListener {

	public static int counter = 0;
	List<String> result;
	List<Place> listOfPlaces = new ArrayList();
	Button buttonNo;
	Button buttonYes;
	Button buttonInfo;
	ImageView view;

	LocationManager locationManager;
	String provider;
	String lat, lon;

	DisplayMetrics metrics;
	int picW;
	int picH;
	
	sqlDatabaseHelper sqlHelper;
	ProgressBar spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		sqlHelper = ((MyApplication)getApplication()).sqlhelper;
		//Bundle extras = getIntent().getExtras();
		//sqlHelper.getInstance(this);
		//sqlHelper = extras.getParcelable("sql");
		//Log.i("OnCreate",sqlHelper.getAllData());
		Log.i("onActivityResult","got bundle");

		// sqlHelper.helper.getInstance(this);

		buttonNo = (Button) findViewById(R.id.butNo);
		buttonYes = (Button) findViewById(R.id.butYes);
		buttonInfo = (Button) findViewById(R.id.butInfo);
		view = (ImageView) this.findViewById(R.id.imageStore);
		spinner = (ProgressBar) findViewById(R.id.progressBar);

		buttonNo.setClickable(false);
		buttonYes.setClickable(false);
		buttonInfo.setClickable(false);

		metrics = this.getResources().getDisplayMetrics();
		picW = metrics.widthPixels;
		picH = metrics.heightPixels;

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
			} else {
				
				Toast.makeText(getBaseContext(), "Location can't be retrieved, Using Test numbers.",
						Toast.LENGTH_SHORT).show();
				//finish();
				// For Testing
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

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.start, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

	DialogInterface.OnClickListener infoClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				break;
			}
		}
	};

	DialogInterface.OnClickListener restartClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				counter = 0;
				new GoogleGetDetail().execute();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked, do nothing
				finish();
				break;
			}
		}
	};

	public void processYes(View view) {
		Place p = listOfPlaces.get(listOfPlaces.size() - 1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss");
		Date now = new Date();
		String time = sdf.format(now);
		Log.i("processYes", "InsertData");

		sqlHelper.insertData(time, p);
		String address = p.getAddress();
		//Log.i("processYes", time);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?saddr=" + lat + ","
						+ lon + "&daddr=" + address));
		startActivity(intent);
		finish();

	}

	public void updateActivity(Place p) {
		Log.i("updateActivity", "starting function");
		TextView name = (TextView) this.findViewById(R.id.textStore);
		TextView rating = (TextView) this.findViewById(R.id.textRating);
		TextView price = (TextView) this.findViewById(R.id.textPrice);

		name.setText(p.getName());
		price.setText("Price: " + p.getPrice());
		rating.setText("Rating: " + p.getRating() + "/5" + " ("
				+ p.getTotalRating() + ")");

		buttonNo.setClickable(true);
		buttonYes.setClickable(true);
		buttonInfo.setClickable(true);
	}

	public void nextStore(View view) throws IOException, JSONException {
		buttonNo.setClickable(false);
		buttonYes.setClickable(false);
		buttonInfo.setClickable(false);
		spinner.setVisibility(View.VISIBLE);
		
		if (result.size() > counter) {
			new GoogleGetDetail().execute();
			counter++;
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"No other stores are open, would you like to see the list again? ")
					.setPositiveButton("Yes", restartClickListener)
					.setNegativeButton("No", restartClickListener).show();
		}
	}

	public void getInfo(View view) {
		Place p = listOfPlaces.get(listOfPlaces.size() - 1);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Address: " + p.getAddress() + "\n\nPhone: " + p.getPhone()
						+ "").setPositiveButton("Okay", infoClickListener)
				.show();
	}

	private class GooglePlaceSearch extends AsyncTask<Void, Integer, Void> {
		
		ProgressDialog spinner = new ProgressDialog(StartActivity.this);
		@Override
		protected void onPreExecute()
		{
			spinner.setMessage("Downloading");
			spinner.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Log.i("GooglePlaceSearch", "Getting Results");
				result = GooglePlaceAPI.getListOfPlaceID(lat, lon);
				Collections.shuffle(result);
				Log.i("GooglePlaceSearch", "Got Results");
				spinner.dismiss();
				new GoogleGetDetail().execute();
			}
			catch (Exception e) {} 

			return null;
		}
	}

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
				String placeId = result.get(counter);
				Log.i("DoInBackground", placeId);
				Log.i("GoogleGetDetail", "Getting Place Info");
				Place p = GooglePlaceAPI.getPlaceDetail(placeId);
				//spinner.setVisibility(View.INVISIBLE);
				new GooglePlacePhoto().execute(p.getPicRef());
				return p;
			}
			catch (Exception e) {}
			
			return null;
		}
	}

	private class GooglePlacePhoto extends AsyncTask<String, Integer, Bitmap> {
		
		@Override
		protected void onPreExecute()
		{
			//spinner.setMessage("Downloading");
			//spinner.setVisibility(View.VISIBLE);
		}
		@Override
		protected Bitmap doInBackground(String... p) {
			Bitmap image = null;
			try {
				if (p[0] == "na") {
					image = BitmapFactory.decodeResource(getResources(),
							R.drawable.not_avail_pic);
					// notBuilder.setLargeIcon(largeIcon);
				} else {
					image = GooglePlaceAPI.getPic(p[0], picW, picH);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap b) {
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
