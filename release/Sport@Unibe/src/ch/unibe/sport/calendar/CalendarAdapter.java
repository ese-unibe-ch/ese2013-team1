package ch.unibe.sport.calendar;

public interface CalendarAdapter {
	public int getDaysCount();
	public int getRowsCount();
	public Day[] getRow(int row);
	public int[] getDayCoordinates(int index);
	public void setCalendarWidth(int width);
	public void setCalendarHeigth(int height);
}
