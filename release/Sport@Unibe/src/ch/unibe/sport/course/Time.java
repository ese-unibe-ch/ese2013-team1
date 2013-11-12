package ch.unibe.sport.course;

import ch.unibe.sport.utils.Utils;

public class Time {
	private static final char DIVIDER = ':';
	private static final String UNKNOWN = "?";
	/* public for fast access on android (OS specific) */
	public int hour;
	public int minute;
	public boolean unknown = false; 
	
	private Time(int hour, int minute, boolean unknown){
		this.hour = hour;
		this.minute = minute;
		this.unknown = unknown;
	}
	
	public Time(int hour, int minute){
		this.hour = hour;
		this.minute = minute;
	}
	
	public Time(int minutes){
		if (minutes < 0 || minutes >= 1440) this.unknown = true;
		else {
			this.minute = minutes % 60;
			this.hour = (minutes - this.minute) / 60;
		}
	}
	
	public Time(String time){
		if (time.equals(UNKNOWN)) this.unknown = true;
		else {
			int[] timeArray = Utils.split(time, DIVIDER, 2);
			this.hour = timeArray[0];
			this.minute = timeArray[1];
		}
	}
	
	public Time(){
		unknown = true;
	}
	
	public Time nextHour(){
		if (unknown) return this;
		else {
			this.hour++;
			if (this.hour >= 24) this.hour=0;
		}
		return this;
	}
	
	public String toString(){
		if (unknown) return UNKNOWN;
		StringBuilder str = new StringBuilder();
		if (hour > 9)str.append(hour);
		else str.append('0').append(hour);
		str.append(DIVIDER);
		if (minute > 9)str.append(minute);
		else str.append('0').append(minute);
		return str.toString();
	}
	
	public int toMinutes(){
		if (this.unknown) return -1;
		return hour * 60 + minute;
	}
	
	public Time copy(){
		return new Time(hour,minute,unknown);
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Time))
		      return false;
		Time t = (Time) obj;
		return (this.hour == t.hour
			&& this.minute == t.minute
			&& this.unknown == t.unknown
		);
	}
	
	@Override
	public int hashCode() {
		final int prime = 32;
		int hash = 1;
		hash = hash * prime + this.hour;
		hash = hash * prime + this.minute;
		hash = hash * prime + ((this.unknown) ? 1 : 0);
		return hash;
	}
}
