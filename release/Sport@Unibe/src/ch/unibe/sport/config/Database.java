package ch.unibe.sport.config;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.DBStructure;
import ch.unibe.sport.utils.Print;
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
	
	public static boolean INIT = false;
	public int VERSION;
	
	private OnDatabaseInitializedListener mOnDatabaseInitializedListener;
	
	public interface OnDatabaseInitializedListener {
		public void onDatabaseInitialized();
	}
	
	public Database(Context context,OnDatabaseInitializedListener l){
		super(TAG,context);
		this.mOnDatabaseInitializedListener = l;
		VERSION = DBAdapter.INST.open(context, TAG).getDB().getVersion();
		DBAdapter.INST.close(TAG);
		Print.log(TAG,VERSION);
		
		if (VERSION == DBStructure.DATABASE_VERSION) setDatabaseInitialized();
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
		INIT = true;
		if (mOnDatabaseInitializedListener != null){
			mOnDatabaseInitializedListener.onDatabaseInitialized();
		}
		else {
			Print.err("mOnDatabaseInitializedListener is null");
		}
	}
	/*------------------------------------------------------------
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/
	/*------------------------------------------------------------
	----------------------------- I N I T ------------------------
	------------------------------------------------------------*/
}
