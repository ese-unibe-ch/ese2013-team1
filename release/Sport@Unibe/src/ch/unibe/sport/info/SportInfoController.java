package ch.unibe.sport.info;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.CourseInfoLoaderTask;
import ch.unibe.sport.course.Sport;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.Point;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SportInfoController implements IPointable {
	public static final String TAG = SportInfoController.class.getName();
	
	private HeaderView header;
	private LinearLayout listView;
	private SportInfoListAdapter listAdapter;
	private Point point;
	private Sport sport;
	private int sportID;

	private ProgressBar progressCircle;
	
	/**
	 * Listner that will be informed when sport info will be loaded from internet
	 */
	private OnTaskCompletedListener<Void, Integer, Boolean> infoLoaderListener = new OnTaskCompletedListener<Void, Integer, Boolean>(){
		@Override
		public void onTaskCompleted(AsyncTask<Void, Integer, Boolean> task) {
			/* if activity was closed while task was running */
			if (listView.getContext() == null) return;
			sport = new Sport(listView.getContext(),sportID);
			listView.removeView(progressCircle);
			initView();
		}
	};
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	
	public SportInfoController(Activity activity) {
		point = Point.initialize(this);
		header = (HeaderView) activity.findViewById(R.id.header);
		listView = (LinearLayout) activity.findViewById(R.id.list);
		this.sportID = initSportID(activity);
		if (sportID == 0){
			Print.err(TAG,"sportID is 0");
			return;
		}

		sport = new Sport(activity,this.sportID);
		if (sport.getCoursesCount() > 0){
			initView();
		}
		else {
			initLoaderTask(sportID);
		}
	}
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	/**
	 * Initializes and launches sport info loader task.
	 * 
	 * @param sportID
	 */
	private void initLoaderTask(final int sportID) {
		progressCircle = new ProgressBar(listView.getContext(), null,android.R.attr.progressBarStyle);
		listView.addView(progressCircle);
		CourseInfoLoaderTask infoLoader = new CourseInfoLoaderTask(listView.getContext(),new int[] { sportID });
		infoLoader.setOnTaskCompletedListener(infoLoaderListener);
		infoLoader.execute();
	}
	
	private void initView(){
		Context context = listView.getContext();
		Resources res = context.getResources();
		listAdapter = new SportInfoListAdapter(sport);
		listAdapter.initialize(context);
		listAdapter.addTo(listView);
		listAdapter.connect(this);
		
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		initHeaderText(header.holder.course,res.getString(R.string.sport_info_course));
		initHeaderText(header.holder.day,res.getString(R.string.sport_info_day));
		initHeaderText(header.holder.time,res.getString(R.string.sport_info_time));
		initHeaderText(header.holder.place,res.getString(R.string.sport_info_place));
	}
	
	private void initHeaderText(TextView view, String text){
		view.setText(text);
		view.setTypeface(null,Typeface.BOLD);
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
	}
	
	/**
	 * Loads sportID from arguments, that were passed to activity
	 * @return - sportID
	 */
	private int initSportID(Activity activity) {
		return activity.getIntent().getIntExtra(SportInfoActivity.SPORT_ID_PARAM_NAME,0);
	}
	
	@Override
	public void process(Message message) {
		message.printTrace();
		MessageAdapter adapter = new MessageAdapter(message);
		/*
		 * Updating course
		 */
		if (adapter.isCourseUpdate()){
			try {
				listAdapter.update(adapter.getCourseID());
			} catch (ParamNotFoundException e) {e.printStackTrace();}
		}
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public void send(Message message) {
		this.point.send(message);
	}

	@Override
	public void connect(IProxyable proxy) {
		this.point.connect(proxy);
	}

	@Override
	public IPoint getPoint() {
		return point;
	}
}
