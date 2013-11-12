package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import ch.unibe.sport.utils.Utils;

public class Sports extends Table{
	public static final String TAG =  Sports.class.getName();

	public static final String NAME = "sports";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"sid integer NOT NULL UNIQUE," +
			"sport char NOT NULL, "+
			"loaded intefer NOT NULL DEFAULT '0'"+
		");";
	public static final String[] DB = {
		"sid",				// 0
		"sport",			// 1
		"loaded"			// 2
	};
	public static final int SID = 0, SPORT = 1, LOADED = 2;
	
	public Sports(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public boolean insertSports(Object[][] sports){
		boolean result;
		try {
			this.bulkInsert(new int[]{SID,SPORT}, sports, "String");
			result = true;
		} catch (TableNotExistsException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public int[] getSportIDs(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[SID]}, "", null, null, null, DB[SID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	

	public int[] getSportIDsNotLoaded() {
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[SID]}, DB[LOADED] +" = ?", new String[]{"0"}, null, null, DB[SID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public void setLoaded(int sportID){
		this.updateByID(SID, sportID, new int[]{LOADED}, new String[]{"1"});
	}
	
	public String[][] getData(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, "", null, null, null, DB[SID]);
		String[][] result = getResultString(cursor);
		db.close(TAG);
		return result;
	}
	
	public String[] getData(int sportID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[SID]+" = ?", new String[]{""+sportID}, null, null, DB[SID]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	public int[] getCoursesIDs(int sportID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(SportCourses.NAME, new String[]{SportCourses.DB[SportCourses.CID]}, SportCourses.DB[SportCourses.SID]+" = ?", new String[]{""+sportID}, null, null, SportCourses.DB[SportCourses.CID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	

}
