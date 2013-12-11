package ch.unibe.sport.calendar;

import ch.unibe.sport.config.Config;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Dimension;
import ch.unibe.sport.widget.view.FastBitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class CellBackground {
	protected Paint bgPaint = new Paint();
	protected static final Paint borderPaint = new Paint();
	protected static final Paint redBorderPaint = new Paint();
	protected static final Paint timePaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
	protected static final Paint moreCoursesPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
	
	private static final int borderSize = 2;
	private static final int redBorderSize = 4;
	private static final int redBorderPadding = -1;
	
	private static final int borderSizePartLittle = calcPartLittle(borderSize);
	private static final int borderSizePartBig = calcPartBig(borderSize);
	
	private static final int borderColor = Color.LTGRAY;
	private static final int redBorderColor = 0xffff0000;
	private static final int timeColor = Color.BLACK;
	private static final int moreCoursesColor = Color.BLACK;

	private static float TIME_TEXT_SIZE_PERCENT = 0.17f;
	private static float TIME_MARGIN_BOTTOM_PERCENT = 0.04f;
	private static float TIME_MARGIN_RIGHT_PERCENT = 0.04f;

	private static float MORE_COURSES_TEXT_SIZE_PERCENT = 0.20f;
	private static float MORE_COURSES_MARGIN_TOP_PERCENT = 0.0f;
	private static float MORE_COURSES_MARGIN_RIGHT_PERCENT = 0.02f;
	
	private float timeTextSize;
	private float timeMarginBottom;
	private float timeMarginRight;
	
	private float moreCoursesTextSize;
	private float moreCoursesMarginTop;
	private float moreCoursesMarginRight;
	
	static {
		borderPaint.setColor(borderColor);
		redBorderPaint.setColor(redBorderColor);
		timePaint.setColor(timeColor);
		moreCoursesPaint.setColor(moreCoursesColor);
	}
	
	private Day day;
	private Date date;
	private Date today;
	protected final Dimension dimension;
	protected final boolean isToday;
	protected int dayOfWeek = 0;
	protected Drawable background;
	protected Canvas canvas;
	
	private boolean invariant(){
		return day != null
				&& dimension != null;
	}
	
	/**
	 * Initializes the cell's background, drawing its border, time and courses.
	 * @param dimension
	 * @param day
	 */
	public CellBackground(Dimension dimension,Day day){
		this.day = day;
		this.date = this.day.date;
		this.today = Config.INST.SYSTEM.TODAY;
		this.dimension = dimension;
		this.isToday = date.isEqual(today);
		this.bgPaint.setColor(day.bgColor);
		
		if (!day.clearDay){
			this.timeTextSize = calcTimeTextSize();
			this.timeMarginBottom = this.calcTimeMarginBottom();
			this.timeMarginRight = this.calcTimeMarginRight();
			timePaint.setTextSize(timeTextSize);
			
			this.moreCoursesTextSize = calcMoreCoursesTextSize();
			this.moreCoursesMarginTop = this.calcMoreCoursesMarginBottom();
			this.moreCoursesMarginRight = this.calcMoreCoursesMarginRight();
			moreCoursesPaint.setTextSize(moreCoursesTextSize);
		}
		assert invariant();
		
		initBackground();
	}

	private void initBackground(){
		Bitmap bitmap = Bitmap.createBitmap(dimension.width, dimension.height, Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);
		
		drawBackground();
		drawBorder();
		
		background = new FastBitmapDrawable(bitmap);
		bitmap = null;
	}
	
	/* Draw background */
	protected void drawBackground(){
		canvas.drawRect(0,0, dimension.width,dimension.height,bgPaint);
		drawTime();
		drawMoreCourses();
	}
	
	private void drawTime(){
		if (!day.timeFrom.unknown){
			String timeText = ""+day.timeFrom.toString();
			float textWidth = timePaint.measureText(""+timeText);
			canvas.drawText(timeText, dimension.width-this.timeMarginRight-textWidth, dimension.height-this.timeMarginBottom, timePaint);
		}
	}
	
	private void drawMoreCourses(){
		if (day.coursesNum > 1){
			String timeText = "+"+(day.coursesNum-1);
			float textWidth = moreCoursesPaint.measureText(""+timeText);
			canvas.drawText(timeText, dimension.width-this.moreCoursesMarginRight-textWidth, this.moreCoursesMarginTop+this.moreCoursesTextSize, moreCoursesPaint);
		}
	}
	

	/*------------------------------------------------------------
	--------------------------- B O R D E R ----------------------
	------------------------------------------------------------*/
	private void drawBorder(){
		drawBorderTop();		// should be drawn before left and right
		drawBorderBottom();	// should be drawn before left and right
		drawBorderRight();	// should be drawn after top and bottom
		drawBorderLeft();		// should be drawn after top and bottom
	}
	
	private void drawBorderTop(){
		canvas.drawRect(0, 0, dimension.width, borderSizePartLittle, borderPaint);
		if (isToday){
			int left = borderSizePartLittle+redBorderPadding+1;
			int top = borderSizePartLittle+redBorderPadding;
			int right = dimension.width-borderSizePartBig-redBorderPadding-1;
			int bottom = borderSizePartLittle+redBorderPadding+redBorderSize;
			canvas.drawRect(left, top, right,bottom , redBorderPaint);
		}
	}
	
	private void drawBorderBottom(){
		canvas.drawRect(0, dimension.height-borderSizePartBig, dimension.width, dimension.height, borderPaint);
		if (isToday){
			int left = borderSizePartLittle+redBorderPadding+1;
			int top = dimension.height-borderSizePartBig-redBorderPadding-redBorderSize;
			int right = dimension.width-borderSizePartBig-redBorderPadding-1;
			int bottom = dimension.height-borderSizePartBig-redBorderPadding;
			canvas.drawRect(left, top, right,bottom , redBorderPaint);
		}
	}
	
	private void drawBorderRight(){
		canvas.drawRect(dimension.width-borderSizePartBig, 0, dimension.width, dimension.height, borderPaint);
		if (isToday){
			int left = dimension.width-borderSizePartBig-redBorderPadding-redBorderSize;
			int top = borderSizePartLittle + redBorderPadding + 1;
			int right = dimension.width-borderSizePartBig-redBorderPadding;
			int bottom = dimension.height - borderSizePartBig - redBorderPadding - 1;
			canvas.drawRect(left, top, right,bottom , redBorderPaint);
		}
	}

	private void drawBorderLeft(){
		canvas.drawRect(0, 0, borderSizePartLittle, dimension.height, borderPaint);
		if (isToday){
			int left = borderSizePartLittle+redBorderPadding;
			int top = borderSizePartLittle + redBorderPadding + 1;
			int right = borderSizePartLittle+redBorderPadding + redBorderSize;
			int bottom = dimension.height - borderSizePartBig - redBorderPadding - 1;
			canvas.drawRect(left, top, right,bottom , redBorderPaint);
		}
	}
	private static int calcPartLittle(int border){
		return (int) Math.floor((double)border/(double)2);
	}
	private static int calcPartBig(int border){
		return (int) Math.ceil((double)border/(double)2);
	}
	
	private float calcTimeTextSize(){
		return Math.min(this.dimension.width,this.dimension.height) * TIME_TEXT_SIZE_PERCENT;
	}
	
	private float calcTimeMarginBottom(){
		return this.dimension.height * TIME_MARGIN_BOTTOM_PERCENT;
	}
	
	private float calcTimeMarginRight(){
		return this.dimension.width * TIME_MARGIN_RIGHT_PERCENT;
	}
	
	private float calcMoreCoursesTextSize(){
		return Math.min(this.dimension.width,this.dimension.height) * MORE_COURSES_TEXT_SIZE_PERCENT;
	}
	
	private float calcMoreCoursesMarginBottom(){
		return this.dimension.height * MORE_COURSES_MARGIN_TOP_PERCENT;
	}
	
	private float calcMoreCoursesMarginRight(){
		return this.dimension.width * MORE_COURSES_MARGIN_RIGHT_PERCENT;
	}
	
	/*------------------------------------------------------------
	-------------------------- G E T T E R S ---------------------
	------------------------------------------------------------*/
	public Drawable getBackground(){
		return this.background;
	}
}
