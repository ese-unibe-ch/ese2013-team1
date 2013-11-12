package ch.unibe.sport.course.info;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tables.FavoriteCourses;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.favorites.FavoriteColors;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.widget.view.ColorToggleButton;

public class CourseChangeColorDialog extends BaseDialog {
	
	public static final String TAG = CourseChangeColorDialog.class.getName();
	
	public static final String BUTTON_PREFIX = "color";
	public static final String COURSE_ID = "courseID";
	
	private int courseID;
	private Course course;
	
	private ArrayList<ColorToggleButton> buttons;
	private ColorToggleButton checkedButton;
	
	private OnClickListener buttonListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			checkedButton = (ColorToggleButton) view;
			if (buttons == null) return;
			for (ColorToggleButton button : buttons){
				if (button != checkedButton) button.setChecked(false);
			}
			if (!checkedButton.isChecked()) {
				disableOkButton();
				checkedButton = null;
			}
			else enableOkButton();
		}
	};
	
	private OnClickListener changeListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (checkedButton == null) return;
			int color = checkedButton.getColor();
			new FavoriteCourses(getContext()).setBGColor(course.getCourseID(), color);
			send(MessageFactory.updateBGColorFromChangeColorDialog(course.getCourseID(), color));
			finish();
		}
		
	};
	
	public CourseChangeColorDialog() {
		super(TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCourseID();
		buttons = new ArrayList<ColorToggleButton>();
		initView();
	}
	
	private void initView(){
		this.addView(this.getView(R.layout.dialog_change_color));
		this.setTitle(R.string.dialog_change_color_title_change_color);
		this.setOkText(R.string.dialog_change_color_change);
		
		disableOkButton();
		initColorButtons();
		this.setOnOkClickListener(changeListener);
		
	}

	private void initColorButtons() {
		int index = 1;
		ColorToggleButton button = (ColorToggleButton) Utils.findView(this, BUTTON_PREFIX+index);
		while(button != null){
			if (index <= FavoriteColors.BG_COLORS.length){
				button.setColor(FavoriteColors.BG_COLORS[index-1]);
				//button.setBorderColor(0x88cccccc);
				button.setBorderColorChecked(0xfffc6868);
			}
			button.setOnClickListener(buttonListener);
			buttons.add(button);
			index++;
			button = (ColorToggleButton) Utils.findView(this, BUTTON_PREFIX+index);
		}
	}
	
	private void initCourseID(){
		this.courseID = this.getIntent().getIntExtra(COURSE_ID, 0);
		if (courseID == 0){
			Print.err(TAG, "courseID is 0");
			finish();
			return;
		}
		this.course = new Course(getContext(),courseID);
		
	}
	
	public static void show(Context context, int courseID) {
		Intent intent = new Intent(context, CourseChangeColorDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(COURSE_ID, courseID);
		context.startActivity(intent);
	}

}
