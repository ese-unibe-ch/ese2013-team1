package ch.unibe.sport.core;

public enum UnknownTime {
	INST;
	public final Time time;
	
	private UnknownTime(){
		time = new Time();
	}
}
