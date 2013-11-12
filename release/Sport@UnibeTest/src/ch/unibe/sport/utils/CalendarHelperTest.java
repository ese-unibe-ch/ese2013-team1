package ch.unibe.sport.utils;

import junit.framework.TestCase;

public class CalendarHelperTest extends TestCase {

	private CalendarHelper calendar;
	private Date date;
	
	private int day = 29;
	private int month = 3;
	private int year = 2013;

	public final void setUp(){
		calendar = new CalendarHelper();
		date = new Date(day, month, year);
	}

	public final void testShouldSetDateWork(){
		calendar.setDate(date);
		assertEquals(day,calendar.getDay());
		assertEquals(month,calendar.getMonth());
		assertEquals(year,calendar.getYear());
	}

	public final void testShouldCreateDateWork(){
		Date date = new Date(day, month, year);
		assertEquals(day,date.getDay());
		assertEquals(month,date.getMonth());
		assertEquals(year,date.getYear());
	}

	public final void testShouldCreateDateWorkByDate(){
		Date date = new Date(day, month, year);
		CalendarHelper calendar = new CalendarHelper(date);
		assertEquals(day,calendar.getDay());
		assertEquals(month,calendar.getMonth());
		assertEquals(year,calendar.getYear());
	}

//	public final void testShouldMatchConvertDayOfWeekMonday(){
//		Date date = new Date(10, 3, 2013);
//		CalendarHelper calendar = new CalendarHelper(date);
//		assertEquals(0,calendar.getDayOfWeek());
//	}
//
//	public final void testShouldMatchConvertDayOfWeekSunday(){
//		Date date = new Date(16, 3, 2013);
//		CalendarHelper calendar = new CalendarHelper(date);
//		assertEquals(6,calendar.getDayOfWeek());
//	}
	
}
