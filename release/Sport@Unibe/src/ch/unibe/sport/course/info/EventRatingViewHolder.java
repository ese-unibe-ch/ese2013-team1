package ch.unibe.sport.course.info;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.unibe.sport.R;

public class EventRatingViewHolder {

	protected final LinearLayout ratingContainer;
	protected final TextView averageText;
	protected final TextView countText;
	protected final ViewGroup rateContainer;
	protected final ImageView rateImage;
	protected final TextView rateText;
	

	protected final EventRatingBarLayout[] bars;
	
	public EventRatingViewHolder(EventRatingCard card) {
		ratingContainer = (LinearLayout) card.findViewById(R.id.rating_container);
        this.averageText = (TextView) card.findViewById(R.id.average_rating);
        this.countText = (TextView) card.findViewById(R.id.total_count);
		rateContainer = (ViewGroup) card.findViewById(R.id.rate_container);
		rateImage = (ImageView) card.findViewById(R.id.rate_image);
		rateText = (TextView) card.findViewById(R.id.rate_text);
		
		bars = new EventRatingBarLayout[5];
		for (int length = bars.length, i = length; i >= 1;i--){
			bars[i-1] = new EventRatingBarLayout(card.getContext(),i);
			ratingContainer.addView(bars[i-1]);
		}
	}
}
