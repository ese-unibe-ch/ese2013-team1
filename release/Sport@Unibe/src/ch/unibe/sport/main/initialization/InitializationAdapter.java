package ch.unibe.sport.main.initialization;

import java.util.ArrayList;

public class InitializationAdapter {
	
	private ArrayList<IInitializationTask> mTasks;
	private int mCurrentTaskIndex = 0;
	private int mSuccessfullyCompleted = 0;
	
	private boolean breakOnError = false;
	
	private final TaskCallback mTaskCallback = new TaskCallback();
	private InitializationProgressListener mInitializationProgressListener;
	
	public InitializationAdapter(){
		this.mTasks = new ArrayList<IInitializationTask>();
	}
	
	public void addTask(IInitializationTask task){
		assert task != null;
		this.mTasks.add(task);
	}
	
	public int getTasksCount(){
		return mTasks.size();
	}
	
	public void setInitializationProgressListener(InitializationProgressListener l){
		mInitializationProgressListener = l;
	}
	
	public void setBreakOnError(boolean breakOnError){
		this.breakOnError = breakOnError;
	}
	
	/**
	 * Executes all tasks one by one.
	 */
	public void execute(){
		initExecution();
	}
	
	private void initExecution(){
		this.mCurrentTaskIndex = 0;
		this.mSuccessfullyCompleted = 0;
		execute(0);
	}
	
	private void execute(int index){
		/* all tasks executed */
		if (index >= getTasksCount()){
			if (mInitializationProgressListener != null){
				mInitializationProgressListener.onAllCompleted(this.mSuccessfullyCompleted,getTasksCount());
			}
			return;
		}
		
		/* executing task with 'index' index number */
		
		// if task isn't yet completed, than execute it
		if (!mTasks.get(index).isCompleted()){
			mTasks.get(index).execute(mTaskCallback);
		}
		// task already executed, skip it
		else {
			this.mCurrentTaskIndex++;
			this.mSuccessfullyCompleted++;
			execute(mCurrentTaskIndex);
		}
	}
	
	private class TaskCallback implements InitializationCallback {

		@Override
		public void onTaskCompleted(IInitializationTask task,InitializationException exception) {
			if (mInitializationProgressListener != null){
				mInitializationProgressListener.onTaskCompleted(task, mCurrentTaskIndex, getTasksCount(),exception);
			}
			if (exception == null) mSuccessfullyCompleted++;
			// breaking execution if exception not null and and break on error flag is true
			else if (breakOnError){
				if (mInitializationProgressListener != null){
					mInitializationProgressListener.onAllCompleted(mSuccessfullyCompleted,getTasksCount());
				}
				return;
			}
			
			mCurrentTaskIndex++;
			execute(mCurrentTaskIndex);
		}
	}
}
