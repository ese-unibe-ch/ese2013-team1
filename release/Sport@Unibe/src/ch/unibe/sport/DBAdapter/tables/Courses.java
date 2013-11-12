package ch.unibe.sport.DBAdapter.tables;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import ch.unibe.sport.DBAdapter.tables.Table;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.DayParser;
import ch.unibe.sport.course.Interval;
import ch.unibe.sport.course.TimeParser;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class Courses extends Table {
	public static final String TAG =  Courses.class.getName();

	public static final String NAME = "courses";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer, " +
			"hash char NOT NULL, "+
			"course char NOT NULL, "+
			"day char default '', "+
			"time char default '', "+
			"period char default '', "+
			"place char default '', "+
			"info text DEFAULT '', "+
			"subscription char default '', "+
			"kew char DEFAULT '', "+
			"CONSTRAINT sub_courses_pkey PRIMARY KEY (cid)"+
		");";
	public static final String[] DB = {
		"cid",					// 0
		"hash",					// 1
		"course",				// 2
		"day",					// 3
		"time",					// 4
		"period",				// 5
		"place",				// 6
		"info",					// 7
		"subscription",			// 8
		"kew"					// 9
	};
	
	public static final int CID = 0, HASH = 1, COURSE = 2, DAY = 3, TIME = 4,
			PERIOD = 5, PLACE = 6, INFO = 7, SUBSCRIPTION = 8, KEW = 9,
			SPORT_SID = 10, SPORT_SPORT = 11, FAVORITE = 12, RATED = 13;
	
	public Courses(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long addCourse(Course course){
		SportCourses scDB = new SportCourses(context);
		String hash = this.generateHash(
				course.getSport(),
				""+course.getSportID(),
				course.getCourse(),
				course.getDay(),
				course.getTime(),
				course.getPeriod(),
				course.getPlace(),
				course.getInfo(),
				course.getSubscription(),
				course.getKew());
		int cid = (int) this.insert(new int[]{ HASH, COURSE, DAY, TIME, PERIOD, PLACE, INFO, SUBSCRIPTION, KEW}, new String[]{
				hash,
				course.getCourse(),
				course.getDay(),
				course.getTime(),
				course.getPeriod(),
				course.getPlace(),
				course.getInfo(),
				course.getSubscription(),
				course.getKew()
		});
		scDB.add(course.getSportID(), (int)cid);
		
		addIntervals(cid, course.getTime());
		addDays(cid, course.getDay());
		return cid;
	}
	
	public void updateCourse(Course course){
		int courseID = course.getCourseID();
		String[] courseData = this.getData(courseID);
		
		String oldTime = courseData[TIME];
		String newTime = course.getTime();
		if (!newTime.equals(oldTime)){
			removeIntervals(courseID);
			addIntervals(courseID, newTime);
		}
		
		String oldDay = courseData[DAY];
		String newDay = course.getDay();
		if (!newDay.equals(oldDay)){
			removeDays(courseID);
			addDays(courseID, newDay);
		}
		
		String hash = this.generateHash(
					course.getSport(),
					""+course.getSportID(),
					course.getCourse(),
					course.getDay(),
					course.getTime(),
					course.getPeriod(),
					course.getPlace(),
					course.getInfo(),
					course.getSubscription(),
					course.getKew());
		
		this.updateByID(CID, course.getCourseID(),
				new int[]{HASH,COURSE, DAY, TIME, PERIOD, PLACE, INFO, SUBSCRIPTION, KEW},
				new String[]{
					hash,
					course.getCourse(),
					course.getDay(),
					course.getTime(),
					course.getPeriod(),
					course.getPlace(),
					course.getInfo(),
					course.getSubscription(),
					course.getKew()
				});
	}
	
	public void removeCourse(int courseID){
		removeDays(courseID);
		removeIntervals(courseID);
		this.removeByID(CID,courseID);
	}
	
	public void removeCourses(int[] courseIDs){
		for (int id : courseIDs){
			removeDays(id);
			removeIntervals(id);
		}
		this.removeByID(CID,courseIDs);
	}
	/**
	 * Returns full data of sub course with it's course id
	 * @param courseID
	 * @return
	 */
	public String[] getData(int courseID){
		db.open(context,TAG);
		String query = "SELECT c.*, s.sid, s.sport, (SELECT COUNT (*) FROM "+FavoriteCourses.NAME+" f WHERE f.cid = c.cid) as favorite, "+
				"(SELECT COUNT (*) FROM "+Rating.NAME+" r WHERE r.cid = c.cid) as rated  FROM " + NAME + " c INNER JOIN " + Sports.NAME + " s, " + SportCourses.NAME + " sc "+
		"ON c.cid = sc.cid AND s.sid = sc.sid WHERE c.cid = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+courseID});
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	/**
	 * Returns full data of sub course with it's course id
	 * @param subCourseID
	 * @return
	 */
	public String[][] getDataBySportID(int sportID){
		db.open(context,TAG);
		String query = "SELECT c.*, s.sid,s.sport,  (SELECT COUNT (*) FROM "+FavoriteCourses.NAME+" f WHERE f.cid = c.cid) as favorite, "+
				"(SELECT COUNT (*) FROM "+Rating.NAME+" r WHERE r.cid = c.cid) as rated FROM " + NAME + " c INNER JOIN " + Sports.NAME + " s, " + SportCourses.NAME + " sc "+
		"ON c.cid = sc.cid AND s.sid = sc.sid WHERE s.sid = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+sportID});
		String[][] result = getResultString(cursor);
		db.close(TAG);
		return result;
	}
	
	public void removeCourses(int sportID){
		db.open(context,TAG);
		String query = "DELETE FROM "+NAME+" WHERE cid in (SELECT cid FROM "+SportCourses.NAME+" WHERE sid = '"+sportID+"');";
		db.getDB().execSQL(query);
		query = "DELETE FROM "+SportCourses.NAME+" WHERE sid = '"+sportID+"';";
		db.getDB().execSQL(query);
		db.close(TAG);
	}
	
	public String[] getDataByCourseName(int sportID, String course){
		db.open(context,TAG);
		String query = "SELECT c.*, s.sid,s.sport,  (SELECT COUNT (*) FROM "+FavoriteCourses.NAME+
				" f WHERE f.cid = c.cid) as favorite, "+
				"(SELECT COUNT (*) FROM "+Rating.NAME+" r WHERE r.cid = c.cid) as rated FROM "+NAME+" c INNER JOIN "+Sports.NAME+" s, "+SportCourses.NAME+
				" sc ON c.cid=sc.cid AND s.sid=sc.sid WHERE s.sid = ? AND c.course = ?";
		String[] result = Utils.getRow(getResultString(db.getDB().rawQuery(query, new String[]{""+sportID,course})),0);
		db.close(TAG);
		return result;
	}
	
	/**
	 * Parses time and adds intervals to tables with respect
	 * to relations between tables.
	 * @param cid - courseID of course in {@code Courses} table
	 * @param time - time String to be parsed and added
	 */
	private void addIntervals(int cid, String time) {
		TimeParser timeParser = new TimeParser(time);
		ArrayList<Interval> intervals = timeParser.getIntervals();
		if (intervals.size() > 0){
			Intervals intervalDB = new Intervals(context);
			CourseIntervals courseIntervalsDB = new CourseIntervals(context);
			for (Interval interval : intervals){
				int iid = (int) intervalDB.add(interval);
				courseIntervalsDB.add(cid, iid);
			}
		}
	}
	
	/**
	 * Removes all time intervals of course by courseID
	 * @param courseID courseID of course in {@code Courses} table
	 */
	private void removeIntervals(int courseID) {
		CourseIntervals courseIntervalsDB = new CourseIntervals(context);
		courseIntervalsDB.remove(courseID);
	}
	
	private void addDays(int cid, String day) {
		DayParser dayParser = new DayParser(day);
		int[] days = dayParser.getParsedDays();
		CourseDays courseDaysDB = new CourseDays(context);
		courseDaysDB.add(cid, days);
	}
	
	private void removeDays(int courseID){
		CourseDays courseDaysDB = new CourseDays(context);
		courseDaysDB.remove(courseID);
	}
	
	public int[] searchCourses(int[] days, Interval[] times){
		db.open(context,TAG);
		String query = "SELECT c.cid FROM "+NAME+" c INNER JOIN "+CourseDays.NAME+" cd, "+CourseIntervals.NAME+" ci, "+Intervals.NAME + " i ON "
				+ "c.cid = cd.cid AND c.cid = ci.cid AND ci.iid = i.iid WHERE cd.dayOfWeek IN("+Print.toString(days, ",")+") AND (";
		boolean first = true;
		for (Interval interval : times){
			if (!first) {
				query += " OR ";
			}
			query += "(i.timeFrom >= "+interval.getTimeFrom().toMinutes()+" AND i.timeFrom <= "+interval.getTimeTo().toMinutes()+")";
			first = false;
		}
		query += ")";
		Cursor cursor = db.getDB().rawQuery(query, null);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}

	public String generateHash(String... strings){
		if (strings == null) return "null";
		if (strings.length == 0) return "empty";
		StringBuilder hashStr = new StringBuilder();
		for (String str : strings){
			hashStr.append(str);
		}
		
		return Utils.md5(hashStr.toString());
		
	}
}
