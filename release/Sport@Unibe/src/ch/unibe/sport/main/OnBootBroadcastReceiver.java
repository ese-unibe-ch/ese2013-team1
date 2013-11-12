package ch.unibe.sport.main;

import ch.unibe.sport.course.info.CourseNotificationAlarmManagerReceiver;
import ch.unibe.sport.utils.Print;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Print.log("Received on boot");
		CourseNotificationAlarmManagerReceiver alarmManager = new CourseNotificationAlarmManagerReceiver();
		alarmManager.setAlarm(context);
	}

}
