package ch.unibe.sport.DBAdapter;

import java.util.ArrayList;

import android.content.Context;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.DBAdapter.tables.CourseDays;
import ch.unibe.sport.DBAdapter.tables.CourseIntervals;
import ch.unibe.sport.DBAdapter.tables.FacebookFriends;
import ch.unibe.sport.DBAdapter.tables.FavoriteCourses;
import ch.unibe.sport.DBAdapter.tables.Intervals;
import ch.unibe.sport.DBAdapter.tables.Rating;
import ch.unibe.sport.DBAdapter.tables.SportCourses;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.DBAdapter.tables.ITable;
import ch.unibe.sport.DBAdapter.tables.Courses;

/**
 * 
 * @author Aliaksei Syrel
 */
public class DBStructure {
	public static final String TAG = "DBAdapter";
    
	public static final String DATABASE_NAME = "sports.db";  
	public static final int DATABASE_VERSION = 32;
    
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
		tables.add(new Courses(context));
		tables.add(new SportCourses(context));
		tables.add(new FavoriteCourses(context));
		tables.add(new FacebookFriends(context));
		tables.add(new CourseIntervals(context));
		tables.add(new Intervals(context));
		tables.add(new CourseDays(context));
		tables.add(new AttendedCourses(context));
		tables.add(new Rating(context));
	}
	/*------------------------------------------------------------
	------------------------- G E T T E R S ----------------------
	------------------------------------------------------------*/
    
	public ArrayList<ITable> getTables(){
		assert invariant();
		return this.tables;
	}
}
