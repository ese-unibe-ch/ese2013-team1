package ch.unibe.sport.course;

import java.util.ArrayList;
import junit.framework.TestCase;

public class TimeParserTest extends TestCase {
	
	public void testTime12dot00minus14dot00() {
		TimeParser tp = new TimeParser("12.00-14.00");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(14, 0), intervals.get(0).getTimeTo());
	}

	public void testTime12colon00minus14colon00() {
		TimeParser tp = new TimeParser("12:00-14:00");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(14, 0), intervals.get(0).getTimeTo());
	}

	public void testTime12comma00minus14comma00() {
		TimeParser tp = new TimeParser("12,00-14,00");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(14, 0), intervals.get(0).getTimeTo());
	}

	public void testTime12dot00() {
		TimeParser tp = new TimeParser("12.00");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(), intervals.get(0).getTimeTo());
	}

	public void testTimeUnknown() {
		TimeParser tp = new TimeParser("");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(), intervals.get(0).getTimeFrom());
		assertEquals(new Time(), intervals.get(0).getTimeTo());
	}

	public void testTime2Intervals() {
		TimeParser tp = new TimeParser("12.00-14.00/16.00-17.00");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(2, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(14, 0), intervals.get(0).getTimeTo());
		assertEquals(new Time(16, 0), intervals.get(1).getTimeFrom());
		assertEquals(new Time(17, 0), intervals.get(1).getTimeTo());
	}

	public void testTime2IntervalsZero2() {
		TimeParser tp = new TimeParser("12.00-14.00/");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(12, 0), intervals.get(0).getTimeFrom());
		assertEquals(new Time(14, 0), intervals.get(0).getTimeTo());
	}

	public void testTimeZeroSlashZero() {
		TimeParser tp = new TimeParser("/");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(0, intervals.size());
	}

	public void testTimeNachAbspraceh() {
		TimeParser tp = new TimeParser("nach Absprache");
		ArrayList<Interval> intervals = tp.getIntervals();
		assertEquals(1, intervals.size());
		assertEquals(new Time(), intervals.get(0).getTimeFrom());
	}

}
