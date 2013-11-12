package ch.unibe.sport.course.info;

import java.util.concurrent.ExecutionException;

import com.parse.ParseException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.AlreadyAttendedException;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.DBParse;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.CalendarHelper;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Utils;

public class CourseAttendDialog extends BaseDialog {

	public static final String TAG = CourseAttendDialog.class.getName();
	public static final String COURSE_ID = "courseID";
	public static final String DATE = "date";
	
	private Course course;
	private int courseID;
	private Date date;
	
	private ViewGroup shareContainer;
	private ToggleButton share;
	private TextView dateView;
	private TextView timeView;
	private TextView attendInfo;
	
	private boolean firstStart = true;
	
	private OnClickListener okListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			disableOkButton();
			disableShare();
			initReadyChecker();
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
	public static void show(Context context, int courseID, Date date){	
		Intent intent = new Intent(context, CourseAttendDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(COURSE_ID, courseID);
		intent.putExtra(DATE, date.toInt());
		context.startActivity(intent);
	}
	
	public CourseAttendDialog() {
		super(TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initArguments();
		if (courseID <= 0) {
			finish();
			return;
		}
		
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initArguments(){
		this.courseID = this.getIntent().getIntExtra(COURSE_ID, 0);
		this.date = new Date(this.getIntent().getIntExtra(DATE, 0));
		if (courseID <= 0) return;
		this.course = new Course(getContext(),courseID);
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
				
		this.timeView.setText(course.getTime());
		this.dateView.setText(date.print()+" ("+Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[new CalendarHelper(date).dayOfWeek]+")");
		this.attendInfo.setText(Utils.getString(getContext(), R.string.dialog_attend_info)+" "+course.getCourse()+"?");
		
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
		if (!firstStart){
			this.disallowFinish();
		}
		ReadyChecker readyChecker = new ReadyChecker(courseID,date.toInt());
		readyChecker.setOnTaskCompletedListener(readyCheckerListener);
		readyChecker.execute(getContext());
	}
	
	protected void initParseCheck() {
		ParseReadyChecker parseReadyChecker = new ParseReadyChecker(course.getCourseHash(),date.toInt());
		parseReadyChecker.setOnTaskCompletedListener(parseReadyCheckerListener);
		parseReadyChecker.execute(getContext());
	}
	
	private void initReadyToAttend(){
		this.firstStart = false;
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
		send(MessageFactory.updateCourse(TAG,courseID));
	}
	
	/*------------------------------------------------------------
	----------- L O C A L   R E A D Y   C H E C K E R ------------
	------------------------------------------------------------*/
	private static class ReadyChecker extends ObservableAsyncTask<Context,Void,Integer> {

		private static final int OK = 0;
		private static final int NO_CONNECTION = 1;
		private static final int ALREADY_ATTENDED = 2;

		private int courseID;
		private int date;
		private ReadyChecker(int courseID, int date){
			this.courseID = courseID;
			this.date = date;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			if (!Utils.haveNetworkConnection(context)) return NO_CONNECTION;
			boolean attended = new AttendedCourses(context).isAttended(courseID,new Date(date));
			if (attended) return ALREADY_ATTENDED;
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
				if (firstStart)initParseCheck();
				else {
					attend();
				}
				break;
			}
			case ReadyChecker.ALREADY_ATTENDED:{
				showError(R.string.dialog_attended_error_already_attended,R.string.dialog_attended_error_already_attended);
				break;
			}
			case ReadyChecker.NO_CONNECTION:{
				showError(R.string.dialog_error_no_connection,R.string.dialog_error_no_connection_details);
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
	----------- P A R S E   R E A D Y   C H E C K E R ------------
	------------------------------------------------------------*/
	private static class ParseReadyChecker extends ObservableAsyncTask<Context,Void,Integer>{
		private static final int OK = 0;
		private static final int ALREADY_ATTENDED = 1;
		private static final int PARSE_ERROR = 2;
		private static final int NOT_SYNCHRONIZED = 3;

		private String courseHash;
		private int date;
		private ParseReadyChecker(String courseHash, int date){
			this.courseHash = courseHash;
			this.date = date;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			boolean rated = false;
			try {
				rated = DBParse.isCourseAttended(ctx[0],courseHash,new Date(date));
			} catch (ParseException e) {
				e.printStackTrace();
				return PARSE_ERROR;
			}
			if (rated) return ALREADY_ATTENDED;

			if (Config.INST.SYNCHRONIZE.SYNCHRONIZED) return OK;
			else return NOT_SYNCHRONIZED;
		}

	}

	private OnTaskCompletedListener<Context,Void,Integer> parseReadyCheckerListener = new OnTaskCompletedListener<Context,Void,Integer>(){
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
			case ParseReadyChecker.OK:{
				if (firstStart)initReadyToAttend();
				break;
			}
			case ParseReadyChecker.ALREADY_ATTENDED:{
				showError(R.string.dialog_attended_error_already_attended,R.string.dialog_attended_error_already_attended);
				break;
			}
			case ParseReadyChecker.PARSE_ERROR:{
				showError(R.string.dialog_error_parse_error,R.string.dialog_error_parse_error_details);
				break;
			}
			case ParseReadyChecker.NOT_SYNCHRONIZED:{
				showError(R.string.dialog_error_sync_error,R.string.dialog_error_sync_error_details);
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
	----------------- A T T E N D   M E T H O D S ----------------
	------------------------------------------------------------*/
	/**
	 * Actially rates course
	 * @param rating
	 */
	protected void attend() {
		this.disableOkButton();
		this.disallowFinish();
		AttendSetter attendSetter = new AttendSetter(courseID,course.getCourseHash(),date.toInt(),share.isChecked());
		attendSetter.setOnTaskCompletedListener(attendSetterListener);
		attendSetter.execute(getContext());
	}

	/**
	 * Integer[0] - courseID
	 * Integer[1] - rating
	 * @author Team 1 2013
	 *
	 */
	private static class AttendSetter extends ObservableAsyncTask<Context,Void,Integer>{
		private static final String TAG = AttendSetter.class.getName();
		private static final int OK = 0;
		private static final int ALREADY_ATTENDED = 1;
		private static final int PARSE_ERROR = 2;
		private static final int UNEXPECTED_ERROR = 3;

		private int courseID;
		private String courseHash;
		private int date;
		boolean share;

		private AttendSetter(int courseID, String courseHash, int date,boolean share){
			this.courseID = courseID;
			this.courseHash = courseHash;
			this.date = date;
			this.share = share;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			try {
				DBParse.addAttendedCourse(context, courseHash, new Date(date),share);
			} catch (ParseException e) {
				e.printStackTrace();
				return PARSE_ERROR;
			} catch (AlreadyAttendedException e) {
				e.printStackTrace();
				return ALREADY_ATTENDED;
			}

			if (context == null) return UNEXPECTED_ERROR;
			DBAdapter.INST.open(context, TAG);
			AttendedCourses attendedDB = new AttendedCourses(context);
			long id = attendedDB.add(courseID, new Date(date),share);
			DBAdapter.INST.close(TAG);
			if (id > 0) return OK;
			else return ALREADY_ATTENDED;
		}
	}

	private OnTaskCompletedListener<Context,Void,Integer> attendSetterListener = new OnTaskCompletedListener<Context,Void,Integer>(){

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
			case ParseReadyChecker.OK:{
				initAttendSuccessful();
				break;
			}
			case ParseReadyChecker.ALREADY_ATTENDED:{
				showError(R.string.dialog_attended_error_already_attended,R.string.dialog_attended_error_already_attended);
				break;
			}
			case ParseReadyChecker.PARSE_ERROR:{
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
}
