package ch.unibe.sport.main.initialization;

public interface IInitializationTask {
	public void execute(InitializationCallback callback,Object... params);
	public boolean isCompleted();
}
