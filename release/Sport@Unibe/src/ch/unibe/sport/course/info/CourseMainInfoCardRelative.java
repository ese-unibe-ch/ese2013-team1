package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.AddOrRemoveCourseFromFavoritesTask;
import ch.unibe.sport.course.Course;
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

public class CourseMainInfoCardRelative extends RelativeLayout implements INodable {

	public static final String TAG = CourseMainInfoCardRelative.class.getName();
	
	private CourseMainInfoViewHolder viewHolder;
	private final DirectionsListener directionsListener = new DirectionsListener();
	private final MoreListener moreListener = new MoreListener();
	private final MenuListener menuListener = new MenuListener();
	private final FavoritesListener favoritesListener = new FavoritesListener();
	
	private Node node;
	
	private boolean menuButton = true;
	
	public CourseMainInfoCardRelative(Context context) {
		super(context);
		init();
	}
	
	public CourseMainInfoCardRelative(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public CourseMainInfoCardRelative(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		node = Node.initialize(this);
        View.inflate(getContext(), R.layout.course_info_main_relative,this);
        viewHolder = new CourseMainInfoViewHolder(this);
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
	 * @param course
	 */
	public void setCourse(Course course){
		if (course == null) {
			Print.err("CourseMainInfoCard","Course is null");
			return;
		}
		/*
		 * Attend icon
		 */
		if (course.getAttended() == 0) {
			viewHolder.attended.setVisibility(GONE);
		}
		else {
			viewHolder.attended.setImageResource(course.getAttended() == 1 ? R.drawable.ic_attended_on_normal : R.drawable.ic_attended_off_normal);
			viewHolder.attended.setVisibility(VISIBLE);
		}
		/*
		 * Favorites icon
		 */
		if (course.isFavorite()){
			viewHolder.favoritesButton.setImageResource(R.drawable.actionbar_icon_favorite_on);
			viewHolder.header.setBackgroundColor(course.getBgColor());
			if (this.menuButton) viewHolder.menuButton.setVisibility(VISIBLE);
		}
		else {
			viewHolder.favoritesButton.setImageResource(R.drawable.actionbar_icon_favorite_off);
			viewHolder.header.setBackgroundColor(0);
			viewHolder.menuButton.setVisibility(GONE);
		}
		
		/*
		 * Course name
		 */
		viewHolder.courseName.setText(course.getCourse());
		/*
		 * Sport name
		 */
		viewHolder.sportName.setText(course.getSport());
		
		/*
		 * Setting time 
		 */
		if (course.getTime().length() > 0){
			viewHolder.time.setText(course.getTime());
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
		if (course.getDay().length() > 0){
			viewHolder.day.setText(course.getDay());
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
		if (course.getPlace().length() > 0){
			viewHolder.place.setText(course.getPlace());
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
		if (course.getPeriod().length() > 0){
			viewHolder.period.setText(course.getPeriod());
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
		if (course.getSubscription().length() > 0){
			viewHolder.subscription.setText(getContext().getResources().getString(R.string.course_info_subscription) + " " + course.getSubscription());
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
		directionsListener.setCourseID(course.getCourseID());
		moreListener.setCourseID(course.getCourseID());
		menuListener.setCourseID(course.getCourseID());
		favoritesListener.setCourseID(course.getCourseID());
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
		
		private void setCourseID(int courseID) {
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
		
		private int courseID;
		
		private void setCourseID(int courseID){
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			CourseInfoActivity.show(getContext(), courseID);
		}
	}
	
	private class MenuListener implements OnClickListener {

		private int courseID;
		
		private void setCourseID(int courseID){
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			ItemMenu.show(getContext(), viewHolder.fakeButton, courseID);
		}
	}
	
	
	private class FavoritesListener implements OnClickListener {

		private int courseID;
		private boolean lock = false;
		
		private void setCourseID(int courseID){
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			if (lock) return;
			lock = true;
			AddOrRemoveCourseFromFavoritesTask task = new AddOrRemoveCourseFromFavoritesTask(courseID);
			task.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Boolean>(){
				@Override
				public void onTaskCompleted(AsyncTask<Context, Void, Boolean> task) {
					node.send(MessageFactory.updateCourse(TAG, courseID));
					lock = false;
				}
			});
			task.execute(getContext());
		}
		
	}
}
