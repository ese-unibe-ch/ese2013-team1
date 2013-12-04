package ch.unibe.sport.main.search;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.core.Time;
import ch.unibe.sport.dialog.DialogFloating;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.widget.view.SeekBar;

public class AdvancedSearchDialog extends DialogFloating {

	public static final String TAG = AdvancedSearchDialog.class.getName();
	private static final String DAY_PREFIX = "day";
	private static final int DAYS = 7;
	
	private ArrayList<ToggleButton> days;
	
	private ToggleButton dayAll;
	private SeekBar<Time> seekBar;
	
	private int daysChecked;
	
	private Button clear;
	private Button search;
	
	public static void show(Context context, View view) {
		DialogFloating.show(context, view, new Bundle(),AdvancedSearchDialog.class);
		//activity.overridePendingTransition(R.anim.translate_out, R.anim.translate_in);
	}
	
	public AdvancedSearchDialog() {
		super(TAG);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams windowLayoutParams = getWindow().getAttributes();  
		windowLayoutParams.dimAmount=0.3f;  
		getWindow().setAttributes(windowLayoutParams);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ViewGroup searchLayout = (ViewGroup) this.getLayoutInflater().inflate(R.layout.advanced_search_layout, null);
		this.getLayout().addView(searchLayout);
		initButtons();
		initSeekBar();
	}

	private void initButtons() {
		days = new ArrayList<ToggleButton>();
		for (int i = 1; i <= DAYS; i++){
			days.add((ToggleButton)Utils.findView(this,DAY_PREFIX+i));
		}
		dayAll = (ToggleButton) this.findViewById(R.id.day_all);
		
		clear = (Button) this.findViewById(R.id.clear);
		search = (Button) this.findViewById(R.id.search);
		initListeners();
		
		search.setVisibility(View.VISIBLE);
		search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				search();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initSeekBar(){
		this.seekBar = (SeekBar<Time>)this.findViewById(R.id.seekBar);
		seekBar.setAdapter(new TimeSeekBarAdapter(7,23));
		seekBar.setDrawAdapter(new TimeSeekBarDrawAdapter(getContext()));
		seekBar.setNotifyWhileDragging(true);
		seekBar.setMinValueIndex(5);
		seekBar.setMaxValueIndex(11);
	}
	
	private void initListeners(){
		for (ToggleButton button : days){
			button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					boolean checked = ((ToggleButton)v).isChecked();
					if (!checked) {
						dayAll.setChecked(false);
						daysChecked--;
					}
					else {
						daysChecked++;
						if (daysChecked == DAYS) dayAll.setChecked(true);
					}
				}
			});
		}
				
		clear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				seekBar.setMinValueIndex(5);
				seekBar.setMaxValueIndex(11);
				for (ToggleButton button : days){
					button.setChecked(false);
				}
				dayAll.setChecked(false);
				daysChecked = 0;
			}
		});
		
		dayAll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean checked = ((ToggleButton)v).isChecked();
				for (ToggleButton button : days){
					button.setChecked(checked);
				}
				daysChecked = (checked) ? DAYS : 0;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void search(){
		JSONObject json = new JSONObject();
		
		JSONArray daysArray = new JSONArray();
		int dayOfWeek = 0;
		for (ToggleButton button : days){
			if (button.isChecked()){
				daysArray.add(dayOfWeek);
			}
			dayOfWeek++;
		}
		
		JSONArray timesArray = new JSONArray();
		JSONObject interval = new JSONObject();
		interval.put(AdvancedSearchResultFragment.JSON_INTERVAL_FROM, seekBar.getSelectedMinValue().toMinutes());
		interval.put(AdvancedSearchResultFragment.JSON_INTERVAL_TO, seekBar.getSelectedMaxValue().toMinutes());
		timesArray.add(interval);
		json.put(AdvancedSearchResultFragment.JSON_DAYS, daysArray);
		json.put(AdvancedSearchResultFragment.JSON_INTERVALS, timesArray);
		
		String searchQuery = json.toJSONString();
		
		if (daysArray.size() == 0 || timesArray.size() == 0){
			Toast.makeText(this, "Please select days and times", Toast.LENGTH_LONG);
			return;
		}
		AdvancedSearchResultActivity.show(this, searchQuery);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/* finishing activity without delay causes action bar in main activity to collapse action views (WTF Android?) */
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 150);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override public void process(Message message) {}
}
