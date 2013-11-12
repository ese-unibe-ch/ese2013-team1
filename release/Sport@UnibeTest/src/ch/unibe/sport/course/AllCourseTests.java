package ch.unibe.sport.course;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCourseTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCourseTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CourseTest.class);
		suite.addTestSuite(DayParserTest.class);
		suite.addTestSuite(IntervalTest.class);
		suite.addTestSuite(SportTest.class);
		suite.addTestSuite(TimeParserTest.class);
		suite.addTestSuite(TimeTest.class);
		suite.addTestSuite(UnknownTimeTest.class);
		//$JUnit-END$
		return suite;
	}

}
