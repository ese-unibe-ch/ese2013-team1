package ch.unibe.sport.course.info;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONParser;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.config.Synchronize;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.Time;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class CourseNotificationAlarmManagerReceiver extends BroadcastReceiver {

	public static final String TAG = CourseNotificationAlarmManagerReceiver.class.getName();
	private static final String SHOWN_KEY = "course_notification_shown";
	@Override
	public void onReceive(Context context, Intent intent) {
		Print.log(TAG,"Starting notificaion here");
		Synchronize sync = new Synchronize(context);
		sync.check();
		
		Time before = new Time(2,0);
		Calendar cal = Calendar.getInstance(); 
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);		
		Time currentTime = new Time(hour,minute);
		Print.log(TAG,hour+":"+minute);
		DBAdapter.INST.open(context, TAG);
		AttendedCourses attended = new AttendedCourses(context);
		int[] courseIDs = attended.getAttendedCoursesInMinutes(currentTime, before, new Date());
		DBAdapter.INST.close(TAG);
		if (courseIDs.length == 0) {
			clearShown(context);
			return;
		}
		
		ArrayList<Long> shownIDs = this.getShownIDs(context);
		for (long id : courseIDs){
			if (shownIDs.contains(id)) continue;
			new CourseNotification(context, new Course(context,(int)id)).show();
			shownIDs.add(id);
		}
		saveShownIDs(context, shownIDs);
	}
	
	private void clearShown(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(SHOWN_KEY, "[]");
		editor.commit();
	}
	
	@SuppressWarnings("unchecked")
	private void saveShownIDs(Context context, ArrayList<Long> ids){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(ids);
		Editor editor = prefs.edit();
		editor.putString(SHOWN_KEY, jsonArray.toString());
		editor.commit();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Long> getShownIDs(Context context){
		ArrayList<Long> ids = new ArrayList<Long>();
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			JSONParser parser = new JSONParser();
			JSONArray jsonArary = (JSONArray) parser.parse(prefs.getString(SHOWN_KEY, "[]"));
			Iterator<Long> iterator = jsonArary.iterator();
			while(iterator.hasNext()){
				ids.add(iterator.next());
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return ids;
	}
	
	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, CourseNotificationAlarmManagerReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// Repeat every 10 minutes
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 60 * 5, pi);
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, CourseNotificationAlarmManagerReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
