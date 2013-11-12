package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;

public class FacebookFriends extends Table {

	public static final String NAME = "facebook_friends";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"fid integer, " +
			"name char NOT NULL, "+
			"CONSTRAINT facebook_friends_pkey PRIMARY KEY (fid)"+
		");";
	public static final String[] DB = {
		"fid",					// 0
		"name"					// 1
	};
	
	public static final int FID = 0, FRIEND_NAME = 1;
	
	public FacebookFriends(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public long insert(String name){
		return this.insert(new int[]{FRIEND_NAME}, new String[]{name});
	}

}
