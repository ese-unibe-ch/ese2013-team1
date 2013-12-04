package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import ch.unibe.sport.utils.Utils;

public class Sports extends Table {
	public static final String TAG =  Sports.class.getName();

	public static final String NAME = "sports";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"sid integer NOT NULL UNIQUE," +
			"hash char NOT NULL, "+
			"sportName char NOT NULL, "+
			"sportLink char NOT NULL, "+
			"sportImage char DEFAULT NULL,"+
			"descriptionHeader text DEFAULT ''"+
		");";
	public static final String[] DB = {
		"sid",					// 0
		"hash",					// 1
		"sportName",			// 2
		"sportLink",			// 3
		"sportImage",			// 4
		"descriptionHeader"		// 5
	};
	public static final int SID = 0, HASH = 1,SPORT_NAME = 2, SPORT_LINK = 3, SPORT_IMAGE = 4, DESCRIPTION_HEADER = 5;
	
	public Sports(Context context) {
		super(context, NAME, CREATE, DB);
	}
		
	public int[] getSportIDs(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[SID]}, "", null, null, null, DB[SPORT_NAME]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
		
	public String[][] getData(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, "", null, null, null, DB[SPORT_NAME]);
		String[][] result = getResultString(cursor);
		db.close(TAG);
		return result;
	}
	
	public String[] getData(int sportID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[SID]+" = ?", new String[]{""+sportID}, null, null, null);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	public int[] getEventIDs(int sportID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(SportEvents.NAME, new String[]{SportEvents.DB[SportEvents.EID]}, SportEvents.DB[SportEvents.SID]+" = ?", new String[]{""+sportID}, null, null, SportEvents.DB[SportEvents.EID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	

}
