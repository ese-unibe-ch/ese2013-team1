package ch.unibe.sport.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
	
	public interface ScrollViewListener {
		void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
	}
	
	private ScrollViewListener scrollViewListener = null;
	private int frameSkip = 15; // default 15
	private int counter = 0;
	
	public ObservableScrollView(Context context){
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener){
		this.scrollViewListener = scrollViewListener;
	}

	public void setFrameSkip(int frameSkip){
		this.frameSkip = frameSkip;
	}
	
	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy){
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null){
			counter++;
			if (counter > frameSkip){
				scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
				counter = 0;
			}
		}
	}
}
