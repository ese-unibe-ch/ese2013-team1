package ch.unibe.sport.taskmanager;

import android.os.AsyncTask;

/**
 * Simple AsyncTask wrapper that allows to get onTaskComplete message
 * when AsyncTask is completed
 * 
 * @param <INPUT>
 * @param <B>
 * @param <RESULT>
 * 
 * @author Aliaksei Syrel
 */
public abstract class ObservableAsyncTask<INPUT,E,RESULT> extends AsyncTask<INPUT,E,RESULT>{

	private OnTaskCompletedListener<INPUT,E,RESULT> mOnTaskCompleted;
		
	@Override
	protected void onPostExecute(RESULT result) {
		super.onPostExecute(result);
		if (mOnTaskCompleted != null) mOnTaskCompleted.onTaskCompleted(this);
	}
	
	public ObservableAsyncTask<INPUT,E,RESULT> setOnTaskCompletedListener(OnTaskCompletedListener<INPUT,E,RESULT> l){
		this.mOnTaskCompleted = l;
		return this;
	}

}
