package ch.unibe.sport.taskmanager;

import android.os.AsyncTask;

public interface OnTaskCompletedListener<INPUT,E,RESULT> {
	public void onTaskCompleted(AsyncTask<INPUT,E,RESULT> task);
}
