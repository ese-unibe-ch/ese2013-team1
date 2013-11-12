package ch.unibe.sport.widget.view;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Print;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 
 * @author Team 1 2013
 *
 * @param <T>
 */
public class SeekBar<T> extends View {
	public static final String TAG = SeekBar.class.getName();
	/**
	 * An invalid pointer id.
	 */
	public static final int INVALID_POINTER_ID = 255;

	// Localized constants from MotionEvent for compatibility
	// with API < 8 "Froyo".
	public static final int ACTION_POINTER_UP = 0x6;
	public static final int ACTION_POINTER_INDEX_MASK = 0x0000ff00;
	public static final int ACTION_POINTER_INDEX_SHIFT = 8;

	private Thumb pressedThumb = null;
	private boolean mIsDragging;
	private float mDownMotionX;
	private int mActivePointerId = INVALID_POINTER_ID;
	private double normalizedMinValue = 0d;
	private double normalizedMaxValue = 1d;
	private int mScaledTouchSlop;

	private boolean notifyWhileDragging = false;
	private OnRangeSeekBarChangeListener<T> listener;
	
	private SeekBarAdapter<T> adapter;
	private SeekBarDrawAdapter drawAdapter;
	
	/**
	 * Callback listener interface to notify about changed range values.
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 * @param <T> - The Number type the RangeSeekBar has been declared with.
	 */
	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(SeekBar<T> bar, T minValue, T maxValue);
	}
	
	public SeekBar(Context context) {
		super(context);
		init();
	}

	public SeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		this.drawAdapter = new SeekBarDrawBaseAdapter();
		this.drawAdapter.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_normal));
		this.drawAdapter.setThumbPressedImage(BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_pressed));
		
		mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	public void setAdapter(SeekBarAdapter<T> adapter){
		this.adapter = adapter;
	}
	
	public void setDrawAdapter(SeekBarDrawAdapter adapter){
		if (adapter != null) this.drawAdapter = adapter;
	}
	
	/**
	 * Registers given listener callback to notify about changed selected values.
	 * 
	 * @param listener
	 *            The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}
	
	/**
	 * Handles thumb selection and movement. Notifies listener callback on certain events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return false;

		int pointerIndex;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:{
			// Remember where the motion event started
			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);

			pressedThumb = evalPressedThumb(mDownMotionX);

			// Only handle thumb presses.
			if (pressedThumb == null){
				return super.onTouchEvent(event);
			}
			setPressed(true);
			invalidate();
			onStartTrackingTouch();
			trackTouchEvent(event);
			attemptClaimDrag();

			break;
		}
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb == null) break;

			if (mIsDragging) {
				trackTouchEvent(event);
			}
			else {
				// Scroll to follow the motion event
				pointerIndex = event.findPointerIndex(mActivePointerId);
				final float x = event.getX(pointerIndex);

				if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
					setPressed(true);
					invalidate();
					onStartTrackingTouch();
					trackTouchEvent(event);
					attemptClaimDrag();
				}
			}

			if (notifyWhileDragging && listener != null) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);
			}
			else {
				// Touch up when we never crossed the touch slop threshold
				// should be interpreted as a tap-seek to that location.
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			pressedThumb = null;
			invalidate();
			if (listener != null && adapter != null) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = event.getPointerCount() - 1;
			// final int index = ev.getActionIndex();
			mDownMotionX = event.getX(index);
			mActivePointerId = event.getPointerId(index);
			invalidate();
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsDragging) {
				onStopTrackingTouch();
				setPressed(false);
			}
			invalidate(); // see above explanation
			break;
		}
		return true;
	}
	
	private final void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose
			// a new active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mDownMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}
	
	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		final float x = event.getX(pointerIndex);

		if (Thumb.MIN.equals(pressedThumb)) {
			setNormalizedMinValue(screenToNearestNormalized(x));
		}
		else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNearestNormalized(x));
		}
	}

	/**
	 * Tries to claim the user's drag motion,
	 * and requests disallowing any ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}
	
	/**
	 * Decides which (if any) thumb is touched by the given x-coordinate.
	 * @param touchX - The x-coordinate of a touch event in screen space.
	 * @return The pressed thumb or null if none has been touched.
	 */
	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
		boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
		if (minThumbPressed && maxThumbPressed) {
			/*
			 * if both thumbs are pressed (they lie on top of each other),
			 * choose the one with more room to drag.
			 * this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
			 */
			result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
		}
		else if (minThumbPressed) {
			result = Thumb.MIN;
		}
		else if (maxThumbPressed) {
			result = Thumb.MAX;
		}
		return result;
	}
	
	/**
	 * Decides if given x-coordinate in screen space needs to be interpreted as "within" the normalized thumb x-coordinate.
	 * @param touchX - The x-coordinate in screen space to check.
	 * @param normalizedThumbValue - The normalized x-coordinate of the thumb to check.
	 * @return true if x-coordinate is in thumb range, false otherwise.
	 */
	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
		return Math.abs(touchX - drawAdapter.normalizedToScreen(getWidth(),normalizedThumbValue)) <= drawAdapter.getThumbHalfWidth();
	}
	
	private double getScaleDeltaNormalized(){
		if (adapter == null) return 1d;
		return 1d/(adapter.getCount()-1);
	}
		
	private int normalizedToNearestIndex(double normalizedCoord){
		if (adapter == null) return 0;
		double delta = getScaleDeltaNormalized();
		return (int) Math.round(normalizedCoord/delta);
	}
	
	private double screenToNearestNormalized(float screenCoord){
		double normalized = drawAdapter.screenToNormalized(getWidth(),screenCoord);
		if (adapter == null) return normalized;
		double delta = getScaleDeltaNormalized();
		return delta*Math.round(normalized/delta);
	}
	
	/**
	 * Returns the currently selected min value.
	 * @param <T>
	 * 
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		if (adapter == null) return null;
		return adapter.getValue(normalizedToNearestIndex(normalizedMinValue));
	}

	/**
	 * Returns the currently selected max value.
	 * @param <T>
	 * 
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		if (adapter == null) return null;
		return adapter.getValue(normalizedToNearestIndex(normalizedMaxValue));
	}
	
	public void setMinValueIndex(int index){
		if (adapter == null) return;
		int count = adapter.getCount();
		if (index >= count) {
			Print.err(TAG,"values OutOfBound, index: "+index);
			return;
		}
		double normalized = ((double)index)/(count-1);
		setNormalizedMinValue(normalized);
	}
	
	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max value <= 1.
	 * The View will get invalidated when calling this method.
	 * @param value - The new normalized min value to set.
	 */
	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	public void setMaxValueIndex(int index){
		if (adapter == null) return;
		int count = adapter.getCount();
		if (index >= count) {
			Print.err(TAG,"values OutOfBound, index: "+index);
			return;
		}
		double normalized = ((double)index)/(count-1);
		setNormalizedMaxValue(normalized);
	}
	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <= value <= 1.
	 * The View will get invalidated when calling this method.
	 * @param value - The new normalized max value to set.
	 */
	public void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}
	
	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}
	
	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		int height = (int) drawAdapter.getActualHeight();
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}	
	
	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawAdapter.drawScaleLine(canvas);
		drawAdapter.drawSelectedScaleLine(canvas, normalizedMinValue, normalizedMaxValue);
		drawAdapter.drawScaleValues(canvas, adapter);
		drawAdapter.drawMinThumb(canvas, adapter, normalizedMinValue, normalizedToNearestIndex(normalizedMinValue),Thumb.MIN.equals(pressedThumb));
		drawAdapter.drawMinThumb(canvas, adapter, normalizedMaxValue, normalizedToNearestIndex(normalizedMaxValue),Thumb.MAX.equals(pressedThumb));
	}
	
	/**
	 * Should the widget notify the listener callback while the user is still dragging a thumb? Default is false.
	 * 
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}
	
	/**
	 * Overridden to save instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the RangeSeekBar
	 * widget using the {@link #setId(int)} method.
	 * Other members of this class than the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		normalizedMinValue = bundle.getDouble("MIN");
		normalizedMaxValue = bundle.getDouble("MAX");
	}
	
	/**
	 * Thumb constants (min and max).
	 */
	public static enum Thumb {
		MIN, MAX
	};
	
}
