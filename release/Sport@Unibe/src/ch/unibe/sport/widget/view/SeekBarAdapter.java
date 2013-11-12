package ch.unibe.sport.widget.view;

public interface SeekBarAdapter<T> {

	public int getCount();
	public String getStringValue(int index);
	public boolean isValueDisplayed(int index);
	public T getValue(int index);

}
