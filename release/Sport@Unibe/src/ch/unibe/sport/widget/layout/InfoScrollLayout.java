package ch.unibe.sport.widget.layout;

import ch.unibe.sport.R;
import ch.unibe.sport.widget.layout.ObservableScrollView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class InfoScrollLayout extends ObservableScrollView{

	private LinearLayout container;
	
	public InfoScrollLayout(Context context) {
		super(context);
		initView();
	}
	
	private InfoScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private InfoScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.scroll_info_layout, this);
        inflater = null;
        initComponents();
	}
	
	private void initComponents(){
		this.container = (LinearLayout) this.findViewById(R.id.info_container);
	}
	
	public abstract void initContent(Object ...objects);
	
	@Override
	public void addView(View view){
		assert this.container != null;
		this.container.addView(view);
	}
	
	public ViewGroup getContainer(){
		return this.container;
	}

}
