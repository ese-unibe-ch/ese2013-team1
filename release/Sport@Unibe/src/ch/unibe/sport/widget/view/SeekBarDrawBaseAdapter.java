package ch.unibe.sport.widget.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;

/**
 * 
 * @author Team 1 2013
 *
 */
public class SeekBarDrawBaseAdapter implements SeekBarDrawAdapter {
	
	private final Paint scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint scaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint selectedScaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint textPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG);
	
	private Bitmap thumbImage;
	private Bitmap thumbPressedImage;
	
	private float thumbWidth;			// width of thumb image
	private float thumbHeight;			// height of thumb image
	
	private float thumbHalfWidth;
	private float thumbHalfHeight;
	
	private float padding;
	
	private int scaleLineHeight = 10;		// scala line height in px
	
	/* preallocated rect objects for faster drawing process */
	private final RectF scaleRect;
	private final RectF selectedScaleRect;
	
	public SeekBarDrawBaseAdapter(){
		scalePaint.setStyle(Style.FILL);
		scalePaint.setColor(Color.GRAY);
		scalePaint.setAntiAlias(true);
		
		scaleLinePaint.setStyle(Style.FILL);
		scaleLinePaint.setColor(Color.GRAY);
		scaleLinePaint.setAntiAlias(true);
		
		selectedScaleLinePaint.setStyle(Style.FILL);
		selectedScaleLinePaint.setAntiAlias(true);
		selectedScaleLinePaint.setColor(0xFF33B5E5);
		
		textPaint.setTextSize(30f);
		textPaint.setColor(Color.GRAY);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setAntiAlias(true);
		
		this.scaleRect = new RectF();
		this.selectedScaleRect = new RectF();
	}
	
	@Override
	public void drawScaleLine(Canvas canvas){
		int height = canvas.getHeight();
		int width = canvas.getWidth();
		scaleRect.top = 0.5f * (height - scaleLineHeight);
		scaleRect.bottom = 0.5f * (height + scaleLineHeight);
		scaleRect.left = padding;
		scaleRect.right = width - padding;
		canvas.drawRect(scaleRect, scalePaint);
	}
	
	@Override
	public void drawSelectedScaleLine(Canvas canvas,double normalizedMinValue,double normalizedMaxValue){
		int height = canvas.getHeight();
		selectedScaleRect.top = 0.5f * (height - scaleLineHeight);
		selectedScaleRect.bottom = 0.5f * (height + scaleLineHeight);
		selectedScaleRect.left = normalizedToScreen(canvas.getWidth(),normalizedMinValue);
		selectedScaleRect.right = normalizedToScreen(canvas.getWidth(),normalizedMaxValue);
		canvas.drawRect(selectedScaleRect, selectedScaleLinePaint);
	}
	
	@Override
	public <T> void drawMinThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMinValue,int index,boolean pressed) {
		drawThumb(normalizedToScreen(canvas.getWidth(),normalizedMinValue), pressed, canvas);
	}

	@Override
	public <T> void drawMaxThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMaxValue,int index,boolean pressed) {
		drawThumb(normalizedToScreen(canvas.getWidth(),normalizedMaxValue), pressed, canvas);
	}
	
	@Override
	public <T> void drawScaleValues(Canvas canvas,SeekBarAdapter<T> adapter){
		if (adapter == null) return;
		int count = adapter.getCount();
		
		float delta = this.getScalaDeltaScreen(canvas.getWidth(),adapter);
		
		float marginTop = 20;
		
		float radius = 3f;
		float scaleLineHeight = 15;
		float scaleLineWidth = 6;
		
		for (int i = 0; i < count; i++){
			float x = padding+i*delta;
			float y = 0.5f * (canvas.getHeight() + scaleLineHeight)+marginTop;
			// draw time and triangle
			if (adapter.isValueDisplayed(i)){
				canvas.drawRect(x-scaleLineWidth/2, y, x+scaleLineWidth/2, y+scaleLineHeight, scalePaint);
				if (adapter != null){
					String text = adapter.getStringValue(i);
					canvas.drawText(text, x-textPaint.measureText(text)/2, y+scaleLineHeight+textPaint.getTextSize()*3/2, textPaint);
				}
			}
			// draw dots
			else {
				canvas.drawCircle(x, y, radius, scalePaint);
			}
		}
	}
	
	/**
	 * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
	 * @param screenCoord - The x-coordinate in screen space where to draw the image.
	 * @param pressed - Is the thumb currently in "pressed" state?
	 * @param canvas - The canvas to draw upon.
	 */
	public void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(pressed ? getThumbPressedImage() : getThumbImage(), screenCoord - thumbHalfWidth, (float) ((0.5f * canvas.getHeight()) - getThumbHalfHeight()), scalePaint);
	}

	@Override
	public void setThumbImage(Bitmap thumb) {
		this.thumbImage = thumb;
		this.thumbWidth = thumb.getWidth();
		this.thumbHeight = thumb.getHeight();
		this.thumbHalfWidth = this.thumbWidth * 0.5f;
		this.thumbHalfHeight = this.thumbHeight * 0.5f;
		this.padding = thumbHalfWidth;
	}

	@Override
	public void setThumbPressedImage(Bitmap thumb) {
		this.thumbPressedImage = thumb;
	}

	@Override
	public void setScaleLineHeight(int height) {
		this.scaleLineHeight = height;
	}
	
	@Override
	public <T> float getScalaDeltaScreen(float fullWidth,SeekBarAdapter<T> adapter){
		if (adapter == null) return getActualWidth(fullWidth);
		return getActualWidth(fullWidth)/(adapter.getCount()-1);
	}
	/**
	 * Converts a normalized value into screen space.
	 * @param normalizedCoord - The normalized value to convert.
	 * @return The converted value in screen space.
	 */
	@Override
	public float normalizedToScreen(float fullWidth, double normalized) {
		return (float) (padding + normalized * getActualWidth(fullWidth));
	}
	
	/**
	 * Converts screen space x-coordinates into normalized values.
	 * @param screen - The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	@Override
	public double screenToNormalized(float fullWidth,float screen) {
		// prevent division by zero, simply return 0.
		if (fullWidth <= 2 * padding) return 0d;
		double result = (screen - padding) / this.getActualWidth(fullWidth);
		return Math.min(1d, Math.max(0d, result));
	}
	
	@Override
	public float getActualWidth(float fullWidth){
		return fullWidth-2*padding;
	}

	@Override
	public float getThumbHalfWidth() {
		return this.thumbHalfWidth;
	}

	@Override
	public float getActualHeight() {
		return this.thumbHeight*2;
	}

	@Override
	public int getThumbHeight() {
		return (int)thumbHeight;
	}

	public Bitmap getThumbImage() {
		return thumbImage;
	}

	public Bitmap getThumbPressedImage() {
		return thumbPressedImage;
	}

	public float getThumbHalfHeight() {
		return thumbHalfHeight;
	}
	
	public float getPadding(){
		return this.padding;
	}
}
