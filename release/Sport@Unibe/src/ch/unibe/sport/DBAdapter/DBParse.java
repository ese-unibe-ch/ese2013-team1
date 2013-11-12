package ch.unibe.sport.DBAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DBParse {
	public static final String TAG = DBParse.class.getName();
	
	public static final String ATTENDED_NAME = "Attended";
	public static final String ATTENDED_UUID = "uuid";
	public static final String ATTENDED_COURSE_ID = "courseID";
	public static final String ATTENDED_DATE = "date";
	public static final String ATTENDED_SHARE = "share";
	
	public static final String RATING_NAME = "Rating";
	public static final String RATING_UUID = "uuid";
	public static final String RATING_COURSE_ID = "courseID";
	public static final String RATING_RATING = "rating";
	
	public static final boolean isCourseRated(Context context,String courseHash) throws ParseException {
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(RATING_NAME);
		query.whereEqualTo(RATING_UUID, Config.INST.SYSTEM.UUID);
		query.whereEqualTo(RATING_COURSE_ID, courseHash);
		List<ParseObject> resultList = query.find();
		return resultList.size() > 0;
	}
	
	public static final void rateCourse(Context context,String courseHash, int rating) throws ParseException, AlreadyRatedException {
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		if (!isCourseRated(context,courseHash)){
			ParseObject obj = new ParseObject(RATING_NAME);
			obj.put(RATING_UUID, Config.INST.SYSTEM.UUID);
			obj.put(RATING_COURSE_ID, courseHash);
			obj.put(RATING_RATING, rating);
			obj.save();
		}
		else {
			throw new AlreadyRatedException(courseHash);
		}
	}
	
	public static final void clearAttendedCourses(Context context) throws ParseException{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ATTENDED_NAME);
		query.whereEqualTo(ATTENDED_UUID, Config.INST.SYSTEM.UUID);
		List<ParseObject> resultList = query.find();
		ParseObject.deleteAll(resultList);
	}

	@Deprecated
	public static final void addAttendedCourses(Context context,final String[] courseHashs, final Date[] dates) throws ParseException {
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		ArrayList<ParseObject> attended = new ArrayList<ParseObject>();
		ParseObject obj = null;
		for (String hash : courseHashs){
			obj = new ParseObject(ATTENDED_NAME);
			obj.put(ATTENDED_UUID, Config.INST.SYSTEM.UUID);
			obj.put(ATTENDED_COURSE_ID, hash);
			attended.add(obj);
		}
		ParseObject.saveAll(attended);
	}
	
	public static final boolean isCourseAttended(Context context, final String courseHash, final Date date) throws ParseException{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ATTENDED_NAME);
		query.whereEqualTo(ATTENDED_UUID, Config.INST.SYSTEM.UUID);
		query.whereEqualTo(ATTENDED_COURSE_ID, courseHash);
		query.whereEqualTo(ATTENDED_DATE, date.toInt());
		List<ParseObject> resultList = query.find();
		return resultList.size() > 0;
	}
	
	public static final void addAttendedCourse(Context context,final String courseHash, final Date date, boolean share) throws ParseException, AlreadyAttendedException {
		if (!isCourseAttended(context, courseHash, date)){
			if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
			ParseObject attended = new ParseObject(ATTENDED_NAME);
			attended.put(ATTENDED_UUID, Config.INST.SYSTEM.UUID);
			attended.put(ATTENDED_COURSE_ID, courseHash);
			attended.put(ATTENDED_DATE, date.toInt());
			attended.put(ATTENDED_SHARE, share);
			attended.save();
		}
		else {
			throw new AlreadyAttendedException(courseHash);
		}
	}
	
	public static final int[] getRating(Context context,final String courseHash) throws ParseException{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new ParseException(0, "no connection");
		int[] rating = new int[5];
		ParseQuery<ParseObject> query = ParseQuery.getQuery(RATING_NAME);
		query.whereEqualTo(ATTENDED_COURSE_ID, courseHash);
		List<ParseObject> resultList = query.find();
		for (ParseObject object : resultList){
			int rate = object.getInt(RATING_RATING);
			if (rate >= 1 && rate <= 5){
				rating[rate-1]++;
			}
			else {
				Print.err(TAG,"course with hash "+object.getString(ATTENDED_COURSE_ID)+" has wrong rating: "+rate);
			}
		}
		return rating;
	}
}
