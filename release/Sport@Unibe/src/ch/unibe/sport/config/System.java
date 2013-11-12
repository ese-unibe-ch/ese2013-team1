package ch.unibe.sport.config;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.provider.Settings.Secure;

/**
 * System configuraton class that loads and stores
 * parameters for simple and fast access in runtime
 * 
 * @version 1.0 2013-09-19
 * @author Aliaksei Syrel
 */

public class System extends Preferences {
	private static final String TAG = "system";
	
	private static final String BUILD_TIME_NAME = "build_time";
	
	public Date TODAY;
	public String BUILD_TIME;
	public String UUID;
		
	public final boolean OK;
	
	public System(Context context) {
		super(TAG, context);
		
		this.TODAY = readToday();
		this.BUILD_TIME = readBuildTime();
		this.UUID = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
		this.OK = true;
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
	
	/*------------------------------------------------------------
	----------------------------- I N I T ------------------------
	------------------------------------------------------------*/
	
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
