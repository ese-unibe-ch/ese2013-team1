package ch.unibe.sport.utils;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class AssociativeListTest extends TestCase {

	public void testShouldAdd(){
		AssociativeList<String> alist = new AssociativeList<String>();
		alist.add("Bob", "bob_name");
		alist.add("Rob", "rob_name");
		alist.add("Ann", 1);
		alist.add("Maria", 2);
		System.out.println(alist);
		assertEquals(4,alist.size());
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
	
	
	public void testSpeedCompareToHashMapStringKey(){
		HashMap<String,String> map = new HashMap<String,String>();
		AssociativeList<String> list = new AssociativeList<String>();
		final int NUM = 100;
		
		String[] keys = new String[NUM];
		String[] values = new String[NUM];
		for (int i = 0; i < NUM; i++){
			keys[i] = ""+(i*50);
			values[i] = ""+(i*75);
		}
		
		Timer timer = new Timer();
		timer.reset();
		for (int i = 0; i < NUM; i++){
			map.put(keys[i].toString(), values[i].toString());
		}
		Print.log("Insert Map: "+timer.timeElapsed());
		timer.reset();
		for (int i = 0; i < NUM; i++){
			list.add(values[i].toString(),keys[i].toString());
		}
		Print.log("Insert List: "+timer.timeElapsed());
		
		timer.reset();
		for (int i = 0; i < NUM; i++){
			assertEquals(values[i],map.get(keys[i]));
		}
		Print.log("Get Map: "+timer.timeElapsed());
		timer.reset();
		for (int i = 0; i < NUM; i++){
			assertEquals(values[i],list.get(keys[i]));
		}
		Print.log("Get List: "+timer.timeElapsed());
		
	}
	
}
