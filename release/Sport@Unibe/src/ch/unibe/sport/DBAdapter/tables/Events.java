package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import ch.unibe.sport.DBAdapter.tables.Table;
import ch.unibe.sport.core.Time;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class Events extends Table {
	public static final String TAG =  Events.class.getName();

	public static final String NAME = "events";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"eid integer UNIQUE NOT NULL, " +
			"iid char DEFAULT ''," +
			"pid char DEFAULT ''," +
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
	
	public int getEventID(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?", new String[]{hash}, null, null, DB[EID]);
		int[] result = Utils.getRow(getResultInt(cursor),0);
		db.close(TAG);
		if (result.length == 0) return 0;
		return result[0];
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
	
	public int[] searchEvents(int[] days, Time timeFrom, Time timeTo,String eventName){
		db.open(context,TAG);
		String query = "SELECT e.eid FROM "+NAME+" e INNER JOIN  "+EventIntervals.NAME + " ei,"+EventDaysOfWeek.NAME + " ed ON "
				+ "e.iid = ei.iid AND e.eid = ed.eid WHERE ed.dayOfWeek IN("+Print.toString(days, ",")+") AND "
				+ "ei.timeFrom >= ? AND ei.timeFrom <= ?";
		Cursor cursor;
		if (eventName != null && eventName.length() > 0){
			query += " AND e.eventName LIKE ?";
			cursor = db.getDB().rawQuery(query, new String[]{""+timeFrom.toMinutes(),""+timeTo.toMinutes(),"%"+eventName+"%"});
		}
		else {
			cursor = db.getDB().rawQuery(query, new String[]{""+timeFrom.toMinutes(),""+timeTo.toMinutes()});
		}
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
}
