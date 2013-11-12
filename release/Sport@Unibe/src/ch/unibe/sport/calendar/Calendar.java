package ch.unibe.sport.calendar;

import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

public class Calendar extends LinearLayout {

	public static final String TAG = Calendar.class.getName();

	private int mWidth;
	private int mHeight;
	
	private CalendarRow[] mCalendarRows;
	private CalendarAdapter mAdapter;
	private OnMeasuredListener mOnMeasuredListener;
	
	
	public interface OnMeasuredListener {
		public void OnMeasured(int width, int height);
	}
	
	private OnClickListener cellClickListener;
	
	private OnGlobalLayoutListener calendarOnGlobalListener = new OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			initDimensions(getWidth(), getHeight());
		}
	};
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	public Calendar(Context context) {
		super(context);
		initView();
	}
	
	public Calendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public Calendar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	
	/*public void initialize(){
        days = new Day[ROWS*COLUMNS];	
        initDate = new Date();
        
        DBAdapter.INST.open(context, TAG);
        FavoriteCourses favoriteCoursesDB = new FavoriteCourses(context);
        String[][] coursesData = CourseData.Calendar.getData(favoriteCoursesDB);
        DBAdapter.INST.close(TAG);
        
        AssociativeList<ArrayList<String[]>> courses = new AssociativeList<ArrayList<String[]>>();
        for (String[] course : coursesData){
        	int day = Utils.Int(course[CourseData.Calendar.DAY_OF_WEEK]);
        	if (courses.get(day) == null) courses.add(new ArrayList<String[]>(), day);
        	courses.get(day).add(course);
        }
        
        Date tmpDate = initDate.findDate(-new CalendarHelper(initDate).dayOfWeek);
        int dayOfWeek = 0;
        for (int i = 0,length = days.length; i < length; i++){
        	if (dayOfWeek == 7) dayOfWeek = 0;
        	int bgColor = Color.WHITE;
        	Time timeFrom = new Time();
        	int coursesNum = 0;
        	if (courses.containsKey(dayOfWeek) && Config.INST.SYSTEM.TODAY.compareTo(tmpDate) != 1){
        		int k = 0;
        		while (timeFrom.unknown && k < courses.get(dayOfWeek).size()){
            		timeFrom = new Time(Utils.Int(courses.get(dayOfWeek).get(k)[CourseData.Calendar.TIME_FROM]));
            		k++;
        		}
        		if (!timeFrom.unknown){
            		bgColor = Utils.Int(courses.get(dayOfWeek).get(k-1)[CourseData.Calendar.BG_COLOR]);
        		}
        		else {
            		bgColor = Utils.Int(courses.get(dayOfWeek).get(0)[CourseData.Calendar.BG_COLOR]);
        		}
        		coursesNum = courses.get(dayOfWeek).size();
        	}
        	days[i] = new Day(i,tmpDate.copy(),Color.BLACK,bgColor,timeFrom,coursesNum);
        	tmpDate.next();
        	dayOfWeek++;
        }
	}*/
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Initializes calendar view, sets corresponding layout parameters, gravity
	 */
	private void initView(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.MATCH_PARENT,
        		LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		this.setLayoutParams(layoutParams);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);
		initDimensions(0,0);
	}
	
	/**
	 * Initializes calendar width and height, if it's impossible get correct dimension
	 * adds OnGlobalLayoutLisener to delay initialization
	 * @param width
	 * @param height
	 */
	private void initDimensions(int width, int height){
		if (width > 0 && height > 0) {
			mWidth = width;
			mHeight = height;
			removeCalendarOnGlobalListener();
			if (mOnMeasuredListener != null) mOnMeasuredListener.OnMeasured(mWidth, mHeight);
			return;
		}
		this.getViewTreeObserver().addOnGlobalLayoutListener(calendarOnGlobalListener);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void removeCalendarOnGlobalListener() {
		ViewTreeObserver obs = getViewTreeObserver();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			obs.removeOnGlobalLayoutListener(calendarOnGlobalListener);
		} else {
			obs.removeGlobalOnLayoutListener(calendarOnGlobalListener);
		}
	}
	
	/**
	 * Starts calendar initializing correspindig to adapter
	 */
	private void initialize(){
		removeAllViews();
		try {
			initRows();
		} catch (NullAdapterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialzies calendar rows
	 * @throws NullAdapterException if adapter is null
	 */
	private void initRows() throws NullAdapterException {
		this.removeAllViews();
		if (this.mAdapter == null) throw new NullAdapterException();
		Context context = getContext();
        mCalendarRows = new CalendarRow[mAdapter.getRowsCount()];
        for (int row = 0, length = mCalendarRows.length; row < length; row++){
        	Day[] weekRow = mAdapter.getRow(row);
        	if (weekRow == null) throw new NullAdapterException();
        	mCalendarRows[row] = new CalendarRow(context,weekRow);
        	mCalendarRows[row].setOnCellCLickListener(cellClickListener);
			addView(mCalendarRows[row]);
        }
	}
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	public void setAdapter(CalendarAdapter adapter){
		this.mAdapter = adapter;
		if (this.mAdapter == null){
			Print.err(TAG, "CalendarAdapter is null, please initialize your adapter");
			return;
		}
		initialize();
	}
	
	public void setOnCellClickListener(OnClickListener l){
		this.cellClickListener = l;
		if (mCalendarRows == null) return;
		for (CalendarRow row : mCalendarRows){
			row.setOnCellCLickListener(cellClickListener);
		}
	}
	
	public void setOnMeasuredListener(OnMeasuredListener l){
		this.mOnMeasuredListener = l;
	}
	
	public Cell getCell(int index) throws NullAdapterException{
		assert index >= 0 && index < mAdapter.getDaysCount();
		if (mAdapter == null) throw new NullAdapterException();
		int[] point = mAdapter.getDayCoordinates(index);
		return mCalendarRows[point[0]].getCell(point[1]);
	}

	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
}
