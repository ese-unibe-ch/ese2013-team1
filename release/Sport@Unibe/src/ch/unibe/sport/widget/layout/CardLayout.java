package ch.unibe.sport.widget.layout;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CardLayout extends LinearLayout{

	private float margins = 10f;		// in dp
	private float padding = 5f;			// in dp
	
	private LinearLayout.LayoutParams layoutParams;
	
	public CardLayout(Context context) {
		super(context);
		initView();
	}
	
	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public CardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	
	private void initView(){
		layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		initMargins(layoutParams);
		this.setLayoutParams(layoutParams);
		this.setPadding(
				(int)Utils.convertDpToPx(getContext(), padding),	// left
				(int)Utils.convertDpToPx(getContext(), padding),	// top
				(int)Utils.convertDpToPx(getContext(), padding),	// right
				(int)Utils.convertDpToPx(getContext(), padding)		// bottom
		);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundResource(R.drawable.card_layout_bg);
	}
	
	private void initMargins(LayoutParams lp){
		if (lp == null) return;
		lp.setMargins(
				(int)Utils.convertDpToPx(getContext(), margins),	// left
				(int)Utils.convertDpToPx(getContext(), margins),	// top
				(int)Utils.convertDpToPx(getContext(), margins),	// right
				0													// bottom
		);
	}
	
	public void setMargins(float margins){
		this.margins = margins;
		initMargins(layoutParams);
	}

}