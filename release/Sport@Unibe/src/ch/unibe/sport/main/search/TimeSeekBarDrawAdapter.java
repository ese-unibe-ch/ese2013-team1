package ch.unibe.sport.main.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import ch.unibe.sport.R;
import ch.unibe.sport.widget.view.SeekBarAdapter;
import ch.unibe.sport.widget.view.SeekBarDrawBaseAdapter;

public class TimeSeekBarDrawAdapter extends SeekBarDrawBaseAdapter {
	private static final float VALUE_TEXT_MARGIN_TOP = 5;
	private static final float INDICATOR_MARGIN_BOTTOM = 10;
	
	private final Paint scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint textPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG);
	
	private final Bitmap thumbImage;
	private final Bitmap thumbPressedImage;
	
	private final Bitmap skaleLeftImage;
	private final Bitmap skaleMiddleImage;
	private final Bitmap skaleRightImage;
	private final Bitmap selectedSkaleImage;
	private final Bitmap skaleIndicatorImage;
	
	private final Bitmap skaleValueDotImage;
	private final Bitmap skaleValueLineImage;

	private final int skaleLeftImageWidth;
	private final int skaleMiddleImageWidth;
	private final int skaleMiddleImageHeight;
	private final int skaleValueDotImageWidth;
	private final int skaleValueDotImageHeight;
	private final int skaleValueLineImageWidth;
	private final int skaleValueLineImageHeight;
	private final int skaleIndicatorImageWidth;
	private final int skaleIndicatorImageHeight;
	
	private final int selectedSkaleImageWidth;
	
	
	public TimeSeekBarDrawAdapter(Context context){
		this.thumbImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_thumb);
		this.thumbPressedImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_thumb);
		this.setThumbImage(thumbImage);
		this.setThumbPressedImage(thumbPressedImage);
		this.selectedSkaleImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_selected_skale);
		this.skaleLeftImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_skale_left);
		this.skaleMiddleImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_skale_middle);
		this.skaleRightImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_skale_right);
		this.skaleValueDotImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_value_dot);
		this.skaleValueLineImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_value_line);
		this.skaleIndicatorImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_bar_indicator);
		
		this.selectedSkaleImageWidth = selectedSkaleImage.getWidth();
		this.skaleLeftImageWidth = skaleLeftImage.getWidth();
		this.skaleMiddleImageWidth = skaleMiddleImage.getWidth();
		this.skaleMiddleImageHeight = skaleMiddleImage.getHeight();
		this.skaleValueDotImageWidth = skaleValueDotImage.getWidth();
		this.skaleValueDotImageHeight = skaleValueDotImage.getHeight();
		this.skaleValueLineImageWidth = skaleValueLineImage.getWidth();
		this.skaleValueLineImageHeight = skaleValueLineImage.getHeight();
		this.skaleIndicatorImageWidth = skaleIndicatorImage.getWidth();
		this.skaleIndicatorImageHeight = skaleIndicatorImage.getHeight();
		
		textPaint.setTextSize(30f);
		textPaint.setColor(Color.GRAY);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setShadowLayer(1f, 1f, 0f, Color.LTGRAY);
		textPaint.setAntiAlias(true);
	}
	
	@Override
	public void drawScaleLine(Canvas canvas){
		float left = getPadding();
		float right = canvas.getWidth()-getPadding();
		float width = right-left;
		int tiles = (int)Math.ceil(width/this.skaleMiddleImageWidth);
		float y = Math.max(skaleIndicatorImageHeight,this.getThumbHalfHeight())+INDICATOR_MARGIN_BOTTOM;
		canvas.drawBitmap(
			skaleLeftImage,
			getPadding()-skaleLeftImageWidth,
			y,
			scalePaint
		);
		for (int i = 0; i < tiles; i++) {
			canvas.drawBitmap(
				skaleMiddleImage,
				left+i * this.skaleMiddleImageWidth,
				y,
				scalePaint
			);
		}
		canvas.drawBitmap(
				skaleRightImage,
				right,
				y,
				scalePaint
			);
	}
	
	@Override
	public void drawSelectedScaleLine(Canvas canvas,double normalizedMinValue,double normalizedMaxValue){
		float left = normalizedToScreen(canvas.getWidth(),normalizedMinValue);
		float right = normalizedToScreen(canvas.getWidth(),normalizedMaxValue);
		float width = right-left;
		int tiles = (int)Math.ceil(width/this.selectedSkaleImageWidth);
		float y = Math.max(skaleIndicatorImageHeight,this.getThumbHalfHeight())+INDICATOR_MARGIN_BOTTOM;
		for (int i = 0; i < tiles; i++) {
	        canvas.drawBitmap(
	        	selectedSkaleImage,
	        	left+i * selectedSkaleImageWidth,
	        	y,
	        	scalePaint
	        );
	    }
	}
	
	@Override
	public <T> void drawScaleValues(Canvas canvas,SeekBarAdapter<T> adapter){
		if (adapter == null) return;
		int count = adapter.getCount();
		
		float delta = this.getScalaDeltaScreen(canvas.getWidth(),adapter);
		
		float y = Math.max(skaleIndicatorImageHeight,this.getThumbHalfHeight()) + this.getThumbHalfHeight()+skaleMiddleImageHeight/2+INDICATOR_MARGIN_BOTTOM;
		
		textPaint.setTextSize(this.skaleIndicatorImageHeight/4);
		
		float textY = y+this.skaleValueLineImageHeight+VALUE_TEXT_MARGIN_TOP+textPaint.getTextSize();
		
		
		for (int i = 0; i < count; i++){
			float x = getPadding()+i*delta;
			// draw time and triangle
			if (adapter.isValueDisplayed(i)){
				canvas.drawBitmap(skaleValueLineImage,x-this.skaleValueLineImageWidth/2, y, scalePaint);
				
				if (adapter != null){
					String text = adapter.getStringValue(i);
					canvas.drawText(text, x-textPaint.measureText(text)/2, textY, textPaint);
				}
			}
			// draw dots
			else {
				canvas.drawBitmap(skaleValueDotImage,x-this.skaleValueDotImageWidth/2, y, scalePaint);
			}
		}
	}
	
	@Override
	public <T> void drawMinThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMinValue,int index,boolean pressed) {
		float x = normalizedToScreen(canvas.getWidth(),normalizedMinValue);
		drawThumb(x, pressed, canvas);
		if (adapter == null) return;
		drawIndicator(canvas,x,adapter.getStringValue(index));
	}

	@Override
	public <T> void drawMaxThumb(Canvas canvas, SeekBarAdapter<T> adapter,double normalizedMaxValue,int index,boolean pressed) {
		float x = normalizedToScreen(canvas.getWidth(),normalizedMaxValue);
		drawThumb(x, pressed, canvas);
		if (adapter == null) return;
		drawIndicator(canvas,x,adapter.getStringValue(index));
	}
	
	@Override
	public void drawThumb(float screen, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(
				pressed ? thumbPressedImage : thumbImage,
				screen - getThumbHalfWidth(),
				skaleIndicatorImageHeight-getThumbHalfHeight()+skaleMiddleImageHeight/2+INDICATOR_MARGIN_BOTTOM,
				scalePaint
		);
	}
	
	private void drawIndicator(Canvas canvas,float screen,String value){
		canvas.drawBitmap(
				this.skaleIndicatorImage,
				screen - this.skaleIndicatorImageWidth/2,
				0,
				scalePaint
		);
		canvas.drawText(value, screen-textPaint.measureText(value)/2, (this.skaleIndicatorImageHeight-(textPaint.descent() + textPaint.ascent()))/2, textPaint);
	}
	
	@Override
	public float getActualHeight() {
		return skaleIndicatorImageHeight
				+INDICATOR_MARGIN_BOTTOM
				+this.getThumbHeight()/2
				+skaleMiddleImageHeight/2
				+Math.max(this.skaleValueDotImageHeight,this.skaleValueLineImageHeight)
				+textPaint.getTextSize()
				+VALUE_TEXT_MARGIN_TOP;
	}
}
