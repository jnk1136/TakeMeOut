package com.example.takemeout;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class sqlDatabaseHelper implements Parcelable 
{
	private static sqlHelper helper;
	public sqlDatabaseHelper(Context context)
	{
		helper = new sqlHelper(context);
	}

	public static sqlHelper getInstance(Context context) {
		if (helper == null) {
			helper = new sqlHelper(context.getApplicationContext());
		}
		Log.i("getInstance", "got instance");
		return helper;
	}
	
	//get all data, not used
	public List<Data> getAllData()
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = {sqlHelper.UID, sqlHelper.TIME, sqlHelper.STORE_NAME, sqlHelper.STORE_ID};
		Cursor cursor = db.query(sqlHelper.TABLE_NAME, columns, null, null, null, null, null);
		StringBuffer buffer = new StringBuffer();
		List<Data> listOfData = new ArrayList<Data>();
		Log.i("getAllData","traversing");
		while(cursor.moveToNext())
		{
			int cid= cursor.getInt(0);
			String time = cursor.getString(1);
			String name = cursor.getString(2);
			String storeId = cursor.getString(3);
			Place p= new Place();
			p.setName(name);
			p.setStoreID(storeId);
			Data data = new Data(time, p);
			listOfData.add(data);
			buffer.append(cid + " " + time + " " + name + " \n");
		}
		Log.i("getAllData",buffer.toString());
		return listOfData;
	}
	
	//get place info
	public Data getPlace(int id)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = {sqlHelper.UID, sqlHelper.TIME, sqlHelper.STORE_NAME};
		Cursor cursor = db.query(sqlHelper.TABLE_NAME, columns, sqlHelper.UID + " = ?", new String[] { String.valueOf(id) }, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		String time = cursor.getString(1);
		String name = cursor.getString(2);
		String storeId = cursor.getString(3);
		
		Place p = null;
		p.setName(name);
		p.setStoreID(storeId);
		//return data;
		Data data = new Data(time, p);
		return data;
	}
	
	//delete the value from table
	public void deleteData(int id){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(sqlHelper.TABLE_NAME,sqlHelper.UID + " = ?", new String[] {String.valueOf(id)});
	
		Log.i("getData", String.valueOf(id));
		db.close();
	}
	
	//insert data to table
	public void insertData(String time, Place p )
	{
		Log.i("insertData", sqlHelper.DATABASE_CREATE);
		
		Log.i("insertData", p.getName());
		Log.i("insertData", p.getStoreID());
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(sqlHelper.TIME, time);
		value.put(sqlHelper.STORE_NAME, p.getName());
		value.put(sqlHelper.STORE_ID, p.getStoreID());
		db.insert(sqlHelper.TABLE_NAME, null, value);
		db.close();
	}
	
	//check if table is empty
	public boolean isEmpty()
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM " + sqlHelper.TABLE_NAME, null);
		Boolean rowExists;
		if (mCursor.moveToFirst())
		{
		  rowExists = false;
		} 
		else
		{
		   rowExists = true;
		}
		return rowExists;
	}
	
	//check if dist is empty
	public boolean isDistEmpty()
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM " + sqlHelper.TABLE_RADIUS, null);
		Boolean rowExists;

		if (mCursor.moveToFirst())
		{
		  rowExists = false;

		} else
		{
		   rowExists = true;
		}
		return rowExists;
	}
	
	//add distance to table
	public void insertDist(int id, float distance)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		//getting up the value for insertion
		ContentValues value = new ContentValues();
		value.put(sqlHelper.UID, id);
		value.put(sqlHelper.RADIUS, distance);
		//insert value to table
		db.insert(sqlHelper.TABLE_RADIUS, null, value);
		db.close();
	}
	//delete distance from table
	public void deleteDist(int id)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		//look in table for matching ID to delete
		db.delete(sqlHelper.TABLE_RADIUS,sqlHelper.UID + " = ?", new String[] {String.valueOf(id)});
		db.close();
	}
	//get the distance from table
	public float getDist(int id)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = {sqlHelper.UID, sqlHelper.RADIUS};
		//get the table with id value
		Cursor cursor = db.query(sqlHelper.TABLE_RADIUS, columns, sqlHelper.UID + " = ?", new String[] { String.valueOf(id) }, null, null, null);
		//go to first cursor with info
		if(cursor != null)
			cursor.moveToFirst();
		//construct Data object and return 
		float distance = cursor.getInt(1);
		return distance;
	}
	
	//sqlhelper 
	static class sqlHelper extends SQLiteOpenHelper
	{
		//const names
		private static final String DATABASE_NAME = "TakeMeOutData";
		private static final String TABLE_NAME = "PlaceTable";
		private static final String TABLE_RADIUS = "RadiusTable";
		private static final String UID = "_id";
		private static final String TIME = "Time";
		private static final String STORE_NAME = "Name";
		private static final String RATING = "Rating";
		private static final String TOTAL_RATING = "Total Rating";
		private static final String PHONE_NUM = "Phone";
		private static final String ADDRESS = "Address";
		private static final String PRICE = "Price";
		private static final String PIC_REF = "Pic";
		private static final String STORE_ID = "StoreID";
		private static final String RADIUS = "Radius";
		
		//	private static sqlHelper sInstance;
		private static final int DATABASE_VERSION = 10;

		private String Json = "";

		//command to create tables
		private static final String DATABASE_CREATE = "CREATE TABLE "
				+ TABLE_NAME + "("				//COLUMN NUMBER
				+ UID + " integer primary key autoincrement, "	//0
				+ TIME + " text, " 		//1
				+ STORE_NAME + " text, "		//2 
				+ STORE_ID + " text" //3
				+ ");";
		
		private static final String DATABASE_RADIUS = "CREATE TABLE "
				+ TABLE_RADIUS + "("
				+ UID + " integer primary key, "
				+ RADIUS + " real"
				+ ");";

		public sqlHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.i("sqlHelper", "constructor called");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("OnCREATE", DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_RADIUS);
			Log.i("OnCREATE", "");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_RADIUS + "");
			onCreate(db);
		}

	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public sqlDatabaseHelper(Parcel in)

	{
	}

	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public sqlDatabaseHelper createFromParcel(Parcel in)
		{
			return new sqlDatabaseHelper(in);
		}

		public sqlDatabaseHelper[] newArray(int size)
		{
			return new sqlDatabaseHelper[size];
		}
	};
}
