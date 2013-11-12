package ch.unibe.sport.config;

import java.util.ArrayList;
import java.util.LinkedList;

import ch.unibe.sport.course.info.CourseNotificationAlarmManagerReceiver;
import ch.unibe.sport.utils.Print;
import android.content.Context;

public enum Config implements Database.OnDatabaseInitializedListener{
	INST;
	public static final String TAG = Config.class.getName();
	public static final String PACKAGE_NAME = "ch.unibe.sport";
	
	private Config(){}

	public System SYSTEM;
	public Strings STRINGS;
	public Display DISPLAY;
	public Database DATABASE;
	public Calendar CALENDAR;
	public Synchronize SYNCHRONIZE;
	
	public boolean REINIT = false;
	public boolean INIT = false;
	
	private LinkedList<String> runningActivities;
	private ArrayList<IPreferences> preferences;
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	public void init(Context context){
		assert context != null;
		runningActivities = new LinkedList<String>();
		preferences = new ArrayList<IPreferences>();
		
		this.SYSTEM = new System(context);
		this.STRINGS = new Strings(context);
		this.DISPLAY = new Display(context);
        DISPLAY.reInit();
		this.DATABASE = new Database(context,this);
		this.CALENDAR = new Calendar(context);
		this.SYNCHRONIZE = new Synchronize(context);
		
		preferences.add(SYSTEM);
		preferences.add(STRINGS);
		preferences.add(DISPLAY);
		preferences.add(DATABASE);
		preferences.add(CALENDAR);
		preferences.add(SYNCHRONIZE);
		
		CourseNotificationAlarmManagerReceiver alarmManager = new CourseNotificationAlarmManagerReceiver();
		alarmManager.setAlarm(context);
		INIT = true;
	}
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/

	public boolean isRunning(Context context,String activityName){
		if (runningActivities == null) {
			init(context);
			Print.log(TAG,"Reinit Config from: "+activityName);
		}
		this.SYNCHRONIZE.check();
		return runningActivities.contains(activityName);
	}

	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void onDatabaseInitialized() {
		
	}
	

	public void addToRuningActivitiesList(String activityName){
		assert !this.runningActivities.contains(activityName);
		Print.log(TAG,"Starting: "+activityName);
		this.runningActivities.add(activityName);
	}
	
	public void removeFromRuningActivitiesList(String activityName){
		Print.log(TAG,"Destroying: "+activityName);
		this.runningActivities.remove(activityName);
	}
	
	public void finishApp(){
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void reInit(){
		for (IPreferences preference : preferences){
			preference.reInit();
		}
	}
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	
	public String toString(){
		return "";
	}
}
