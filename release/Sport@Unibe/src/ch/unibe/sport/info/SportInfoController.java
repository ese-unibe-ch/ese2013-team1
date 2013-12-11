package ch.unibe.sport.info;

import java.util.concurrent.ExecutionException;

import ch.unibe.sport.R;
import ch.unibe.sport.core.Sport;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.Point;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SportInfoController implements IPointable {
	public static final String TAG = SportInfoController.class.getName();
	
	private WebView description;
	private HeaderView header;
	private LinearLayout listView;
	private SportInfoListAdapter listAdapter;
	private Point point;
	private Sport sport;
	private int sportID;
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	
	public SportInfoController(SportInfoActivity activity) {
		point = Point.initialize(this);
		description = (WebView) activity.findViewById(R.id.description);
		header = (HeaderView) activity.findViewById(R.id.header);
		listView = (LinearLayout) activity.findViewById(R.id.events_container);
		initHeader(activity.getResources());
		this.sportID = initSportID(activity);
		if (sportID == 0){
			Print.err(TAG,"sportID is 0");
			return;
		}

		new SportLoader(activity).execute(activity);
	}
	
	private class SportLoader extends ObservableAsyncTask<Context, Void, Sport>{
		
		private SportInfoActivity mActivity;
		
		private SportLoader(SportInfoActivity activity){
			this.mActivity = activity;
			this.setOnTaskCompletedListener(new OnTaskCompletedListener<Context, Void, Sport>() {
				@Override
				public void onTaskCompleted(AsyncTask<Context, Void, Sport> task) {
					try {
						sport = task.get();
						mActivity.setActionBarTitle(sport.getSportName());
					} catch (InterruptedException e) {
						e.printStackTrace();
						mActivity.disableLogoSpinner();
						return;
					} catch (ExecutionException e) {
						e.printStackTrace();
						mActivity.disableLogoSpinner();
						return;
					}
					initView();
					mActivity.disableLogoSpinner();
				}
			});
		}
		
		@Override
		protected void onPreExecute(){
			mActivity.enableLogoSpinner();
		}
		
		@Override
		protected Sport doInBackground(Context... arg0) {
			return new Sport(arg0[0],sportID);
		}
	}
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	private void initView(){
		Context context = listView.getContext();
		if (context == null) return;
		initDescription();
		
		listAdapter = new SportInfoListAdapter(sport);
		listAdapter.initialize(context);
		listAdapter.connect(this);
		
		if (sport.getEventCount() == 0){
			header.setVisibility(View.GONE);
			return;
		}

		listAdapter.addTo(listView);
	}

	private void initHeader(Resources res) {
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
	
	private void initDescription(){
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><header><style type=\"text/css\">");
		// fast workaround
		String css = "a {color: #ff4444;text-decoration: none;} p{color: #222222} h1{font-family: Arial, Helvetica, SunSans-Regular, sans-serif;font-size: 93%;margin: 0px;margin-bottom: 8pt;font-weight: bold;line-height: 103%;}h2{font-family: Arial, Helvetica, SunSans-Regular, sans-serif;font-size: 86%;margin: 0px;font-weight: bold;}h3{font-family: Arial, Helvetica, SunSans-Regular, sans-serif;font-size: 76%;margin: 0px;font-weight: bold;}h4{font-size: 69%;font-weight: normal;margin: 0px;}ul{font-size: 76%;margin-left: 0px;line-height: 120%;list-style-type: disc;margin-top: 5px;margin-bottom: 10px;padding: 0px 5px 0px 25px}ul ul{ font-size: 100%;}ul ul ul{ list-style-type: none;}p {margin-top: 0px;padding: 0px 3px 5px 5px;margin-bottom: 5px;font-size: 76%;line-height: 130%;}.linkliste{text-indent: 0px;text-decoration: none;} li.linkliste{color: #FF0033;} li.linkliste a {color:#222222;text-decoration: underline;} table {width:100%;margin-top: 5px;margin-bottom: 10px;rules:none;}td {vertical-align:top;font-size: 76%;line-height: 110%;}th{font-size: 76%;text-align: left;}table ul{font-size: 100%;}.unirot{color: #ff8800;}.lead{color: #666666;font-weight: bold;}";
		html.append(css);
		html.append("</style></header><body>");
		html.append(sport.getDescriptionHeader());
		html.append("</body></html>");
		description.loadDataWithBaseURL(null, html.toString(), "text/html", "utf-8", null);
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
				listAdapter.update(listView.getContext(),adapter.getCourseID());
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
