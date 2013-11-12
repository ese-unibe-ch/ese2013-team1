package ch.unibe.sport.course;

public class Interval {
	public static final String TAG =  Interval.class.getName();
	
	private Time timeFrom;
	private Time timeTo;
	
	public Interval(Time timeFrom,Time timeTo){
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
	}

	public Time getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(Time timeFrom) {
		this.timeFrom = timeFrom;
	}

	public Time getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(Time timeTo) {
		this.timeTo = timeTo;
	}
	
	public boolean isUnknown(){
		return this.timeFrom.unknown;
	}
}
