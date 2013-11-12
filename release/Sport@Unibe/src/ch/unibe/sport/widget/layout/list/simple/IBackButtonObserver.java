package ch.unibe.sport.widget.layout.list.simple;

public interface IBackButtonObserver {
	public void registerSubject(IBackButtonSubject subject);
	public void removeSubject(IBackButtonSubject subject);
	public void notifyBackButton();
}
