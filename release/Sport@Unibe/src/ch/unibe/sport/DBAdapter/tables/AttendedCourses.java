package ch.unibe.sport.DBAdapter.tables;

import java.util.ArrayList;

import ch.unibe.sport.config.Config;
import ch.unibe.sport.course.Time;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class AttendedCourses extends Table{
	public static final String TAG = AttendedCourses.class.getName();
	
	public static final String NAME = "attended_courses";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer NOT NULL, "+
			"date integer NOT NULL, "+
			"share integer NOT NULL,"+
			"CONSTRAINT chk_attended_courses CHECK (cid > 0)"+
			"CONSTRAINT unique_cid_date UNIQUE (cid,date)"+
		");";
	
	public static final String[] DB = {
		"cid",				// 0
		"date",				// 1
		"share"				// 2
	};
	
	public static final int CID = 0, DATE = 1, SHARE = 2;
	
	public AttendedCourses(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long add(int courseID, Date date, boolean share){
		long id = 0;
		if (!this.isAttended(courseID,date)) {
			id = this.insert(new int[]{CID,DATE,SHARE},new String[]{""+courseID,""+date.toInt(),(share) ? "1" : "0"});
			Config.INST.SYNCHRONIZE.setSynchronized(false);
		}
		return id;
	}
		
	public int[] getAttendedCoursesIDs(){
		db.open(context,TAG);
		String query = "SELECT DISTINCT "+DB[CID]+" FROM "+AttendedCourses.NAME;
		Cursor cursor = db.getDB().rawQuery(query, null);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public String[] getAttendedCoursesHashs(){
		db.open(context,TAG);
		String query = "SELECT c.hash FROM "+Courses.NAME+" c INNER JOIN "+NAME +" ac ON ac.cid = c.cid";
		Cursor cursor = db.getDB().rawQuery(query, null);
		String[] result = Utils.getRow(Utils.transpose(getResultString(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	/**
	 * 
	 * @param courseID
	 * @return 0 - not attended, 1 - active attendance, -1 - past attendance
	 */
	public int isAttended(int courseID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[CID]+" = ?",new String[]{""+courseID}, null, null, DB[DATE]+" DESC");
		int[] result = Utils.getRow(getResultInt(cursor),0);
		db.close(TAG);
		if (result.length == 0) return 0;
		Date lastAttended = new Date(result[DATE]);
		Date today = new Date();
		if (today.compareTo(lastAttended) == 1){
			return -1;
		}
		else return 1;
	}
	
	public ArrayList<Date> getAttendedDates(int courseID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[DATE]}, DB[CID]+" = ?",new String[]{""+courseID}, null, null, DB[DATE]+" DESC");
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		ArrayList<Date> dates = new ArrayList<Date>();
		for (int date : result){
			dates.add(new Date(date));
		}
		db.close(TAG);
		return dates;
	}
	
	public boolean isAttended(int courseID, Date date){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[CID]}, DB[CID]+" = ? AND "+DB[DATE]+" = ?",new String[]{""+courseID,""+date.toInt()}, null, null, DB[CID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result.length > 0;
	}
	
	public int[] getAttendedCoursesInMinutes(Time now, Time before, Date date){
		db.open(context,TAG);
		String query = "SELECT ac.cid FROM "+NAME+" ac INNER JOIN "+Courses.NAME+" c, "+CourseIntervals.NAME+" ci, "+Intervals.NAME + " i ON "
			+" ac.cid = c.cid AND ci.cid = c.cid AND ci.iid = i.iid WHERE i.timeFrom >= ? AND i.timeFrom <= ? AND ac.date = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+now.toMinutes(),""+(now.toMinutes()+before.toMinutes()),""+date.toInt()});
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
}
