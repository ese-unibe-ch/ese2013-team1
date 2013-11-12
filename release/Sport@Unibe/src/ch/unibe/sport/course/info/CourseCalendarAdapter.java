package ch.unibe.sport.course.info;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.DBAdapter.tables.CourseDays;
import ch.unibe.sport.DBAdapter.tables.CourseIntervals;
import ch.unibe.sport.calendar.CalendarAdapter;
import ch.unibe.sport.calendar.Day;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.Interval;
import ch.unibe.sport.course.UnknownTime;
import ch.unibe.sport.utils.CalendarHelper;
import ch.unibe.sport.utils.Date;

public class CourseCalendarAdapter implements CalendarAdapter {
	public static final String TAG = CourseCalendarAdapter.class.getName();
	
	public static final int COLUMNS = 7;
	public static final int ROWS = 5;
	private static final int holidaysBgColor = 0xFFFFF5F5;
	private static final int attendedTextColor = Color.RED;
	
	private int calendarWidth;
	private int calendarHeight;
	
	private int cellWidth;
	private int cellHeight;
	
	private int leftRest;
	private int topRest;
	private int rightRest;
	private int bottomRest;
		
	private Day[] mDays;
			
	/**
	 * Initializes days to be displayed in calendar
	 */
	public void initialize(Context context, Course course) {
		Date initDate = new Date().findDate(-7);
		
		this.mDays = new Day[getDaysCount()];
		
		int day = initWeekRow(initDate, 0);
		initCalendarDays(context,course,initDate, day);
	}

	private int initWeekRow(Date initDate, int day) {
		while (day < COLUMNS){
			this.mDays[day] = new Day(
					day, 
					cellWidth + ((day == 0) ? leftRest : ((day == 6) ? rightRest : 0)),
					cellHeight + topRest,
					initDate.copy().setDay((initDate.day == 1) ?  11 : 1), 
					Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[day], 
					Color.BLACK,
					(day < 5) ? Color.WHITE : holidaysBgColor, 
					UnknownTime.INST.time,
				0);
			day++;
		}
		return day;
	}
	
	private void initCalendarDays(Context context, Course course,Date initDate, int day) {
		DBAdapter.INST.open(context, TAG);
		int[] courseDaysRaw = new CourseDays(context).getDays(course.getCourseID());
		Interval[] intervals = new CourseIntervals(context).getIntervals(course.getCourseID());
		ArrayList<Date> attendedDates = new AttendedCourses(context).getAttendedDates(course.getCourseID());
		DBAdapter.INST.close(TAG);
		
		Date tmpDate = initDate.findDate(-new CalendarHelper(initDate).dayOfWeek);
		int daysCount = getDaysCount();
		int dayOfWeek = 0;
		
		int[] courseDays = new int[7];
		for (int k : courseDaysRaw){
			courseDays[k] = 1;
		}
		int rowNum = 1;
		while (day < daysCount){
			if (dayOfWeek == 7) {
				dayOfWeek = 0;
				rowNum++;
			}
			this.mDays[day] = new Day(
					day,
					cellWidth + ((dayOfWeek == 0) ? leftRest : ((dayOfWeek == 6) ? rightRest : 0)),
					cellHeight + ((rowNum == ROWS) ? bottomRest : 0),
					tmpDate.copy(),
					""+tmpDate.day,
					(attendedDates.contains(tmpDate)) ? attendedTextColor : ((Config.INST.SYSTEM.TODAY.monthsUntil(tmpDate) == 0) ? Color.BLACK : Color.LTGRAY),
					(tmpDate.compareTo(Config.INST.SYSTEM.TODAY) == -1) ? Color.WHITE : ((courseDays[dayOfWeek] > 0) ? ((course.isFavorite())) ? course.getBgColor() : ((course.getAttended() != 0) ? 0xffe6ffe0 : 0xffffebeb) : Color.WHITE),
					(tmpDate.compareTo(Config.INST.SYSTEM.TODAY) == -1) ? UnknownTime.INST.time : ((courseDays[dayOfWeek] > 0) ? ((intervals.length > 0) ? intervals[0].getTimeFrom() : UnknownTime.INST.time) : UnknownTime.INST.time),
					(tmpDate.compareTo(Config.INST.SYSTEM.TODAY) == -1) ? 0 : ((courseDays[dayOfWeek] > 0) ? intervals.length : 0),
					(tmpDate.compareTo(Config.INST.SYSTEM.TODAY) == -1) ? true : (courseDays[dayOfWeek] == 0)
				);
			tmpDate.next();
			day++;
			dayOfWeek++;
		}
	}
	
	/**
	 * Calculates cell width, corresponding calendar width
	 */
	private void initCellWidth(){
		cellWidth = (int)((double)calendarWidth/COLUMNS);
	}
	
	/**
	 * @param calendarHeight - calendar height
	 * @return - cell height
	 */
	private void initCellHeight(){
		cellHeight = (int)(Math.floor((double)calendarHeight/ROWS));
	}
	
	@Override
	public int getDaysCount() {
		return COLUMNS * ROWS;
	}

	@Override
	public Day[] getRow(int row) {
		Day[] days = new Day[COLUMNS];
		System.arraycopy(mDays, row*COLUMNS, days, 0, COLUMNS);
		return days;
	}

	@Override
	public void setCalendarWidth(int width) {
		this.calendarWidth = width;
		initCellWidth();
		initWidthRest();
	}

	@Override
	public void setCalendarHeigth(int height) {
		this.calendarHeight = height;
		initCellHeight();
		initHeightRest();
	}
	
	private void initWidthRest(){
		int rest = this.calendarWidth - this.cellWidth * COLUMNS;
		leftRest = rest / 2;
		rightRest = leftRest + rest % 2;
	}
	
	private void initHeightRest(){
		int rest = this.calendarHeight - this.cellHeight * ROWS;
		topRest = rest / 2;
		bottomRest = topRest + rest % 2;
	}

	@Override
	public int[] getDayCoordinates(int index) {
		int rowId = (int) Math.floor((double)index/(double)COLUMNS);
		assert rowId >= 0 && rowId < ROWS;
		int cellId = index - rowId*COLUMNS;
		return new int[]{rowId,cellId};
	}

	@Override
	public int getRowsCount() {
		return ROWS;
	}

}
