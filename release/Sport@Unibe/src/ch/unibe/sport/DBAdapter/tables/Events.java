package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import ch.unibe.sport.DBAdapter.tables.Table;
import ch.unibe.sport.core.Interval;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class Events extends Table {
	public static final String TAG =  Events.class.getName();

	public static final String NAME = "events";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"eid integer UNIQUE NOT NULL, " +
			"iid char DEFAULT NULL," +
			"pid char DEFAULT NULL," +
			"hash char NOT NULL, "+
			"eventName char NOT NULL, "+
			"date char DEFAULT '', "+
			"infoLink char DEFAULT '', "+
			"registration char DEFAULT '', "+
			"registrationLink char DEFAULT ''"+
		");";
	public static final String[] DB = {
		"eid",					// 0
		"iid",					// 1
		"pid",					// 2
		"hash",					// 3
		"eventName",			// 4
		"date",					// 5
		"infoLink",				// 6
		"registration",			// 7
		"registrationLink"		// 8
	};
	
	public static final int EID = 0, IID = 1, PID = 2, HASH = 3, EVENT_NAME = 4,
			DATE = 5, INFO_LINK = 6, REGISTRATION = 7, REGISTRATION_LINK = 8;
	
	public Events(Context context) {
		super(context, NAME, CREATE, DB);
	}		
	
	/**
	 * Returns full data of sub course with it's course id
	 * @param courseID
	 * @return
	 */
	public String[] getData(int eventID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[EID]+" = ?", new String[]{""+eventID}, null, null, DB[EID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	public String[] getData(String hash) {
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?", new String[]{""+hash}, null, null, DB[EID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
		
	//TOOD
	public int[] searchCourses(int[] days, Interval[] times){
		db.open(context,TAG);
		String query = "SELECT c.cid FROM "+NAME+" c INNER JOIN  "+EventIntervals.NAME + " i ON "
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
}
