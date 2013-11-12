package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.course.Course;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class CourseNotification {
	private final Context context;
	private final Course course;

	private Notification notification;

	public CourseNotification (Context context, Course course){
		this.context = context;
		this.course = course;
		initView();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initView() {
		Intent intent = new Intent(context, CourseInfoActivity.class);
		intent.putExtra(CourseInfoActivity.COURSE_ID_PARAM_NAME, course.getCourseID());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, course.getCourseID(),intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification.Builder notificationBuilder = new Notification.Builder(context)
				.setContentTitle("Don't forget about sport today!")
				.setContentText(course.getCourse())
				.setTicker(course.getCourse())
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
		notificationManager.notify(course.getCourseID(), notification);
	}
}
