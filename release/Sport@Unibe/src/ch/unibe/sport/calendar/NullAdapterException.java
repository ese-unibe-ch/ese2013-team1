package ch.unibe.sport.calendar;

public class NullAdapterException extends Exception{
	private static final long serialVersionUID = -7671940407125400061L;

	public NullAdapterException(){
		super("CalendarAdapter is null, please set your adapter before initializing calendar");
	}
}
