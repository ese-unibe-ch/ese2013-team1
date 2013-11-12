package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.course.Interval;
import ch.unibe.sport.course.Time;
import ch.unibe.sport.utils.Utils;
import android.content.Context;

public class CourseIntervals extends Table{
	public static final String TAG =  CourseIntervals.class.getName();

	public static final String NAME = "course_intervals";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer NOT NULL, "+
			"iid integer NOT NULL, "+
			"CONSTRAINT chk_course_intervals CHECK (cid > 0 AND iid > 0), "+
			"CONSTRAINT unique_cid_iid UNIQUE (cid,iid)"+
		");";
	
	public static final String[] DB = {
		"cid",					// 0
		"iid",					// 1
	};
	
	public static final int CID = 0, IID = 1;
	
	public CourseIntervals(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public void add(int cid, int iid){
		this.insert(new int[]{CID,IID}, new String[]{""+cid,""+iid});
	}
	
	public int[] getIntervalID(int courseID){
		db.open(context,TAG);
		int[] result = 
			Utils.getRow(
				Utils.transpose(
					getResultInt(
						db.getDB().query(
							NAME,
							new String[]{DB[IID]}, DB[CID] + " = ?",
							new String[]{""+courseID}, null, null, null
						)
					)
				),
			CID);
		db.close(TAG);
		return result;
	}
	
	public void remove(int cid){
		this.removeByID(CID, cid);
	}
	
	public void remove(int cid, int iid){
		db.open(context,TAG);
		db.getDB().delete(NAME, DB[CID] + " = ? AND " + DB[IID] + " = ?", new String[]{""+cid,""+iid});
		db.close(TAG);
	}
	
	public Interval[] getIntervals(int courseID){
		db.open(getContext(), TAG);
		String query = "SELECT i.timeFrom,i.timeTo FROM "+Intervals.NAME+" i INNER JOIN "+NAME +" ci  ON ci.iid = i.iid WHERE ci.cid = ? ORDER BY i.timeFrom";
		int[][] result = getResultInt(db.getDB().rawQuery(query, new String[]{""+courseID}));
		db.close(TAG);
		Interval[] intervals = new Interval[result.length];
		int i = 0;
		for (int[] intData : result){
			intervals[i] = new Interval(new Time(intData[0]),new Time(intData[1]));
			i++;
		}
		return intervals;
	}

}
