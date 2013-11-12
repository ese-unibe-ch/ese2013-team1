package ch.unibe.sport.course.info;

import ch.unibe.sport.R;
import ch.unibe.sport.widget.view.GravityTextView;
import ch.unibe.sport.widget.view.RightAlignImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CourseMainInfoViewHolder {
	public final RelativeLayout root;
	
	public final ImageView attended;
	public final View header;
	
	public final TextView courseName;
	public final TextView sportName;
	
	public final View fakeButton;
	public final RightAlignImageView menuButton;
	public final ImageView favoritesButton;
	
	public final TextView time;
	public final ImageView timeImage;
	public final TextView day;
	public final ImageView dayImage;
	public final TextView place;
	public final ImageView placeImage;
	public final TextView period;
	public final ImageView periodImage;
	public final TextView subscription;
	public final ImageView subscriptionImage;
	
	public final GravityTextView directions;
	public final GravityTextView more;
	public final ImageView moreImage;
	
	public CourseMainInfoViewHolder(CourseMainInfoCardRelative card){
		root = (RelativeLayout) card.findViewById(R.id.root);
		
		attended = (ImageView) card.findViewById(R.id.attended);
		header = card.findViewById(R.id.header);
		
		courseName = (TextView) card.findViewById(R.id.course);
		sportName = (TextView) card.findViewById(R.id.sport);
		
		fakeButton = card.findViewById(R.id.fake_button);
		menuButton = (RightAlignImageView)card.findViewById(R.id.menu_button);
		favoritesButton = (ImageView)card.findViewById(R.id.favorites_button);
		
		time = (TextView) card.findViewById(R.id.time);
		timeImage = (ImageView) card.findViewById(R.id.time_image);
		day = (TextView) card.findViewById(R.id.day);
		dayImage = (ImageView) card.findViewById(R.id.day_image);
		place = (TextView) card.findViewById(R.id.place);
		placeImage = (ImageView) card.findViewById(R.id.place_image);
		period = (TextView) card.findViewById(R.id.period);
		periodImage = (ImageView) card.findViewById(R.id.period_image);
		subscription = (TextView) card.findViewById(R.id.subscription);
		subscriptionImage = (ImageView) card.findViewById(R.id.subscription_image);
		
		directions = (GravityTextView) card.findViewById(R.id.directions);
		more = (GravityTextView) card.findViewById(R.id.more);
		moreImage = (ImageView) card.findViewById(R.id.more_image);
	}
}
