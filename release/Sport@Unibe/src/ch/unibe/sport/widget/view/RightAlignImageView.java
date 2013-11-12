package ch.unibe.sport.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RightAlignImageView extends ImageView {

		public RightAlignImageView(Context context) {
			super(context);
		}
		
		public RightAlignImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public RightAlignImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void onDraw(Canvas canvas){
			Drawable drawable = this.getDrawable();
			Rect bound = drawable.getBounds();
			int padding =(getHeight()-bound.height())/2;
			drawable.setBounds(getWidth()-bound.width(), padding, getWidth(), getHeight()-padding);
			this.getDrawable().draw(canvas);
		}
	}