package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class Rating extends Table{

	public static final String TAG =  Rating.class.getName();

	public static final String NAME = "rating";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"cid integer NOT NULL UNIQUE, "+
			"rating integer NOT NULL, "+
			"CONSTRAINT chk_rating CHECK (cid > 0 AND rating BETWEEN 1 AND 5), "+
			"CONSTRAINT unique_cid_iid UNIQUE (cid,rating)"+
		");";
	
	public static final String[] DB = {
		"cid",				// 0
		"rating"			// 1
	};
	
	public static final int CID = 0,RATING = 1;
	
	public Rating(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long add(int courseID, int rating){
		return this.insert(new int[]{CID,RATING}, new String[]{""+courseID,""+rating});
	}
	
	public void set(int courseID, int rating){
		if (isRated(courseID))this.updateByID(CID, courseID, new int[]{RATING}, new String[]{""+rating});
		else add(courseID,rating);
	}
	
	public boolean isRated(int courseID) {
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[CID]}, DB[CID]+" = ?",new String[]{""+courseID}, null, null, DB[CID]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result.length > 0;
	}
	
	public int[] getData(int courseID){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[CID]+" = ?", new String[]{""+courseID}, null, null, DB[CID]);
		int[] result = Utils.getRow(getResultInt(cursor),0);
		db.close(TAG);
		return result;
	}
	

}
