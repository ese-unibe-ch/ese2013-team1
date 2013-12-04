package ch.unibe.sport.main.initialization;

public interface InitializationProgressListener {
	/**
	 * 
	 * @param task - completed task
	 * @param index - task index number in task pool
	 * @param count - all number of tasks
	 */
	public void onTaskCompleted(IInitializationTask task, int index, int count,InitializationException exception);
	public void onAllCompleted(int successfull, int count);
}
