package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.database.Cursor;

public final class CourseData {

	public final static class Calendar {
		public static final String TAG =  Calendar.class.getName();
		
		public static final int DAY_OF_WEEK = 0, TIME_FROM = 1, TEXT_COLOR = 2,
				BG_COLOR = 3, BG_TYPE = 4;
		
		/**
		 * Returns favorite course data for live calendar
		 * @param table
		 * @return
		 */
		public static final String[][] getData(Table table) {
			table.getDB().open(table.getContext(),TAG);
			String query = "SELECT cd.dayOfWeek, i.timeFrom, f.text_color,f.bg_color,f.bg_type FROM " +
					FavoriteCourses.NAME + " f INNER JOIN "+
					CourseIntervals.NAME + " ci, " +CourseDays.NAME+" cd, " + Intervals.NAME +" i "+
					"ON f.cid = ci.cid AND cd.cid = f.cid AND i.iid = ci.iid ORDER BY cd.dayOfWeek,i.timeFrom";
			Cursor cursor = table.getDB().getDB().rawQuery(query, new String[]{});
			String[][] result = Table.getResultString(cursor);
			table.getDB().close(TAG);
			return result;
		}
	}
	
	public final static class CalendarDetails {
		public static final String TAG = CalendarDetails.class.getName();
		
		public static final int CID = 0;
		
		public static final int[] getFavoriteCoursesIDsByDayOfWeek(Table table,int dayOfWeek){
			table.getDB().open(table.getContext(), TAG);
			String query = "SELECT c.cid FROM "+Courses.NAME +" c INNER JOIN "+
					FavoriteCourses.NAME + " f, "+CourseDays.NAME + " cd ON f.cid = c.cid AND cd.cid = f.cid "+
					"WHERE cd."+CourseDays.DB[CourseDays.DAY] + " = ?";
			int[] result = Utils.getRow(Utils.transpose(Table.getResultInt(table.getDB().getDB().rawQuery(query, new String[]{""+dayOfWeek}))), 0);
			table.getDB().close(TAG);
			return result;
		}
		
		public static final boolean isCourseTakesPlaceOnDayOfWeek(Table table,int courseID, int dayOfWeek){
			table.getDB().open(table.getContext(), TAG);
			String query = "SELECT COUNT(*) FROM "+FavoriteCourses.NAME + " f INNER JOIN "+
					Courses.NAME +" c, "+CourseDays.NAME + " cd ON f.cid = c.cid AND cd.cid = f.cid "+
					"WHERE cd."+CourseDays.DB[CourseDays.DAY] + " = ? AND f.cid = ?";
			int result = Utils.getRow(Utils.transpose(Table.getResultInt(table.getDB().getDB().rawQuery(query, new String[]{""+dayOfWeek,""+courseID}))), 0)[0];
			table.getDB().close(TAG);
			return result > 0;
		}
	}
}
