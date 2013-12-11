package ch.unibe.sport.event.info;

import ch.unibe.sport.R;
import ch.unibe.sport.calendar.Calendar;
import ch.unibe.sport.calendar.Calendar.OnMeasuredListener;
import ch.unibe.sport.calendar.Cell;
import ch.unibe.sport.core.Event;
import android.content.Context;
import android.view.View;

/**
 * Handles the attending of calendar.
 * @author Team 1
 *
 */
public class EventCalendarCard extends AbstractInfoCard {

	public static final String TAG = EventCalendarCard.class.getName();

	private final Calendar mCalendar;
	private final DayListener dayListener = new DayListener();
	
	private EventCalendarAdapter calendarAdapter;
		
	public EventCalendarCard(Context context) {
		super(context, TAG);
		View.inflate(getContext(), R.layout.course_info_calendar, this);
		this.mCalendar = (Calendar) this.findViewById(R.id.calendar);
	}

	private void init(final Event event) {
		this.dayListener.setEventID(event.getEventID());
        this.mCalendar.setOnMeasuredListener(new OnMeasuredListener(){
			@Override
			public void OnMeasured(int width, int height) {
				calendarAdapter = new EventCalendarAdapter();
				calendarAdapter.setCalendarWidth(width);
				calendarAdapter.setCalendarHeigth(height);
				calendarAdapter.initialize(getContext(),event);
		        mCalendar.setAdapter(calendarAdapter);
		        mCalendar.setOnCellClickListener(dayListener);
			}
        });
	}
	
	public void setEvent(Event event){
		init(event);
	}
	
	public void update(Event event){
		this.dayListener.setEventID(event.getEventID());
		calendarAdapter.initialize(getContext(),event);
        mCalendar.setAdapter(calendarAdapter);
        mCalendar.setOnCellClickListener(dayListener);
	}
	
	
	private class DayListener implements OnClickListener {
		private int eventID;
		
		private void setEventID(int eventID){
			this.eventID = eventID;
		}
		
		@Override
		public void onClick(View v) {
			EventAttendDialog.show(getContext(), eventID,((Cell)v).getDate());
		}
		
	}

}
