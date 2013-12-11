package ch.unibe.sport.event.info;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.main.favorites.FavoriteColors;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.widget.view.ColorToggleButton;

/**
 * Class used to toggle the favorites event color.
 * @author Team 1
 *
 */
public class EventChangeColorDialog extends BaseDialog {
	
	public static final String TAG = EventChangeColorDialog.class.getName();
	
	public static final String BUTTON_PREFIX = "color";
	public static final String EVENT_ID = "eventID";
	
	private int eventID;
	private Event event;
	
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
			new EventFavorite(getContext()).setBGColor(event.getEventHash(), color);
			send(MessageFactory.updateCourse(TAG, event.getEventID()));
			finish();
		}
		
	};
	
	public EventChangeColorDialog() {
		super(TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initEventID();
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

	/**
	 * Initializes favorites predefined color buttons
	 */
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
	
	private void initEventID(){
		this.eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		if (eventID == 0){
			Print.err(TAG, "eventID is 0");
			finish();
			return;
		}
		this.event = new Event(getContext(),eventID);
		
	}
	
	public static void show(Context context, int eventID) {
		Intent intent = new Intent(context, EventChangeColorDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(EVENT_ID, eventID);
		context.startActivity(intent);
	}

}
