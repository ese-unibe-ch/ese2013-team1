package ch.unibe.sport.config;

import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Display configuration class that loads and stores
 * parameters for simple and fast access in runtime
 * 
 * @version 1.0 2013-09-19
 * @author Team 1
 */
public class Display extends Preferences {
	private static final String TAG  = "display";
	
	private static final String WIDTH_NAME = "width";
	private static final String HEIGHT_NAME = "height";
	private static final String DPI_NAME = "dpi";
	private static final String STATUS_BAR_HEIGHT_NAME = "status_bar_height";
	
	public int HEIGHT;
	public int WIDTH;
	public final int DPI;
	public int STATUS_BAR_HEIGHT;
	
	public final boolean OK;
	
	public Display(Context context){
		super(TAG,context);
		this.WIDTH = readScreenWidth();
		this.HEIGHT = readScreenHeight();
		this.DPI = readDensityDpi();
		this.STATUS_BAR_HEIGHT = readStatusBarHeight();
		
		check();
		this.OK = true;
	}
	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void reInit() {
		Print.log(TAG,"reinit");
		this.WIDTH = initScreenWidth();
		this.HEIGHT = initScreenHeight();
		this.STATUS_BAR_HEIGHT = initStatusBarHeight();
	}
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/
	@Override
	public void check() {
		if(WIDTH != getScreenWidth() || HEIGHT != getScreenHeight())  Config.INST.REINIT = true;
		if (Config.INST.REINIT){
			reInit();
		}
	}
	/*------------------------------------------------------------
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/
	
	private int readScreenWidth(){
		int width = this.getInt(WIDTH_NAME);
		if (width == NONE) width = this.initScreenWidth();
		return width;
	}
	
	private int readScreenHeight(){
		int height = this.getInt(HEIGHT_NAME);
		if (height == NONE) height = this.initScreenHeight();
		return height;
	}
	
	private int readDensityDpi(){
		int dpi = this.getInt(DPI_NAME);
		if (dpi == NONE) dpi = this.initDensityDpi();
		return dpi;
	}
	
	private int readStatusBarHeight(){
		int statusBarHeight = this.getInt(STATUS_BAR_HEIGHT_NAME);
		if (statusBarHeight == NONE) statusBarHeight = this.initStatusBarHeight();
		return statusBarHeight;
	}
	/*------------------------------------------------------------
	----------------------------- I N I T ------------------------
	------------------------------------------------------------*/
	
	private int initScreenWidth() {
		int width = getScreenWidth();
		save(WIDTH_NAME, width);
		return width;
	}
	
	private int initScreenHeight() {
		int height = getScreenHeight();
		save(HEIGHT_NAME, height);
		return height;
	}

	private int initDensityDpi(){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int dpi = dm.densityDpi;
		save(DPI_NAME, dpi);
		return dpi;
	}
	
	private int initStatusBarHeight() {
		int sbHeight = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			sbHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		save(STATUS_BAR_HEIGHT_NAME, sbHeight);
		return sbHeight;
	}
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private int getScreenWidth() {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		android.view.Display display = wm.getDefaultDisplay();
		int width = 0;
		/*
		 * New api method since Honeycomb MR2
		 */
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		}
		else {
			width = display.getWidth();  // for compatibility with api < 13
		}
		return width;
	}
	

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private int getScreenHeight(){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		android.view.Display display = wm.getDefaultDisplay();
		int height = 0;
		/*
		 * New api method since Honeycomb MR2
		 */
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			Point size = new Point();
			display.getSize(size);
			height = size.y;
		}
		else {
			height = display.getWidth();  // for compatibility with api < 13
		}
		return height;
	}
}
