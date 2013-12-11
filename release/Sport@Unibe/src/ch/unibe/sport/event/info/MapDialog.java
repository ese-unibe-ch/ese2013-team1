package ch.unibe.sport.event.info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import ch.unibe.sport.R;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.utils.Print;

/**
 * Triggers google maps with location from event
 * @author Team 1
 *
 */
public class MapDialog extends BaseDialog {
	public static final String TAG = MapDialog.class.getName();
	
	public static final String EVENT_ID = "eventID";
	
	private Event event;
	private int eventID;
	
	private EditText location;
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public static void show(final Context context,final int courseID){
		Intent intent = new Intent(context, MapDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(EVENT_ID, courseID);
		context.startActivity(intent);
	}

	public MapDialog() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		if (eventID <= 0) {
			finish();
			return;
		}
		this.event = new Event(getContext(),eventID);
		if (event.getPlace() == null) {
			finish();
			return;
		}
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initView(){
		addView(R.layout.dialog_map_layout);
		
		this.setTitle(R.string.dialog_map_loading_title);
		this.location = (EditText) this.findViewById(R.id.location);
		if (event.getPlace() != null){
			this.location.setText(this.event.getPlace().getPlaceName());
		}
		this.setOkText(R.string.dialog_map_show);
		
		initLocationLoader();
	}
	
	
	private void initLocationLoader() {
		String label = event.getPlace().getPlaceName();
		double latitude = event.getPlace().getLat();
		double longitude = event.getPlace().getLon();
		Print.log(latitude+" "+longitude);
		
		if (latitude == 0.0d && longitude == 0.0d){
			showSearchBox();
			return;
		}
		showGoogleMaps(getContext(), label, latitude, longitude);
	}
	
	private void showSearchBox(){
		this.hideFade();
		this.hideSpinner();
		this.enableOkButton();
		this.location.setFocusable(true);
		this.location.setEnabled(true);
		this.location.setFocusableInTouchMode(true);
		this.setTitle(R.string.dialog_map_not_found_title);
		
		this.setOnOkClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String place = location.getText().toString();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+place+"("+place+")"));
				getContext().startActivity(intent);
				finish();
			}
		});
	}
	
	private void showGoogleMaps(Context context, String place, double latitude, double longitude){		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+"("+place+")"));
		getContext().startActivity(intent);
		finish();
	}
}
