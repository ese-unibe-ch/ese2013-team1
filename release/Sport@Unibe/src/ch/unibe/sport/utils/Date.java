package ch.unibe.sport.utils;

import java.util.Calendar;

/**
 * Stable and tested Date class for fast operations with dates
 * @version 1.6 2013-08-14
 * @author Team
 */
public class Date {
	public static final int[] DAYS_IN_MONTH = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static final int JANUARY = 1;
	public static final int FEBRUARY = 2;
	public static final int MARCH = 3;
	public static final int APRIL = 4;
	public static final int MAY = 5;
	public static final int JUNI = 6;
	public static final int JULY = 7;
	public static final int AUGUST = 8;
	public static final int SEPTEMBER = 9;
	public static final int OCTOBER = 10;
	public static final int NOVEMBER = 11;
	public static final int DECEMBER = 12;

	public int day;
	public int month;
	public int year;

	private boolean invariant(){
		return isValidDate(day,month,year)
				&& DAYS_IN_MONTH.length == 12;
	}
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	/**
	 * Constucts with current date
	 */
	public Date(){
		this(Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				Calendar.getInstance().get(Calendar.MONTH)+1,
				Calendar.getInstance().get(Calendar.YEAR));
	}
	/**
	 * Date(int day, int month, int year)
	 * @param day
	 * @param month
	 * @param year
	 */
	public Date(int day,int month,int year){
		this.day = day;
		this.month = month;
		this.year = year;
		assert invariant();
	}
	public Date(String date){
		int[] tmp = Utils.split(date,'-',3);
		this.day = tmp[2];
		this.month = tmp[1];
		this.year = tmp[0];
		assert invariant();
	}
	
	public Date(int inttime){
		this.year = inttime/10000;
		this.month = (inttime - this.year * 10000) / 100;
		this.day = (inttime - this.year * 10000 - this.month * 100);
		assert invariant();
	}
	
	/*------------------------------------------------------------
	-------------------------- G E T T E R S ---------------------
	------------------------------------------------------------*/
	public int getDay(){
		assert invariant();
		return this.day;
	}
	public int getMonth(){
		assert invariant();
		return this.month;
	}
	public int getYear(){
		assert invariant();
		return this.year;
	}
	public int getDaysInMonth(){
		return DAYS_IN_MONTH[month-1] + ((this.month == FEBRUARY) && isLeapYear(year) ? 1 : 0);
	}
	/*------------------------------------------------------------
	-------------------------- S E T T E R S ---------------------
	------------------------------------------------------------*/
	public Date setDay(int day){
		assert day >= 1 && day <= 31;
		this.day = day;
		assert invariant();
		return this;
	}
	
	public Date setMonth(int month){
		assert month >= 1 && month <= 12;
		this.month = month;
		assert invariant();
		return this;
	}
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	/** Compares {@code Date} object with anotherDate and
	 *  returns:</br>
	 * -1 (when anotherDate > this Date),</br>
	 *  0 (when anotherDate = this Date),</br>
	 * +1 (when anotherDate < this Date) 
	 */
	public int compareTo(Date anotherDate){
		assert anotherDate != null;
		assert invariant();
		if (this.year > anotherDate.year) return 1;
		else if (this.year < anotherDate.year) return -1;
		else if (this.month > anotherDate.month) return 1;
		else if (this.month < anotherDate.month) return -1;
		else if (this.day > anotherDate.day) return 1;
		else if(this.day < anotherDate.day)	return -1;
		else return 0;
	}
	/**
	 * Returns number of days between this date and another date.</br>
	 * If this date is bigger then another, then number will be negative.</br>
	 * In case of the same dates 0 will be returned.
	 * @param anotherDate
	 * @return int - number of days between this and another date
	 */
	public int daysUntil(Date anotherDate){
		assert invariant();
		assert anotherDate != null;
		int compare = this.compareTo(anotherDate);
		if (compare == 0) return 0;
		boolean reverse = (compare == -1) ? false : true;
		int diff = 0;
		for (int year = (reverse) ? anotherDate.year : this.year; year <= ((reverse) ? this.year : anotherDate.year); year++){
			for (int month = (year == ((reverse) ? anotherDate.year : this.year)) ? ((reverse) ? anotherDate.month : this.month) : 1; month <= (year == ((reverse) ? this.year : anotherDate.year) ? ((reverse) ? this.month : anotherDate.month) : 12); month++){
				for (int day = (month == ((reverse) ? anotherDate.month : this.month) && year == ((reverse) ? anotherDate.year : this.year)) ? ((reverse) ? anotherDate.day : this.day): 1; day <= ((month == ((reverse) ? this.month : anotherDate.month) && year == ((reverse) ? this.year : anotherDate.year)) ? ((reverse) ? this.day : anotherDate.day) : DAYS_IN_MONTH[month-1] + (((month == FEBRUARY) && isLeapYear(year)) ? 1 : 0)); day++){
					diff++;
				}
			}
		}
		return (reverse) ? -diff+1 : diff-1;
	}
	/**
	 * Returns number of month between this date and another date.</br>
	 * If another date is bigger then this, then number will be negative.</br>
	 * In case of the same dates 0 will be returned.
	 * @param anotherDate
	 * @return int - number of month between this and another date
	 */
	public int monthsUntil(Date anotherDate){
		assert invariant();
		assert anotherDate != null;
		int compare = this.compareTo(anotherDate);
		if (compare == 0) return 0;
		boolean reverse = (compare == -1) ? false : true;
		int diff = 0;
		for (int year = (reverse) ? anotherDate.year : this.year; year <= ((reverse) ? this.year : anotherDate.year); year++){
			for (int month = (year == ((reverse) ? anotherDate.year : this.year)) ? ((reverse) ? anotherDate.month : this.month) : 1; month <= (year == ((reverse) ? this.year : anotherDate.year) ? ((reverse) ? this.month : anotherDate.month) : 12); month++){
				diff++;
			}
		}
		return (reverse) ? -diff+1 : diff-1;
	}
	/**
	 * Returns date that was of will be {@code dayOffset} days ago.
	 * @param dayOffset - number of days
	 * @return {@code Date} with dayOffset offset
	 */
	public Date findDate(int dayOffset){
		assert invariant();
		Date date = new Date(day,month,year);
		if (dayOffset == 0) return date;
		for (int i = 0; i < Math.abs(dayOffset); i++){
			if (dayOffset < 0) date.prev();
			else date.next();
		}
		return date;
	}
	/**
	 * Returns next to this date and changes date to next day;
	 * @return {@code Date} - next date
	 */
	public Date next(){
		assert invariant();
		day++;
		if (day > DAYS_IN_MONTH[month-1] + ((this.month == FEBRUARY) && isLeapYear(year) ? 1 : 0)) {
			day = 1;
			month++;
			if (month > 12){
				month = 1;
				year++;
			}
		}
		assert invariant();
		return this;
	}
	/**
	 * Returns previous to this date and changes date to previous day;
	 * @return {@code Date} - previous date
	 */
	public Date prev(){
		assert invariant();
		day--;
		if (day < 1){
			month--;
			if (month < 1){
				month = 12;
				year--;
			}
			day = DAYS_IN_MONTH[month-1] + ((month == FEBRUARY) && isLeapYear(year) ? 1 : 0);
		}
		assert invariant();
		return this;
	}
	
	/**
	 * Changes month to delta amount
	 * @param delta - number of months to be jumped.
	 * @return
	 */
	public Date jumpMonth(int delta){
		assert invariant();
		if (delta == 0) return this;
		boolean lastDay = false;
		if (this.day == this.getDaysInMonth()){
			lastDay = true;
		}
		boolean reverse = (delta < 0);
		for (int i = 0; i < Math.abs(delta); i++){
			month += (reverse) ? -1 : 1;
			if (reverse){
				if (month < 1){
					month = 12;
					year--;
				}
			}
			else {
				if (month > 12){
					month = 1;
					year++;
				}
			}
		}
		if (lastDay || this.day > this.getDaysInMonth()){
			this.day = this.getDaysInMonth();
		}
		assert invariant();
		return this;
	}
	
	public Date copy(){
		return new Date(day,month,year);
	}
	
	/*------------------------------------------------------------
	-------------------------- C H E C K S -----------------------
	------------------------------------------------------------*/
	/**
	 * Returns {@code True} if this Date's day is previous for input Date,
	 * otherwise returns {@code False}
	 * @return {@code True} if day is previuos for input, otherwise {@code False}
	 */
	public boolean isPrev(Date date){
		int d = day;
		int m = month;
		int y = year;
		d++;
		if (d > Date.DAYS_IN_MONTH[m-1] + ((m == Date.FEBRUARY) && Date.isLeapYear(y) ? 1 : 0)){
			d = 1;
			m++;
			if (m > 12){
				m = 1;
				y++;
			}
		}
		if (d == date.day && m == date.month && y == date.year){
			return true;
		}
		else {
			return false;
		}
}
	/**
	 * Returns {@code True} if this Date's day is next for input Date,
	 * otherwise returns {@code False}
	 * @return {@code True} if day is next for input, otherwise {@code False}
	 */
	public boolean isNext(Date date){
		return (this.compareTo(date.findDate(1)) == 0);
	}
	/**
	 * Returns {@code True} if this Date's day is equal for input Date,
	 * otherwise returns {@code False}
	 * @return {@code True} if day is equal for input, otherwise {@code False}
	 */
	public boolean isEqual(Date date){
		return (this.compareTo(date) == 0);
	}
	/**
	 * Returns {@code True} if this Date's month is equal to input's Date month,
	 * otherwise returns {@code False}
	 * @return {@code True} if month is equal for input, otherwise {@code False}
	 */
	public boolean isEqualMonth(Date date){
		return (this.month == date.month) && (this.year == date.year);
	}
	/**
	 * Returns {@code True} if this Date's day is last day in the month,
	 * otherwise returns {@code False}
	 * @return {@code True} if day is last in the month, otherwise {@code False}
	 */
	public boolean isLastDayInMonth(){
		assert invariant();
		return (this.day == DAYS_IN_MONTH[month-1] + ((month == FEBRUARY) && isLeapYear(year) ? 1 : 0)) ? true : false;
	}
	/**
	 * Returns {@code True} if this Date's day is first day in the month,
	 * otherwise returns {@code False}
	 * @return {@code True} if day is first in the month, otherwise {@code False}
	 */
	public boolean isFirstDayInMonth(){
		assert invariant();
		return (this.day == 1);
	}
	/**
	 * Returns {@code True} if {@code year} is leap year,
	 * otherwise returns {@code False}
	 * @return {@code True} if {@code year} is leap year, otherwise {@code False}
	 */
	public static boolean isLeapYear(int year){
		assert year > 0;
		return (((year%4 == 0) && (year%100 != 0)) || (year%400 == 0));
	}
	
	public static boolean isValidDate(int day,int month,int year){
		return (isValidMonth(month) && isValidYear(year) && isValidDay(day) && (day <= DAYS_IN_MONTH[month-1] + ((month == FEBRUARY) && isLeapYear(year) ? 1 : 0)));
	}
	
	private static boolean isValidDay(int day){
		return day >= 1 && day <= 31;
	}
	
	/**
	 * Day of week should be between [1..7]
	 * @param month
	 * @return
	 */
	public static boolean isValidDayOfWeek(int day){
		return day >= 1 && day <= 7;
	}
	/**
	 * Month should be between [1..12]
	 * @param month
	 * @return
	 */
	private static boolean isValidMonth(int month){
		return month >= 1 &&  month <= 12;
	}
	private static boolean isValidYear(int year){
		return true;
	}
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	/**
	 * Converts this {@code Date} object to the yyyy-mm-dd representation.
	 * @return {@code String} object holding string representation of {@code Date}.
	 */
	public String toString(){
		StringBuilder str = new StringBuilder().append(year).append('-');
		if (month < 10) str.append('0');
		str.append(month).append('-');
		if (day < 10) str.append('0');
		str.append(day);
		return str.toString();
	}
	
	public String print(){
		StringBuilder str = new StringBuilder();
		if (day < 10) str.append('0');
		str.append(day).append('.');
		if (month < 10) str.append('0');
		str.append(month).append('.');
		str.append(year);
		return str.toString();
	}
	
	public int toInt(){
		assert invariant();
		return this.year*10000+this.month*100+this.day;
	}
	
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Date))
		      return false;
		Date date = (Date) obj;
		return (date.day == this.day && date.month == this.month
				&& date.year == this.year);
	}
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = hash * prime + day;
		hash = hash * prime + month;
		hash = hash * prime + year;
		return hash;
	}

}
