package ch.unibe.sport.main.search;

import java.util.concurrent.ExecutionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONParser;
import org.json.simple.ParseException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.Courses;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.Interval;
import ch.unibe.sport.course.Time;
import ch.unibe.sport.course.info.CoursesListAdapter;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.PointSherlockListFragment;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;

public class AdvancedSearchResultFragment extends PointSherlockListFragment implements IFilterable{

	public static final String TAG = AdvancedSearchResultFragment.class.getName();

	public static final String JSON_QUERY = "json_query";
	public static final String JSON_DAYS = "days";
	public static final String JSON_INTERVALS = "intervals";
	public static final String JSON_INTERVAL_FROM = "interval_from";
	public static final String JSON_INTERVAL_TO = "interval_to";
	
	private static CoursesListAdapter listAdapter;
	
	
	public AdvancedSearchResultFragment() {
		super(TAG);
	}

	@Override
	public void onCreated(Bundle savedInstanceState) {
		getListView().setDivider(null);
		
		ResultCoursesLoader coursesLoader = new ResultCoursesLoader();
		coursesLoader.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Course[]>(){
			@Override
			public void onTaskCompleted(AsyncTask<Context,Void,Course[]> task) {
				Course[] courses = new Course[0];
				try {
					courses = task.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return;
				}
				initAdapter(courses);
			}
		});
		coursesLoader.execute(getActivity());
	}
	
	private class ResultCoursesLoader extends ObservableAsyncTask<Context,Void,Course[]>{
		@Override
		protected Course[] doInBackground(Context... context) {
			String json = getJson();
			
			JSONParser parser = new JSONParser();
			JSONObject params = null;
			try {
				params = (JSONObject) parser.parse(json);
			} catch (ParseException e) {
				e.printStackTrace();
				return new Course[0];
			}
			JSONArray daysArray = (JSONArray) params.get(JSON_DAYS);
			int[] days = new int[daysArray.size()];
			for (int i = 0, length = days.length; i < length; i++){
				days[i] = ((Long) daysArray.get(i)).intValue();
			}
			
			JSONArray intervalsArray = (JSONArray) params.get(JSON_INTERVALS);
			Interval[] times = new Interval[intervalsArray.size()];
			
			for (int i = 0, length = times.length; i < length; i++){
				Time timeFrom = new Time(((Long) ((JSONObject)intervalsArray.get(i)).get(JSON_INTERVAL_FROM)).intValue());
				Time timeTo = new Time(((Long) ((JSONObject)intervalsArray.get(i)).get(JSON_INTERVAL_TO)).intValue());
				times[i] = new Interval(timeFrom,timeTo);
			}
			
			DBAdapter.INST.beginTransaction(getActivity(),TAG);
			Courses courseDB = new Courses(context[0]);
			int[] courseIDs = courseDB.searchCourses(days, times);
			Course[] courses = new Course[courseIDs.length];
			int i = 0;
			for (int id : courseIDs){
				courses[i] = new Course(getActivity(),id);
				i++;
			}
			DBAdapter.INST.endTransaction(TAG);
			return courses;
		}
	}
	
	private String getJson(){
		return this.getArguments().getString(JSON_QUERY);
	}
	
	private void initAdapter(Course[] courses){
		listAdapter = new CoursesListAdapter(getActivity(),courses);
		setListAdapter(listAdapter);
	}
	
	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) listAdapter = (CoursesListAdapter) this.getListAdapter();
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
				int courseID = adapter.getCourseID();
				updateCourse(courseID);
			} catch (ParamNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateCourse(int courseID){
		listAdapter.update(courseID);
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
