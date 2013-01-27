package ocept.gogo.db;
import java.util.Date;

import ocept.gogo.Go;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class goDB {
	private SQLiteDatabase db;
	private final Context context;
	private final goDBHelper dbhelper;
	public goDB(Context c){
		context = c;
		dbhelper = new goDBHelper(context, Constants.DATABASE_NAME, null,
		Constants.DATABASE_VERSION);
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
			newTaskValue.put(Constants.LAST_CHECKED_NAME, go.LastChecked); //save last checked date as 0
			return db.insert(Constants.TABLE_NAME, null, newTaskValue);
		}
		catch(SQLiteException ex) {
		Log.v("Insert into database exception caught",
				ex.getMessage());
			return -1;
		}
	}
	public long checkGo(int id, boolean isChecked){
		try{
		ContentValues newCheckValue = new ContentValues();
		if(isChecked)
			newCheckValue.put(Constants.LAST_CHECKED_NAME, new Date().getTime());
		else
			newCheckValue.put(Constants.LAST_CHECKED_NAME, 0);
		String sFilter = Constants.KEY_ID+"= " + id;
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
}