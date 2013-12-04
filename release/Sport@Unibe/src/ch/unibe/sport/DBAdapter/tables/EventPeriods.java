package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import ch.unibe.sport.utils.Utils;

public class EventPeriods extends Table {
	public static final String TAG = EventPeriods.class.getName();
	
	public static final String NAME = "eventPeriods";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"eid integer NOT NULL,"+
			"periods TINYINT NOT NULL,"+
			"CHECK (periods BETWEEN 1 AND 5),"+
			"CONSTRAINT unique_eid_period UNIQUE (eid,periods)"+
		");";
	
	public static final String[] DB = {
		"eid",						// 0
		"periods",					// 1
	};
	
	public static final int EID = 0, PERIOD= 1;
	
	public EventPeriods(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public int[] getPeriods(int eventID){
		db.open(context,TAG);
		int[] result = 
			Utils.getRow(
				Utils.transpose(
					getResultInt(
						db.getDB().query(
							NAME,
							new String[]{DB[PERIOD]}, DB[EID] + " = ?",
							new String[]{""+eventID}, null, null, DB[PERIOD]
						)
					)
				),
			EID);
		db.close(TAG);
		return result;
	}
}
