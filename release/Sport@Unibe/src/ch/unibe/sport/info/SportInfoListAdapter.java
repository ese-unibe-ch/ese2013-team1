package ch.unibe.sport.info;

import ch.unibe.sport.core.Event;
import ch.unibe.sport.core.Sport;
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
		SportInfoEntryCard entryCard = null;
		for (int index = 0,length = sport.getEventCount(); index<length; index++){
			entryCard = new SportInfoEntryCard(context);
			entryCard.setEvent(sport.getEventAt(index));
			cards.add(entryCard,sport.getEventAt(index).getEventID());
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
	
	public void update(Context context,int eventID){
		Event event = sport.getEvent(eventID);
		/* it means, that course doesn't exists in sport. It's possible and ok. */
		if (event == null) return;
		event.update(context);
		cards.get(eventID).setEvent(event);
	}

}
