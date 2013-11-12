package ch.unibe.sport.info;

import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.Sport;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.utils.AssociativeList;
import android.content.Context;
import android.view.ViewGroup;

public class SportInfoListAdapter {

	private Sport sport;
	private AssociativeList<SportInfoEntryCard> cards;
	
	public SportInfoListAdapter(Sport sport){
		this.sport = sport;
		cards = new AssociativeList<SportInfoEntryCard>();
	}
	
	public void initialize(Context context){		
		SportInfoEntryCard tmpView = null;
		for (int i = 0,length = sport.getCoursesCount(); i<length; i++){
			tmpView = new SportInfoEntryCard(context);
			tmpView.setCourse(sport.getCourseAt(i));
			cards.add(tmpView,sport.getCourseAt(i).getCourseID());
		}
	}
	
	public void addTo(ViewGroup view){
		for (SportInfoEntryCard card : cards){
			view.addView(card);
		}
	}
	
	public void connect(IPointable point){
		for (SportInfoEntryCard card : cards){
			card.connect(point);
		}
	}
	
	public void update(int courseID){
		Course course = sport.getCourseByID(courseID);
		/* it means, that course doesn't exists in sport. It's possible and ok. */
		if (course == null) return;
		course.update();
		cards.get(courseID).setCourse(course);
	}

}
