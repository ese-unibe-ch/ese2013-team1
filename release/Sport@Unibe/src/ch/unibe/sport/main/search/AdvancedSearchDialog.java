package ch.unibe.sport.main.search;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
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
	private EditText event;
	
	private int daysChecked;
	
	private Button close;
	private Button search;
	
	public static void show(Context context, View view) {
		DialogFloating.show(context, view, new Bundle(),AdvancedSearchDialog.class);
		((Activity)context).overridePendingTransition(R.anim.translate_out, R.anim.translate_in);
	}
	
	public AdvancedSearchDialog() {
		super(TAG);
	}

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
		this.event = (EditText) this.findViewById(R.id.event);
		initButtons();
		initSeekBar();
	}

	private void initButtons() {
		days = new ArrayList<ToggleButton>();
		for (int i = 1; i <= DAYS; i++){
			days.add((ToggleButton)Utils.findView(this,DAY_PREFIX+i));
		}
		dayAll = (ToggleButton) this.findViewById(R.id.day_all);
		
		close = (Button) this.findViewById(R.id.close);
		search = (Button) this.findViewById(R.id.search);
		initListeners();
		
		search.setVisibility(View.VISIBLE);
		search.setOnClickListener(new OnClickListener() {
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
		for (ToggleButton button : days) {
			button.setOnClickListener(new OnClickListener() {
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
				
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		dayAll.setOnClickListener(new OnClickListener() {
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
	
	private void search() {	
		SearchRequest request = new SearchRequest();
	
		ArrayList<Integer> daysArray = new ArrayList<Integer>();
		for (int index = 0,length = days.size(); index < length; index++) {
			if (days.get(index).isChecked()){
				daysArray.add(index+1);
			}
		}
		
		int[] daysInt = new int[daysArray.size()];
		for (int index = 0,length = daysInt.length; index < length; index++) {
			daysInt[index] = daysArray.get(index);
		}
		
		request.setDays(daysInt);
		request.setTimeFrom(seekBar.getSelectedMinValue());
		request.setTimeTo(seekBar.getSelectedMaxValue());
		request.setEventName(event.getText().toString());
		
		String searchQuery = request.toJson().toJSONString();
		
		if (request.getDays().length == 0) {
			Toast.makeText(this, "Please select day", Toast.LENGTH_SHORT).show();
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
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override public void process(Message message) {}
}
