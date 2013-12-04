package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;

public class EventKew extends Table {
	public static final String NAME = "eventKew";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"eid integer NOT NULL,"+
			"kew varchar(1) NOT NULL,"+
			"CONSTRAINT unique_eid_kew UNIQUE (eid,kew)"+
		");";
	
	public static final String[] DB = {
		"eid",						// 0
		"kew",						// 1
	};
	
	public static final int EID = 0, KEW = 1;
	
	public EventKew(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public String[] getKew(int eventID){
		db.open(context,TAG);
		String[] result = 
			Utils.getRow(
				Utils.transpose(
					getResultString(
						db.getDB().query(
							NAME,
							new String[]{DB[KEW]}, DB[EID] + " = ?",
							new String[]{""+eventID}, null, null, DB[KEW]
						)
					)
				),
			EID);
		db.close(TAG);
		return result;
	}
}
