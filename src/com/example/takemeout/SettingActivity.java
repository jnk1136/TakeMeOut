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
import android.widget.ListView;

public class SettingActivity extends ActionBarActivity {

	sqlDatabaseHelper sqlHelper;
	ListView listData;
	List<Place> listOfPlaces;
	List<Data> listOfData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sqlHelper = ((MyApplication)getApplication()).sqlhelper;	
		listData = (ListView) findViewById(R.id.listViewData);

		
		Log.i("onActivityResult","got bundle");
		if (!sqlHelper.isEmpty())
		{
			listOfData = sqlHelper.getAllData();
			if (listOfData != null) 
			{
				HistoryAdapter adapter = new HistoryAdapter(SettingActivity.this, listOfData);
				listData.setAdapter(adapter);
			}
		}
		
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
	
	
	protected boolean onLongListItemClick(View v, int pos, long id) throws InterruptedException, ExecutionException {
	    Log.i("onLongListItemClick", "onLongListItemClick id=" + id + " pos="+pos);
	    Place p = new GoogleGetDetail().execute(listOfData.get(pos).getPlace().getStoreID()).get();
	   callDialog(pos, p);
	    
	    return true;
	}

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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				p.getName() + "\nAddress: " + p.getAddress() + "\nPhone: "
						+ p.getPhone() + "\nRating: " + p.getRating() + "/5 ("
						+ p.getTotalRating() + ")" + "\nPrice: " + p.getPrice())
				.setNegativeButton("Delete", dialogClickListener)
				.setNeutralButton("Okay", dialogClickListener).show();

		
	}
    private class GoogleGetDetail extends AsyncTask<String, Integer, Place> {

		@Override
		protected Place doInBackground(String... storeId) {
			Log.i("DoInBackground", "Start doInBackground for detail");
			try {
				Log.i("DoInBackground", storeId[0]);
				Log.i("GoogleGetDetail", "Getting Place Info");
				Place p = GooglePlaceAPI.getPlaceDetail(storeId[0]);
				return p;
			}
			catch (Exception e) {}
			
			return null;
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
