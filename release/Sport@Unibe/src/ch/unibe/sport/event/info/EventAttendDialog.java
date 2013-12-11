package ch.unibe.sport.event.info;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.restApi.Attend;
import ch.unibe.sport.DBAdapter.restApi.AttendSpringRequest;
import ch.unibe.sport.DBAdapter.restApi.IsAttended;
import ch.unibe.sport.DBAdapter.restApi.IsAttendedSpringRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.DBAdapter.tables.EventAttended;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.utils.CalendarHelper;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

/**
 * 
 * Class that controls the attendance of each course
 * per day. Access to the spiceService is done
 * through spiceManager to execute actions.
 * 
 * @author Team 1
 *
 */

public class EventAttendDialog extends BaseDialog {

	public static final String TAG = EventAttendDialog.class.getName();
	public static final String EVENT_ID = "eventID";
	public static final String DATE = "date";
	
	private static final String JSON_IS_ATTENDED_KEY = "json_is_attended_key";
	private static final String JSON_ATTEND_KEY = "json_attend_key";
	
	private Event event;
	private int eventID;
	private Date date;
	
	private ViewGroup shareContainer;
	private ToggleButton share;
	private TextView dateView;
	private TextView timeView;
	private TextView attendInfo;

	private SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
    private IsAttendedSpringRequest isAttendedSpringRequest;
    private AttendSpringRequest attendSpringRequest;
    
	private OnClickListener okListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			attend();
		}
	};
	
	private OnClickListener shareListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			share.setChecked(!share.isChecked());
		}
	};
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public static void show(Context context, int eventID, Date date){	
		Intent intent = new Intent(context, EventAttendDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(EVENT_ID, eventID);
		intent.putExtra(DATE, date.toInt());
		context.startActivity(intent);
	}
	
	public EventAttendDialog() {
		super(TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initArguments();
		if (eventID <= 0) {
			finish();
			return;
		}
		
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initArguments() {
		this.eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		this.date = new Date(this.getIntent().getIntExtra(DATE, 0));
		if (eventID <= 0) return;
		this.event = new Event(getContext(),eventID);
	}

	private void initView() {
		addView(R.layout.course_attend_dialog_layout);
		setTitle(R.string.dialog_title_loading);
		
		this.shareContainer = (ViewGroup) this.findViewById(R.id.share_container);
		this.shareContainer.setOnClickListener(shareListener);
		this.share = (ToggleButton) this.findViewById(R.id.share_checkbox);
		this.dateView = (TextView) this.findViewById(R.id.date);
		this.timeView = (TextView) this.findViewById(R.id.time);
		this.attendInfo = (TextView) this.findViewById(R.id.attend_info);
				
		this.timeView.setText(event.getTimeString());
		this.dateView.setText(date.print()+" ("+Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[new CalendarHelper(date).dayOfWeek]+")");
		this.attendInfo.setText(Utils.getString(getContext(), R.string.dialog_attend_info)+" "+event.getEventName()+"?");
		
		initButtons();
		initReadyChecker();
	}
	
	private void initButtons() {
		this.setOnOkClickListener(okListener);
		this.setOkText(R.string.dialog_attend_button_ok);
		disableOkButton();
		disableShare();
	}
	
	private void initReadyChecker(){
		showSpinner();
		showFade();
		
		isAttendedSpringRequest = new IsAttendedSpringRequest(Config.INST.SYSTEM.UUID,event.getEventHash(),date);
        spiceManager.execute(isAttendedSpringRequest, JSON_IS_ATTENDED_KEY, DurationInMillis.ALWAYS_EXPIRED, new IsAttendedRequestListener());
	}
	
	private void initReadyToAttend(){
		setTitle(R.string.dialog_attend_title_select_course);
		hideSpinner();
		enableShare();
		enableOkButton();
		hideFade();
	}
	
	protected void initAttendSuccessful(){
		this.setTitle(R.string.dialog_attend_title_done);
		this.hideSpinner();
		this.hideFade();
		switchOkCancelToClose();
		this.allowFinish();
		/* notify additional course fragment about successful rating */
		send(MessageFactory.updateCourse(TAG,eventID));
	}
	
	/*------------------------------------------------------------
	----------- I S   A T T E N D E D   C H E C K E R ------------
	------------------------------------------------------------*/
	private class IsAttendedRequestListener implements RequestListener<IsAttended>, RequestProgressListener {

		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
			Toast.makeText(EventAttendDialog.this, "Failed to load attend data.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onRequestSuccess(IsAttended isAttended) {
			if (done) return;
			if (isAttended == null) return;

			if (isAttended.isError()){
				showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
				return;
			}
			
			if (isAttended.isAttended()){
				showError(R.string.dialog_attended_error_already_attended,R.string.dialog_attended_error_already_attended);
				return;
				// TODO implement unattend
			}
			
			done = true;
			initReadyToAttend();
			
			
			Print.log(isAttended.getResult());
		}

		@Override public void onRequestProgressUpdate(RequestProgress progress) {}
	}
	
	
	/*------------------------------------------------------------
	----------------- A T T E N D   M E T H O D S ----------------
	------------------------------------------------------------*/
	/**
	 * Actually rates course
	 * @param rating
	 */
	protected void attend() {
		this.disableOkButton();
		this.disallowFinish();
		this.disableShare();
		this.showSpinner();
		this.showFade();
		
		attendSpringRequest = new AttendSpringRequest(Config.INST.SYSTEM.UUID,event.getEventHash(),date,share.isChecked());
        spiceManager.execute(attendSpringRequest, JSON_ATTEND_KEY, DurationInMillis.ALWAYS_EXPIRED, new AttendRequestListener());
	}

	private class AttendRequestListener implements RequestListener<Attend>, RequestProgressListener {
		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
			Toast.makeText(EventAttendDialog.this, "Failed to load attend data.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onRequestSuccess(Attend attend) {
			if (done) return;
			if (attend == null) return;

			if (attend.isError()){
				showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
				return;
			}
			
			if (attend.isAttended()){
				showError(R.string.dialog_attended_error_already_attended,R.string.dialog_attended_error_already_attended);
				return;
			}
			done = true;
			
			DBAdapter.INST.open(getContext(), TAG);
			EventAttended attendedDB = new EventAttended(getContext());
			attendedDB.add(event.getEventHash(), date,share.isChecked());
			DBAdapter.INST.close(TAG);
			initAttendSuccessful();
		}
		
		@Override public void onRequestProgressUpdate(RequestProgress arg0) {}
	}
	/*------------------------------------------------------------
	---------------- P R I V A T E   M E T H O D S ---------------
	------------------------------------------------------------*/
	private void disableShare(){
		if (shareContainer == null || share == null) return;
		shareContainer.setClickable(false);
		share.setClickable(false);
	}
	
	private void enableShare(){
		if (shareContainer == null || share == null) return;
		shareContainer.setClickable(true);
		share.setClickable(true);
	}
	
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	@Override
	protected void onStart() {
		super.onStart();
		spiceManager.start(this);
		spiceManager.addListenerIfPending(IsAttended.class, JSON_IS_ATTENDED_KEY, new IsAttendedRequestListener());
		spiceManager.getFromCache(IsAttended.class, JSON_IS_ATTENDED_KEY, DurationInMillis.ALWAYS_RETURNED, new IsAttendedRequestListener());
	}
	
	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}
}
