package ch.unibe.sport.widget.layout.list.simple;

import ch.unibe.sport.widget.layout.list.simple.List.OnEntryClickListener;
import android.view.View;
import android.view.View.OnClickListener;

public interface IEntry {
	public void add(IEntry entry);
	public boolean isLeaf();
	public boolean isComposite();
	public View getHeaderView();
	public View getView();
	public String getName();
	public void setOnClickListener(OnClickListener l);
	public void setOnEntryClickListener(OnEntryClickListener l);
	public void setView(IEntryView entryView);
	public void registerObserver(IMenuObserver observer);
	public void removeObserver(IMenuObserver observer);
	public void notifyObserversEntryAdd(IEntry entry);
	public void notifyObserversHeaderClicked(IEntryView entryView);
	public void notifyObserversToCollapse();
	public boolean isExpanded();
	public void registerBackButtonSubject(IBackButtonSubject subject);
	public void removeBackButtonSubject(IBackButtonSubject subject);
}
