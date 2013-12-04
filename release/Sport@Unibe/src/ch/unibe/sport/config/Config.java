package ch.unibe.sport.config;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import ch.unibe.sport.course.info.EventNotificationAlarmManagerReceiver;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.utils.Print;

public enum Config implements Database.OnDatabaseInitializedListener, System.OnSystemInitializedListener {
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
	
	public interface OnConfigInitializedListener {
		public void onConfigInitialized();
	}
	
	public OnConfigInitializedListener mOnConfigInitializedListener;
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	public void init(Context context){
		assert context != null;
		
		runningActivities = new LinkedList<String>();
		preferences = new ArrayList<IPreferences>();
		
		this.SYSTEM = new System(context,this);
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
		
		EventNotificationAlarmManagerReceiver alarmManager = new EventNotificationAlarmManagerReceiver();
		alarmManager.setAlarm(context);
		INIT = true;
	}
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/

	public boolean readyToStart(){
		return (Database.INIT && System.INIT);
	}
	
	public boolean isRunning(Context context,String activityName){
		if (runningActivities == null) {
			init(context);
			Print.log(TAG,"Reinit Config from: "+activityName);
		}
		this.SYNCHRONIZE.check();
		return runningActivities.contains(activityName);
	}

	private void checkInitialization(){
		if (readyToStart() && this.mOnConfigInitializedListener != null){
			this.mOnConfigInitializedListener.onConfigInitialized();
		}
	}
	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void onDatabaseInitialized() {
		Print.log("DB is initialized");
		checkInitialization();
	}
	
	@Override
	public void onSystemInitialized() {
		Print.log("System is initialized");
		checkInitialization();
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
	
	/**
	 * Finishes all activities in application
	 * @param activity
	 */
	public void finishApp(ProxySherlockFragmentActivity activity){
		if (activity != null && runningActivities != null && runningActivities.size() > 0){
			Print.log(TAG,"Finishing application");
			/* sends finish message to all running activities */
			activity.send(MessageFactory.finishActivities(TAG, runningActivities));
		}
	}
	
	public void reInit(){
		for (IPreferences preference : preferences){
			preference.reInit();
		}
	}
	
	public void setOnConfigInitializedListener(OnConfigInitializedListener l){
		this.mOnConfigInitializedListener = l;
	}
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	
	public String toString(){
		return "";
	}
}
