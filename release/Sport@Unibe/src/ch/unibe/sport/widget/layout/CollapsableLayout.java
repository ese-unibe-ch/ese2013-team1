package ch.unibe.sport.widget.layout;

import ch.unibe.sport.R;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Extended LinearLayout that holds HiddableLayouts and allows to hide or show them.
 * It's possible to have two hiddable layouts: for simple click and for longClick
 * on layout's title. User can set his own title view, that will act just as default,
 * so all click or long click listeners of custom title View will be overwritten.
 * 
 * @version 1.2 2013-09-21
 * @author Aliaksei Syrel
 */

public class CollapsableLayout extends LinearLayout implements OnClickListener, OnLongClickListener  {
	private Context context;
	private FrameLayout titleContainer;
	private View customTitleView;
	private TextView titleText;
	private Button expandButton;
	private Hiddable hiddable;
	private Hiddable longHiddable;
	private int customTitleResID;
	
	private boolean isForceClick = false;
	private boolean isForceLongClick = false;
	private boolean customTitle = false;
	
	private OnClickListener titleOnClickListener;
	private OnLongClickListener titleOnLongClickListener;
	private OnTouchListener titleOnTouchListener;
	
	public CollapsableLayout(Context context) {
		super(context);
		this.context = context;
		initLayout();
	}
	
	private CollapsableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private CollapsableLayout(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private void initLayout(){
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setOrientation(VERTICAL);
		initTitleContainer();
	}
	
	private void initTitleContainer(){
		titleContainer = new FrameLayout(context);
		titleContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		titleContainer.setBackgroundResource(R.drawable.table_title_bg_normal);
		initTitleText();
		initButton();
		this.addView(titleContainer);
	}
	
	private void initTitleText(){
		titleText = new TextView(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.LEFT | Gravity.CENTER_VERTICAL);
		params.setMargins((int) Utils.convertDpToPx(getContext(),10), 0, 0, 0);
		titleText.setTextColor(Color.WHITE);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		titleContainer.addView(titleText,params);
	}
	
	private void initButton(){
		expandButton = new Button(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		expandButton.setBackgroundResource(R.drawable.ic_menu_expand);
		titleContainer.addView(expandButton,params);
		expandButton.setGravity(Gravity.RIGHT);
		titleContainer.setOnClickListener(this);
		expandButton.setOnClickListener(this);
		titleContainer.setOnLongClickListener(this);
		expandButton.setOnLongClickListener(this);
	}
	
	public void setButtonExpanded(){
		if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
	}
	
	public void setTitleText(String title){
		titleText.setText(title);
	}
	
	public void setTitleOnClickListener(OnClickListener l){
		this.titleOnClickListener = l;
	}
	
	public void setTitleOnLongClickListener(OnLongClickListener l){
		this.titleOnLongClickListener = l;
	}
	
	public void setTitleOnTouchListener(OnTouchListener l){
		this.titleOnTouchListener = l;
		titleContainer.setOnTouchListener(this.titleOnTouchListener);
	}
	
	public void setTitleBackgroundResource(int resid){
		titleContainer.setBackgroundResource(resid);
		customTitleResID = resid;
	}
	
	public void setTitleTextColor(int color){
		if (titleText != null) titleText.setTextColor(color);
	}
	
	public void setTitleTextSize(float sp){
		if (titleText != null) titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
	}
	
	public void setTitleShadowLayer(float radius, float dx, float dy, int color){
		if (titleText != null) titleText.setShadowLayer(radius, dx, dy, color);
	}
	
	public void setHiddable(Hiddable hiddable){
		if (this.hiddable != null){
			this.removeView(this.hiddable.getView());
		}
		this.hiddable = hiddable;
		this.addView(this.hiddable.getView());
	}
	
	public Hiddable getHiddable(){
		return this.hiddable;
	}
	
	public void setLongHiddable(Hiddable longHiddable){
		if (this.longHiddable != null){
			this.removeView(this.longHiddable.getView());
		}
		this.longHiddable = longHiddable;
		this.addView(this.longHiddable.getView());
	}
	
	public Hiddable getLongHidable(){
		return this.longHiddable;
	}
	
	public void setTitleView(View title){
		titleContainer.removeAllViews();
		titleContainer.setBackgroundResource(0);
		titleContainer.setOnClickListener(this);
		titleContainer.setOnLongClickListener(this);
		titleContainer.addView(title);
		customTitleView = title;
		customTitle = true;

		FrameLayout fog = new FrameLayout(context);
		fog.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,Gravity.CENTER));
		fog.setBackgroundResource(R.drawable.collapsed_layout_custom_title_bg);
		titleContainer.addView(fog);	
	}
	
	public View getTitleView(){
		return this.titleContainer;
	}
	
	public void removeTitleView(View title){
		titleContainer.removeAllViews();
		titleContainer.setBackgroundResource((customTitleResID != 0)?customTitleResID:R.drawable.table_title_bg_normal);
		initTitleText();
		initButton();
		customTitle = false;
		customTitleView = null;
		if (hiddable.isVisible() || longHiddable.isVisible()){
			expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
		}
	}
	
	public void showHiddable(){		
		if (hiddable != null && !hiddable.isLocked() && !hiddable.isVisible()){
			
			if (longHiddable != null && longHiddable.isVisible()){
				if(!longHiddable.isLocked()){
					longHiddable.hide();
				}
				else {
					return;
				}
			}
			
			hiddable.show();
			if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
		}
	}
	
	public void showLongHiddable(){
		if (longHiddable != null && !longHiddable.isLocked() && !longHiddable.isVisible()){
			
			if (hiddable != null && hiddable.isVisible()){
				if(!hiddable.isLocked()){
					hiddable.hide();
				}
				else {
					return;
				}
			}
			
			longHiddable.show();
			if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
		}
	}
	
	public void setForceClick(boolean force){
		this.isForceClick = force;
	}
	
	public void setForceLongClick(boolean force){
		this.isForceLongClick = force;
	}

	@Override
	public void onClick(View v) {
		if (isForceClick && this.titleOnClickListener != null) {
			this.titleOnClickListener.onClick((customTitleView != null)?customTitleView:titleContainer);
			return;
		}
		
		if (longHiddable != null){
			if (longHiddable.isVisible()){
				longHiddable.hide();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_expand);
				if (!isForceClick)return;
			}
		}
		
		if (hiddable != null && !hiddable.isLocked()){
			if (hiddable.isVisible()){
				hiddable.hide();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_expand);
			}
			else {
				hiddable.show();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
			}
		}
		
		else if (hiddable == null){
			if (this.titleOnClickListener != null){
				this.titleOnClickListener.onClick((customTitleView != null)?customTitleView:titleContainer);
			}
		}
	}
	
	@Override
	public boolean onLongClick(View v){
		if (isForceLongClick && this.titleOnLongClickListener != null) {
			this.titleOnLongClickListener.onLongClick((customTitleView != null)?customTitleView:titleContainer);
			return true;
		}
		
		if (longHiddable == null) return false;
		if (hiddable != null) {
			if (hiddable.isVisible()){
				hiddable.hide();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_expand);
			}
		}
		
		if (longHiddable != null && !longHiddable.isLocked()){
			if (longHiddable.isVisible()){
				longHiddable.hide();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_expand);
			}
			
			else {
				longHiddable.show();
				if (!customTitle)expandButton.setBackgroundResource(R.drawable.ic_menu_collapse);
			}
		}
		
		else if (longHiddable == null){
			if (this.titleOnLongClickListener != null){
				this.titleOnLongClickListener.onLongClick((customTitleView != null)?customTitleView:titleContainer);
			}
		}
		
		return true;
	}
}
