package ch.unibe.sport.widget.layout.list.simple;

public interface IBackButtonSubject {
	public void registerObserver(IBackButtonObserver observer);
	public void removeObserver(IBackButtonObserver observer);
	public void notifyObserversBackButton();
}
