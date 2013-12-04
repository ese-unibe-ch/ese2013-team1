package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class SportEvents extends Table {

	public static final String NAME = "sport_courses";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"sid integer NOT NULL, "+
			"eid integer NOT NULL, "+
			"CONSTRAINT chk_sport_events CHECK (eid > 0 AND sid > 0),"+
			"CONSTRAINT unique_eid_sid UNIQUE (sid,eid)"+
		");";
	
	public static final String[] DB = {
		"sid",				// 0
		"eid"				// 1
	};
	
	public static final int SID = 0, EID = 1;
	
	public SportEvents(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public String getSportName(int eventID){
		db.open(context,TAG);
		String query = "SELECT s.sportName FROM "+Sports.NAME+" s INNER JOIN  "+NAME + " se ON se.sid = s.sid WHERE se.eid = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+eventID});
		String result = Utils.getRow(Utils.transpose(getResultString(cursor)),0)[0];
		db.close(TAG);
		return result;
	}
}
