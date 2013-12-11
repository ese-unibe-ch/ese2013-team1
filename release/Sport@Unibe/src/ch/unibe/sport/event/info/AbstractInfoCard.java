package ch.unibe.sport.event.info;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.widget.layout.CardLayout;

/**
 * 
 * Abstract class extended by EventAdditionalInfoCard,
 * EventCalendarCard 
 * 
 * @author Team 1
 *
 */

public class AbstractInfoCard extends CardLayout {

	private IPoint point;
	
	public AbstractInfoCard(Context context,String tag) {
		super(context);
	}
	
	public AbstractInfoCard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AbstractInfoCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void initCourseParam(ViewGroup container,TextView paramView, Object param){
		assert container != null;
		assert paramView != null;
		
		String data = (param != null) ? param.toString() : "";
		if (data.length() == 0 && container != null){
			container.setVisibility(GONE);
			return;
		}
		if (paramView == null) return;
		paramView.setText(data);
	}
	
	public IPoint getPoint(){
		return this.point;
	}
	
	public void recycle() {
		
		point = null;
	}

}
