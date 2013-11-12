package ch.unibe.sport.taskmanager;

import android.os.AsyncTask;

/**
 * UI-Thread task, that is used with dialogs to controll task progress
 * @author Aliaksei Syrel
 *
 */
public abstract class Task extends AsyncTask<Void, String, Boolean> {

	private Boolean result;
	private String progressMessage;
	private IProgressTracker progressTracker;

	public Task() {
		progressMessage = "Please wait...";
	}

	/**
	 * Attach progress tracker to this task
	 * @param progressTracker - to be attached
	 */
	public void setProgressTracker(IProgressTracker progressTracker) {
		this.progressTracker = progressTracker;
		if (progressTracker != null) {
			progressTracker.onProgress(progressMessage);
			if (result != null) {
				progressTracker.onCompleted();
			}
		}
	}

	@Override
	protected void onCancelled() {
		progressTracker = null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		progressMessage = values[0];
		if (progressTracker != null) {
			progressTracker.onProgress(progressMessage);
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		this.result = result;
		if (progressTracker != null) {
			progressTracker.onCompleted();
		}
		progressTracker = null;
	}
}