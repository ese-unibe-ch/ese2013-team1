package ch.unibe.sport.course.info;
import java.util.concurrent.ExecutionException;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.AddOrRemoveCourseFromFavoritesTask;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;

public class CourseInfoActivity extends ProxySherlockFragmentActivity {
	public static final String TAG = CourseInfoActivity.class.getName();

	public static final String COURSE_ID_PARAM_NAME = "courseID";
	
	private int courseID;
	private Course course;
	private boolean favoriteButtonLock;

	private CourseMainInfoCardRelative mainInfo;
	private CourseCalendarCard calendar;
	private CourseRatingCard rating;
	
	private Menu menu;
	
	public CourseInfoActivity() {
		super(TAG);
	}

	/*------------------------------------------------------------
	------------------------- M E S S A G E ----------------------
	------------------------------------------------------------*/
		
	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		if (adapter.isCourseUpdate()){
			course.update();
			if (this.menu != null)initFavoriteMenuItem(this.menu.findItem(R.id.menu_favorite),course.isFavorite());
			mainInfo.setCourse(course);
			calendar.update(course);
			rating.setCourse(course);
		}
		if (adapter.isCourseRatedUpdate()){
			try {
				int rating = adapter.getCourseRatedUpdate();
				this.rating.updateRaiting(rating);
			} catch (ParamNotFoundException e) {e.printStackTrace();}
		}
	}
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarHomeAsBack();
		this.setContentView(R.layout.course_info);
		
		courseID = initCourseID();
		if (courseID <= 0) {
			finish();
			return;
		}
		
		course = new Course(this,courseID);
		
		Loader loader = new Loader();
		loader.setOnTaskCompletedListener(new OnTaskCompletedListener<Object,Void,View>(){

			@Override
			public void onTaskCompleted(AsyncTask<Object,Void,View> task) {
				ViewGroup container = (ViewGroup)findViewById(R.id.info_container);
				if (container == null) return;
				try {
					container.addView(task.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		
		loader.execute(new Object[]{this,course});
	}
	
	private class Loader extends ObservableAsyncTask<Object,Void,View>{

		@Override
		protected View doInBackground(Object... params) {
			Context context = (Context)params[0];
			Course course = (Course)params[1];
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			mainInfo = new CourseMainInfoCardRelative(context);
			mainInfo.setCourse(course);
			mainInfo.hideFavoritesButton();
			mainInfo.hideMoreInfoButton();
			layout.addView(mainInfo);
			
			calendar = new CourseCalendarCard(context);
			calendar.setCourse(course);
			layout.addView(calendar);
			
			layout.addView(new CourseAdditionalInfoCard(context,course));
			
			rating = new CourseRatingCard(context);
			rating.setCourse(course);
			layout.addView(rating);
			return layout;
		}
	}
	
	public static void show(final Context context,final int courseID) {
		if (courseID <= 0 || context == null) return;
		Intent intent = new Intent();
		intent.setClass(context, CourseInfoActivity.class);
		intent.putExtra(COURSE_ID_PARAM_NAME, courseID);
		context.startActivity(intent);
	}

	private int initCourseID() {
		return this.getIntent().getIntExtra(COURSE_ID_PARAM_NAME,0);
	}
	
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/* finishing activity without delay causes action bar in main activity to collapse action views */
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 150);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getSupportMenuInflater().inflate(R.menu.course_menu, menu);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initFavoriteMenuItem(menu.findItem(R.id.menu_favorite),course.isFavorite());
		return true;
	}

	private void initFavoriteMenuItem(MenuItem favorite,boolean isFavorite){
		favorite.setIcon((isFavorite)?R.drawable.actionbar_icon_favorite_on:R.drawable.actionbar_icon_favorite_off);
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:{
			finish();
			return true;
		}
		case R.id.menu_favorite:{			
			if (favoriteButtonLock) return true;
			favoriteButtonLock = true;
			AddOrRemoveCourseFromFavoritesTask task = new AddOrRemoveCourseFromFavoritesTask(course.getCourseID());
			task.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Boolean>(){
				@Override
				public void onTaskCompleted(AsyncTask<Context,Void,Boolean> task) {
					favoriteButtonLock = false;
					send(MessageFactory.updateCourse(TAG,course.getCourseID()));
				}
			});
			task.execute(this);
			return true;
		}

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
