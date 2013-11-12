package ch.unibe.sport.widget.layout.list.simple;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class ListView extends LinearLayout{

	public ListView(Context context) {
		super(context);
		initView();
	}
	
	protected abstract void initView();

}
