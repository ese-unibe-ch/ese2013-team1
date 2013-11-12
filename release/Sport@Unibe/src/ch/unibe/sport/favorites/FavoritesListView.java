package ch.unibe.sport.favorites;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.DBAdapter.tables.FavoriteCourses;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.info.CoursesListAdapter;
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
import android.widget.ListView;

public class FavoritesListView extends ListView implements IMainTab, IFilterable, IPointable {
	public static final String TAG = FavoritesListView.class.getName();
	
	private CoursesListAdapter listAdapter;
	private Point point;
	
	public FavoritesListView(Context context) {
		super(context);
	}
	
	public FavoritesListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FavoritesListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private OnTaskCompletedListener<Context,Void,Course[]> onLoadListener = new OnTaskCompletedListener<Context,Void,Course[]>(){
		@Override
		public void onTaskCompleted(AsyncTask<Context,Void,Course[]> task) {
			Course[] courses = new Course[0];
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
		this.setScrollingCacheEnabled(false);
		new FavoriteCoursesLoader().setOnTaskCompletedListener(onLoadListener).execute(getContext());
	}
	
	private class FavoriteCoursesLoader extends ObservableAsyncTask<Context,Void,Course[]>{
		@Override
		protected Course[] doInBackground(Context... context) {
			DBAdapter.INST.beginTransaction(getContext(),TAG);
			AttendedCourses attendedDB = new AttendedCourses(context[0]);
			FavoriteCourses favoriteDB = new FavoriteCourses(context[0]);
			
			int[] attendedIDs = attendedDB.getAttendedCoursesIDs();
			int[] favoritesIDs = favoriteDB.getFavoritesIDsWithoutAttended();
			
			Course[] courses = new Course[attendedIDs.length+favoritesIDs.length];
			
			int i = 0;
			for (int id : attendedIDs){
				courses[i] = new Course(getContext(), id);
				i++;
			}
			for (int id : favoritesIDs){
				courses[i] = new Course(getContext(), id);
				i++;
			}
			DBAdapter.INST.endTransaction(TAG);
			return courses;
		}
	}
	
	private void initAdapter(Course[] courses){
		listAdapter = new CoursesListAdapter(getContext(),courses);
		setAdapter(listAdapter);
	}

	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) listAdapter = (CoursesListAdapter) this.getAdapter();
		return listAdapter != null && listAdapter.getFilter() != null;
	}

	@Override
	public void filter(String prefix) {
		if (isFilterExists())listAdapter.getFilter().filter(prefix);
	}

	@Override
	public void initMenu(Menu menu) {
		if (menu == null) return;
		menu.clear();
		/* Initalizes search item in actionbar menu */
		new ActionBarSearchItem(this, menu, R.id.menu_search);
		new ActionBarListMenu(menu,R.id.menu_navigation_drawer);
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
}
