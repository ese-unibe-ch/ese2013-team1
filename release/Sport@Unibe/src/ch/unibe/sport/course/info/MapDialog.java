package ch.unibe.sport.course.info;

import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.Events;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Json;
import ch.unibe.sport.utils.Print;

public class MapDialog extends BaseDialog {
	public static final String TAG = MapDialog.class.getName();
	public static final String API_URL = "http://scg.unibe.ch/ese/unisport/location.php?loc=";
	
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
		initCourseHash();
		if (eventID <= 0) {
			finish();
			return;
		}
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initCourseHash(){
		this.eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		this.event = new Event(getContext(),eventID);
	}
	
	private void initView(){
		addView(R.layout.dialog_map_layout);
		
		this.setTitle(R.string.dialog_map_loading_title);
		this.location = (EditText) this.findViewById(R.id.location);
		this.location.setText(this.event.getPlace());
		this.setOkText(R.string.dialog_map_show);
		
		initLocationLoader();
	}
	
	
	private void initLocationLoader() {
		
		this.showFade();
		this.showSpinner();
		this.disableOkButton();
		
		
		final String place = getLabel(getContext(),eventID);
		PlaceLoader loader = new PlaceLoader();
		loader.setOnTaskCompletedListener(new OnTaskCompletedListener<String, Void, Double[]>(){
			@Override
			public void onTaskCompleted(AsyncTask<String, Void, Double[]> task) {
				Double[] coordinates;
				try {
					coordinates = task.get();
				} catch (InterruptedException e) {
					showSearchBox();
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					showSearchBox();
					e.printStackTrace();
					return;
				}
				if (coordinates == null) {
					showSearchBox();
					return;
				}
				//Else launch google maps directly
				double latitude = coordinates[0];
				double longitude = coordinates[1];
				Print.log(latitude+"/"+longitude);
				showGoogleMaps(getContext(),place, latitude, longitude);
			}	
		});
		loader.execute(place);
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
	
	private static class PlaceLoader extends ObservableAsyncTask<String,Void,Double[]>{
		@Override
		protected Double[] doInBackground(String... url) {
			Double[] coord;
			try {
				coord = getLatLng(url[0]);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return coord;
		}
	}
	
	private static String getLabel(final Context context, final int courseID){
		
		DBAdapter.INST.open(context, TAG);
		Events coursesDB = new Events(context);
		String [] coursesDBString = coursesDB.getData(courseID);
		//String placeName = coursesDBString[Events.PLACE];
			
		DBAdapter.INST.close(TAG);
		
		return "";
	}
	
	private static Double[] getLatLng(String placeName) throws Exception {
		String json = Json.getInputStream(API_URL+placeName);
		JSONObject object = Json.parseJson(json);
		JSONObject result = (JSONObject) object.get("result");
		if (result == null) throw new Exception("Place not found: "+placeName);
		double lat = Double.parseDouble((String)result.get("lat"));
		double lon = Double.parseDouble((String)result.get("lon"));
		return new Double[]{lat,lon};
	}
}
