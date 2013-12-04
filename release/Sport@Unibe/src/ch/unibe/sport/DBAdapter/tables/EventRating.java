package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.database.Cursor;

public class EventRating extends Table{

	public static final String TAG =  EventRating.class.getName();

	public static final String NAME = "rating";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"hash char NOT NULL UNIQUE, "+
			"rating integer NOT NULL, "+
			"CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5), "+
			"CONSTRAINT unique_hash_rating UNIQUE (hash,rating)"+
		");";
	
	public static final String[] DB = {
		"hash",				// 0
		"rating"			// 1
	};
	
	public static final int HASH = 0, RATING = 1;
	
	public EventRating(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long add(String hash, int rating){
		return this.insert(new int[]{HASH,RATING}, new String[]{hash,""+rating});
	}
	
	public void set(String hash, int rating){
		if (isRated(hash))this.updateByID(HASH, hash, new int[]{RATING}, new String[]{""+rating});
		else add(hash,rating);
	}
	
	public boolean isRated(String hash) {
		return getRating(hash) > 0;
	}
	
	public int getRating(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?", new String[]{hash}, null, null, DB[HASH]);
		int[] result = Utils.getRow(getResultInt(cursor),0);
		db.close(TAG);
		if (result.length == 0) return 0;
		else return result[RATING];
	}
	

}
