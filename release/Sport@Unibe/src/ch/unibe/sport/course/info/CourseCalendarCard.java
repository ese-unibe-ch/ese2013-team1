package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.calendar.Calendar;
import ch.unibe.sport.calendar.Calendar.OnMeasuredListener;
import ch.unibe.sport.calendar.Cell;
import ch.unibe.sport.course.Course;
import android.content.Context;
import android.view.View;

public class CourseCalendarCard extends AbstractInfoCard {

	public static final String TAG = CourseCalendarCard.class.getName();

	private final Calendar mCalendar;
	private final DayListener dayListener = new DayListener();
	
	private CourseCalendarAdapter calendarAdapter;
		
	public CourseCalendarCard(Context context) {
		super(context, TAG);
		View.inflate(getContext(), R.layout.course_info_calendar, this);
		this.mCalendar = (Calendar) this.findViewById(R.id.calendar);
	}

	private void init(final Course course) {
		this.dayListener.setCourseID(course.getCourseID());
        this.mCalendar.setOnMeasuredListener(new OnMeasuredListener(){
			@Override
			public void OnMeasured(int width, int height) {
				calendarAdapter = new CourseCalendarAdapter();
				calendarAdapter.setCalendarWidth(width);
				calendarAdapter.setCalendarHeigth(height);
				calendarAdapter.initialize(getContext(),course);
		        mCalendar.setAdapter(calendarAdapter);
		        mCalendar.setOnCellClickListener(dayListener);
			}
        });
	}
	
	public void setCourse(Course course){
		init(course);
	}
	
	public void update(Course course){
		this.dayListener.setCourseID(course.getCourseID());
		calendarAdapter.initialize(getContext(),course);
        mCalendar.setAdapter(calendarAdapter);
        mCalendar.setOnCellClickListener(dayListener);
	}
	
	
	private class DayListener implements OnClickListener {
		private int courseID;
		
		private void setCourseID(int courseID){
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			CourseAttendDialog.show(getContext(), courseID,((Cell)v).getDate());
		}
		
	}

}
