package ch.unibe.sport.DBAdapter;

import java.util.ArrayList;

import android.content.Context;
import ch.unibe.sport.DBAdapter.tables.EventAttended;
import ch.unibe.sport.DBAdapter.tables.EventDaysOfWeek;
import ch.unibe.sport.DBAdapter.tables.EventKew;
import ch.unibe.sport.DBAdapter.tables.EventPeriods;
import ch.unibe.sport.DBAdapter.tables.EventPlaces;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.DBAdapter.tables.EventIntervals;
import ch.unibe.sport.DBAdapter.tables.EventRating;
import ch.unibe.sport.DBAdapter.tables.SportEvents;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.DBAdapter.tables.ITable;
import ch.unibe.sport.DBAdapter.tables.Events;

/**
 * 
 * @author Team 1
 */
public class DBStructure {
	public static final String TAG = "DBAdapter";
    
	public static final String DATABASE_NAME = "sports.db";  
	public static final int DATABASE_VERSION = 36;
    
	public static final String WHERE_DELIMITER = " = ?";
	public static final String AND_CLAUSE = " AND ";
	
	private ArrayList<ITable> tables;
	private Context context;
	
	private boolean invariant(){
		return tables != null;
	}
	
	public DBStructure(Context context){
		this.context = context;
		initTableList();
		assert invariant();
	}
	
	/**
	 * Initializes ArrayList, that holds all tables
	 */
	private void initTableList(){
		tables = new ArrayList<ITable>();
		tables.add(new Sports(context));
		tables.add(new Events(context));
		tables.add(new SportEvents(context));
		tables.add(new EventFavorite(context));
		tables.add(new EventIntervals(context));
		tables.add(new EventPlaces(context));
		tables.add(new EventKew(context));
		tables.add(new EventPeriods(context));
		tables.add(new EventDaysOfWeek(context));
		tables.add(new EventAttended(context));
		tables.add(new EventRating(context));
	}
	/*------------------------------------------------------------
	------------------------- G E T T E R S ----------------------
	------------------------------------------------------------*/
    
	public ArrayList<ITable> getTables(){
		assert invariant();
		return this.tables;
	}
}
