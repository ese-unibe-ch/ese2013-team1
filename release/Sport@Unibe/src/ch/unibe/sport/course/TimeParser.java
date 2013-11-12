package ch.unibe.sport.course;

import java.util.ArrayList;
import java.util.Locale;

public class TimeParser {
	public static final String ALL_DAY = "ganzer Tag";
	public static final String ON_REQUEST = "auf Anfrage";
	public static final String INTERVAL_DIVIDER = "/";
	
	public static final Time ALL_DAY_START_TIME = new Time(0,0);
	public static final Time ALL_DAY_FINISH_TIME = new Time(23,59);
	public static final Time UNKNOWN_TIME = new Time();
		
	private ArrayList<Interval> intervals;
	
	public TimeParser(String str) throws NullPointerException,ArrayIndexOutOfBoundsException{
		intervals = new ArrayList<Interval>();
		if (str == null){
			intervals.add(new Interval(UNKNOWN_TIME,UNKNOWN_TIME));
		}
		else if (str.length() == 0){
			intervals.add(new Interval(UNKNOWN_TIME,UNKNOWN_TIME));
		}
		else {
			String[] intervalStr = str.split(INTERVAL_DIVIDER);
			for (String strToParse : intervalStr){
				parse(strToParse);
			}
		}
	}
	
	public ArrayList<Interval> getIntervals(){
		return this.intervals;
	}
	
	private void parse(String str){
		char[] chars = str.toCharArray();
		if (chars.length == 0){
			intervals.add(new Interval(UNKNOWN_TIME,UNKNOWN_TIME));
			return;
		}
		if (str.toLowerCase(Locale.getDefault()).equals(ALL_DAY.toLowerCase(Locale.getDefault()))){
			intervals.add(new Interval(ALL_DAY_START_TIME,ALL_DAY_FINISH_TIME));
			return;
		}
		if (str.toLowerCase(Locale.getDefault()).equals(ON_REQUEST.toLowerCase(Locale.getDefault()))){
			intervals.add(new Interval(UNKNOWN_TIME,UNKNOWN_TIME));
			return;
		}
		int startHour = 0;
		int startMinute = 0;
		int finishHour = 0;
		int finishMinute = 0;
		int startHourFound = 0;
		int startMinuteFound = 0;
		int finishHourFound = 0;
		int finishMinuteFound = 0;
		int integer;
		int lastFoundAt = 0,k = 0;
		for (char c : chars){
			integer = c-'0';
			if (integer >= 0 && integer <= 9){
				if (startHourFound < 2){
					startHour *= startHourFound * 10;
					startHour += integer;
					startHourFound++;
					k++;
					lastFoundAt = k;
					continue;
				}
				if (startMinuteFound < 2){
					if (startMinuteFound == 0 && lastFoundAt == k) return;
					startMinute *= startMinuteFound * 10;
					startMinute += integer;
					startMinuteFound++;
					k++;
					lastFoundAt = k;
					continue;
				}
				if (finishHourFound < 2){
					if (finishHourFound == 0 && lastFoundAt == k) return;
					finishHour *= finishHourFound * 10;
					finishHour += integer;
					finishHourFound++;
					k++;
					lastFoundAt = k;
					continue;
				}
				if (finishMinuteFound < 2){
					if (finishMinuteFound == 0 && lastFoundAt == k) return;
					finishMinute *= finishMinuteFound * 10;
					finishMinute += integer;
					finishMinuteFound++;
					k++;
					lastFoundAt = k;
					continue;
				}
				else return;
			}
			k++;
		}
		if (startHourFound == 2 && startMinuteFound == 2 && finishHourFound == 2 && finishMinuteFound == 2){
			intervals.add(new Interval(new Time(startHour,startMinute),new Time(finishHour,finishMinute)));
		}
		else if (startHourFound == 2 && startMinuteFound == 2){
			intervals.add(new Interval(new Time(startHour,startMinute),UNKNOWN_TIME));
		}
		else {
			intervals.add(new Interval(UNKNOWN_TIME,UNKNOWN_TIME));
		}
	}
}
