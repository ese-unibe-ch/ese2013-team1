package ch.unibe.sport.main.favorites;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.EventAttended;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.event.info.EventsListAdapter;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.main.IMainTab;
import ch.unibe.sport.main.search.ActionBarSearchItem;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.Point;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FavoritesListView extends LinearLayout implements IMainTab, IFilterable, IPointable {
	public static final String TAG = FavoritesListView.class.getName();
	
	private EventsListAdapter listAdapter;
	private Point point;
	private ActionBarSearchItem searchAction;
	private ListView list;
	private View emptyView;
	
	public FavoritesListView(Context context) {
		super(context);
	}
	
	public FavoritesListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FavoritesListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private OnTaskCompletedListener<Context,Void,Event[]> onLoadListener = new OnTaskCompletedListener<Context,Void,Event[]>(){
		@Override
		public void onTaskCompleted(AsyncTask<Context,Void,Event[]> task) {
			Event[] courses = new Event[0];
			try {courses = task.get();} catch (Exception e) {e.printStackTrace();return;}
			initAdapter(courses);
		}
	};
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Starts initializing process of favorites list view
	 */
	public void initialize() {
		point = Point.initialize(this);
		list = new ListView(getContext());
		addView(list,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.emptyView = View.inflate(getContext(), R.layout.favorites_layout_empty_view, null);
		addView(this.emptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		list.setScrollingCacheEnabled(false);
		new FavoriteEventsLoader().setOnTaskCompletedListener(onLoadListener).execute(getContext());
	}
	
	private class FavoriteEventsLoader extends ObservableAsyncTask<Context,Void,Event[]>{
		@Override
		protected Event[] doInBackground(Context... context) {
			DBAdapter.INST.beginTransaction(getContext(),TAG);
			EventAttended attendedDB = new EventAttended(context[0]);
			EventFavorite favoriteDB = new EventFavorite(context[0]);
			
			String[] attendedIDs = attendedDB.getAttendedEventsHashs();
			String[] favoritesIDs = favoriteDB.getFavoritesIDsWithoutAttended();
			
			
			Event[] events = new Event[attendedIDs.length+favoritesIDs.length];
			int i = 0;
			for (String id : attendedIDs){
				events[i] = new Event(getContext(), id);
				i++;
			}
			for (String id : favoritesIDs){
				events[i] = new Event(getContext(), id);
				i++;
			}
			DBAdapter.INST.endTransaction(TAG);
			return events;
		}
	}
	
	private void initAdapter(Event[] events){
		listAdapter = new EventsListAdapter(getContext(),events);
		list.setAdapter(listAdapter);
		list.setEmptyView(emptyView);
	}
	
	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) listAdapter = (EventsListAdapter) list.getAdapter();
		return listAdapter != null && listAdapter.getFilter() != null;
	}

	@Override
	public void filter(String prefix) {
		if (isFilterExists())listAdapter.filter(prefix);
	}

	@Override
	public void initMenu(Menu menu) {
		if (menu == null) return;
		menu.clear();
		/* Initalizes search item in actionbar menu */
		searchAction = new ActionBarSearchItem(this.getContext(),this, menu, R.id.menu_search);
		//new ActionBarListMenu(menu,R.id.menu_navigation_drawer);
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
			default:{
				return false;
			}
		}
	}

	@Override
	public String getTitle() {
		return "Favorites";
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		/*
		 * Updating course
		 */
		if (adapter.isCourseUpdate()){
			try {
				int courseID = adapter.getCourseID();
				listAdapter.update(courseID);
			} catch (ParamNotFoundException e) {e.printStackTrace();}
		}
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

	@Override
	public void collapseActionBar() {
		if (searchAction != null)searchAction.clear();
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		// TODO Auto-generated method stub
		
	}
}
