package ch.unibe.sport.calendar;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Print;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CalendarRow extends LinearLayout {
	public static final String TAG = CalendarRow.class.getName();
	
	private Cell[] cells;
	private final Day[] days;
	
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	public CalendarRow(Context context, Day[] days) {
		super(context);
		if (days == null){
			this.days = null;
			Print.err(TAG,"days array is null");
			return;
		}
		
		/**
		 *  makes a real copy of days array
		 */
		this.days = new Day[days.length];
		System.arraycopy(days, 0, this.days, 0, days.length);
		
		initView();
		initCells();
	}
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	private void initView(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.flat_calendar_line, this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.WRAP_CONTENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY);
		this.setLayoutParams(layoutParams);
		this.setOrientation(LinearLayout.HORIZONTAL);
	}
	
	private void initCells(){
		Context context = getContext();
		cells = new Cell[days.length];
		for (int day = 0,length = cells.length; day < length; day++){
			cells[day] = new Cell(context,days[day]);
			this.addView(cells[day]);
		}
	}

	/*------------------------------------------------------------
	-------------------------- G E T T E R S ---------------------
	------------------------------------------------------------*/
	protected Cell getCell(int index){
		assert index >=0 && index < cells.length;
		return cells[index];
	}
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	public void setOnCellCLickListener(OnClickListener l){
		for (Cell cell : cells){
			cell.setOnCellClickListener(l);
		}
	}
}
