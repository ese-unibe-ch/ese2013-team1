package ch.unibe.sport.dialog;

import ch.unibe.sport.R;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class DialogGuide extends ProxySherlockFragmentActivity {

	public static final String TAG = DialogGuide.class.getName();

	public static final String DIALOG_GUIDE_VIEW_POSITION = "dialog_guide_view_position";
	public static final String DIALOG_LAYOUT_RESOURCE = "dialog_layout_resource";
	public static final String DIALOG_GUIDE_TAG = "dialog_guide_tag";
	public static final String GUIDE_SHARED_NAME = "guide";
	
	private String guideTag;
	
	public DialogGuide() {
		super(TAG);
	}

	public static void show(final Context context, final View anchor,final int layoutRes, final boolean hideKeyBoard, final String guideTag) {
		if (context.getSharedPreferences(GUIDE_SHARED_NAME, Context.MODE_PRIVATE).getBoolean(guideTag, false)) return;
		ViewTreeObserver vto = anchor.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (hideKeyBoard)Utils.hideKeyboardImplicit((Activity) context);
				Intent newIntent = new Intent(context, DialogGuide.class);
				newIntent.putExtra(DIALOG_LAYOUT_RESOURCE, layoutRes);
				newIntent.putExtra(DIALOG_GUIDE_VIEW_POSITION,calcCoordinates(anchor));
				newIntent.putExtra(DIALOG_GUIDE_TAG, guideTag);
				context.startActivity(newIntent);

				ViewTreeObserver obs = anchor.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	private static int[] calcCoordinates(View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);
		int x = location[0];
		int y = location[1];
		int width = view.getWidth();
		int height = view.getHeight();

		return new int[] { x, y, width, height };
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int[] viewMeasure = getIntent().getIntArrayExtra(DIALOG_GUIDE_VIEW_POSITION);
		final int layoudID = getIntent().getIntExtra(DIALOG_LAYOUT_RESOURCE, 0);
		this.guideTag = getIntent().getStringExtra(DIALOG_GUIDE_TAG);
		if (layoudID == 0) {
			finish();
			return;
		}
		this.setFinishOnTouchOutside(true);
		setContentView(R.layout.dialog_guide_layout);
		ViewGroup root = (ViewGroup) this.findViewById(R.id.root);
		ImageView oval = new ImageView(this);
		oval.setImageResource(R.drawable.guide_oval);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(viewMeasure[2],viewMeasure[3]);
		lp.leftMargin = viewMeasure[0];
		lp.topMargin = viewMeasure[1]-Config.INST.DISPLAY.STATUS_BAR_HEIGHT;
		oval.setLayoutParams(lp);
		oval.setScaleType(ScaleType.FIT_XY);
		Print.p(viewMeasure);
		root.addView(oval);
		
		final ViewGroup hint = (ViewGroup) View.inflate(this, layoudID, null);
		FrameLayout.LayoutParams hintLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		hintLP.topMargin = viewMeasure[1]-Config.INST.DISPLAY.STATUS_BAR_HEIGHT + viewMeasure[3];
		hint.setLayoutParams(hintLP);
		root.addView(hint);
		
		Button close  = (Button) hint.findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SharedPreferences pref = getActivity().getSharedPreferences(GUIDE_SHARED_NAME, Context.MODE_PRIVATE);
				pref.edit().putBoolean(guideTag, true).commit();
				finish();
			}
		});
	}

	@Override
	public void process(Message message) {}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
