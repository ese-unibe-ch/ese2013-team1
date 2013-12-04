package ch.unibe.sport.course.info;
import java.util.concurrent.ExecutionException;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.RatingList;
import ch.unibe.sport.DBAdapter.restApi.RatingSpringRequest;
import ch.unibe.sport.DBAdapter.tasks.AddOrRemoveEventFromFavoritesTask;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;

public class EventInfoActivity extends ProxySherlockFragmentActivity {
	public static final String TAG = EventInfoActivity.class.getName();

	public static final String EVENT_ID_PARAM_NAME = "eventID";
	private static final String JSON_RATING_KEY = "json_rating_key";
	
	private int eventID;
	private Event event;
	private boolean favoriteButtonLock;

	private EventMainInfoCardRelative mainInfo;
	private EventCalendarCard calendar;
	private EventRatingCard ratingCard;
	
	private Menu menu;
	
	public EventInfoActivity() {
		super(TAG);
	}
	
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
    private RatingSpringRequest ratingSpringRequest;

	/*------------------------------------------------------------
	------------------------- M E S S A G E ----------------------
	------------------------------------------------------------*/
		
	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		if (adapter.isCourseUpdate()){
			event.update(this);
			if (this.menu != null)initFavoriteMenuItem(this.menu.findItem(R.id.menu_favorite),event.isFavorite());
			mainInfo.setEvent(event);
			calendar.update(event);
			ratingCard.setEvent(event);
		}
		if (adapter.isCourseRatedUpdate()){
			try {
				int rating = adapter.getCourseRatedUpdate();
				this.ratingCard.updateRaiting(rating);
			} catch (ParamNotFoundException e) {e.printStackTrace();}
		}
	}
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setActionBarHomeAsBack();
		this.setContentView(R.layout.course_info);
		
		eventID = initEventID();
		if (eventID <= 0) {
			finish();
			return;
		}
		
		event = new Event(this,eventID);		

		ratingSpringRequest = new RatingSpringRequest(event.getEventHash());
        spiceManager.execute(ratingSpringRequest, JSON_RATING_KEY, DurationInMillis.ALWAYS_EXPIRED, new RatingRequestListener());
		
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
		
		loader.execute(new Object[]{this,event});
	}
	
	private class Loader extends ObservableAsyncTask<Object,Void,View>{

		@Override
		protected void onPreExecute(){
			EventInfoActivity.this.enableLogoSpinner();
		}
		
		@Override
		protected View doInBackground(Object... params) {
			Context context = (Context)params[0];
			Event event = (Event)params[1];
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			mainInfo = new EventMainInfoCardRelative(context);
			mainInfo.setEvent(event);
			mainInfo.hideFavoritesButton();
			mainInfo.hideMoreInfoButton();
			layout.addView(mainInfo);
			
			calendar = new EventCalendarCard(context);
			calendar.setEvent(event);
			layout.addView(calendar);
			
			layout.addView(new EventAdditionalInfoCard(context,event));
			
			ratingCard = new EventRatingCard(context);
			ratingCard.setEvent(event);
			layout.addView(ratingCard);
			return layout;
		}
	}
	
	public static void show(final Context context,final int eventID) {
		if (eventID <= 0 || context == null) return;
		Intent intent = new Intent();
		intent.setClass(context, EventInfoActivity.class);
		intent.putExtra(EVENT_ID_PARAM_NAME, eventID);
		context.startActivity(intent);
	}

	private int initEventID() {
		return this.getIntent().getIntExtra(EVENT_ID_PARAM_NAME,0);
	}
	
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	@Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        spiceManager.addListenerIfPending(RatingList.class, JSON_RATING_KEY, new RatingRequestListener());
        spiceManager.getFromCache(RatingList.class, JSON_RATING_KEY, DurationInMillis.ALWAYS_RETURNED, new RatingRequestListener());
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
    
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
		initFavoriteMenuItem(menu.findItem(R.id.menu_favorite),event.isFavorite());
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
			AddOrRemoveEventFromFavoritesTask task = new AddOrRemoveEventFromFavoritesTask(event.getEventHash());
			task.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Boolean>(){
				@Override
				public void onTaskCompleted(AsyncTask<Context,Void,Boolean> task) {
					favoriteButtonLock = false;
					send(MessageFactory.updateCourse(TAG,event.getEventID()));
				}
			});
			task.execute(this);
			return true;
		}

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class RatingRequestListener implements RequestListener<RatingList>, RequestProgressListener {

		@Override
		public void onRequestFailure(SpiceException e) {
			if (!(e instanceof RequestCancelledException)) {
				Toast.makeText(EventInfoActivity.this, "Failed to load rating data.", Toast.LENGTH_SHORT).show();
			}
			EventInfoActivity.this.disableLogoSpinner();
		}

		@Override
		public void onRequestSuccess(RatingList ratingList) {

			if (ratingList == null) {
				return;
			}

			int[] rating = ratingList.getRating();

			if (rating != null && ratingCard != null){
				ratingCard.setRating(rating);
			}
			Print.p(rating);
			EventInfoActivity.this.disableLogoSpinner();
		}

		@Override
		public void onRequestProgressUpdate(RequestProgress progress) {
			String status = progress.toString();
			Print.log(status);
			//textViewProgress.setText(status);
		}
	}
}
