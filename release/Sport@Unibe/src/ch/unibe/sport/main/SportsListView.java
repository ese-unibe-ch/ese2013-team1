package ch.unibe.sport.main;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.DBAdapter.tasks.SportsListLoaderTask;
import ch.unibe.sport.info.SportInfoActivity;
import ch.unibe.sport.main.search.ActionBarSearchItem;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.Point;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SportsListView extends ListView  implements OnRefreshListener, IFilterable, IMainTab, IPointable {
	public static final String TAG = SportsListView.class.getName();
	public static final String URL = "http://scg.unibe.ch/ese/unisport/sports.php";

	private Sports sportsDB;
	private String[][] sportsData;
	private SportsListAdapter listAdapter;
	
	private Point point;
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			showCourseInfo(listAdapter.getIndex(position));
		}
	};
	
	private OnTaskCompletedListener<Void,Void,Boolean> onLoadCompleted = new OnTaskCompletedListener<Void,Void,Boolean>(){
		@Override
		public void onTaskCompleted(AsyncTask<Void,Void,Boolean> task) {
			try {
				if (!task.get()){
					Print.toast(getContext(), "Check your connection");
					initEmptyView("Check your connection");
					return;
				}
			} catch (Exception e) {initEmptyView("Check your connection");return;}
			sportsData = sportsDB.getData();
			if (sportsData.length == 0){
				Print.toast(getContext(), "No sports found");
				initEmptyView("No sports found");
				return;
			}
			initView();
		}
	};
	
	public SportsListView(Context context) {
		super(context);
	}
	
	public SportsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SportsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Starts initializing process of sorts list
	 */
	public void initialize(){
		this.point = Point.initialize(this);
		sportsDB = new Sports(getContext());
		sportsData = sportsDB.getData();
		
		if (sportsData.length == 0){
			SportsListLoaderTask sportsLoader = new SportsListLoaderTask(this.getContext(),URL);
			sportsLoader.setOnTaskCompletedListener(onLoadCompleted);
			sportsLoader.execute();
		}
		else initView();
	}
	
	/**
	 * Initializes list adapter from sports data
	 */
	private void initView() {
		listAdapter = new SportsListAdapter(getContext(),sportsData);
		setAdapter(listAdapter);
		this.setOnItemClickListener(itemClickListener);
	}
	
	private void initEmptyView(String msg){
		setAdapter(null);
		TextView empty = new TextView(getContext());
		empty.setText(msg);
		this.setEmptyView(empty);
	}

	private void showCourseInfo(int index) {
		final int courseID = Utils.Int(sportsData[index][Sports.SID]);
		SportInfoActivity.show(getContext(),courseID);
	}
	
	@Override
	public void initMenu(Menu menu) {
		if (menu == null) return;
		menu.clear();
		/* Initalizes search item in actionbar menu */
		new ActionBarSearchItem(this, menu, R.id.menu_search);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_search: {
				ActionBarSearchItem.onItemSelected(this,item);
				return true;
			}
			default: {
				return false;
			}
		}
	}

	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) {
			listAdapter = (SportsListAdapter) this.getAdapter();
		}
		return listAdapter != null && listAdapter.getFilter() != null;
	}

	@Override
	public void filter(String prefix) {
		if (isFilterExists())listAdapter.getFilter().filter(prefix);
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO pull to refresh
	}

	@Override
	public String getTitle() {
		return "Sports List";
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public void process(Message message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void send(Message message) {
		point.send(message);
	}

	@Override
	public void connect(IProxyable proxy) {
		point.connect(proxy);
	}

	@Override
	public IPoint getPoint() {
		return point;
	}
	

}
