package ch.unibe.sport.config;

import android.content.Context;

public class User extends Preferences {
	private static final String TAG = "user";
	
	public static final String INVARIANT_PREFERENCES_NAME = Config.PACKAGE_NAME+".invariant";
	
	private static final String USER_ID_NAME = "user_id";
	private static final String USER_NICKNAME_NAME = "user_nickname";
	private static final String USER_USERNAME_NAME = "user_username";
	
	public int ID;
	public String NICKNAME;
	public String USERNAME;
	
	public User (Context context) {
		super(TAG, context);
		this.ID = readUserID();
		this.NICKNAME = readUserNickname();
		this.USERNAME = readUserUsername();
	}

	@Override
	public void reInit() {}

	@Override
	public void check() {}
	
	public boolean isRegistered(){
		return ID > 0 && this.NICKNAME != null && this.NICKNAME.length() > 0 && !this.NICKNAME.equals(NULL);
	}
	
	/*------------------------------------------------------------
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/
	public int readUserID() {
		return this.getInt(USER_ID_NAME);
	}
	
	public String readUserNickname(){
		return this.getString(USER_NICKNAME_NAME);
	}
	
	public String readUserUsername(){
		return this.getString(USER_USERNAME_NAME);
	}
	
	/*------------------------------------------------------------
	----------------------------- S A V E ------------------------
	------------------------------------------------------------*/
	public void setUserID(int id) {
		this.save(USER_ID_NAME, id);
		this.ID = id;
	}
	
	public void setUserNickname(String nickname){
		this.save(USER_NICKNAME_NAME,nickname);
		this.NICKNAME = nickname;
	}
	
	public void setUserUsername(String username){
		this.save(USER_USERNAME_NAME,username);
		this.USERNAME = username;
	}
}
