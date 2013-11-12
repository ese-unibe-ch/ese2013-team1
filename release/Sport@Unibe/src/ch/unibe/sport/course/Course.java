package ch.unibe.sport.course;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.AttendedCourses;
import ch.unibe.sport.DBAdapter.tables.Courses;
import ch.unibe.sport.DBAdapter.tables.FavoriteCourses;
import ch.unibe.sport.DBAdapter.tables.Rating;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import android.content.Context;

public class Course {
	public static final String TAG =  Course.class.getName();
	
	private boolean valueOf = false;
	private boolean existsInDb = false;
	
	private Context context;
	private Courses coursesDB;
	
	private int sportID;						// > 0
	private int courseID;						// > 0
	
	private String sport;						// not null or empty
	private String course;						// not null or empty
	private String courseHash;					// not null or empty
	private String day;							// not null or empty
	private String time;						// may empty
	private String period;						// may empty
	private String place;						// not null or empty
	private String info;						// may empty
	private String subscription;				// may empty
	private String kew;							// may empty
	private boolean favorite;
	private int attended;
	private boolean rated;
	private int rating;
	
	private int textColor;
	private int bgColor;
	private int bgType;
	
	private double latitude;
	private double longitude;
	
	protected boolean invariant(){
		return (valueOf || (context != null && coursesDB != null))
				&& (!existsInDb || (sportID > 0 && courseID > 0))
				&& sport != null
				&& sport.length() > 0
				&& course != null
				&& course.length() > 0
				&& courseHash != null
				&& courseHash.length() > 0
				&& day != null
				&& day.length() > 0
				&& time != null
				&& period != null
				&& place != null
				&& place.length() > 0
				&& info != null
				&& subscription != null
				&& kew != null;
	}
	
	public Course(Context context,String[] courseData){
		assert courseData != null;
		assert courseData.length > 0;
		this.existsInDb = true;
		this.valueOf = true;
		
		this.context = context;
		
		this.courseID = Utils.Int(courseData[Courses.CID]);
		this.course = courseData[Courses.COURSE];
		this.courseHash = courseData[Courses.HASH];
		this.day = courseData[Courses.DAY];
		this.period = courseData[Courses.PERIOD];
		this.time = courseData[Courses.TIME];
		this.place = courseData[Courses.PLACE];
		this.info = courseData[Courses.INFO];
		this.subscription = courseData[Courses.SUBSCRIPTION];
		this.kew = courseData[Courses.KEW];
		this.sportID = Utils.Int(courseData[Courses.SPORT_SID]);
		this.sport = courseData[Courses.SPORT_SPORT];
		this.favorite = Utils.Int((courseData[Courses.FAVORITE])) > 0;
		this.rated = Utils.Int((courseData[Courses.RATED])) > 0;
		
		if (favorite){
			initFavoriteData();
		}
		
		if (rated){
			initRatingData();
		}
		
		initAttendedDate();
		
		assert invariant();
	}
	
	public Course(Context context, int courseID){
		assert context != null;
		assert courseID > 0;
		this.context = context;
		this.courseID = courseID;
		this.existsInDb = true;
		init();
		assert invariant();
	}
	
	private Course(String course,String day,String time, String period, String place, String info, String subscription, String kew){
		this.valueOf = true;
		this.course = course;
		this.day = day;
		this.time = time;
		this.period = period;
		this.place = place;
		this.info = info;
		this.subscription = subscription;
		this.kew = kew;
		
		assert invariant();
	}
	
	public static Course valueOf(String course,String day,String time, String period, String place, String info, String subscription, String kew){
		return new Course(course, day,time, period, place, info, subscription, kew);
	}
	
	public boolean update(){
		if (this.existsInDb && this.context != null){
			init();
			return true;
		}
		else return false;
	}
	
	private void init(){
		this.coursesDB = new Courses(context);
		final String[] courseData = this.coursesDB.getData(courseID);
		
		if (courseData.length == 0){
			Print.err(TAG,"Error loading subcourse, wasn't found in database");
			return;
		}
		
		this.course = courseData[Courses.COURSE];
		this.courseHash = courseData[Courses.HASH];
		this.day = courseData[Courses.DAY];
		this.period = courseData[Courses.PERIOD];
		this.time = courseData[Courses.TIME];
		this.place = courseData[Courses.PLACE];
		this.info = courseData[Courses.INFO];
		this.subscription = courseData[Courses.SUBSCRIPTION];
		this.kew = courseData[Courses.KEW];
		this.favorite = Utils.Int((courseData[Courses.FAVORITE])) > 0;
		this.rated = Utils.Int((courseData[Courses.RATED])) > 0;
		this.sportID = Utils.Int(courseData[Courses.SPORT_SID]);
		this.sport = courseData[Courses.SPORT_SPORT];
		
		if (favorite){
			initFavoriteData();
		}
		
		if (rated){
			initRatingData();
		}
		
		initAttendedDate();
	}
	
	public int save(Context context, int sportID) {
		if (!existsInDb){
			this.context = context;
			this.sportID = sportID;
			DBAdapter.INST.beginTransaction(context,TAG);
			Sports sportsDB = new Sports(context);
			this.sport = sportsDB.getData(sportID)[Sports.SPORT];
			this.coursesDB = new Courses(context);
			String[] courseData = this.coursesDB.getDataByCourseName(sportID, course);
			if (courseData.length > 0){
				this.courseID = Utils.Int(courseData[Courses.CID]);
				coursesDB.updateCourse(this);
			}
			else {
				long sid = this.coursesDB.addCourse(this);
				this.courseID = (int)sid;
			}
			DBAdapter.INST.endTransaction(TAG);
			
			this.existsInDb = true;
			this.valueOf = false;
		}
		else Print.err(TAG,"Error saving subcourse, already exists in database");
		assert invariant();
		return this.courseID;
	}
	
	public String getSport(){
		return this.sport;
	}
	
	public int getSportID() {
		assert invariant();
		return sportID;
	}

	public int getCourseID() {
		assert invariant();
		return courseID;
	}

	public String getCourse() {
		assert invariant();
		return course;
	}
	
	public String getCourseHash(){
		return this.courseHash;
	}

	public String getDay() {
		assert invariant();
		return day;
	}
	
	public String getTime(){
		assert invariant();
		return this.time;
	}

	public String getPeriod() {
		assert invariant();
		return period;
	}

	public String getPlace() {
		assert invariant();
		return place;
	}

	public String getInfo() {
		assert invariant();
		return info;
	}

	public String getSubscription() {
		assert invariant();
		return subscription;
	}

	public String getKew() {
		assert invariant();
		return kew;
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean favorite){
		this.favorite = favorite;
		if (favorite && context != null){
			initFavoriteData();
		}
		assert invariant();
	}

	private void initFavoriteData() {
		if (context == null || !favorite) return;
		DBAdapter.INST.open(context, TAG);
		FavoriteCourses favorites = new FavoriteCourses(context);
		String[] favoriteInfo = favorites.getData(courseID);
		DBAdapter.INST.close(TAG);
		this.textColor = Utils.Int(favoriteInfo[FavoriteCourses.TEXT_COLOR]);
		this.bgColor = Utils.Int(favoriteInfo[FavoriteCourses.BG_COLOR]);
		this.bgType = Utils.Int(favoriteInfo[FavoriteCourses.BG_TYPE]);
	}
	
	private void initRatingData(){
		if (context == null || !rated) return;
		DBAdapter.INST.open(context, TAG);
		Rating ratingDB = new Rating(context);
		int[] ratingInfo = ratingDB.getData(courseID);
		DBAdapter.INST.close(TAG);
		this.rating = ratingInfo[Rating.RATING];
	}

	private void initAttendedDate(){
		if (context == null) return;
		this.attended = new AttendedCourses(context).isAttended(courseID);
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public int getTextColor() {
		return textColor;
	}

	public int getBgColor() {
		return bgColor;
	}

	public int getBgType() {
		return bgType;
	}
	
	public int getAttended() {
		return attended;
	}
	
	public boolean isRated() {
		return rated;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Course))
		      return false;
		Course c = (Course) obj;
		return (this.sportID == c.sportID
			&& this.sport.equals(c.sport)
			&& this.courseID == c.courseID
			&& this.course.equals(c.course)
			&& this.courseHash.equals(c.courseHash)
			&& this.day.equals(c.day)
			&& this.period.equals(c.period)
			&& this.place.equals(c.place)
			&& this.info.equals(c.info)
			&& this.subscription.equals(c.subscription)
			&& this.kew.equals(c.kew)
			&& this.favorite == c.favorite
			&& this.attended == c.attended
		);
	}
	
	@Override
	public int hashCode() {
		final int prime = 3;
		int hash = 1;
		hash = hash * prime + this.sportID;
		hash = hash * prime + this.courseID;
		hash = hash * prime + this.course.hashCode();
		hash = hash * prime + this.courseHash.hashCode();
		hash = hash * prime + this.day.hashCode();
		hash = hash * prime + this.period.hashCode();
		hash = hash * prime + this.place.hashCode();
		hash = hash * prime + this.info.hashCode();
		hash = hash * prime + this.subscription.hashCode();
		hash = hash * prime + this.kew.hashCode();
		hash = hash * prime + this.sport.hashCode();
		hash = hash * prime + ((this.favorite) ? 1 : 0);
		hash = hash * prime + this.attended;
		return hash;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(this.course);
		str.append(' ');
		str.append(this.sport);
		str.append(' ');
		str.append(this.day);
		str.append(' ');
		str.append(this.time);
		str.append(' ');
		str.append(this.period);
		str.append(' ');
		str.append(this.place);
		str.append(' ');
		str.append(this.kew);
		return str.toString();
	}
}
