package ch.unibe.sport.course.info;

import java.util.concurrent.ExecutionException;

import com.parse.ParseException;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.DBParse;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

public class CourseRatingCard extends AbstractInfoCard {

	public static final String TAG = CourseRatingCard.class.getName();
	
	private CourseRatingViewHolder holder;
	private int[] mRating = new int[5];
	
	private RateListener rateClickListener = new RateListener();
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public CourseRatingCard(Context context) {
		super(context, TAG);
		init();
	}

	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	private void init() {
        View.inflate(getContext(), R.layout.course_info_rating, this);
        
        holder = new CourseRatingViewHolder(this);
		     
		initAverage(0);
		initCount(0);
	}
	
	/**
	 * Initializes rating
	 * @param rating
	 */
	private void initRating(int[] rating){
		this.mRating = rating;
		int count = 0;
		int sum = 0;
		int average = 0;
		for (int i = 0,length = rating.length; i < length; i++){
			count += rating[i];
			sum += rating[i]*(i+1);
		}
		
		average = (int)((double)sum/count);
		
		initAverage(average);
		initCount(count);
		
		for (int i = 5; i >= 1; i--){
			holder.bars[i-1].setRatingCount(rating[i-1]);
			holder.bars[i-1].setPercent((int)((100*(double)rating[i-1])/count));
		}
	}
	
	private void initAverage(int average){
		holder.averageText.setText(""+average);
	}
	
	private void initCount(int count){
		holder.countText.setText(""+count);
	}

	
	private void initCourseRateButton(Course course){
		holder.rateContainer.setVisibility(course.getAttended() != 0 ? VISIBLE : GONE);
		holder.rateContainer.setBackgroundResource((course.isRated()) ? R.drawable.course_info_link_bg : R.drawable.course_info_link_bg_green_light);
		holder.rateContainer.setOnClickListener(rateClickListener);
		if (course.isRated()) {
			holder.rateText.setTextColor(0xffffbb33);
			String text = Utils.getString(getContext(), R.string.course_info_already_rated)+" ("+course.getRating()+")";
			holder.rateText.setText(text);
			int imageRes = Utils.getDrawableResId(getContext(), "ic_rating_"+course.getRating()+"_normal");
			holder.rateImage.setImageResource(imageRes);
		}
		else {
			holder.rateImage.setImageResource(R.drawable.ic_stars_off);
			holder.rateText.setTextColor(0xbb222222);
			holder.rateText.setText(R.string.course_info_rate);		
		}	
	}
	
	
	private void initRatingLoader(Course course){
		RatingLoader loader = new RatingLoader();
		loader.setOnTaskCompletedListener(new OnTaskCompletedListener<Object,Void,Integer[]>(){

			@Override
			public void onTaskCompleted(AsyncTask<Object,Void,Integer[]> task) {
				Integer[] rating = null;
				try {
					rating = task.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if (rating != null && getContext() != null)
					setRating(Utils.toInt(rating));
			}

		});
		loader.execute(getContext(),course.getCourseHash());
	}
	
	/*------------------------------------------------------------
	-------------------------- P U B L I C -----------------------
	------------------------------------------------------------*/
	
	public void setRating(int[] rating) {
		initRating(rating);
	}
	
	public void updateRaiting(int rate){
		this.mRating[rate-1]++;
		initRating(mRating);
	}
	
	public void setCourse(Course course){
		initCourseRateButton(course);
		initRatingLoader(course);
		this.rateClickListener.setCourseID(course.getCourseID());
	}
	
	/*------------------------------------------------------------
	--------------------------- I N N E R ------------------------
	------------------------------------------------------------*/
	private class RateListener implements OnClickListener {
		
		private int courseID;
		
		private void setCourseID(int courseID){
			this.courseID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			CourseRatingDialog.show(getContext(),courseID);
		} 
	}
	
	private class RatingLoader extends ObservableAsyncTask<Object,Void,Integer[]>{

		@Override
		protected Integer[] doInBackground(Object... param) {
			Context context = (Context) param[0];
			String courseHash = (String)param[1];
			int[] rating = null;
			try {
				rating = DBParse.getRating(context,courseHash);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
			return Utils.toInteger(rating);
		}

	}
}
