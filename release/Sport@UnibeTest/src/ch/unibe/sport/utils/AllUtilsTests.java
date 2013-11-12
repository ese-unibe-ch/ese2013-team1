package ch.unibe.sport.utils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilsTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(AssociativeListTest.class);
		suite.addTestSuite(CalendarHelperTest.class);
		suite.addTestSuite(ColorTest.class);
		suite.addTestSuite(DateTest.class);
		//$JUnit-END$
		return suite;
	}

}
