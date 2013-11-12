package ch.unibe.sport.config;

import android.content.Context;

/**
 * Database configuraton class that loads and stores
 * parameters for simple and fast access in runtime
 * 
 * @version 1.1 2013-09-19
 * @author Aliaksei Syrel
 */
public class Database extends Preferences {
	protected static final String TAG = "database";

	public static final String INIT_NAME = "init";
	
	public boolean INIT = false;
	
	private OnDatabaseInitializedListener mOnDatabaseInitializedListener;
	
	public interface OnDatabaseInitializedListener{
		public void onDatabaseInitialized();
	}
	
	public Database(Context context,OnDatabaseInitializedListener l){
		super(TAG,context);
		this.mOnDatabaseInitializedListener = l;
		this.INIT = readInit();
		
		check();
	}

	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void reInit() {}
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/
	@Override
	public void check() {
		if (Config.INST.REINIT){
			reInit();
		}
	}
	/*------------------------------------------------------------
	-------------------------- P U B L I C  ----------------------
	------------------------------------------------------------*/	
	public void setDatabaseInitialized(){
		save(INIT_NAME,true);
		this.INIT = true;
		if (mOnDatabaseInitializedListener != null){
			mOnDatabaseInitializedListener.onDatabaseInitialized();
		}
	}
	/*------------------------------------------------------------
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/
	private boolean readInit() {
		return this.getBoolean(INIT_NAME);
	}
	/*------------------------------------------------------------
	----------------------------- I N I T ------------------------
	------------------------------------------------------------*/
}
