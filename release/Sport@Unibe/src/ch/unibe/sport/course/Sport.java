package ch.unibe.sport.course;

import android.content.Context;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.Courses;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.utils.Utils;

public class Sport {
	private static final String TAG = Sport.class.getName();

	private final Context context;
	private Courses coursesDB;
	private Sports sportDB;
		
	private final int sportID;
	private String sport;
	
	private AssociativeList<Course> courses;
	
	public Sport (Context context, int sportID){
		this.context = context;
		this.sportID = sportID;
		
		init();
	}
	
	private void init(){
		this.courses = new AssociativeList<Course>();
		DBAdapter.INST.beginTransaction(context, TAG);
		this.sportDB = new Sports(context);
		this.coursesDB = new Courses(context);
		sport = sportDB.getData(sportID)[Sports.SPORT];
		String[][] coursesData = coursesDB.getDataBySportID(sportID);
		
		for (int i = 0, length = coursesData.length; i < length; i++){
			courses.add(new Course(context,coursesData[i]),Utils.Int(coursesData[i][Courses.CID]));
		}
		DBAdapter.INST.endTransaction(TAG);
	}
	
	public int getCoursesCount(){
		return courses.getSize();
	}
	
	public Course getCourseByID(int courseID){
		return courses.get(courseID);
	}
	
	public Course getCourseAt(int index){
		return courses.getAt(index);
	}
	
	public void replaceCourse(Course course){
		if (courses.containsKey(course.getCourseID())){
			this.courses.replace(course, course.getCourseID());
		}
	}
	
	public String getSportName(){
		return this.sport;
	}
}
