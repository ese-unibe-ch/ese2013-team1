package ch.unibe.sport.calendar;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Dimension;
import ch.unibe.sport.widget.view.CacheLayoutProvider;
import ch.unibe.sport.widget.view.FastTextView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.Gravity;

public class Cell extends FastTextView{
	
	private final int selectedColor = Color.MAGENTA;
	
	private Day day;
	private Dimension dimension;
	private OnClickListener mOnCellClickListener;
	
	private boolean invariant(){
		return day != null
				&& dimension != null;
	}
	
	public Cell(Context context, Day day) {
		super(context);
		this.day = day;
		this.dimension = new Dimension(day.cellWidth,day.cellHeight);
		assert invariant();
		initView();
	}
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	private void initView(){
		initSize();
		initText();
		initBackground();
	}
	private void initSize(){
		this.setLayoutProvider(new CacheLayoutProvider());
		width = this.dimension.width;
		height = this.dimension.height;
	}
	
	private void initText(){
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.setTextSize(day.textSize);
		this.setTextColor(day.textColor);
		this.setText(""+day.text);
	}
	
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	private void initBackground(){
		CellBackground cellBackground = new CellBackground(dimension,day);
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},getResources().getDrawable(R.drawable.course_info_link_bg));
		states.addState(new int[] { },cellBackground.background);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			this.setBackground(states);
		}
		else {
			this.setBackgroundDrawable(states);
		}
	}
	/*------------------------------------------------------------
	-------------------------- G E T T E R S ---------------------
	------------------------------------------------------------*/
	public Date getDate(){
		return day.date;
	}
	
	public int getCellID(){
		return day.id;
	}

	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	
	public void select(){
		this.setTextColor(selectedColor);
		invalidate();
	}
	public void unselect(){
		this.setTextColor(day.textColor);
		invalidate();
	}
	public void reDraw(Day day){
		assert day != null;
		this.day = day;
		initView();
	}
	
	public void setOnCellClickListener(OnClickListener l) {
		if (this.day.coursesNum == 0) return;
		this.mOnCellClickListener = l;
		if (this.mOnCellClickListener != null){
			this.setOnClickListener(l);
		}
	}
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Date))
		      return false;
		Cell flatCell = (Cell) obj;
		return (flatCell.day == this.day);
	}
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = hash * prime + day.hashCode();
		hash = hash * prime + dimension.hashCode();
		return hash;
	}
	
}
