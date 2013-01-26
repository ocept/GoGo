package ocept.gogo;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;

import ocept.gogo.db.Constants;
import ocept.gogo.db.goDB;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
//import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class GoList extends ListActivity {
	goDB dba;
	public static goAdapter myAdapter;
	public String title;
	public String content;
	public String recorddate;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	dba = new goDB(this);
    	dba.open();
    	setContentView(R.layout.activity_go_list);
        super.onCreate(savedInstanceState);
        
        myAdapter = new goAdapter(this);
        this.setListAdapter(myAdapter);
        
        //connect up buttons
        Button newGoButton = (Button) findViewById(R.id.newGoButton);
        newGoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), NewGo.class); //TODO chekc 1st parameter
				startActivity(intent);
			}
		});
    }
    
    @Override
    protected void onResume(){
    	//myAdapter.notifyDataSetChanged();
    	super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_go_list, menu);
        return true;
    }
    public class goAdapter extends BaseAdapter {
    	private LayoutInflater mInflater;
    	private ArrayList<Go> diaries;
    	public goAdapter(Context context) {
	    	mInflater = LayoutInflater.from(context);
	    	diaries = new ArrayList<Go>();
	    	getdata();
    	}
    	public void getdata(){ //fetch data from DB
	    	Cursor c = dba.getdiaries();
	    	startManagingCursor(c);
	    	if(c.moveToFirst()){
		    	do{
		    		try{
				    	String title = c.getString(c.getColumnIndex(Constants.TITLE_NAME));
				    	String desc = c.getString(c.getColumnIndex(Constants.CONTENT_NAME));
				    	Date lastChecked = new Date(c.getLong(c.getColumnIndex(Constants.LAST_CHECKED_NAME)));
						Go temp = new Go(title,desc);
						diaries.add(temp);
		    		}
		    		catch(java.lang.RuntimeException ex){
		    			Log.e(ACTIVITY_SERVICE, "Failed to read go from db", ex);
		    		}
					} while(c.moveToNext());
				}
			}
    	@Override
    	public int getCount() {return diaries.size();} //called by listview
    	public Go getItem(int i) {return diaries.get(i);} //called to get 1 list item
    	public long getItemId(int i) {return i;}
    	public View getView(int arg0, View arg1, ViewGroup arg2) { //called by listview to get view for 1 list item
	    	final ViewHolder holder;
	    	View v = arg1;
	    	if ((v == null) || (v.getTag() == null)) {
		    	v = mInflater.inflate(R.layout.go_row, null);
		    	holder = new ViewHolder();
		    	holder.mTitle = (TextView)v.findViewById(R.id.name);
		    	holder.mDesc = (TextView)v.findViewById(R.id.descText);
		    	holder.mDone = (CheckBox)v.findViewById(R.id.checkDone);
		    	v.setTag(holder);
	    	} else {
	    		holder = (ViewHolder) v.getTag();
	    	}
	    	holder.mdiary = getItem(arg0);
	    	holder.mTitle.setText(holder.mdiary.Name);
	    	holder.mDesc.setText(holder.mdiary.Desc); //TODO, replace w/ desc
	    	v.setTag(holder);
	    	
	    	return v;
    	}
    	public class ViewHolder {
    		Go mdiary;
    		TextView mTitle;
    		TextView mDesc;
    		CheckBox mDone;
		}
    }
}


