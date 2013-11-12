package ch.unibe.sport.taskmanager;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * 
 * @author Aliaksei Syrel
 */
public final class AsyncTaskManager implements IProgressTracker, OnCancelListener {

	private OnTaskCompletedListener mOnTaskCompleteListener;
	public interface OnTaskCompletedListener {
		public void onTaskCompleted(Task task);
	}
	
	public void setOnTaskCompletedListener(OnTaskCompletedListener l){
		this.mOnTaskCompleteListener = l;
	}
	
	private Task asyncTask;

	/**
	 * Setups task
	 * @param asyncTask - to be setuped
	 */
	public void setupTask(Task asyncTask) {
		this.asyncTask = asyncTask;
		asyncTask.setProgressTracker(this);
		asyncTask.execute();
	}

	/**
	 * Cancels task
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		asyncTask.cancel(true);
		mOnTaskCompleteListener.onTaskCompleted(asyncTask);
		asyncTask = null;
	}

	/**
	 * Resets task after onComplete
	 */
	@Override
	public void onCompleted() {
		mOnTaskCompleteListener.onTaskCompleted(asyncTask);
		asyncTask = null;
	}

	/**
	 * Retains task detachs it from tracker (this) before retaining
	 * @return
	 */
	public Object retainTask() {
		if (asyncTask != null) {
			asyncTask.setProgressTracker(null);
		}
		return asyncTask;
	}

	/**
	 * Restores retained task and attach it to the tracker (this)
	 * @param instance
	 */
	public void handleRetainedTask(Object instance) {
		if (instance instanceof Task) {
			asyncTask = (Task) instance;
			asyncTask.setProgressTracker(this);
		}
	}

	/**
	 * Track current task status
	 * @return true if task is still working or false otherwise
	 */
	public boolean isWorking() {
		return asyncTask != null;
	}
	

	@Override public void onProgress(String message) {}
}