package ch.unibe.sport.DBAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.Stack;

import ch.unibe.sport.DBAdapter.tables.EventAttended;
import ch.unibe.sport.DBAdapter.tables.EventDaysOfWeek;
import ch.unibe.sport.DBAdapter.tables.EventKew;
import ch.unibe.sport.DBAdapter.tables.EventPeriods;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.DBAdapter.tables.EventIntervals;
import ch.unibe.sport.DBAdapter.tables.EventPlaces;
import ch.unibe.sport.DBAdapter.tables.EventRating;
import ch.unibe.sport.DBAdapter.tables.SportEvents;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.DBAdapter.tables.Events;
import ch.unibe.sport.utils.Print;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * Database adapter, which controls access to SQLiteDatabase.
 * It's safe to open and close database from everywhere,
 * but it's important, that the number of opens == number of closes
 * 
 * @version 1.1 2013-09-21
 * @author Team 1 2013
 */
public enum DBAdapter {
	INST;
	public static final String TAG = DBAdapter.class.getName();
	private static final boolean DEBUG = false;
	private static final boolean TRANSACTION_HARD_LOCK = true;
	
	public volatile Stack<OpenRequest> openRequests;
	public volatile Stack<OpenRequest> transactionRequests;
	private ArrayList<OnOpenedListener> mOnOpenedListeners;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	private boolean open = false;
	private boolean lock = false;
	private Context context;
	
	private String customDBName;
	
	public void useCustomDatabase(String name){
		this.customDBName = name;
	}
	
	public void useDefaultDatabase(){
		this.customDBName = null;
	}
	
	public interface OnOpenedListener {
		public void onOpened();
		public void onClosed();
	}
	
	private DBAdapter(){
		openRequests = new Stack<OpenRequest>();
		transactionRequests = new Stack<OpenRequest>();
	}
			
	public void setOnOpenedListener(OnOpenedListener l){
		if (mOnOpenedListeners == null) mOnOpenedListeners = new ArrayList<OnOpenedListener>();
		if (l != null) this.mOnOpenedListeners.add(l);
	}
	
	public SQLiteDatabase getDB(){
		return db;
	}
	
	public Context getContext(){
		return this.context;
	}
  
	/**
	 * Tries to open writable database. Adds a new open request in stack
	 * to control open-close pairs.
	 * @param context
	 * @return DBAdapter instance
	 * @throws SQLException
	 */
	public DBAdapter open(Context context,String tag) throws SQLException {
		if (TRANSACTION_HARD_LOCK && lock) return this;
		if (!open){
			if (customDBName == null){
				this.DBHelper = new DatabaseHelper(context);
			}
			else {
				this.DBHelper = new DatabaseHelper(context,this.customDBName);
			}
			db = DBHelper.getWritableDatabase();
			open = true;
			if (mOnOpenedListeners != null) {
				for (OnOpenedListener l : mOnOpenedListeners){
					l.onOpened();
				}
			}
		}
		openRequests.push(new OpenRequest(tag));
		if(DEBUG) Print.log(TAG,"OpenRequest from "+tag);
		return this;  
	}
 
	/**
	 * Tries to close writable database. Pops open request from stack
	 * to control open-close pairs. If stack is empty closing will
	 * be failed with an error
	 * @return true - if database was successfully closed, otherwise false
	 */
	public void close(String tag){
		if (TRANSACTION_HARD_LOCK && lock) return;
		try {
			OpenRequest request = openRequests.pop();
			if(DEBUG) Print.log(TAG,"CloseRequest of " + request.tag);
		}
		catch (EmptyStackException e){
			Print.err(TAG,"Database couldn't be closed because it's not open");
			e.printStackTrace();
		}
		if (!this.lock && open && openRequests.size() == 0){
			DBHelper.close();
			open = false;
			if (mOnOpenedListeners != null) {
				for (OnOpenedListener l : mOnOpenedListeners){
					l.onClosed();
				}
			}
		}
	}
	
	/**
	 * Executes all queries such as "DROP TABLE" or "CREATE TABLE"
	 * @param str - SQL query to be executed
	 */
	public void exec(String str){
		db.execSQL(str);
	}
	
	/**
	 * Begins global database transaction. In this mode database
	 * become locked for close requests. If you want to close connection
	 * to database use endTransaction(). begin-end transactions should
	 * go in pairs.
	 */
	public void beginTransaction(Context context,String tag){
		transactionRequests.push(new OpenRequest(tag));
		if(DEBUG) Print.log(TAG,"[DB] BeginTransaction from "+tag);
		if (!this.lock) {
			this.open(context, tag);
			this.lock = true;
		}
	}
	
	public void endTransaction(String tag){
		try {
			OpenRequest request = transactionRequests.pop();
			if(DEBUG) Print.log(TAG,"[DB] EndTransaction of " + request.tag);
		}
		catch (EmptyStackException e){
			Print.err(TAG,"Database transaction couldn't be ended because it's not open");
			e.printStackTrace();
		}
		if (this.lock && transactionRequests.size() == 0){
			this.lock = false;
			this.close(tag);
		}
	}

	/********************************************************************************************************************/
	/****************************************************B A C K U P*****************************************************/
	/********************************************************************************************************************/
	public boolean dbBackup(){
		if (this.isExternalStorageAvail()) new ExportDatabaseFileTask().execute();
		else return false;
		return true;
	}
	
	private boolean isExternalStorageAvail() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper  {
		DatabaseHelper(Context context) {  
			super(context, DBStructure.DATABASE_NAME, null, DBStructure.DATABASE_VERSION);  
		}
		
		DatabaseHelper(Context context,String name) {  
			super(context, name, null, DBStructure.DATABASE_VERSION);  
		}
		
		@Override  
		public void onCreate(SQLiteDatabase db) {
			System.out.println("Create db");
			create(db);
		}
		
		@Override  
		public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {  
			Log.w(DBStructure.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			drop(db);
            create(db);
		}
	}
	
	public static void create(SQLiteDatabase db) {
		db.execSQL(Sports.CREATE);
		db.execSQL(Events.CREATE);
		db.execSQL(SportEvents.CREATE);
		db.execSQL(EventFavorite.CREATE);
		db.execSQL(EventIntervals.CREATE);
		db.execSQL(EventPlaces.CREATE);
		db.execSQL(EventAttended.CREATE);
		db.execSQL(EventKew.CREATE);
		db.execSQL(EventDaysOfWeek.CREATE);
		db.execSQL(EventPeriods.CREATE);
		db.execSQL(EventRating.CREATE);
	}
	
	public static void drop(final SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS "+Sports.NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Events.NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SportEvents.NAME);
        db.execSQL("DROP TABLE IF EXISTS "+EventFavorite.NAME);
        db.execSQL("DROP TABLE IF EXISTS "+EventIntervals.NAME);
        db.execSQL("DROP TABLE IF EXISTS "+EventPlaces.NAME);
		db.execSQL("DROP TABLE IF EXISTS "+EventAttended.NAME);
		db.execSQL("DROP TABLE IF EXISTS "+EventKew.NAME);
		db.execSQL("DROP TABLE IF EXISTS "+EventDaysOfWeek.NAME);
		db.execSQL("DROP TABLE IF EXISTS "+EventPeriods.NAME);
		db.execSQL("DROP TABLE IF EXISTS "+EventRating.NAME);
	}
	
	private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(final String... args) {
			File dbFile = new File(db.getPath());
			String dbName = DBStructure.DATABASE_NAME;
			File exportDir = new File(Environment.getExternalStorageDirectory(), "Sport@Unibe/");
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
			File file = new File(exportDir, dbName);
			try {
				file.createNewFile();
				this.copyFile(dbFile, file);
				return true;
			}
			catch (IOException e) {
				return false;
			}
		}

		@SuppressWarnings("resource")
		void copyFile(File src, File dst) throws IOException {
			FileChannel inChannel = new FileInputStream(src).getChannel();
			FileChannel outChannel = new FileOutputStream(dst).getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			}
			finally {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			}
		} 
	}
	
	public class OpenRequest{
		protected final String time;
		protected final String tag;
		protected OpenRequest(String tag){
			this.time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			this.tag = tag;
		}
		
		@Override
		public String toString(){
			return time +" in "+tag;
		}
	}
}


