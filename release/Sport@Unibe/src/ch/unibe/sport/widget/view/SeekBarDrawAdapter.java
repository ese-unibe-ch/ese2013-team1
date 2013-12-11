package ch.unibe.sport.widget.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * 
 * @author Team 1
 *
 */
public interface SeekBarDrawAdapter {

	public void drawScaleLine(Canvas canvas);

	public void drawSelectedScaleLine(Canvas canvas,double normalizedMinValue,double normalizedMaxValue);

	public <T> void drawScaleValues(Canvas canvas,SeekBarAdapter<T> adapter);

	public <T> void drawMinThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMinValue, int index,boolean pressed);
	public <T> void drawMaxThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMaxValue, int index,boolean pressed);
	
	public void setThumbImage(Bitmap thumb);
	public void setThumbPressedImage(Bitmap thumb);
	/**
	 * Height in px
	 * @param height in px
	 */
	public void setScaleLineHeight(int height);

	public int getThumbHeight();
	
	public float getThumbHalfWidth();

	public float getActualWidth(float fullWidth);

	public float normalizedToScreen(float fullWidth, double normalized);

	public <T> float getScalaDeltaScreen(float fullWidth, SeekBarAdapter<T> adapter);

	public double screenToNormalized(float fullWidth, float screen);

	public float getActualHeight();
}
