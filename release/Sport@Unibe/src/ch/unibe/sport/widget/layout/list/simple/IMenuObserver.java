package ch.unibe.sport.widget.layout.list.simple;

public interface IMenuObserver {
	public void notifyEntryAdd(IEntry entry);
	public void notifyEntryHeaderClicked(IEntryView entryView);
	public void notifyToCollapse();
}
