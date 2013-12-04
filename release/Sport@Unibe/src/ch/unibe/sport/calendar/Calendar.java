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
	 * Starts calendar initializing correspondig to adapter
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
	 * Initializes calendar rows
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
