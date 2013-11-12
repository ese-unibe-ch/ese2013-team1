package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import ch.unibe.sport.course.Time;
import ch.unibe.sport.favorites.FavoriteColors;
import ch.unibe.sport.utils.Utils;

public class FavoriteCourses extends Table{
	public static final String TAG =  FavoriteCourses.class.getName();

	public static final String NAME = "favorite_courses";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer NOT NULL UNIQUE, "+
			"text_color integer NOT NULL, "+
			"bg_color integer NOT NULL, "+
			"bg_type integer NOT NULL, "+
			"CONSTRAINT chk_favorite_courses CHECK (cid > 0)"+
		");";
	
	public static final String[] DB = {
		"cid",				// 0
		"text_color",		// 1
		"bg_color",			// 2
		"bg_type"			// 3
	};
	
	public static final int CID = 0,TEXT_COLOR = 1,BG_COLOR = 2, BG_TYPE = 3;
	
	public FavoriteCourses(Context context) {
		super(context, NAME, CREATE, DB);
	}

	public void add(int courseID){
		if (isFavorite(courseID)) return;
		this.insert(new int[]{CID,TEXT_COLOR,BG_COLOR,BG_TYPE},
			new String[]{
				""+courseID,
				""+Color.BLACK,
				""+FavoriteColors.BG_COLORS[Utils.randInt(0, FavoriteColors.BG_COLORS.length-1)],
				"1"
			});
	}
	
	public String[] getData(int courseID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[CID]+" = ?", new String[]{""+courseID}, null, null, DB[CID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	public void remove(int courseID){
		this.removeByID(CID, courseID);
	}
	
	public int[] getFavoritesIDs(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[CID]}, "", null, null, null, DB[CID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public int[] getFavoritesIDsWithoutAttended(){
		db.open(context,TAG);
		String query = "SELECT f.cid FROM "+NAME + " f WHERE NOT EXISTS (SELECT ac.cid FROM "+AttendedCourses.NAME+" ac WHERE ac.cid = f.cid)";
		Cursor cursor = db.getDB().rawQuery(query, null);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public String[][] getAllFavoritesData(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, "", null, null, null, DB[CID]);
		String[][] result = getResultString(cursor);
		db.close(TAG);
		return result;
	}
	
	public boolean isFavorite(int courseID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[CID]+" = ?", new String[]{""+courseID}, null, null, DB[CID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		if (result.length == 0) return false;
		else return true;
	}
	
	public int[] getFavoriteCoursesInMinutes(Time now, Time before, int dayOfWeek){
		db.open(context,TAG);
		String query = "SELECT f.cid FROM "+NAME+" f INNER JOIN "+Courses.NAME+" c, "+CourseIntervals.NAME+" ci, "+Intervals.NAME + " i, "+CourseDays.NAME +" cd ON "
			+" f.cid = c.cid AND ci.cid = c.cid AND ci.iid = i.iid AND cd.cid = c.cid WHERE i.timeFrom >= ? AND i.timeFrom <= ? AND cd.dayOfWeek = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+now.toMinutes(),""+(now.toMinutes()+before.toMinutes()),""+dayOfWeek});
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public void setBGColor(int courseID, int color){
		db.open(context,TAG);
		this.updateByID(CID, courseID, new int[]{BG_COLOR}, new String[]{""+color});
		db.close(TAG);
	}
}
