package ocept.gogo.db;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

import ocept.gogo.Go;
import ocept.gogo.GoList;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class goDB extends Observable{
	private SQLiteDatabase db;
	private final Context context;
	private final goDBHelper dbhelper;
	private static ArrayList<Observer> observers = new ArrayList<Observer>();
	public goDB(Context c){
		context = c;
		dbhelper = new goDBHelper(context, Constants.DATABASE_NAME, null,
		Constants.DATABASE_VERSION);
	}
    @Override
    public void addObserver(Observer observer) {
           observers.add(observer);

    }

    @Override
    public void deleteObserver(Observer observer) {
           observers.remove(observer);

    }
    @Override
    public void notifyObservers() {
           for (Observer ob : observers) {
                  System.out.println("Notifying observers on data change ");
                  try{
                	  ob.update(this, null);
                  }
                  catch(java.util.ConcurrentModificationException ex){
                	  Log.w("multiple observers modifying at once?", ex);
                  }
                  
           }

    }

	public void close()
	{
		db.close();
	}
	public void open() throws SQLiteException
	{
		try {
			db = dbhelper.getWritableDatabase();
			} 
		catch(SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}
	public long insertGo(Go go)
	{
		try{
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.TITLE_NAME, go.Name);
			newTaskValue.put(Constants.CONTENT_NAME, go.Desc);
			newTaskValue.put(Constants.LAST_CHECKED_NAME, go.LastChecked);
			newTaskValue.put(Constants.BOUNTY_NAME, go.Bounty);
			long returnValue =  db.insert(Constants.TABLE_NAME, null, newTaskValue);
			notifyObservers();
			return returnValue;
		}
		catch(SQLiteException ex) {
		Log.v("Insert into database exception caught",
				ex.getMessage());
			return -1;
		}
	}
	public long checkGo(int id, boolean isChecked, int goBounty){
		try{
			ContentValues newCheckValue = new ContentValues();
			SharedPreferences bountyPrefs = context.getSharedPreferences(GoList.BOUNTY_PREFS, 0);
			int totalBounty = bountyPrefs.getInt(GoList.TOTAL_BOUNTY_KEY, 0);
			
			if(isChecked){
				newCheckValue.put(Constants.LAST_CHECKED_NAME, new Date().getTime());
				totalBounty += goBounty;
			}
			else{
				newCheckValue.put(Constants.LAST_CHECKED_NAME, 0);
				totalBounty -= goBounty;
			}
			Editor bountyPrefsEdit = bountyPrefs.edit();
			bountyPrefsEdit.putInt(GoList.TOTAL_BOUNTY_KEY, totalBounty);
			bountyPrefsEdit.apply();
			
			String sFilter = Constants.KEY_ID+"= " + Integer.toString(id);
			db.update(Constants.TABLE_NAME, newCheckValue, sFilter, null);
			
			return 1;
		}
		catch(SQLiteException ex){
			Log.e("Update db exception", ex.getMessage());
			return -1;
		}
	}
	
	public Cursor getGos()
	{
		Cursor c = db.query(Constants.TABLE_NAME, null, null,
		null, null, null, null);
		return c;
	}
	public long deleteGos(List<Integer> ids)
	{
		try{
			for(Iterator<Integer> i = ids.iterator(); i.hasNext();){
				db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=" + i.next().toString(), null);
			}
			return 1;
		}
		catch(SQLiteException ex) {
		Log.v("Delete from database exception caught",
				ex.getMessage());
			return -1;
		}
	}
}