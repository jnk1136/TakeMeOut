package com.example.takemeout;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	//global sqlHelper
	sqlDatabaseHelper sqlHelper;
	int activityID = 0x100;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //setting global for databse for use in all activities
        sqlHelper = new sqlDatabaseHelper(this);
        MyApplication app  = (MyApplication)getApplication();
        app.sqlhelper = sqlHelper;
        
        //check if distance table is empty and add
        if (sqlHelper.isDistEmpty()) {
			Log.i("OnCREATEMain","distance is empty");
			sqlHelper.insertDist(1, 4828);
		} 
        
        //set id for buttons
        Button butStart = (Button)findViewById(R.id.butStart);
        Button butSetting = (Button) findViewById(R.id.butSetting);
        
        //button listener for activity transitions
        butStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent initializeStart = new Intent(MainActivity.this, StartActivity.class);
				Log.i("MainActivityOnclick", "Switching to Start");
				startActivityForResult(initializeStart,activityID);
				
			}
		});
        butSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent initializeStart = new Intent(MainActivity.this, SettingActivity.class);
				Log.i("MainActivityOnclick", "Switching to Setting");
				startActivityForResult(initializeStart,activityID);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
}
