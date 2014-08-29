package com.example.takemeout;

import java.util.List;
import java.util.concurrent.ExecutionException;

//import com.example.takemeout.StartActivity.GooglePlacePhoto;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SettingActivity extends ActionBarActivity {

	//global variables
	sqlDatabaseHelper sqlHelper;
	ListView listData;
	List<Place> listOfPlaces;
	List<Data> listOfData;
	EditText editRadius;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//set id for layouts
		sqlHelper = ((MyApplication)getApplication()).sqlhelper;	
		listData = (ListView) findViewById(R.id.listViewData);
		editRadius = (EditText)findViewById(R.id.editRadius);
		
		Log.i("onActivityResult","got bundle");
		//if sql is not empty
		if (!sqlHelper.isEmpty())
		{
			//populate listView
			listOfData = sqlHelper.getAllData();
			if (listOfData != null) 
			{
				HistoryAdapter adapter = new HistoryAdapter(SettingActivity.this, listOfData);
				listData.setAdapter(adapter);
			}
		}
		
		//longclick for listview and call dialogbox
		listData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> aV, View view,int pos, long id) {
				boolean b = false;
				try {b = onLongListItemClick(view, pos, id);} 
				catch (Exception e) {}
				
				return b;
			}
		});
	}
	
	//onlongclick to get detail of store
	protected boolean onLongListItemClick(View v, int pos, long id) throws InterruptedException, ExecutionException {
	    Log.i("onLongListItemClick", "onLongListItemClick id=" + id + " pos="+pos);
	    //make API call with storeID
	    Place p = new GoogleGetDetail().execute(listOfData.get(pos).getPlace().getStoreID()).get();
	   callDialog(pos, p);
	    
	    return true;
	}

	//dialog box for store detail info
	public void callDialog(final int position, Place p) {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_NEGATIVE:
					listOfData.remove(position);
					HistoryAdapter adapter = new HistoryAdapter(SettingActivity.this, listOfData);
					listData.setAdapter(adapter);
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					break;

				}
			}
		};
		
		//call dialogbox
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				p.getName() + "\nAddress: " + p.getAddress() + "\nPhone: "
						+ p.getPhone() + "\nRating: " + p.getRating() + "/5 ("
						+ p.getTotalRating() + ")" + "\nPrice: " + p.getPrice())
				.setNegativeButton("Delete", dialogClickListener)
				.setNeutralButton("Okay", dialogClickListener).show();
	}
	
	//used to get details of store
    private class GoogleGetDetail extends AsyncTask<String, Integer, Place> {
		@Override
		protected Place doInBackground(String... storeId) {
			Log.i("DoInBackground", "Start doInBackground for detail");
			try {
				Log.i("DoInBackground", storeId[0]);
				Log.i("GoogleGetDetail", "Getting Place Info");
				//make google api call
				Place p = GooglePlaceAPI.getPlaceDetail(storeId[0]);
				return p;
			}
			catch (Exception e) {}
			
			return null;
		}
	}
    
    //when set button was clicked
    public void SetDistance(View view)
    {
    	//see if the value is valid
    	if(editRadius.getText().toString().equals("") || editRadius.getText().toString().equals("."))
    	{
    		editRadius.setText("");
    		Log.i("setDistance", "Blank or decimal");
    		Toast.makeText(getApplicationContext(), "Can't be blank or just a decimal",
  				   Toast.LENGTH_LONG).show();
    	}
    	//see if it is zero
    	else if(Float.parseFloat(editRadius.getText().toString()) == 0)
    	{
    		editRadius.setText("");
    		Toast.makeText(getApplicationContext(), "Can't be 0",
 				   Toast.LENGTH_LONG).show();
    	}
    	//see if it is passed searching distance
    	else if(Float.parseFloat(editRadius.getText().toString()) > 30)
    	{
    		editRadius.setText("");
    		Toast.makeText(getApplicationContext(), "Can't be great than 30",
    				   Toast.LENGTH_LONG).show();
    	}
    	//if distance is valid
    	else
    	{
    		//delete first index
    		sqlHelper.deleteDist(1);
    		Log.i("setDistance", "has correct content");
    		
    		//convert mile to meter to place in DB
        	float mile = Float.parseFloat(editRadius.getText().toString());
        	Log.i("setDistance", Float.toString(mile));
        	float meter = (float) (mile * 1609.34);      	
        	Log.i("setDistance", Float.toString(meter));
        	
        	//insert first index
        	sqlHelper.insertDist(1, meter);
        	
        	Toast.makeText(getApplicationContext(),"Distance set to "+Float.toString(mile) + " miles",
 				   Toast.LENGTH_LONG).show();
        	editRadius.setText("");
    	}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onBackPressed()
	{
		finish();
	}
}
