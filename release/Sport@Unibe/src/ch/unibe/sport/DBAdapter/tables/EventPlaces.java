package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class EventPlaces extends Table {
	public static final String NAME = "places";

    public static final String CREATE = "CREATE TABLE "+NAME+" ("+
        "pid varchar(16) NOT NULL UNIQUE,"+
		"placeName varchar(255) NOT NULL UNIQUE,"+
		"lat char DEFAULT '0.0',"+
		"lon char DEFAULT '0.0',"+
		"CONSTRAINT unique_place UNIQUE (placeName)"+
	")";

    public static final String[] DB = new String[]{
        "pid",			    // 0
        "placeName",        // 1
        "lat",              // 2
        "lon"               // 3
    };

    public static final int PID = 0, PLACE_NAME = 1, LAT = 2, LON = 3;
    
    public EventPlaces(Context context) {
		super(context, NAME, CREATE, DB);
	}
    
    public String[] getData(String placeID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[PID]+" = ?", new String[]{""+placeID}, null, null, DB[PID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
}
