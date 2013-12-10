package ch.unibe.sport.config;

import java.util.concurrent.ExecutionException;

import com.parse.ParseException;

import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.os.AsyncTask;

public class Synchronize extends Preferences {
	private static final String TAG = "synchronize";

	private static final String SYNC_NAME = "sync";

	public boolean SYNCHRONIZED;
	public boolean SYNC_LOCK = false;

	public Synchronize(Context context) {
		super(TAG, context);
		SYNCHRONIZED = readSynchronized();
	}
	
	/**
	 * Checks synchronized status, if false, checks the task's status 
	 * and sets synchronized status.
	 */
	@Override
	public void check() {
		if (SYNCHRONIZED) return;
		if (SYNC_LOCK) return;
		if (!Utils.haveNetworkConnection(context)) return;
		AttendedSynchronizer synchronizer = new AttendedSynchronizer();
		synchronizer.setOnTaskCompletedListener(new OnTaskCompletedListener<Void,Void,Boolean>(){
			@Override
			public void onTaskCompleted(AsyncTask<Void, Void, Boolean> task) {
				boolean result = false;
				try {
					result = task.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if (result){
					setSynchronized(true);
				}
				else {
					setSynchronized(false);
					Print.log(TAG,"Error adding courses");
				}
			}
		});
		synchronizer.execute();
	}
	
	
	private class AttendedSynchronizer extends ObservableAsyncTask<Void,Void,Boolean> {
		@Override
		protected void onPreExecute(){
			SYNC_LOCK = true;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				syncAttended();
				return true;
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			SYNC_LOCK = false;
			super.onPostExecute(result);
		}
		
	}

	/**
	 * Synchronizes attended courses with Parse back-end server
	 * @throws ParseException 
	 */
	private void syncAttended() throws ParseException {
		if (!Utils.haveNetworkConnection(context)) return;
		
	}

	private boolean readSynchronized() {
		return this.getBoolean(SYNC_NAME);
	}

	public void setSynchronized(boolean sync) {
		this.SYNCHRONIZED = sync;
		this.save(SYNC_NAME, sync);
	}
	
	@Override public void reInit() {}
}
