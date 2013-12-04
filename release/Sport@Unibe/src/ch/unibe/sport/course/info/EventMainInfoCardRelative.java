package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.AddOrRemoveEventFromFavoritesTask;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.network.INodable;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.Node;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Print;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class EventMainInfoCardRelative extends RelativeLayout implements INodable {

	public static final String TAG = EventMainInfoCardRelative.class.getName();
	
	private EventMainInfoViewHolder viewHolder;
	private final DirectionsListener directionsListener = new DirectionsListener();
	private final MoreListener moreListener = new MoreListener();
	private final MenuListener menuListener = new MenuListener();
	private final FavoritesListener favoritesListener = new FavoritesListener();
	
	private Node node;
	
	private boolean menuButton = true;
	
	public EventMainInfoCardRelative(Context context) {
		super(context);
		init();
	}
	
	public EventMainInfoCardRelative(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public EventMainInfoCardRelative(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		node = Node.initialize(this);
        View.inflate(getContext(), R.layout.course_info_main_relative,this);
        viewHolder = new EventMainInfoViewHolder(this);
        viewHolder.directions.setOnClickListener(directionsListener);
        viewHolder.more.setOnClickListener(moreListener);
        viewHolder.menuButton.setOnClickListener(menuListener);
        viewHolder.favoritesButton.setOnClickListener(favoritesListener);
	}

	@Override
	public void process(Message message) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Updates card according to course data
	 * @param event
	 */
	public void setEvent(Event event){
		if (event == null) {
			Print.err("CourseMainInfoCard","Course is null");
			return;
		}
		/*
		 * Attend icon
		 */
		if (event.getAttended() == 0) {
			viewHolder.attended.setVisibility(GONE);
		}
		else {
			viewHolder.attended.setImageResource(event.getAttended() == 1 ? R.drawable.ic_attended_on_normal : R.drawable.ic_attended_off_normal);
			viewHolder.attended.setVisibility(VISIBLE);
		}
		/*
		 * Favorites icon
		 */
		if (event.isFavorite()){
			viewHolder.favoritesButton.setImageResource(R.drawable.actionbar_icon_favorite_on);
			viewHolder.header.setBackgroundColor(event.getBackground());
			if (this.menuButton) viewHolder.menuButton.setVisibility(VISIBLE);
		}
		else {
			viewHolder.favoritesButton.setImageResource(R.drawable.actionbar_icon_favorite_off);
			viewHolder.header.setBackgroundColor(0);
			viewHolder.menuButton.setVisibility(GONE);
		}
		
		/*
		 * Event name
		 */
		viewHolder.courseName.setText(event.getEventName());
		/*
		 * Sport name
		 */
		viewHolder.sportName.setText(event.getSportName());
		
		/*
		 * Setting time 
		 */
		if (event.getTimeString().length() > 0){
			viewHolder.time.setText(event.getTimeString());
			viewHolder.time.setVisibility(VISIBLE);
			viewHolder.timeImage.setVisibility(VISIBLE);
		}
		else {
			viewHolder.time.setVisibility(GONE);
			viewHolder.timeImage.setVisibility(GONE);
		}
		/*
		 * Setting day
		 */
		String days = event.getDaysOfWeekString();
		if (days.length() > 0){
			viewHolder.day.setText(days);
			viewHolder.day.setVisibility(VISIBLE);
			viewHolder.dayImage.setVisibility(VISIBLE);
		}
		else {
			viewHolder.day.setVisibility(GONE);
			viewHolder.dayImage.setVisibility(GONE);
		}
		/*
		 * Setting place
		 */
		if (event.getPlace().length() > 0){
			viewHolder.place.setText(event.getPlace());
			viewHolder.place.setVisibility(VISIBLE);
			viewHolder.placeImage.setVisibility(VISIBLE);
		}
		else {
			viewHolder.place.setVisibility(GONE);
			viewHolder.placeImage.setVisibility(GONE);
		}
		
		/*
		 * Setting period
		 */
		String period = event.getPeriodString();
		if (period.length() > 0){
			viewHolder.period.setText(period);
			viewHolder.period.setVisibility(VISIBLE);
			viewHolder.periodImage.setVisibility(VISIBLE);
		}
		else {
			viewHolder.period.setVisibility(GONE);
			viewHolder.periodImage.setVisibility(GONE);
		}
		
		/* 
		 * Setting subscription
		 */
		if (event.getRegistration().length() > 0){
			viewHolder.subscription.setText(getContext().getResources().getString(R.string.course_info_subscription) + " " + event.getRegistration());
			viewHolder.subscription.setVisibility(VISIBLE);
			viewHolder.subscriptionImage.setVisibility(VISIBLE);
		}
		else {
			viewHolder.subscription.setVisibility(GONE);
			viewHolder.subscriptionImage.setVisibility(GONE);
		}
		
		/*
		 * Updating click listeners
		 */
		directionsListener.setEventID(event.getEventID());
		moreListener.setEventID(event.getEventID());
		menuListener.setEventID(event.getEventID());
		menuListener.setEventHash(event.getEventHash());
		favoritesListener.setEventID(event.getEventID());
		favoritesListener.setEventHash(event.getEventHash());
	}
	
	/**
	 * Displays favorites button
	 */
	public void showFavoritesButton(){
		viewHolder.favoritesButton.setVisibility(VISIBLE);
	}
	
	/**
	 * Hides favorites button
	 */
	public void hideFavoritesButton(){
		viewHolder.favoritesButton.setVisibility(GONE);
	}
	
	/**
	 * Displays more info button
	 */
	public void showMoreInfoBitton(){
		viewHolder.more.setVisibility(VISIBLE);
		viewHolder.moreImage.setVisibility(VISIBLE);
	}
	/**
	 * Hides more info button
	 */
	public void hideMoreInfoButton(){
		viewHolder.more.setVisibility(GONE);
		viewHolder.moreImage.setVisibility(GONE);
	}
	
	/**
	 * Displays context menu button
	 */
	public void showMenuButton(){
		viewHolder.menuButton.setVisibility(VISIBLE);
		menuButton = true;
	}
	
	/**
	 * Hides context menu button
	 */
	public void hideMenuButton(){
		viewHolder.menuButton.setVisibility(GONE);
		menuButton = false;
	}
	
	/**
	 * Hides header
	 */
	public void hideHeader(){
		viewHolder.header.setVisibility(GONE);
	}
	
	/**
	 * Changes background resourse
	 */
	@Override
	public void setBackgroundResource(int resId){
		viewHolder.root.setBackgroundResource(resId);
	}
	
	/**
	 * Changes top margin of the card
	 * @param margin
	 */
	public void setMarginTop(int margin){
		LayoutParams lp = (LayoutParams) viewHolder.root.getLayoutParams();
		lp.topMargin = 0;
		viewHolder.root.setLayoutParams(lp);
	}
	
	/**
	 * Changes bottom margin of the card
	 * @param margin
	 */
	public void setMarginBottom(int margin){
		LayoutParams lp = (LayoutParams) viewHolder.root.getLayoutParams();
		lp.bottomMargin = 0;
		viewHolder.root.setLayoutParams(lp);
	}
	
	@Override
	public String tag() {
		return TAG;
	}
	
	@Override
	public void connect(IPointable point) {
		node.connect(point);
	}
	
	/**
	 * OnCLickListener implimentation, that defines action after user
	 * clicks "Show on map"
	 * 
	 * @author Team 1 2013
	 */
	private class DirectionsListener implements OnClickListener {
		
		private int courseID;
		
		private void setEventID(int courseID) {
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			MapDialog.show(getContext(), courseID);
		}
	}
	
	/**
	 * OnCLickListener implimentation, that defines action after user
	 * clicks "More info"
	 * @author Team 1 2013
	 */
	private class MoreListener implements OnClickListener {
		
		private int eventID;
		
		private void setEventID(int eventID){
			this.eventID = eventID;
		}
		
		@Override
		public void onClick(View v) {
			EventInfoActivity.show(getContext(), eventID);
		}
	}
	
	private class MenuListener implements OnClickListener {

		private String eventHash;
		private int eventID;
		
		private void setEventID(int eventID){
			this.eventID = eventID;
		}
		
		private void setEventHash(String eventHash){
			this.eventHash = eventHash;
		}
		
		@Override
		public void onClick(View v) {
			ItemMenu.show(getContext(), viewHolder.fakeButton, eventID,eventHash);
		}
	}
	
	
	private class FavoritesListener implements OnClickListener {

		private String eventHash;
		private int eventID;
		private boolean lock = false;
		
		private void setEventID(int eventID){
			this.eventID = eventID;
		}
		
		private void setEventHash(String eventHash){
			this.eventHash = eventHash;
		}
		
		@Override
		public void onClick(View v) {
			if (lock) return;
			lock = true;
			AddOrRemoveEventFromFavoritesTask task = new AddOrRemoveEventFromFavoritesTask(eventHash);
			task.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Boolean>(){
				@Override
				public void onTaskCompleted(AsyncTask<Context, Void, Boolean> task) {
					node.send(MessageFactory.updateCourse(TAG, eventID));
					lock = false;
				}
			});
			task.execute(getContext());
		}
		
	}
}
