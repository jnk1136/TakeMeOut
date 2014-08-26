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

	sqlDatabaseHelper sqlHelper;
	int activityID = 0x100;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sqlHelper.getInstance(this);
        sqlHelper = new sqlDatabaseHelper(this);
        MyApplication app  = (MyApplication)getApplication();
        app.sqlhelper = sqlHelper;
        
        Button butStart = (Button)findViewById(R.id.butStart);
        Button butSetting = (Button) findViewById(R.id.butSetting);
        butStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent initializeStart = new Intent(MainActivity.this, StartActivity.class);
				//Bundle b = new Bundle();
			    //b.putParcelable("sql", sqlHelper);
				//initializeStart.putExtras(b);
				Log.i("MainActivityOnclick", "Switching to Start");
				startActivityForResult(initializeStart,activityID);
				
			}
		});
        
        butSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent initializeStart = new Intent(MainActivity.this, SettingActivity.class);
				//Bundle b = new Bundle();
			    //b.putParcelable("sql", sqlHelper);
				//initializeStart.putExtras(b);
				Log.i("MainActivityOnclick", "Switching to Setting");
				startActivityForResult(initializeStart,activityID);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode)
    	{
    	case 0x100:
    	{
    		if(resultCode == RESULT_OK)
    		{
    			//Bundle extras = data.getExtras();
    			//sqlHelper = extras.getParcelable("sql");
    			//Log.i("onActivityResult","got bundle");
    		}
    	}
    	}
    	
    	
    	
    	
    }
    
    
    
}
