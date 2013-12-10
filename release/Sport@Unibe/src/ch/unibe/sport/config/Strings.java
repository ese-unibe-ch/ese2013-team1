package ch.unibe.sport.config;

import ch.unibe.sport.R;
import android.content.Context;

/**
 * String configuration class that loads and stores
 * data from strings.xml for multi-language support
 * and fast access in runtime
 * 
 * @version 1.0 2013-09-19
 * @author Team 1
 */
public class Strings extends Preferences {
	private static final String TAG = "strings";

	public final String[] DAYS_OF_WEEK_SHORT = new String[7];
	public final String[] MONTHS_OF_YEAR = new String[12];
	
	public final boolean OK;
	
	public Strings(Context context){
		super(TAG,context);
		
		readDaysOfWeekShort();
		readMonthsOfYear();
		
		check();
		this.OK = true;
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
	----------------------------- R E A D ------------------------
	------------------------------------------------------------*/
	private void readDaysOfWeekShort(){
		DAYS_OF_WEEK_SHORT[0] = resources.getString(R.string.monday_short);
		DAYS_OF_WEEK_SHORT[1] = resources.getString(R.string.tuesday_short);
		DAYS_OF_WEEK_SHORT[2] = resources.getString(R.string.wednesday_short);
		DAYS_OF_WEEK_SHORT[3] = resources.getString(R.string.thursday_short);
		DAYS_OF_WEEK_SHORT[4] = resources.getString(R.string.friday_short);
		DAYS_OF_WEEK_SHORT[5] = resources.getString(R.string.saturday_short);
		DAYS_OF_WEEK_SHORT[6] = resources.getString(R.string.sunday_short);
	}
	
	private void readMonthsOfYear(){
		MONTHS_OF_YEAR[0] = resources.getString(R.string.january);
		MONTHS_OF_YEAR[1] = resources.getString(R.string.february);
		MONTHS_OF_YEAR[2] = resources.getString(R.string.march);
		MONTHS_OF_YEAR[3] = resources.getString(R.string.april);
		MONTHS_OF_YEAR[4] = resources.getString(R.string.may);
		MONTHS_OF_YEAR[5] = resources.getString(R.string.june);
		MONTHS_OF_YEAR[6] = resources.getString(R.string.july);
		MONTHS_OF_YEAR[7] = resources.getString(R.string.august);
		MONTHS_OF_YEAR[8] = resources.getString(R.string.september);
		MONTHS_OF_YEAR[9] = resources.getString(R.string.october);
		MONTHS_OF_YEAR[10] = resources.getString(R.string.november);
		MONTHS_OF_YEAR[11] = resources.getString(R.string.december);
	}
}
