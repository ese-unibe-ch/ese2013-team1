package ch.unibe.sport.event.info;

import ch.unibe.sport.R;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.view.View;

/**
 * 
 * @author Team 1
 *
 */
public class EventRatingCard extends AbstractInfoCard {

	public static final String TAG = EventRatingCard.class.getName();
	
	private EventRatingViewHolder holder;
	private int[] mRating = new int[5];
	
	private RateListener rateClickListener = new RateListener();
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public EventRatingCard(Context context) {
		super(context, TAG);
		init();
	}

	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	private void init() {
        View.inflate(getContext(), R.layout.course_info_rating, this);
        
        holder = new EventRatingViewHolder(this);
		     
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

	
	private void initEventRateButton(Event event){
		holder.rateContainer.setVisibility(event.getAttended() != 0 ? VISIBLE : GONE);
		holder.rateContainer.setBackgroundResource((event.isRated()) ? R.drawable.course_info_link_bg : R.drawable.course_info_link_bg_green_light);
		holder.rateContainer.setOnClickListener(rateClickListener);
		if (event.isRated()) {
			holder.rateText.setTextColor(0xffffbb33);
			String text = Utils.getString(getContext(), R.string.course_info_already_rated)+" ("+event.getRating()+")";
			holder.rateText.setText(text);
			int imageRes = Utils.getDrawableResId(getContext(), "ic_rating_"+event.getRating()+"_normal");
			holder.rateImage.setImageResource(imageRes);
		}
		else {
			holder.rateImage.setImageResource(R.drawable.ic_stars_off);
			holder.rateText.setTextColor(0xbb222222);
			holder.rateText.setText(R.string.course_info_rate);		
		}	
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
	
	public void setEvent(Event event){
		initEventRateButton(event);
		this.rateClickListener.setEventID(event.getEventID());
	}
	
	/*------------------------------------------------------------
	--------------------------- I N N E R ------------------------
	------------------------------------------------------------*/
	private class RateListener implements OnClickListener {
		
		private int eventID;
		
		private void setEventID(int courseID){
			this.eventID = courseID;
		}
		
		@Override
		public void onClick(View v) {
			EventRatingDialog.show(getContext(),eventID);
		} 
	}
}
