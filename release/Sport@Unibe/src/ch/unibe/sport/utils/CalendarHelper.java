package ch.unibe.sport.utils;

import java.util.Calendar;

import android.util.MonthDisplayHelper;

public class CalendarHelper{
	private Calendar calendar;
	private Date date;
	public int dayOfWeek;
	
	protected boolean invariant(){
		return calendar != null
				&& dayOfWeek >=0
				&& dayOfWeek <=6;
	}
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	public CalendarHelper(){
		update();
		assert invariant();
	}
	public CalendarHelper(Date date){
		initCalendar();
		setDate(date);
		assert invariant();
	}
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/* should run first */
	private void initCalendar(){
		calendar = Calendar.getInstance();
	}
	private void initDate(){
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		date = new Date(day,month,year);
		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		switch (dayWeek){
			case Calendar.SUNDAY:{
				this.dayOfWeek = 6;
				break;
			}
			case Calendar.MONDAY:{
				this.dayOfWeek = 0;
				break;
			}
			case Calendar.TUESDAY:{
				this.dayOfWeek = 1;
				break;
			}
			case Calendar.WEDNESDAY:{
				this.dayOfWeek = 2;
				break;
			}
			case Calendar.THURSDAY:{
				this.dayOfWeek = 3;
				break;
			}
			case Calendar.FRIDAY:{
				this.dayOfWeek = 4;
				break;
			}
			case Calendar.SATURDAY:{
				this.dayOfWeek = 5;
				break;
			}
		}
	}
	
	/**
	 * Updated current date
	 */
	public void update(){
		initCalendar();
		this.initDate();
		assert invariant();
	}
	
	/*------------------------------------------------------------
	-------------------------- S E T T E R S ---------------------
	------------------------------------------------------------*/
	public void setDate(Date date){
		assert date != null;
		calendar.set(date.getYear(), date.getMonth()-1, date.getDay());
		this.date = date;
		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		switch (dayWeek){
			case Calendar.SUNDAY:{
				this.dayOfWeek = 6;
				break;
			}
			case Calendar.MONDAY:{
				this.dayOfWeek = 0;
				break;
			}
			case Calendar.TUESDAY:{
				this.dayOfWeek = 1;
				break;
			}
			case Calendar.WEDNESDAY:{
				this.dayOfWeek = 2;
				break;
			}
			case Calendar.THURSDAY:{
				this.dayOfWeek = 3;
				break;
			}
			case Calendar.FRIDAY:{
				this.dayOfWeek = 4;
				break;
			}
			case Calendar.SATURDAY:{
				this.dayOfWeek = 5;
				break;
			}
		}
		assert invariant();
	}
	/*------------------------------------------------------------
	-------------------------- G E T T E R S ---------------------
	------------------------------------------------------------*/
	public Date getFirstCalendarDate(){
		MonthDisplayHelper mHelper = new MonthDisplayHelper(date.year, date.month-1,2);
		int day = mHelper.getDigitsForRow(0)[0];
		if(mHelper.isWithinCurrentMonth(0,0)){
	    	return new Date(day,date.month,date.year);
	    }
		else {
			int tmpMonth = date.month - 1;
			int tmpYear = date.year;
			if (tmpMonth == 0){
				tmpMonth = 12;
				tmpYear = date.year - 1;
			}
			return new Date(day, tmpMonth, tmpYear);
		}
	}

	public int getDay(){
		assert invariant();
		return this.date.getDay();
	}
	public int getMonth(){
		assert invariant();
		return this.date.getMonth();
	}
	public int getYear(){
		assert invariant();
		return this.date.getYear();
	}
	public int getDayOfWeek(){
		assert invariant();
		return this.dayOfWeek;
	}
}
