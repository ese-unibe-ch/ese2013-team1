package ch.unibe.sport.main.search;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ch.unibe.sport.core.Time;

public class SearchRequest {
	public static final String DAYS_NAME = "days";
	public static final String TIME_FROM_NAME = "timeFrom";
	public static final String TIME_TO_NAME = "timeTo";
	public static final String EVENT_NAME = "eventName";
	
	private int[] days;
	private Time timeFrom;
	private Time timeTo;
	private String eventName;
		
	public int[] getDays() {
		return days;
	}
	
	public void setDays(int[] days) {
		this.days = days;
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
	
	public void setEventName(String eventName){
		this.eventName = eventName;
	}
	
	public String getEventName(){
		return this.eventName;
	}
	
	public JSONObject<String,Object> toJson(){
		JSONObject<String,Object> request = new JSONObject<String,Object>();
		
		JSONArray<Integer> daysArray = new JSONArray<Integer>();
		for (int day : days){
			daysArray.add(day);
		}
		
		request.put(DAYS_NAME, daysArray);
		request.put(TIME_FROM_NAME, timeFrom.toJson());
		request.put(TIME_TO_NAME, timeTo.toJson());
		request.put(EVENT_NAME,this.eventName);
		return request;
	}
}
