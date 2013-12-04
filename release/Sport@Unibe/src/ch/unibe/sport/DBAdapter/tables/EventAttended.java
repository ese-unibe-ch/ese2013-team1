package ch.unibe.sport.DBAdapter.tables;

import java.util.ArrayList;

import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.Time;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class EventAttended extends Table{
	public static final String TAG = EventAttended.class.getName();
	
	public static final String NAME = "eventAttended";
	
	public static final String[] DB = {
		"hash",				// 0
		"date",				// 1
		"share"				// 2
	};
	
	public static final int HASH = 0, DATE = 1, SHARE = 2;
	
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			DB[HASH]+" char NOT NULL, "+
			DB[DATE]+" integer NOT NULL, "+
			DB[SHARE]+" integer NOT NULL,"+
			"CONSTRAINT unique_hash_date UNIQUE ("+DB[HASH]+","+DB[DATE]+")"+
		");";
	
	public EventAttended(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long add(String hash, Date date, boolean share){
		long id = 0;
		if (!this.isAttended(hash,date)) {
			id = this.insert(new int[]{HASH,DATE,SHARE},new String[]{hash,""+date.toInt(),(share) ? "1" : "0"});
			Config.INST.SYNCHRONIZE.setSynchronized(false);
		}
		return id;
	}
	
	public void remove(String hash,Date date) {
		db.open(context,TAG);
		String query = "DELETE FROM "+NAME+" WHERE "+DB[HASH]+" = "+hash+" AND "+DB[DATE]+" = "+date.toInt();
		db.getDB().execSQL(query);
		db.close(TAG);
	}
		
	public String[] getAttendedEventsHashs(){
		db.open(context,TAG);
		String query = "SELECT DISTINCT "+DB[HASH]+" FROM "+NAME;
		Cursor cursor = db.getDB().rawQuery(query, null);
		String[] result = Utils.getRow(Utils.transpose(getResultString(cursor)),0);
		db.close(TAG);
		return result;
	}
		
	/**
	 * 
	 * @param hash
	 * @return 0 - not attended, 1 - active attendance, -1 - past attendance
	 */
	public int isAttended(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?",new String[]{hash}, null, null, DB[DATE]+" DESC");
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		if (result.length == 0) return 0;
		Date lastAttended = new Date(Utils.Int(result[DATE]));
		Date today = new Date();
		if (today.compareTo(lastAttended) == 1){
			return -1;
		}
		else return 1;
	}
	
	public ArrayList<Date> getAttendedDates(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[DATE]}, DB[HASH]+" = ?",new String[]{hash}, null, null, DB[DATE]+" DESC");
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		ArrayList<Date> dates = new ArrayList<Date>();
		for (int date : result){
			dates.add(new Date(date));
		}
		db.close(TAG);
		return dates;
	}
	
	public boolean isAttended(String hash, Date date){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[HASH]}, DB[HASH]+" = ? AND "+DB[DATE]+" = ?",new String[]{hash,""+date.toInt()}, null, null, DB[HASH]);
		String[] result = Utils.getRow(Utils.transpose(getResultString(cursor)),0);
		db.close(TAG);
		return result.length > 0;
	}
	
	public int[] getAttendedCoursesInMinutes(Time now, Time before, Date date){
		db.open(context,TAG);
		/*String query = "SELECT ac.cid FROM "+NAME+" ac INNER JOIN "+Events.NAME+" c, "+CourseIntervals.NAME+" ci, "+EventIntervals.NAME + " i ON "
			+" ac.cid = c.cid AND ci.cid = c.cid AND ci.iid = i.iid WHERE i.timeFrom >= ? AND i.timeFrom <= ? AND ac.date = ?";
		Cursor cursor = db.getDB().rawQuery(query, new String[]{""+now.toMinutes(),""+(now.toMinutes()+before.toMinutes()),""+date.toInt()});
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);*/
		db.close(TAG);
		return new int[0];
	}
}
