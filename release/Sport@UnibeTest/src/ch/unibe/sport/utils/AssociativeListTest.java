package ch.unibe.sport.utils;

import java.util.ArrayList;
import junit.framework.TestCase;

public class AssociativeListTest extends TestCase {

	public void testShouldAdd(){
		AssociativeList<String> alist = new AssociativeList<String>();
		alist.add("Bob", "bob_name");
		alist.add("Rob", "rob_name");
		alist.add("Ann", 1);
		alist.add("Maria", 2);
		System.out.println(alist);
		assertEquals(4,alist.getSize());
		assertEquals("Bob",alist.get("bob_name"));
		assertEquals("Rob",alist.get("rob_name"));
		assertEquals("Ann",alist.get(1));
		assertEquals("Maria",alist.get(2));
	}
	
	public void testValueOf(){
		ArrayList<String> values = new ArrayList<String>();
		values.add("value1");
		values.add("value2");
		ArrayList<String> strKey = new ArrayList<String>();
		strKey.add("key-for-value1");
		ArrayList<Integer> intKey = new ArrayList<Integer>();
		intKey.add(null);
		intKey.add(2);
		AssociativeList<String> alist = AssociativeList.valueOf(values, intKey, strKey);
		assertEquals("value1",alist.get("key-for-value1"));
		assertEquals("value2",alist.get(2));
	}
	
}
