package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;

public class FriendsFacebook extends Table{

	public static final String NAME = "friends_facebook";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"fbid integer NOT NULL UNIQUE, " +
			"name char NOT NULL, "+
			"installed integer default '0', "+
			"picture char NOT NULL,"+
		");";
	public static final String[] DB = {
		"fbid",					// 0
		"name",					// 1
		"installed",			// 2
		"picture"
	};
	
	protected FriendsFacebook(Context context) {
		super(context, NAME, CREATE, DB);
	}

}
