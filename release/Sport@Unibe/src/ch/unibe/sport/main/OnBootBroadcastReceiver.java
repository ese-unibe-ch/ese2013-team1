package ch.unibe.sport.main;

import ch.unibe.sport.event.info.EventNotificationAlarmManagerReceiver;
import ch.unibe.sport.utils.Print;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Print.log("Received on boot");
		EventNotificationAlarmManagerReceiver alarmManager = new EventNotificationAlarmManagerReceiver();
		alarmManager.setAlarm(context);
	}

}
