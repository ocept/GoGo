package ocept.gogo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ocept.gogo.db.Constants;
import ocept.gogo.db.goDB;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GoList extends ListActivity {
	goDB dba;
	public static goAdapter myAdapter;
	public String title;
	public String content;
	public String recorddate;
	public static final String BOUNTY_PREFS = "BountyPrefs";
	public static final String TOTAL_BOUNTY_KEY = "TotalBounty";
	
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
        
        //set up context menu
        initCAM();
        
        //load and display total bounty
        SharedPreferences bountyPrefs = getSharedPreferences(BOUNTY_PREFS, MODE_PRIVATE);
        Integer bounty = bountyPrefs.getInt(TOTAL_BOUNTY_KEY, 0);
        
        TextView bountyDisplay = (TextView) findViewById(R.id.totalBountyDisplay);
        bountyDisplay.setText(Integer.toString(bounty));
    }
    
    private void initCAM()
    {
    	final ListView listView = getListView();
    	
    	
    	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    	listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

    		@Override
    	    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
    	        // Here you can do something when items are selected/de-selected,
    	        // such as update the title in the CAB
    			
    			//hightlight rows
    			if(checked) listView.getChildAt(position).setBackgroundColor(Color.DKGRAY);
    			else listView.getChildAt(position).setBackgroundColor(Color.WHITE);    			
    	    }

    		
    	    @Override
    	    
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    	        // Respond to clicks on the actions in the CAB
    	        switch (item.getItemId()) {
    	            case R.id.menu_delete:
    	            	SparseBooleanArray checked =  listView.getCheckedItemPositions();
    	            	List<Integer> ids = new ArrayList<Integer>();
    	    	    	for(int i = 0; i < checked.size(); i++)
    	    	    	{
    	    	    		if(checked.valueAt(i)){
    	    	    			ViewHolder v = (ViewHolder) listView.getChildAt(checked.keyAt(i)).getTag();   
    	    	    			ids.add(v.mGo.KeyId);
    	    	    		}
    	    	    	}
    	            	
    	    			dba.deleteGos(ids);
    	                mode.finish(); // Action picked, so close the CAB
    	                return true;
    	            default:
    	                return false;
    	        }
    	    }

    	    @Override
    	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    	        // Inflate the menu for the CAB
    	        MenuInflater inflater = mode.getMenuInflater();
    	        inflater.inflate(R.menu.go_list_context, menu);
    	        return true;
    	    }
    	    
    	    @Override
    	    public void onDestroyActionMode(ActionMode mode) {
    	        // Here you can make any necessary updates to the activity when
    	        // the CAB is removed. By default, selected items are deselected/unchecked.
    	    	SparseBooleanArray checked =  listView.getCheckedItemPositions();
    	    	for(int i = 0; i < checked.size(); i++)
    	    	{
    	    		if(checked.valueAt(i))
    	    				listView.getChildAt(checked.keyAt(i)).setBackgroundColor(Color.WHITE);   
    	    	}
    	    }

    	    @Override
    	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	        // Here you can perform updates to the CAB due to
    	        // an invalidate() request
    	        return false;
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
    	private ArrayList<Go> goArray;
    	public goAdapter(Context context) {
	    	mInflater = LayoutInflater.from(context);
	    	goArray = new ArrayList<Go>();
	    	getdata();
    	}
    	public void getdata(){ //fetch data from DB
	    	Cursor c = dba.getGos();
	    	startManagingCursor(c);
	    	if(c.moveToFirst()){
		    	do{
		    		try{
				    	String title = c.getString(c.getColumnIndex(Constants.TITLE_NAME));
				    	String desc = c.getString(c.getColumnIndex(Constants.CONTENT_NAME));
				    	Long lastChecked = c.getLong(c.getColumnIndex(Constants.LAST_CHECKED_NAME));
				    	int id = c.getInt(c.getColumnIndex(Constants.KEY_ID));
				    	int bounty = c.getInt(c.getColumnIndex(Constants.BOUNTY_NAME));
						Go temp = new Go(title,desc, lastChecked, id, bounty);
						goArray.add(temp);
		    		}
		    		catch(java.lang.RuntimeException ex){
		    			Log.e(ACTIVITY_SERVICE, "Failed to read go from db", ex);
		    		}
					} while(c.moveToNext());
				}
			}
    	@Override
    	public int getCount() {return goArray.size();} //called by listview
    	public Go getItem(int i) {return goArray.get(i);} //called to get 1 list item
    	public long getItemId(int i) {return i;}
    	public View getView(int position, View arg1, ViewGroup arg2) { //called by listview to get view for 1 list item
    		final int pos = position;
	    	final ViewHolder holder;
	    	View v = arg1;
	    	if ((v == null) || (v.getTag() == null)) {
		    	v = mInflater.inflate(R.layout.go_row, null);
		    	holder = new ViewHolder();
		    	holder.mTitle = (TextView)v.findViewById(R.id.name);
		    	holder.mDesc = (TextView)v.findViewById(R.id.descText);
		    	holder.mDone = (CheckBox)v.findViewById(R.id.checkDone);
		    	holder.mBounty = (TextView)v.findViewById(R.id.bountyDisplay);
		    	v.setTag(holder);
	    	} else {
	    		holder = (ViewHolder) v.getTag();
	    	}
	    	holder.mGo = getItem(position);
	    	holder.mTitle.setText(holder.mGo.Name);
	    	holder.mDesc.setText(holder.mGo.Desc);
	    	holder.mBounty.setText(Integer.toString(holder.mGo.Bounty));
	    	
	    	//only check if time of last check is today
	    	Calendar Today = new GregorianCalendar();
	    	Today.setTime(new Date());
	    	Today.add(Calendar.HOUR_OF_DAY, -Calendar.HOUR_OF_DAY);
	    	Today.add(Calendar.MINUTE, -Calendar.MINUTE);
	    	if(new Date(holder.mGo.LastChecked).after(Today.getTime())){
	    		holder.mDone.setChecked(true);
	    	}
	    	else{
	    		holder.mDone.setChecked(false);
	    	}
	    	v.setTag(holder);
	    	
	    	//bind chekcbox clicks
	    	holder.mDone.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	    		@Override
	    		public void onCheckedChanged(CompoundButton b, boolean isChecked){
	    			Toast.makeText(getBaseContext(), "Checked "+pos , Toast.LENGTH_SHORT).show();
	    			dba.checkGo(holder.mGo.KeyId, isChecked, holder.mGo.Bounty);
	    		}
	    		
	    	});
	    	
	    	return v;
    	}
    	
    }
	public class ViewHolder{
		Go mGo;
		TextView mTitle;
		TextView mDesc;
		CheckBox mDone;
		TextView mBounty;
	}
}


