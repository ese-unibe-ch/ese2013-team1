package ch.unibe.sport.info;

import android.content.Context;
import android.widget.AbsListView;
import ch.unibe.sport.course.Course;
import ch.unibe.sport.course.info.CourseMainInfoCardRelative;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.widget.layout.CollapsableLayout;
import ch.unibe.sport.widget.layout.HiddableLayout;

/**
 * 
 * @author Team 1 2013
 */
public class SportInfoEntryCard extends CollapsableLayout {

	private HiddableCourseInfo hiddableCourseInfo;
	private HeaderView header;
	
	public SportInfoEntryCard(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		this.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
		
		header = new HeaderView(getContext());
		this.setTitleView(header);
		
		hiddableCourseInfo = new HiddableCourseInfo(getContext());
		hiddableCourseInfo.setProgressBarShown(false);
		this.setHiddable(hiddableCourseInfo);
	}
	
	
	public void setCourse(Course course){
		header.setCourse(course);
		hiddableCourseInfo.setCourse(course);
		/* uncomment to allow auto expand if course is in favorites */
		//if (!hiddableCourseInfo.isInitialized())hiddableCourseInfo.setVisible(course.isFavorite());
	}
	
	public void connect(IPointable point){
		hiddableCourseInfo.connect(point);
	}
	
	
	/**
	 * 
	 * @author Team 1 2013
	 */
	private class HiddableCourseInfo extends HiddableLayout {
		
		private CourseMainInfoCardRelative courseInfo;
		private Course course;
		private IPointable point;
		
		public HiddableCourseInfo(Context context) {
			super(context);
		}

		@Override
		protected void doInInitialization() {
			courseInfo = new CourseMainInfoCardRelative(getContext());
			courseInfo.setBackgroundResource(0);
			courseInfo.showFavoritesButton();
			courseInfo.hideMenuButton();
			courseInfo.setCourse(course);
			courseInfo.setMarginTop(0);
			courseInfo.setMarginBottom(0);
			courseInfo.hideHeader();
			if (point != null)courseInfo.connect(point);
			this.addView(courseInfo);
		}

		public void setCourse(Course course) {
			//TODO copy course
			this.course = course;
			if (courseInfo != null)courseInfo.setCourse(course);
		}
		
		public void connect(IPointable point){
			this.point = point;
			if (courseInfo != null)courseInfo.connect(point);
		}

		@Override protected void onPreInitialize() {}
		@Override protected void onPostInitialize() {}
	}

}
