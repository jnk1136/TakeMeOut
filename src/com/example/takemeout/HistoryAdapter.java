package com.example.takemeout;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//Used to pop listView
public class HistoryAdapter extends BaseAdapter{

	private List<Data> listOfData;
	private LayoutInflater inflator;
	
	@Override
	public int getCount() {
		return listOfData.size();
	}

	@Override
	public Object getItem(int i) {
		return listOfData.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	TextView time = null;
	TextView name = null;
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		viewHolder holder;
		
		//see if the view is null
		if(view == null)
		{
			//if so, set to right id
			holder = new viewHolder();
			view = inflator.inflate(R.layout.history_custom_layout, null);
			holder.time = (TextView) view.findViewById(R.id.textTime);
			holder.name = (TextView) view.findViewById(R.id.textStoreName);
			view.setTag(holder);
		}
		else
		{
			//load the locations that were stored
			holder = (viewHolder)view.getTag();
		}
		
		//set Text of the layout
		holder.time.setText(listOfData.get(i).getTime());
		holder.name.setText(listOfData.get(i).getPlace().getName());
		return view;
		
	}

	//to hold layout id 
	static class viewHolder
	{
		TextView time;
		TextView name;
	}
	
	//call to pop listview
	public HistoryAdapter(Context context, List<Data> listOfData)
	{
		this.listOfData = listOfData;
		this.inflator = LayoutInflater.from(context);
	}
}
