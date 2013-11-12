package ch.unibe.sport.DBAdapter.tables;

import android.content.Context;

public class SportCourses extends Table{

	public static final String NAME = "sport_courses";
	public static final String CREATE = "CREATE TABLE " + NAME + " (" +
			"sid integer NOT NULL, "+
			"cid integer NOT NULL UNIQUE, " +
			"CONSTRAINT chk_sport_courses CHECK (cid > 0 AND sid > 0)"+
		");";
	
	public static final String[] DB = {
		"sid",				// 0
		"cid"				// 1
	};
	
	public static final int SID = 0, CID = 1;
	
	public SportCourses(Context context) {
		super(context, NAME, CREATE, DB);
	}
	
	public void add(int sid, int cid){
		this.insert(new int[]{SID,CID}, new String[]{""+sid,""+cid});
	}

}
