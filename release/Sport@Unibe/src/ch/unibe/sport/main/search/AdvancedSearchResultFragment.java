package ch.unibe.sport.main.search;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.Events;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.event.info.EventsListAdapter;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.PointSherlockListFragment;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Objeckson;

public class AdvancedSearchResultFragment extends PointSherlockListFragment implements IFilterable{

	public static final String TAG = AdvancedSearchResultFragment.class.getName();

	public static final String JSON_QUERY = "json_query";
	public static final String JSON_DAYS = "days";
	public static final String JSON_INTERVALS = "intervals";
	public static final String JSON_INTERVAL_FROM = "interval_from";
	public static final String JSON_INTERVAL_TO = "interval_to";
	
	private static EventsListAdapter listAdapter;
	
	
	public AdvancedSearchResultFragment() {
		super(TAG);
	}

	@Override
	public void onCreated(Bundle savedInstanceState) {
		getListView().setDivider(null);
		
		ResultEventsLoader eventsLoader = new ResultEventsLoader();
		eventsLoader.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Event[]>(){
			@Override
			public void onTaskCompleted(AsyncTask<Context,Void,Event[]> task) {
				Event[] events = new Event[0];
				try {
					events = task.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return;
				}
				initAdapter(events);
			}
		});
		eventsLoader.execute(getActivity());
	}
	
	private class ResultEventsLoader extends ObservableAsyncTask<Context,Void,Event[]>{
		@Override
		protected Event[] doInBackground(Context... context) {
			SearchRequest request = Objeckson.fromJson(getJson(), SearchRequest.class);
			
			DBAdapter.INST.beginTransaction(getActivity(),TAG);
			Events eventDB = new Events(context[0]);
			int[] eventIDs = eventDB.searchEvents(request.getDays(), request.getTimeFrom(),request.getTimeTo(),request.getEventName());
			Event[] events = new Event[eventIDs.length];
			int i = 0;
			for (int id : eventIDs){
				events[i] = new Event(getActivity(),id);
				i++;
			}
			DBAdapter.INST.endTransaction(TAG);
			return events;
		}
	}
	
	private String getJson(){
		return this.getArguments().getString(JSON_QUERY);
	}
	
	private void initAdapter(Event[] events){
		listAdapter = new EventsListAdapter(getActivity(),events);
		setListAdapter(listAdapter);
	}
	
	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) listAdapter = (EventsListAdapter) this.getListAdapter();
		return listAdapter != null && listAdapter.getFilter() != null;
	}

	@Override
	public void filter(String prefix) {
		if (isFilterExists())listAdapter.getFilter().filter(prefix);
	}
	
	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		message.removeReceiver(getMemberTag());
		if (adapter.isCourseUpdate()){
			try {
				int eventID = adapter.getCourseID();
				updateEvent(eventID);
			} catch (ParamNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateEvent(int eventID){
		listAdapter.update(eventID);
	}
	
	/**
	 * Prevents fragment recreation memory leak.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public static AdvancedSearchResultFragment newInstance() {
		return new AdvancedSearchResultFragment();
	}
	

}
