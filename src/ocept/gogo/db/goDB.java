package ocept.gogo.db;
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
	public long insertdiary(String title, String content)
	{
		try{
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Constants.TITLE_NAME, title);
			newTaskValue.put(Constants.CONTENT_NAME, content);
			newTaskValue.put(Constants.DATE_NAME,
			java.lang.System.currentTimeMillis());
			return db.insert(Constants.TABLE_NAME, null, newTaskValue);
		}
		catch(SQLiteException ex) {
		Log.v("Insert into database exception caught",
		ex.getMessage());
		return -1;
		}
	}
	public long insertGo(Go go)
	{
		
		return 1;
	}
	public Cursor getdiaries()
	{
		Cursor c = db.query(Constants.TABLE_NAME, null, null,
		null, null, null, null);
		return c;
	}
}