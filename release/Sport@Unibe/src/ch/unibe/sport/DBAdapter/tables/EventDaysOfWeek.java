package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import ch.unibe.sport.utils.Utils;

public class EventDaysOfWeek extends Table {
	public static final String NAME = "eventDaysOfWeek";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"eid integer NOT NULL,"+
			"dayOfWeek TINYINT NOT NULL,"+
			"CHECK (dayOfWeek BETWEEN 1 AND 7),"+
			"CONSTRAINT unique_eid_dayOfWeek UNIQUE (eid,dayOfWeek)"+
		");";
	
	public static final String[] DB = {
		"eid",						// 0
		"dayOfWeek"					// 1
	};
	
	public static final int EID = 0, DAY_OF_WEEK= 1;
	
	public EventDaysOfWeek(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public int[] getDaysOfWeek(int eventID){
		db.open(context,TAG);
		int[] result = 
			Utils.getRow(
				Utils.transpose(
					getResultInt(
						db.getDB().query(
							NAME,
							new String[]{DB[DAY_OF_WEEK]}, DB[EID] + " = ?",
							new String[]{""+eventID}, null, null, DB[DAY_OF_WEEK]
						)
					)
				),
			EID);
		db.close(TAG);
		return result;
	}
}
