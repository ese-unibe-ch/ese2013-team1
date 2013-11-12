package ch.unibe.sport.DBAdapter.tasks;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.ParseException;

import android.content.Context;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.Courses;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.utils.Json;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class CourseInfoLoaderTask extends ObservableAsyncTask<Void, Integer, Boolean> {
	public static final String TAG = CourseInfoLoaderTask.class.getName();

	private static final String URL = "http://scg.unibe.ch/ese/unisport/sport.php?id=";
	
	private Context context;
	private Courses coursesDB;
	private Sports sportsDB;
	private int[] sportIDs;
	
	private boolean terminate = false;
	
	public interface OnProgressUpdateListener{
		public void onProgressUpdate(int current, int all);
	}
	
	private OnProgressUpdateListener mOnProgressUpdateListener;
	
	public void setOnProgressUpdateListener(OnProgressUpdateListener l){
		this.mOnProgressUpdateListener = l;
	}
	
	public CourseInfoLoaderTask(Context context, int[] courseIDs){
		this.context = context;
		this.sportIDs = courseIDs;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (mOnProgressUpdateListener != null){
			mOnProgressUpdateListener.onProgressUpdate(progress[0],progress[1]);
		}
	}
		
	@Override
	protected Boolean doInBackground(Void... params) {
		DBAdapter.INST.beginTransaction(context,TAG);
		Sports sportsDB = new Sports(context);
		for (int i = 0, length = this.sportIDs.length; i < length; i++){
			if (this.terminate || isCancelled()) {
				Print.log("CourseInfoLoader canceled!");
				DBAdapter.INST.endTransaction(TAG);
				return false;
			}
			boolean result = loadSportInfo(sportIDs[i]);
			if (!result){
				Print.err("Error loading data for sport: "+sportIDs[i]);
			}
			else {
				if (this.terminate || isCancelled()) {
					Print.log("CourseInfoLoader canceled!");
					DBAdapter.INST.endTransaction(TAG);
					return false;
				}
				Print.log("Data loaded for sport: "+sportIDs[i]);
				sportsDB.setLoaded(sportIDs[i]);
				publishProgress(i,length);
			}
		}

		DBAdapter.INST.endTransaction(TAG);
		return true;
	}
	
	private boolean loadSportInfo(int sportID){
		String json = null;
		try {
			json = Json.getInputStream(URL+sportID);
		} catch (Exception e1) {
			Print.err("[CourseInfoLoader] Connection error");
			return false;
		}
		if (json.length() == 0) return false;
		JSONObject resultObject = null;
		try {
			resultObject = Json.parseJson(json);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		if (this.terminate || isCancelled()) {
			return false;
		}
		//TODO something better
		AssociativeList<String>[] courses = Json.buildAssociativeListOfSubCourses(resultObject);
		if (courses == null || courses.length == 0) return false;

		coursesDB = new Courses(context);
		sportsDB = new Sports(context);
		ArrayList<Integer> coursesIDs = Utils.intArrayToArrayList(sportsDB.getCoursesIDs(sportID));
		for (int i = 0, length = courses.length; i < length; i++){
			String courseName = (courses[i].containsKey("course")) ? courses[i].get("course") : "";
			if (courseName == null || courseName.equals("null")) courseName = "";
			String day = (courses[i].containsKey("day")) ? courses[i].get("day") : "";
			if (day == null || day.equals("null")) day = "";
			String time = (courses[i].containsKey("time")) ? courses[i].get("time") : "";
			if (time == null || time.equals("null")) time = "";
			String period = (courses[i].containsKey("period")) ? courses[i].get("period") : "";
			if (period == null || period.equals("null")) period = "";
			String place = (courses[i].containsKey("place")) ? courses[i].get("place") : "";
			if (place == null || place.equals("null")) place = "";
			String info = (courses[i].containsKey("info")) ? courses[i].get("info") : "";
			if (info == null || info.equals("null")) info = "";
			String subscription = (courses[i].containsKey("subscription")) ? courses[i].get("subscription") : "";
			if (subscription == null || subscription.equals("null")) subscription = "";
			String kew = (courses[i].containsKey("kew")) ? courses[i].get("kew") : "";
			if (kew == null || kew.equals("null")) kew = "";
			Course course = Course.valueOf(courseName, day, time, period, place, info, subscription, kew);
			int cid = course.save(context, sportID);
			coursesIDs.remove((Integer)cid);
		}
		int[] toDelete = Utils.toInt(Utils.arrayListToArray(coursesIDs));
		coursesDB.removeCourses(toDelete);
		return true;
	}

	public void requestTermination() {
		terminate = true;
	}
}
