package ch.unibe.sport.config;

import android.content.Context;

public class Calendar extends Preferences{
	private static final String TAG  = "calendar";
		
	public final boolean OK;
	
	public Calendar(Context context) {
		super(TAG, context);

		
		check();
		this.OK = true;
	}
	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	@Override
	public void reInit() {
		
	}
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

}
