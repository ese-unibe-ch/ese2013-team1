package ch.unibe.sport.config;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * System configuraton class that loads and stores
 * parameters for simple and fast access in runtime
 * 
 * @version 1.0 2013-09-19
 * @author Team 1
 */

public class System extends Preferences {
	private static final String TAG = "system";
	
	public static final String INVARIANT_PREFERENCES_NAME = Config.PACKAGE_NAME+".invariant";
	
	private static final String BUILD_TIME_NAME = "build_time";
	private static final String UUID_NAME = "uuid";
	private static final String LAST_UPDATE_NAME = "last_update";
	
	public Date TODAY;
	public String BUILD_TIME;
	public String UUID = NULL;
	public int LAST_UPDATE;
		
	public static boolean INIT = false;
	
	
	private OnSystemInitializedListener mOnSystemInitializedListener;
	
	public interface OnSystemInitializedListener {
		public void onSystemInitialized();
	}
	
	public System(Context context,OnSystemInitializedListener l) {
		super(TAG, context);
		this.mOnSystemInitializedListener = l;
		this.TODAY = readToday();
		this.BUILD_TIME = readBuildTime();
		this.LAST_UPDATE = readLastUpdate();
		this.UUID = readUUID();
		checkInitialized();
	}
	
	private void checkInitialized(){
		if (checkUUIDInitialized() && checkDataUpdated()) setSystemInitialized();
	}
	
	public boolean checkUUIDInitialized(){
		Print.log(TAG,"UUID: "+UUID);
		return !UUID.equals(NULL);
	}
	
	public boolean checkDataUpdated(){
		Print.log(TAG,"Last update: "+LAST_UPDATE);
		return LAST_UPDATE > 0 && new Date(LAST_UPDATE).daysUntil(TODAY) < 4;
	}
	
	
	/*------------------------------------------------------------
	-------------------------- P U B L I C  ----------------------
	------------------------------------------------------------*/	
	public void setSystemInitialized(){
		INIT = true;
		Print.log(TAG,"setSystemInit");
		if (mOnSystemInitializedListener != null){
			mOnSystemInitializedListener.onSystemInitialized();
		}
		else {
			Print.err("mOnSystemInitializedListener is null");
		}
	}
	

	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void reInit() {
		this.BUILD_TIME = initBuildTime();
	}
	
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/
	@Override
	public void check(){
		if(!BUILD_TIME.equals(getBuildTime())) Config.INST.REINIT = true;
		if (Config.INST.REINIT){
			reInit();
		}
	}
	/*------------------------------------------------------------
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/

	private Date readToday(){
		return new Date();
	}
	
	private String readBuildTime(){
		String buildTime = this.getString(BUILD_TIME_NAME);
		if (buildTime.equals(NULL)) buildTime = initBuildTime();
		return buildTime;
	}
	
	public String readUUID() {
		return this.getString(INVARIANT_PREFERENCES_NAME,UUID_NAME);
	}
	
	public int readLastUpdate(){
		return this.getInt(LAST_UPDATE_NAME);
	}
	
	
	
	/*------------------------------------------------------------
	----------------------------- I N I T ------------------------
	------------------------------------------------------------*/
	public static String generateUUID(){
		return java.util.UUID.randomUUID().toString();
	}
	
	public void saveUUID(String uuid){
		UUID = uuid;
		save(INVARIANT_PREFERENCES_NAME,UUID_NAME,UUID);
		checkInitialized();
		/* notify backup manager that it should store UUID in cloud */
		new BackupManager(context).dataChanged();
	}
	
	public void setUnisportDataUpdated(){
		this.LAST_UPDATE = TODAY.toInt();
		save(LAST_UPDATE_NAME,LAST_UPDATE);
		checkInitialized();
	}
	
	private String initBuildTime(){
		String buildTime = getBuildTime();
		save(BUILD_TIME_NAME, buildTime);
		return buildTime;
	}
	
	/**
	 * Returns a build time of application. The build time is obtained from
	 * creation time of classes.dex of application
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getBuildTime(){
		String buildTime = NULL;
		try{
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(Config.PACKAGE_NAME, 0);
			ZipFile zipFile = new ZipFile(appInfo.sourceDir);
			ZipEntry zipEntry = zipFile.getEntry("classes.dex");
			long time = zipEntry.getTime();
			buildTime = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new java.util.Date(time));
		}
		catch(Exception e){
			Print.err(TAG,"buildTime: "+e);
		}
		if (buildTime.equals(NULL)){
			Print.err(TAG,"buildTime: "+buildTime);
		}
		return buildTime;
	}
}
