package ch.unibe.sport.guide;

import android.content.Context;
import android.view.View;
import ch.unibe.sport.R;
import ch.unibe.sport.dialog.DialogGuide;


public class GuideFactory {
	
	public static final void showSearchGuide(Context context, View anchor){
		DialogGuide.show(context, anchor, R.layout.dialog_guide_advanced_search,true, "advanced_search");
	}
}
