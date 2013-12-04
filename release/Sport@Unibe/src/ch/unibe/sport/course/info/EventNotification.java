package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.core.Event;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class EventNotification {
	private final Context context;
	private final Event event;

	private Notification notification;

	public EventNotification (Context context, Event event){
		this.context = context;
		this.event = event;
		initView();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initView() {
		Intent intent = new Intent(context, EventInfoActivity.class);
		intent.putExtra(EventInfoActivity.EVENT_ID_PARAM_NAME, event.getEventID());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, event.getEventID(),intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification.Builder notificationBuilder = new Notification.Builder(context)
				.setContentTitle("Don't forget about sport today!")
				.setContentText(event.getEventName())
				.setTicker(event.getEventName())
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_launcher);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			notification = notificationBuilder.build();
		}
		else {
			notification = notificationBuilder.getNotification();
		}
	}
	
	public void show(){
		assert notification != null;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(event.getEventID(), notification);
	}
}
