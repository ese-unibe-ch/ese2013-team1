package ch.unibe.sport.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * @author Team 1
 *
 */
public class ColorToggleButton extends ToggleButton {
	
	private final Paint outterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final float INNER_BORDER = 15f;
	private final float CORNER_RADIUS = 10f;
	
	private RectF outterRect;
	private RectF innerRect;
	private int color = Color.MAGENTA;
	
	private int outterColor = Color.LTGRAY;
	private int outterColorChecked = Color.RED;

	public ColorToggleButton(Context context) {
		super(context);
		init();
	}

	public ColorToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ColorToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		innerPaint.setColor(color);
		this.outterRect = new RectF();
		this.innerRect = new RectF();		
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected void onDraw(Canvas canvas){

		outterPaint.setColor((isChecked())?this.outterColorChecked:this.outterColor);
		
		int width = getWidth();
		int height = getHeight();
		this.outterRect.set(0, 0, width, height);
		this.innerRect.set(INNER_BORDER, INNER_BORDER, width-INNER_BORDER, height-INNER_BORDER);
		canvas.drawRoundRect(outterRect, CORNER_RADIUS, CORNER_RADIUS, this.outterPaint);
		canvas.drawRoundRect(innerRect, CORNER_RADIUS, CORNER_RADIUS, this.innerPaint);
	}

	/**
	 * @param color
	 */
	public void setBorderColor(int color){
		this.outterColor = color;
		this.invalidate();
	}
	
	/**
	 * @param color
	 */
	public void setBorderColorChecked(int color){
		this.outterColorChecked = color;
		this.invalidate();
	}
	
	/**
	 * @param color
	 */
	/**
	 * @param color
	 */
	public void setColor(int color){
		this.color = color;
		this.innerPaint.setColor(this.color);
		this.invalidate();
	}
	
	/**
	 * @return color
	 */
	public int getColor(){
		return this.color;
	}

}
