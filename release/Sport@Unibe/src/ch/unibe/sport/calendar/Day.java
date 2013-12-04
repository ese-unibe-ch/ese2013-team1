package ch.unibe.sport.calendar;

import ch.unibe.sport.core.Time;
import ch.unibe.sport.utils.Date;

public final class Day {
	public static final int CELL_TEXT_SIZE_PERCENT = 40;
	/**
	 * Methods in Android JIT are much more slower than final fields
	 */
	public final int id;				// id
	public final String text;			// text to be drawn on cell
	public final Date date;  			// date
	public final int textColor;			// text color
	public final int bgColor;			// background color
	public final Time timeFrom;			// time when course begins
	public final int coursesNum;		// number of courses at concrete day
	public final int cellWidth;			// cell width in px
	public final int cellHeight;		// cell height in px
	public final float textSize;		// text size in sp
	public boolean clearDay;			// true if day is clear (without any event)

	public Day(int id, int cellWidth, int cellHeight, Date date, String text, int textColor, int bgColor, Time timeFrom, int coursesNum){
		this(id, cellWidth, cellHeight, date, text,textColor, bgColor, timeFrom, coursesNum,true);
	}
	
	public Day(int id, int cellWidth, int cellHeight, Date date, String text, int textColor, int bgColor, Time timeFrom, int coursesNum, boolean clearDay){
		this.id = id;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.date = date;
		this.text = text;
		this.textColor = textColor;
		this.bgColor = bgColor;
		this.timeFrom = timeFrom;
		this.coursesNum = coursesNum;
		
		this.clearDay = clearDay;
		this.textSize = calcCellTextSize(this.cellWidth,this.cellHeight);
	}
	
	/**
	 * Makes new real exact copy of Day object 
	 * @return
	 */
	public Day copy(){
		return new Day(id, cellWidth, cellHeight, date, text,textColor, bgColor, timeFrom, coursesNum,clearDay);
	}
	
	/**
	 * Calculates cell text size, corresponding cell width and cell height
	 * @param cellWidth
	 * @param cellHeight
	 * @return
	 */
	private int calcCellTextSize(int cellWidth,int cellHeight){
		int cellTextSize = (int)((double)Math.min(cellWidth, cellHeight)*((double)CELL_TEXT_SIZE_PERCENT/100));
		return cellTextSize;
	}
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Day))
		      return false;
		Day day = (Day) obj;
		return (day.id == this.id && day.date.equals(this.date) && this.timeFrom.equals(day.timeFrom));
	}
	public int hashCode() {
		final int prime = 7;
		int hash = 1;
		hash = hash * prime + id;
		hash = hash * prime + date.hashCode();
		hash = hash * prime + textColor;
		hash = hash * prime + bgColor;
		hash = hash * prime + timeFrom.hashCode();
		return hash;
	}
}
