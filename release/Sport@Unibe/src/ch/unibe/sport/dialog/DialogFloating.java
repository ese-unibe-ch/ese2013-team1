package ch.unibe.sport.dialog;

import ch.unibe.sport.R;
import ch.unibe.sport.config.Config;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

public abstract class DialogFloating extends Dialog {

	public DialogFloating(String tag) {
		super(tag);
	}

	public static final String DIALOG_FLOAT_VIEW_POSITION = "dialog_float_view_position";

	private LinearLayout container;
	
	protected static void show(Context context, View view, Bundle extras, Class<?> cls){
		Intent newIntent = new Intent(context, cls);
		newIntent.putExtras(extras);
		newIntent.putExtra(DIALOG_FLOAT_VIEW_POSITION, calcLocation(view));
		context.startActivity(newIntent);
	}

	private static int[] calcLocation(View view){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		int width = view.getWidth();
		int height = view.getHeight();
		return new int[]{x,y,width,height};
	}

	/**
	 * Calculates position of dialogue and creates it.
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int[] viewMeasure = extras.getIntArray(DIALOG_FLOAT_VIEW_POSITION);
		this.setFinishOnTouchOutside(true);
		setContentView(R.layout.dialog_float_activity);
		final LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.dialog_float_layout);
		container = (LinearLayout) findViewById(R.id.dialog_float_container);
		ViewTreeObserver vto = dialogLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				int dialogHeight = dialogLayout.getHeight();
				if (dialogHeight > Config.INST.DISPLAY.HEIGHT){
					dialogHeight = Config.INST.DISPLAY.HEIGHT;
				}
				int[] dialogLocation = calcDialogLocation(viewMeasure,new int[]{dialogLayout.getWidth(),dialogHeight});
				setDialogLocation(dialogLocation,dialogHeight);
				ViewTreeObserver obs = dialogLayout.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	private int[] calcDialogLocation(int[] viewMeasure, int[] dialogMeasure){
		int screenWidth = Config.INST.DISPLAY.WIDTH;
		int screenHeight = Config.INST.DISPLAY.HEIGHT;
		int statusBarHeight = Config.INST.DISPLAY.STATUS_BAR_HEIGHT;
		int dialogWidth = dialogMeasure[0];
		int dialogHeight = dialogMeasure[1];
		int viewWidth = viewMeasure[2];
		int viewHeight = viewMeasure[3];
		int viewX = viewMeasure[0];
		int viewY = viewMeasure[1];

		boolean underView = (viewY+viewHeight+dialogHeight <= screenHeight);
		boolean leftToView = (viewX+dialogWidth <= screenWidth);

		int x,y = 0;
		if (underView)y = viewY + viewHeight-statusBarHeight;
		else y = viewY - dialogHeight-statusBarHeight;
		if (leftToView)	x = viewX;
		else x = viewX + viewWidth - dialogWidth;
		return new int[]{x,y};
	}

	private void setDialogLocation(int[] dialogLocation,int dialogHeight){
		WindowManager.LayoutParams params = DialogFloating.this.getWindow().getAttributes();
		DialogFloating.this.getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
		params.x = dialogLocation[0];
		params.y = dialogLocation[1];
		params.height = dialogHeight;
		getWindow().setAttributes(params);
	}
	
	public LinearLayout getLayout(){
		return container;
		
	}
}
