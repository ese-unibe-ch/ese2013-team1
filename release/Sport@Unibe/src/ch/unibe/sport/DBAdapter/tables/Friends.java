package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;

public class Friends extends Table{

	public static final String NAME = "friends";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"fid integer, " +
			"name char NOT NULL, "+
			"fb char default '', "+
			"CONSTRAINT friends_pkey PRIMARY KEY (fid)"+
		");";
	public static final String[] DB = {
		"fid",					// 0
		"name",					// 1
		"fb"					// 2
	};
	
	protected Friends(Context context) {
		super(context, NAME, CREATE, DB);
	}

}
