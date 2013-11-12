package ch.unibe.sport.course;

import java.util.ArrayList;
import java.util.Arrays;

public class DayParser {
	private static final String MONDAY = "Mo";
	private static final String TUESDAY = "Di";
	private static final String WEDNESDAY = "Mi";
	private static final String THURSDAY = "Do";
	private static final String FRIDAY = "Fr";
	private static final String SATURDAY = "Sa";
	private static final String SUNDAY = "So";
	private static final String DIVIDER_INTERVAL = "-";
	private static final String DIVIDER_AND = "/";
	
	private final String str;
	private int[] days;
	
	public DayParser(String str){
		this.str = str;
		try {
			parse();
		} catch (Exception e) {
			days = new int[0];
		}
	}
	
	private void parse() throws Exception{
		if (this.str == null) throw new Exception("Unable to parse string "+str);
		if (this.str.length() == 0) throw new Exception("Unable to parse string "+str);
		ArrayList<Integer> tmpDays = new ArrayList<Integer>();
		String[] days = str.split(DIVIDER_AND);
		String[] interval = null;
		for (String day : days){
			interval = day.split(DIVIDER_INTERVAL);
			if (interval.length == 2){
				int dayFrom = parseDay(interval[0]);
				int dayTo = parseDay(interval[1]);
				if (dayTo <= dayFrom) throw new Exception("Unable to parse string "+str);
				for (int i = dayFrom; i <= dayTo;i++){
					if (!tmpDays.contains(i)) tmpDays.add(i);
				}
			}
			else if (interval.length == 1){
				int dayOfWeek = parseDay(interval[0]);
				if (!tmpDays.contains(dayOfWeek)) tmpDays.add(dayOfWeek);
			}
			else {
				throw new Exception("Unable to parse string "+str);
			}
		}
		
		int[] daysOfWeek = new int[tmpDays.size()];
		int i = 0;
		for (Integer day : tmpDays){
			daysOfWeek[i] = day;
			i++;
		}
		Arrays.sort(daysOfWeek);
		this.days = daysOfWeek;
	}

	private int parseDay(String day) throws Exception{
		if (day == null) throw new Exception("Unable to parse string "+str);
		if (day.length() == 0) throw new Exception("Unable to parse string "+str);
		if (day.equals(MONDAY)){
			return 0;
		}
		else if (day.equals(TUESDAY)){
			return 1;
		}
		else if (day.equals(WEDNESDAY)){
			return 2;
		}
		else if (day.equals(THURSDAY)){
			return 3;
		}
		else if (day.equals(FRIDAY)){
			return 4;
		}
		else if (day.equals(SATURDAY)){
			return 5;
		}
		else if (day.equals(SUNDAY)){
			return 6;
		}
		else {
			throw new Exception("Unable to parse string "+str);
		}
	}
	
	public int[] getParsedDays() {
		return this.days;
	}
}
