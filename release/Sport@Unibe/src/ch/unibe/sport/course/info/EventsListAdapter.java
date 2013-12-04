package ch.unibe.sport.course.info;

import ch.unibe.sport.core.Event;
import ch.unibe.sport.utils.AssociativeList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class EventsListAdapter extends BaseAdapter implements Filterable {
	public static final String TAG = EventsListAdapter.class.getName();
	private volatile Context context;
	
	private final Object lock = new Object();
	
	private AssociativeList<Event> events;
	private AssociativeList<Event> filteredEvents;
	
	private EventListFilter eventFilter;
	
	public interface OnListIsEmptyListener {
		public void OnListIsEmpty();
	}
	
	private EventMainInfoCardRelative tmpView;
	
	public EventsListAdapter(Context context, Event[] events){
		this.context = context;
		this.events = new AssociativeList<Event>();
		for (Event event : events){
			this.events.add(event,event.getEventID());
		}
		this.filteredEvents = this.events.copy();
	}
			
	/**
	 * Updates Event refreshes list view
	 * @param eventID
	 */
	public void update(int eventID) {
		/*
		 * Event is already favorite or attended
		 */
		if (this.events.containsKey(eventID)){
			Event event = this.events.get(eventID);
			event.update(context);
			/*
			 * Checking if it's neccessary to remove event from list
			 */
			if (!event.isFavorite() && event.getAttended() == 0){
				this.events.remove(eventID);
				this.filteredEvents.remove(eventID);
			}
		}
		/*
		 * Event wasn't favorite and attended
		 */
		else {
			/*
			 * Checking if we need to add event int the list
			 */
			Event event = new Event(context,eventID);
			if (event.isFavorite() || event.getAttended() != 0){
				this.events.add(event, eventID);
				this.filteredEvents.add(event, eventID);
			}
		}
		notifyDataSetChanged();
	}
		
	@Override
	public int getCount() {
		return this.filteredEvents.getSize();
	}

	@Override
	public Object getItem(int position) {
		return this.filteredEvents.getAt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		tmpView = (EventMainInfoCardRelative)convertView;
		if (tmpView == null) {
			tmpView = new EventMainInfoCardRelative(context);
			tmpView.hideFavoritesButton();
	    }
		tmpView.setEvent(this.filteredEvents.getAt(position));
		return tmpView;
	}

	@Override
	public boolean areAllItemsEnabled() {
	    return false;
	}

	@Override
	public boolean isEnabled(int position) {
	  return false;
	}

	@Override
	public Filter getFilter() {
		if (eventFilter == null) {
			eventFilter = new EventListFilter();
		}
		return eventFilter;
	}
	
	/**
	 * Custom filter, that allows to filter favorite Events
	 * by all it's data (name, place, time, period, favorite)
	 * @author Team 1
	 */
	@SuppressLint("DefaultLocale")
	private class EventListFilter extends Filter {
		
		@Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
            	AssociativeList<Event> newEventsList;
            	/* synchronizing threads */
                synchronized (lock) {
                	newEventsList = events.copy();
                }
                results.values = newEventsList;
                results.count = newEventsList.getSize();
            }
            else {
                String prefixString = prefix.toString().toLowerCase();

                AssociativeList<Event> originalEventsList;
                synchronized (lock) {
                    originalEventsList = events.copy();
                }
                
                final AssociativeList<Event> newValues = new AssociativeList<Event>();

                for (final Event event : originalEventsList) {
                    final String eventText = event.toString().toLowerCase();

                    /*
                     * simple situation, when String representation of Event
                     * starts with entered prefix
                     */
                    if (eventText.startsWith(prefixString)) {
                        newValues.add(event,event.getEventID());
                    }
                    else {
                    	/*
                    	 * more complex situation, it's needed to look through
                    	 * all Event values, that should be divided by space
                    	 */
                        final String[] words = eventText.split(" ");
                        final int wordCount = words.length;

                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(event,event.getEventID());
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.getSize();
            }
            return results;
        }

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,FilterResults results) {
			AssociativeList<Event> newEventsList = null;
		    if (results.count == 0){
		    	newEventsList = new AssociativeList<Event>();
		    }
		    else {
		    	newEventsList = (AssociativeList<Event>) results.values;
		    }
		    filteredEvents = newEventsList;
		    if (results.count > 0){
			    notifyDataSetChanged();
			}
		    else {
				notifyDataSetInvalidated();
			}
		}
	}
}
