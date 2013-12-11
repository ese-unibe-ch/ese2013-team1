package ch.unibe.sport.course;

import ch.unibe.sport.core.Time;
import junit.framework.TestCase;

public class TimeTest extends TestCase {

	public void testTimeFormMinutes() {
		Time time = new Time(657);
		assertEquals(false, time.unknown);
		assertEquals(10, time.hours);
		assertEquals(57, time.minutes);
	}

	public void testTimeFormMinutesZero() {
		Time time = new Time(0);
		assertEquals(false,time.unknown);
		assertEquals(0,time.hours);
		assertEquals(0,time.minutes);
	}

	public void testTimeFormMinutesLessZero() {
		Time time = new Time(-1);
		assertEquals(true,time.unknown);
		assertEquals(0,time.hours);
		assertEquals(0,time.minutes);
	}

	public void testTimeFormMinutes1440() {
		Time time = new Time(1440);
		assertEquals(true,time.unknown);
		assertEquals(0,time.hours);
		assertEquals(0,time.minutes);
	}
	
	public void testTimeFormMinutesMore1440() {
		Time time = new Time(2440);
		assertEquals(true,time.unknown);
		assertEquals(0,time.hours);
		assertEquals(0,time.minutes);
	}

}
