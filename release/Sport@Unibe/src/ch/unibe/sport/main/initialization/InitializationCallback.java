package ch.unibe.sport.main.initialization;

public interface InitializationCallback {
	public void onTaskCompleted(IInitializationTask task,InitializationException exception);
}
