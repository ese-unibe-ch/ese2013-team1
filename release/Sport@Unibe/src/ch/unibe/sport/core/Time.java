package ch.unibe.sport.core;

import ch.unibe.sport.utils.Utils;

public class Time {
	private static final char DIVIDER = ':';
	private static final String UNKNOWN = "?";
	/* public for fast access on android (OS specific) */
	public int hours;
	public int minutes;
	public boolean unknown = false; 
		
	private Time(int hour, int minute, boolean unknown){
		this.hours = hour;
		this.minutes = minute;
		this.unknown = unknown;
	}
	
	public Time(int hour, int minute){
		this.hours = hour;
		this.minutes = minute;
	}
	
	public Time(int minutes){
		if (minutes < 0 || minutes >= 1440) this.unknown = true;
		else {
			this.minutes = minutes % 60;
			this.hours = (minutes - this.minutes) / 60;
		}
	}
	
	public Time(String time){
		if (time.equals(UNKNOWN)) this.unknown = true;
		else {
			int[] timeArray = Utils.split(time, DIVIDER, 2);
			this.hours = timeArray[0];
			this.minutes = timeArray[1];
		}
	}
	
	public Time(){
		unknown = true;
	}
	
	public void setHours(int hours){
		this.hours = hours;
	}
	
	public void setMinutes(int minutes){
		this.minutes = minutes;
	}
	
	public void setUnknown(boolean unknown){
		this.unknown = unknown;
	}
	
	public Time nextHour(){
		if (unknown) return this;
		else {
			this.hours++;
			if (this.hours >= 24) this.hours=0;
		}
		return this;
	}
	
	public String toString(){
		if (unknown) return UNKNOWN;
		StringBuilder str = new StringBuilder();
		if (hours > 9)str.append(hours);
		else str.append('0').append(hours);
		str.append(DIVIDER);
		if (minutes > 9)str.append(minutes);
		else str.append('0').append(minutes);
		return str.toString();
	}
	
	public int toMinutes(){
		if (this.unknown) return -1;
		return hours * 60 + minutes;
	}
	
	public Time copy(){
		return new Time(hours,minutes,unknown);
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
		return (this.hours == t.hours
			&& this.minutes == t.minutes
			&& this.unknown == t.unknown
		);
	}
	
	@Override
	public int hashCode() {
		final int prime = 32;
		int hash = 1;
		hash = hash * prime + this.hours;
		hash = hash * prime + this.minutes;
		hash = hash * prime + ((this.unknown) ? 1 : 0);
		return hash;
	}
}
