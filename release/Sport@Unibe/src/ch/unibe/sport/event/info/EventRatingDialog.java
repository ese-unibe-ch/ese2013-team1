package ch.unibe.sport.event.info;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.restApi.WebApi;
import ch.unibe.sport.DBAdapter.tables.EventRating;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class EventRatingDialog extends BaseDialog {

	public static final String TAG = EventRatingDialog.class.getName();
	public static final String EVENT_ID = "eventID";

	private static final String STAR_PREFIX = "star";

	private ArrayList<ToggleButton> stars;
	private ToggleButton checkedStar;

	private boolean firstStart = true;

	private Event event;
	private int rating;

	private OnClickListener rateListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (checkedStar == null) {
				Print.toast(getContext(), "Please select rating");
				return;
			}
			disableStars();
			if (stars != null && checkedStar != null) {
				rating = stars.indexOf(checkedStar)+1;
				rate(rating);
			}
		}
	};

	private OnClickListener starListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			ToggleButton star = (ToggleButton)v;
			if (star.isChecked()){
				if (checkedStar != null) {
					checkedStar.setChecked(false);
				}
				checkedStar = star;
				enableOkButton();
			}
			else {
				checkedStar = null;
				disableOkButton();
			}
		}
	};
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	
	public static void show(Context context, int eventID){	
		Intent intent = new Intent(context, EventRatingDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(EVENT_ID, eventID);
		context.startActivity(intent);
	}

	public EventRatingDialog() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		if (eventID <= 0) {
			finish();
			return;
		}
		this.event = new Event(this,eventID);
		
		stars = new ArrayList<ToggleButton>();
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/

	private void initView() {
		addView(R.layout.course_rating_dialog_layout);
		setTitle(R.string.dialog_title_loading);
		initButtons();
		initReadyChecker();
	}

	private void initButtons() {
		this.setOnOkClickListener(rateListener);
		this.setOkText(R.string.dialog_raiting_button_ok);
		for (int i = 1; i <= 5; i++){
			stars.add((ToggleButton)Utils.findView(this, STAR_PREFIX+i));
		}
		disableStars();
		disableOkButton();
	}

	private void initReadyChecker(){
		showSpinner();
		showFade();
		if (!firstStart){
			this.disallowFinish();
		}
		ReadyChecker readyChecker = new ReadyChecker();
		readyChecker.setOnTaskCompletedListener(readyCheckerListener);
		readyChecker.execute(getContext());
	}
		
	private void initReadyToRate(){
		this.firstStart = false;
		setTitle(R.string.dialog_raiting_title_select_rating);
		hideSpinner();
		enableStarListeners();
		enableStars();
		hideFade();
	}
	
	protected void initRateSuccessful(){
		this.setTitle(R.string.dialog_raiting_title_done);
		this.hideSpinner();
		this.hideFade();
		switchOkCancelToClose();
		this.allowFinish();
		/* notify additional course fragment about successful rating */
		send(MessageFactory.updateRatedFromRatingDialog(event.getEventID(), rating));
	}
	/*------------------------------------------------------------
	----------- L O C A L   R E A D Y   C H E C K E R ------------
	------------------------------------------------------------*/
	private class ReadyChecker extends ObservableAsyncTask<Context,Void,Integer> {

		private static final int OK = 0;
		private static final int NO_CONNECTION = 1;
		private static final int NOT_ATTENDED = 2;
		private static final int ALREADY_RATED = 3;
		private static final int ALREADY_RATED_UPDATE = 31;

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			if (!Utils.haveNetworkConnection(context)) return NO_CONNECTION;
			
			AssociativeList<String> data = null;
			try {
				data = WebApi.isRated(context, event.getEventHash());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (data != null && !event.isRated()){
				new EventRating(context).add(event.getEventHash(), Utils.Int(data.get(WebApi.RATING)));
				return ALREADY_RATED_UPDATE;
			}
			
			if (event.isRated()) return ALREADY_RATED;
			return OK;
		}
	}

	private OnTaskCompletedListener<Context,Void,Integer> readyCheckerListener = new OnTaskCompletedListener<Context,Void,Integer>(){

		@Override
		public void onTaskCompleted(AsyncTask<Context,Void,Integer> task) {
			int code = -1;
			try {
				code = task.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			switch(code){
				case ReadyChecker.OK:{
					initReadyToRate();
					break;
				}
				
				case ReadyChecker.ALREADY_RATED:{
					showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
					break;
				}
				
				case ReadyChecker.ALREADY_RATED_UPDATE:{
					showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
					send(MessageFactory.updateRatedFromRatingDialog(event.getEventID(), rating));
					break;
				}
				
				case ReadyChecker.NO_CONNECTION:{
					showError(R.string.dialog_error_no_connection,R.string.dialog_error_no_connection_details);
					break;
				}
				
				case ReadyChecker.NOT_ATTENDED:{
					showError(R.string.dialog_raiting_error_not_attended,R.string.dialog_raiting_error_not_attended_details);
					break;
				}
				
				default:{
					showError(R.string.dialog_error_unexpected_error,R.string.dialog_error_unexpected_error_details);
					break;
				}
			}
		}
	};

	/*------------------------------------------------------------
	------------------- R A T E   M E T H O D S ------------------
	------------------------------------------------------------*/
	/**
	 * Actually rates course
	 * @param rating
	 */
	protected void rate(int rating) {
		if (rating < 1 || rating > 5) {
			Print.err(TAG,"Wrong rating number, should be [1,5]");
			return;
		}
		this.disableOkButton();
		this.disallowFinish();
		this.showSpinner();
		this.showFade();
		RateSetter rateSetter = new RateSetter(event.getEventHash(),rating);
		rateSetter.setOnTaskCompletedListener(rateSetterListener);
		rateSetter.execute(getContext());
	}

	/**
	 * Integer[0] - courseID
	 * Integer[1] - rating
	 * @author Team 1 2013
	 *
	 */
	private static class RateSetter extends ObservableAsyncTask<Context,Void,Integer>{
		private static final String TAG = RateSetter.class.getName();
		private static final int OK = 0;
		private static final int ALREADY_RATED = 1;
		private static final int SERVER_ERROR = 2;
		private static final int UNEXPECTED_ERROR = 3;

		private String eventHash;
		private int rating;

		private RateSetter(String eventHash, int rating){
			this.eventHash = eventHash;
			this.rating = rating;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			try {
				WebApi.rate(context, eventHash, rating);
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage().equals(WebApi.RATED)){
					return ALREADY_RATED;
				}
				return SERVER_ERROR;
			}

			if (context == null) return UNEXPECTED_ERROR;
			DBAdapter.INST.open(context, TAG);
			EventRating ratingDB = new EventRating(context);
			long id = ratingDB.add(eventHash, rating);
			DBAdapter.INST.close(TAG);
			if (id > 0) return OK;
			else return ALREADY_RATED;
		}
	}

	private OnTaskCompletedListener<Context,Void,Integer> rateSetterListener = new OnTaskCompletedListener<Context,Void,Integer>(){

		@Override
		public void onTaskCompleted(AsyncTask<Context, Void, Integer> task) {
			int code = -1;
			try {
				code = task.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			switch(code){
			case RateSetter.OK:{
				initRateSuccessful();
				break;
			}
			case RateSetter.ALREADY_RATED:{
				showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
				break;
			}
			case RateSetter.SERVER_ERROR:{
				showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
				break;
			}
			default:{
				showError(R.string.dialog_error_unexpected_error,R.string.dialog_error_unexpected_error_details);
				break;
			}
			}
		}
	};
	
	/*------------------------------------------------------------
	---------------- P R I V A T E   M E T H O D S ---------------
	------------------------------------------------------------*/

	private void enableStarListeners() {
		if (stars == null) return;
		for (ToggleButton star : stars){
			star.setOnClickListener(starListener);
		}
	}

	private void enableStars(){
		if (stars == null) return;
		for (ToggleButton star : stars){
			star.setClickable(true);
		}
	}

	private void disableStars(){
		if (stars == null) return;
		for (ToggleButton star : stars){
			star.setClickable(false);
		}
	}
}
