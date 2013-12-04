package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventRatingBarLayout extends LinearLayout{
	public static final String TAG = EventRatingBarLayout.class.getName();
	private static final int[] BAR_COLORS = new int[]{0x7733b5e5,0x77aa66cc,0x7799cc00,0x77ffbb33,0x77ff4444};
	
	private View bar;
	private TextView starsText;
	private TextView ratingCountText;
	private LinearLayout barContainer;
	
	private int ratingCount;
	private int percent;
	private int containerWidth;
	
	private final int stars;
	
	public EventRatingBarLayout(Context context, int stars) {
		super(context);
		this.stars = stars;
		init();
	}

	private void init(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rating_bar_layout, this);
        inflater = null;
        
        this.bar = this.findViewById(R.id.bar);
        this.starsText = (TextView) this.findViewById(R.id.stars_name);
        this.ratingCountText = (TextView) this.findViewById(R.id.rating_count);
        this.barContainer = (LinearLayout) this.findViewById(R.id.bar_container);

        initView();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setPercentageFromCreate(final int percent) {
		this.percent = percent;
		ViewTreeObserver vto = this.barContainer.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				containerWidth = barContainer.getWidth();
				if (containerWidth > 0){
					ViewGroup.LayoutParams lp = bar.getLayoutParams();
					lp.width = (int) (((double)percent/100)*containerWidth);
					bar.setLayoutParams(lp);
					ViewTreeObserver obs = barContainer.getViewTreeObserver();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						obs.removeOnGlobalLayoutListener(this);
					} else {
						obs.removeGlobalOnLayoutListener(this);
					}
				}
			}
		});
	}
	
	private void initPercentage(int percent){
		containerWidth = barContainer.getWidth();
		if (containerWidth > 0 && percent > 0){
			ViewGroup.LayoutParams lp = bar.getLayoutParams();
			lp.width = (int) (((double)percent/100)*containerWidth);
			bar.setLayoutParams(lp);
		}
		else if (containerWidth == 0){
			Print.err(TAG,"containerWidth == 0");
		}
	}
	
	private void initView(){
		this.starsText.setText(stars+" "+getContext().getResources().getString(R.string.course_rating_stars));
		this.bar.setBackgroundColor(BAR_COLORS[stars-1]);
		setRatingCount(0);
	}
	
	public void setPercent(int percent) {
		this.percent = percent;
		initPercentage(this.percent);
	}
	
	public void setRatingCount(int count){
		this.ratingCount = count;
		this.ratingCountText.setText(""+this.ratingCount);
	}

}
