package ch.unibe.sport.course;

public enum UnknownTime {
	INST;
	public final Time time;
	
	private UnknownTime(){
		time = new Time();
	}
}
