package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;

public class FriendsCourse extends Table{

	public static final String NAME = "friends_course";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"fid integer NOT NULL, " +
			"cid integer NOT NULL "+
		");";
	public static final String[] DB = {
		"fid",					// 0
		"cid"					// 1
	};
	
	protected FriendsCourse(Context context) {
		super(context, NAME, CREATE, DB);
	}

}
