package ch.unibe.sport.course.info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.unibe.sport.R;
import ch.unibe.sport.course.Course;

public class CourseAdditionalInfoCard extends AbstractInfoCard{
	
	public static final String TAG = CourseAdditionalInfoCard.class.getName();
	
	public static final String INFO_URL = "http://www.sport.unibe.ch/infos/";
	
	private Course course;
	private ViewGroup courseContainer;
	private ViewGroup onlineContainer;
	private ViewGroup weatherContainer;
	private ViewGroup infoContainer;
	private ViewGroup kewContainer;
	private ViewGroup webContainer;
	
	public CourseAdditionalInfoCard(Context context,Course course) {
		super(context,TAG);
		this.course = course;
		init();
	}
	
	private OnClickListener infoListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (course.getInfo().length() > 0){
				goToUrl(INFO_URL+course.getInfo()+".pdf");
			}
		}
	};
	
	private void init(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.course_info_additional, this);
        inflater = null;
        initViews();
        initCourseData();
	}
	
	public void update(){
        initCourseData();
	}
	
	private void initViews(){
		courseContainer = (ViewGroup) this.findViewById(R.id.course_container);
		onlineContainer = (ViewGroup) this.findViewById(R.id.online_container);
		weatherContainer = (ViewGroup) this.findViewById(R.id.weather_container);
		infoContainer = (ViewGroup) this.findViewById(R.id.info_container);
		kewContainer = (ViewGroup) this.findViewById(R.id.kew_container);
		webContainer = (ViewGroup) this.findViewById(R.id.web_container);
	}
		
	private void initCourseData(){
		initKew();
		intWeb();
	}
	
	private void initKew(){

		courseContainer.setVisibility(GONE);
		onlineContainer.setVisibility(GONE);
		weatherContainer.setVisibility(GONE);

		if (course.getKew().length() > 0) {
			String[] kewTmp = course.getKew().split("/");
			for (String kew : kewTmp){
				if (kew.equals("k")) courseContainer.setVisibility(VISIBLE);
				if (kew.equals("e")) onlineContainer.setVisibility(VISIBLE);
				if (kew.equals("w")) weatherContainer.setVisibility(VISIBLE);
			}
		}
		
		if (courseContainer.getVisibility() == GONE
				&& onlineContainer.getVisibility() == GONE
				&& weatherContainer.getVisibility() == GONE)
		{
			kewContainer.setVisibility(GONE);
		}
	}
	
	private void intWeb(){
		initCourseInfo();
		
		iniVisibility();
	}

	private void iniVisibility() {
		if (infoContainer.getVisibility() == GONE){
			webContainer.setVisibility(GONE);
		}
	}
	
	private void initCourseInfo(){
		this.initCourseParam(infoContainer, null, course.getInfo());
		this.infoContainer.setOnClickListener(infoListener);
	}
	
	private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        getContext().startActivity(launchBrowser);
    }
}
