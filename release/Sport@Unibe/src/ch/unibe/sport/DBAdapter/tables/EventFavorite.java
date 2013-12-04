package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import ch.unibe.sport.main.favorites.FavoriteColors;
import ch.unibe.sport.utils.Utils;

public class EventFavorite extends Table{
	public static final String TAG =  EventFavorite.class.getName();

	public static final String NAME = "favoriteEvents";
	
	public static final String[] DB = {
		"hash",				// 0
		"text_color",		// 1
		"bg_color",			// 2
		"bg_type"			// 3
	};
	
	public static final int HASH = 0,TEXT_COLOR = 1,BG_COLOR = 2, BG_TYPE = 3;
	
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"hash char NOT NULL UNIQUE, "+
			"text_color integer NOT NULL, "+
			"bg_color integer NOT NULL, "+
			"bg_type integer NOT NULL, "+
			"CONSTRAINT unique_hash_date UNIQUE ("+DB[HASH]+")"+
		");";
	
	public EventFavorite(Context context) {
		super(context, NAME, CREATE, DB);
	}

	public void add(String hash){
		if (isFavorite(hash)) return;
		this.insert(new int[]{HASH,TEXT_COLOR,BG_COLOR,BG_TYPE},
			new String[]{
				""+hash,
				""+Color.BLACK,
				""+FavoriteColors.BG_COLORS[Utils.randInt(0, FavoriteColors.BG_COLORS.length-1)],
				"1"
			});
	}
	
	public boolean isFavorite(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?", new String[]{""+hash}, null, null, DB[HASH]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		if (result.length == 0) return false;
		else return true;
	}
	
	public String[] getData(String hash){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, DB[HASH]+" = ?", new String[]{""+hash}, null, null, DB[HASH]);
		String[] result = Utils.getRow(getResultString(cursor),0);
		db.close(TAG);
		return result;
	}
	
	public void remove(String hash){
		this.removeByID(HASH, hash);
	}
	
	public int[] getFavoritesIDs(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, new String[]{DB[HASH]}, "", null, null, null, DB[HASH]);
		int[] result = Utils.getRow(Utils.transpose(getResultInt(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public String[] getFavoritesIDsWithoutAttended(){
		db.open(context,TAG);
		String query = "SELECT ef.hash FROM "+EventFavorite.NAME + " ef WHERE NOT EXISTS (SELECT ea.hash FROM "+EventAttended.NAME+" ea WHERE ea.hash = ef.hash)";
		Cursor cursor = db.getDB().rawQuery(query, null);
		String[] result = Utils.getRow(Utils.transpose(getResultString(cursor)),0);
		db.close(TAG);
		return result;
	}
	
	public String[][] getAllFavoritesData(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().query(NAME, DB, "", null, null, null, DB[HASH]);
		String[][] result = getResultString(cursor);
		db.close(TAG);
		return result;
	}
		
	public void setBGColor(String hash, int color){
		db.open(context,TAG);
		this.updateByID(HASH, hash, new int[]{BG_COLOR}, new String[]{""+color});
		db.close(TAG);
	}
}
