package ch.unibe.sport.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class GravityTextView extends TextView {

	public GravityTextView(Context context) {
		super(context);
	}
	
	public GravityTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GravityTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom){
		super.onLayout(changed, left, top, right, bottom);
        super.measure(right-left, bottom-top);
	}
	
	@Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

	

}
