package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class EventIntervals extends Table {
	public static final String TAG = EventIntervals.class.getName();

	public static final String NAME = "intervals";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"iid char UNIQUE NOT NULL, "+
			"timeFrom integer, "+
			"timeTo integer, "+
			"status char DEFAULT NULL, "+
			"CONSTRAINT chk_favorite_timeFrom CHECK (timeFrom >= -1 AND timeFrom < 1440), "+
			"CONSTRAINT chk_favorite_timeTo CHECK (timeTo >= -1 AND timeTo < 1440)"+
		");";
	public static final String[] DB = {
		"iid",					// 0
		"timeFrom",				// 1
		"timeTo",				// 2
		"status"				// 3
	};
	
	public static final int IID = 0, TIME_FROM = 1, TIME_TO = 2, STATUS = 3;
	
	public EventIntervals(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public String[] getData(String intervalID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[IID]+" = ?", new String[]{""+intervalID}, null, null, DB[IID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}

	
	
}
