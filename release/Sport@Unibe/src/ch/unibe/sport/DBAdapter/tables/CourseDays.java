package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.utils.Utils;
import android.content.Context;

public class CourseDays extends Table{
	public static final String TAG =  CourseDays.class.getName();

	public static final String NAME = "course_days";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer NOT NULL, "+
			"dayOfWeek integer NOT NULL, "+
			"CONSTRAINT chk_course_days CHECK (cid > 0 AND dayOfWeek  >= 0 AND dayOfWeek < 7)"+
			"CONSTRAINT unique_cid_dayOfWeek UNIQUE (cid,dayOfWeek)"+
		");";
	
	public static final String[] DB = {
		"cid",					// 0
		"dayOfWeek",					// 1
	};
	
	public static final int CID = 0, DAY = 1;
	
	public CourseDays(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public void add(int cid, int day){
		this.insert(new int[]{CID,DAY}, new String[]{""+cid,""+day});
	}
	
	public void add(int cid, int[] days){
		if (days.length == 0) return;
		Integer[][] values = new Integer[days.length][2];
		int i = 0;
		for (int day : days){
			values[i][0] = cid;
			values[i][1] = day;
			i++;
		}
		try {
			this.bulkInsert(new int[]{CID,DAY}, values, "int");
		} catch (TableNotExistsException e) {
			e.printStackTrace();
		}
	}
	
	public int[] getDays(int courseID){
		DBAdapter.INST.open(context,TAG);
		int[] result = 
			Utils.getRow(
				Utils.transpose(
					getResultInt(
						db.getDB().query(
							NAME,
							new String[]{DB[DAY]}, DB[CID] + " = ?",
							new String[]{""+courseID}, null, null, DB[DAY]
						)
					)
				),
			CID);
		DBAdapter.INST.close(TAG);
		return result;
	}
	
	public void remove(int cid){
		this.removeByID(CID, cid);
	}
	
	public void remove(int cid, int day){
		db.open(context,TAG);
		db.getDB().delete(NAME, DB[CID] + " = ? AND " + DB[DAY] + " = ?", new String[]{""+cid,""+day});
		db.close(TAG);
	}

}
