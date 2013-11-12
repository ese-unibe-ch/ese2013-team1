package ch.unibe.sport.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public class FastTextView extends View {

	private LayoutProvider provider;
	private Layout layout;
	private CharSequence text;
	private TextPaint paint;

	protected int width = 0;
	protected int height = 0;
	protected float textSize = 16f;
	protected int color = Color.BLACK;
	protected boolean layoutChanged = true;
	protected int gravity;
	
	private float dy = 0;
	
	public FastTextView(Context context) {
		super(context);
		this.paint = new TextPaint();
	}

	public FastTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.paint = new TextPaint();
	}

	public FastTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.paint = new TextPaint();
	}

	public void setTextSize(float textSize){
		this.textSize = textSize;
		this.paint.setTextSize(textSize);
		setGravity(gravity);
		this.layoutChanged = true;
	}

	public void setTextColor(int color){
		this.color = color;
		this.paint.setColor(color);
		this.layoutChanged = true;
	}

	public void setLayoutProvider(LayoutProvider provider) {
		this.provider = provider;
		this.layoutChanged = true;
	}

	public void setText(CharSequence text) {
		this.text = text;
		this.layoutChanged = true;
	}
	
	public void setGravity(int gravity){
		switch(gravity){
			case Gravity.CENTER_VERTICAL:{
				this.gravity = gravity;
				dy = (int) Math.floor(((double)(height-textSize))/2);
				this.layoutChanged = true;
				break;
			}
		}
	}

	private Layout getLayout() {
		if (this.layoutChanged) {
			this.layoutChanged = false;
			this.layout = null;
			if (provider != null && text != null) {
				return this.layout = this.provider.getLayout(this.text, width, this.paint);
			}
		}
		return this.layout;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Layout layout = getLayout();
		canvas.translate(0, dy);
		if (layout != null) {
			layout.draw(canvas);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.layoutChanged = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Layout layout = getLayout();
		if (layout != null) {
			setMeasuredDimension(width,height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
