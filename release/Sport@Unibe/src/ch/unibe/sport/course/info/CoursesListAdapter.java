package ch.unibe.sport.course.info;

import ch.unibe.sport.course.Course;
import ch.unibe.sport.utils.AssociativeList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class CoursesListAdapter extends BaseAdapter implements Filterable {
	public static final String TAG = CoursesListAdapter.class.getName();
	private volatile Context context;
	
	private final Object lock = new Object();
	
	private AssociativeList<Course> courses;
	private AssociativeList<Course> filteredCourses;
	
	private CourseListFilter courseFilter;
	
	public interface OnListIsEmptyListener {
		public void OnListIsEmpty();
	}
	
	private CourseMainInfoCardRelative tmpView;
	
	public CoursesListAdapter(Context context, Course[] courses){
		this.context = context;
		this.courses = new AssociativeList<Course>();
		for (Course course : courses){
			this.courses.add(course,course.getCourseID());
		}
		this.filteredCourses = this.courses.copy();
	}
			
	/**
	 * Updates course refreshes list view
	 * @param courseID
	 */
	public void update(int courseID) {
		/*
		 * Course is already favorite or attended
		 */
		if (this.courses.containsKey(courseID)){
			Course course = this.courses.get(courseID);
			course.update();
			/*
			 * Checking if it's neccessary to remove course from list
			 */
			if (!course.isFavorite() && course.getAttended() == 0){
				this.courses.remove(courseID);
				this.filteredCourses.remove(courseID);
			}
		}
		/*
		 * Course wasn't favorite and attended
		 */
		else {
			Course course = new Course(context,courseID);
			/*
			 * Checking if we need to add course int the list
			 */
			if (course.isFavorite() || course.getAttended() != 0){
				this.courses.add(course, courseID);
				this.filteredCourses.add(course, courseID);
			}
		}
		notifyDataSetChanged();
	}
		
	@Override
	public int getCount() {
		return this.filteredCourses.getSize();
	}

	@Override
	public Object getItem(int position) {
		return this.filteredCourses.getAt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		tmpView = (CourseMainInfoCardRelative)convertView;
		if (tmpView == null) {
			tmpView = new CourseMainInfoCardRelative(context);
			tmpView.hideFavoritesButton();
	    }
		tmpView.setCourse(this.filteredCourses.getAt(position));
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
		if (courseFilter == null) {
			courseFilter = new CourseListFilter();
		}
		return courseFilter;
	}
	
	/**
	 * Custom filter, that allows to filter favorite courses
	 * by all it's data (name, place, time, period, favorite)
	 * @author Team 1
	 */
	@SuppressLint("DefaultLocale")
	private class CourseListFilter extends Filter {
		
		@Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
            	AssociativeList<Course> newCoursesList;
            	/* synchronizing threads */
                synchronized (lock) {
                	newCoursesList = courses.copy();
                }
                results.values = newCoursesList;
                results.count = newCoursesList.getSize();
            }
            else {
                String prefixString = prefix.toString().toLowerCase();

                AssociativeList<Course> originalCoursesList;
                synchronized (lock) {
                    originalCoursesList = courses.copy();
                }
                
                final AssociativeList<Course> newValues = new AssociativeList<Course>();

                for (final Course course : originalCoursesList) {
                    final String courseText = course.toString().toLowerCase();

                    /*
                     * simple situation, when String representation of course
                     * starts with entered prefix
                     */
                    if (courseText.startsWith(prefixString)) {
                        newValues.add(course,course.getCourseID());
                    }
                    else {
                    	/*
                    	 * more complex situation, it's needed to look through
                    	 * all course values, that should be divided by space
                    	 */
                        final String[] words = courseText.split(" ");
                        final int wordCount = words.length;

                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(course,course.getCourseID());
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
			AssociativeList<Course> newCoursesList = null;
		    if (results.count == 0){
		    	newCoursesList = new AssociativeList<Course>();
		    }
		    else {
		    	newCoursesList = (AssociativeList<Course>) results.values;
		    }
		    filteredCourses = newCoursesList;
		    if (results.count > 0){
			    notifyDataSetChanged();
			}
		    else {
				notifyDataSetInvalidated();
			}
		}
	}
}
