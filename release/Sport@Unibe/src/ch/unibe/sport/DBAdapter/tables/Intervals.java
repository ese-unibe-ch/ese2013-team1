package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.course.Interval;
import ch.unibe.sport.utils.Utils;
import android.content.Context;

public class Intervals extends Table {
	public static final String TAG = Intervals.class.getName();

	public static final String NAME = "time_intervals";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"iid integer, "+
			"timeFrom integer, "+
			"timeTo integer, "+
			"CONSTRAINT intervals_pkey PRIMARY KEY (iid), "+
			"CONSTRAINT chk_favorite_timeFrom CHECK (timeFrom >= -1 AND timeFrom < 1440), "+
			"CONSTRAINT chk_favorite_timeTo CHECK (timeTo >= -1 AND timeTo < 1440), "+
			"CONSTRAINT unique_interval UNIQUE (timeFrom,timeTo)"+
		");";
	public static final String[] DB = {
		"iid",					// 0
		"timeFrom",				// 1
		"timeTo"				// 2
	};
	
	public static final int IID = 0, TIME_FROM = 1, TIME_TO = 2;
	
	public Intervals(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	/**
	 * Adds new interval and returns it's ID, or just returns ID
	 * of existed interval
	 * @param timeFrom
	 * @param timeTo
	 * @return ID of added or already existed interval
	 */
	public long add(Interval interval){
		int intervalID = getIntervalID(interval);
		return (intervalID > 0)
				? intervalID
				: this.insert(new int[]{TIME_FROM,TIME_TO},
						new String[]{""+interval.getTimeFrom().toMinutes(),""+interval.getTimeTo().toMinutes()});
	}
	
	/**
	 * Returns intervalID or null if interval doesn't exist
	 * @param timeFrom
	 * @param timeTo
	 * @return intervalID or '0' if it doesn't exist
	 */
	public int getIntervalID(Interval interval){
		db.open(context,TAG);
		int result[] = Utils.getRow(getResultInt(db.getDB().query(NAME, new String[]{DB[IID]}, DB[1] + " = ? AND "+ DB[2] + " = ?",
				new String[]{""+interval.getTimeFrom().toMinutes(),""+interval.getTimeTo().toMinutes()}, null, null, null)),0);
		db.close(TAG);
		return (result.length > 0) ? result[0]: 0;
	}
}
