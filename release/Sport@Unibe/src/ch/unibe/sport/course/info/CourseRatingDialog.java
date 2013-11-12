package ch.unibe.sport.course.info;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.parse.ParseException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.AlreadyRatedException;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.DBParse;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.DBAdapter.tables.Courses;
import ch.unibe.sport.DBAdapter.tables.Rating;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class CourseRatingDialog extends BaseDialog {

	public static final String TAG = CourseRatingDialog.class.getName();
	public static final String COURSE_ID = "courseID";

	private static final String STAR_PREFIX = "star";

	private ArrayList<ToggleButton> stars;
	private ToggleButton checkedStar;

	private boolean firstStart = true;

	private int courseID;
	private String courseHash;
	private int rating;

	private OnClickListener rateListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (checkedStar == null) {
				Print.toast(getContext(), "Please select rating");
				return;
			}
			disableStars();
			initReadyChecker();
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
	
	public static void show(Context context, int courseID){	
		Intent intent = new Intent(context, CourseRatingDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		intent.putExtra(COURSE_ID, courseID);
		context.startActivity(intent);
	}

	public CourseRatingDialog() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCourseHash();
		if (courseID <= 0) {
			finish();
			return;
		}
		stars = new ArrayList<ToggleButton>();
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initCourseHash(){
		this.courseID = this.getIntent().getIntExtra(COURSE_ID, 0);
		DBAdapter.INST.open(getContext(), TAG);
		Courses coursesDB = new Courses(getContext());
		this.courseHash = coursesDB.getData(courseID)[Courses.HASH];
		DBAdapter.INST.close(TAG);
	}

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
		ReadyChecker readyChecker = new ReadyChecker(courseID);
		readyChecker.setOnTaskCompletedListener(readyCheckerListener);
		readyChecker.execute(getContext());
	}
	
	protected void initParseCheck() {
		ParseReadyChecker parseReadyChecker = new ParseReadyChecker(courseHash);
		parseReadyChecker.setOnTaskCompletedListener(parseReadyCheckerListener);
		parseReadyChecker.execute(getContext());
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
		send(MessageFactory.updateRatedFromRatingDialog(courseID, rating));
	}
	/*------------------------------------------------------------
	----------- L O C A L   R E A D Y   C H E C K E R ------------
	------------------------------------------------------------*/
	private static class ReadyChecker extends ObservableAsyncTask<Context,Void,Integer> {
		private static final String TAG = ReadyChecker.class.getName();

		private static final int OK = 0;
		private static final int NO_CONNECTION = 1;
		private static final int NOT_ATTENDED = 2;
		private static final int ALREADY_RATED = 3;

		private int courseID;
		private ReadyChecker(int courseID){
			this.courseID = courseID;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			if (!Utils.haveNetworkConnection(context)) return NO_CONNECTION;
			DBAdapter.INST.open(context, TAG);
			AttendedCourses attendedDB = new AttendedCourses(context);
			Rating ratingDB = new Rating(context);
			int attended = attendedDB.isAttended(courseID);
			boolean rated = ratingDB.isRated(courseID);
			DBAdapter.INST.close(TAG);
			if (attended == 0) return NOT_ATTENDED;
			if (rated) return ALREADY_RATED;
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
				else if (stars != null && checkedStar != null) {
					rating = stars.indexOf(checkedStar)+1;
					rate(rating);
				}
				break;
			}
			case ReadyChecker.ALREADY_RATED:{
				showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
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
	----------- P A R S E   R E A D Y   C H E C K E R ------------
	------------------------------------------------------------*/
	private static class ParseReadyChecker extends ObservableAsyncTask<Context,Void,Integer>{
		private static final int OK = 0;
		private static final int ALREADY_RATED = 1;
		private static final int PARSE_ERROR = 2;
		private static final int NOT_SYNCHRONIZED = 3;

		private String courseHash;
		private ParseReadyChecker(String courseHash){
			this.courseHash = courseHash;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			boolean rated = false;
			try {
				rated = DBParse.isCourseRated(ctx[0],courseHash);
			} catch (ParseException e) {
				e.printStackTrace();
				return PARSE_ERROR;
			}
			if (rated) return ALREADY_RATED;

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
				if (firstStart)initReadyToRate();
				break;
			}
			case ParseReadyChecker.ALREADY_RATED:{
				showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
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
	------------------- R A T E   M E T H O D S ------------------
	------------------------------------------------------------*/
	/**
	 * Actially rates course
	 * @param rating
	 */
	protected void rate(int rating) {
		if (rating < 1 || rating > 5) {
			Print.err(TAG,"Wrong rating number, should be [1,5]");
			return;
		}
		this.disableOkButton();
		this.disallowFinish();
		RateSetter rateSetter = new RateSetter(courseID,courseHash,rating);
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
		private static final int PARSE_ERROR = 2;
		private static final int UNEXPECTED_ERROR = 3;

		private int courseID;
		private String courseHash;
		private int rating;

		private RateSetter(int courseID, String courseHash, int rating){
			this.courseID = courseID;
			this.courseHash = courseHash;
			this.rating = rating;
		}

		@Override
		protected Integer doInBackground(Context... ctx) {
			Context context = ctx[0];
			try {
				DBParse.rateCourse(context,courseHash, rating);
			} catch (ParseException e) {
				e.printStackTrace();
				return PARSE_ERROR;
			} catch (AlreadyRatedException e) {
				e.printStackTrace();
				return ALREADY_RATED;
			}

			if (context == null) return UNEXPECTED_ERROR;
			DBAdapter.INST.open(context, TAG);
			Rating ratingDB = new Rating(context);
			long id = ratingDB.add(courseID, rating);
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
			case ParseReadyChecker.OK:{
				initRateSuccessful();
				break;
			}
			case ParseReadyChecker.ALREADY_RATED:{
				showError(R.string.dialog_raiting_error_already_rated,R.string.dialog_raiting_error_already_rated_details);
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
