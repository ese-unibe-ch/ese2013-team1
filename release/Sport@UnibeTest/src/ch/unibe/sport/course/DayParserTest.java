package ch.unibe.sport.course;

import junit.framework.TestCase;

public class DayParserTest extends TestCase {

	public void testDayMondayToWednesday() {
		DayParser parser = new DayParser("Mo-Mi");
		int[] days = parser.getParsedDays();
		assertEquals(3, days.length);
		assertEquals(0, days[0]);
		assertEquals(1, days[1]);
		assertEquals(2, days[2]);
	}

	public void testDayMondayAndWednesday() {
		DayParser parser = new DayParser("Mo/Mi");
		int[] days = parser.getParsedDays();
		assertEquals(2, days.length);
		assertEquals(0, days[0]);
		assertEquals(2, days[1]);
	}

	public void testDaySunday() {
		DayParser parser = new DayParser("So");
		int[] days = parser.getParsedDays();
		assertEquals(1, days.length);
		assertEquals(6, days[0]);
	}

	public void testDayZero() {
		DayParser parser = new DayParser("");
		int[] days = parser.getParsedDays();
		assertEquals(0, days.length);
	}

	public void testDayTrash() {
		DayParser parser = new DayParser("Trash");
		int[] days = parser.getParsedDays();
		assertEquals(0, days.length);
	}

}
