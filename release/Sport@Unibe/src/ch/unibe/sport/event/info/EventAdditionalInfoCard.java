package ch.unibe.sport.event.info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.unibe.sport.R;
import ch.unibe.sport.core.Event;

/**
 * 
 * Class that handles information such as registration,
 * weather dependency, course dates, etc, providing 
 * additional support when available (links, etc). 
 * 
 * @author Team 1
 *
 */

public class EventAdditionalInfoCard extends AbstractInfoCard{
	
	public static final String TAG = EventAdditionalInfoCard.class.getName();
	
	private Event event;
	private ViewGroup courseContainer;
	private ViewGroup onlineContainer;
	private ViewGroup weatherContainer;
	private ViewGroup infoContainer;
	private ViewGroup kewContainer;
	private ViewGroup webContainer;
	
	public EventAdditionalInfoCard(Context context,Event event) {
		super(context,TAG);
		this.event = event;
		init();
	}
	
	private OnClickListener infoListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if (event.getInfoLink().length() > 0){
				goToUrl(event.getInfoLink());
			}
		}
	};
	
	private void init(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.course_info_additional, this);
        inflater = null;
        initViews();
        initEventData();
	}
	
	public void update(){
        initEventData();
	}
	
	private void initViews(){
		courseContainer = (ViewGroup) this.findViewById(R.id.course_container);
		onlineContainer = (ViewGroup) this.findViewById(R.id.online_container);
		weatherContainer = (ViewGroup) this.findViewById(R.id.weather_container);
		infoContainer = (ViewGroup) this.findViewById(R.id.info_container);
		kewContainer = (ViewGroup) this.findViewById(R.id.kew_container);
		webContainer = (ViewGroup) this.findViewById(R.id.web_container);
	}
		
	private void initEventData(){
		initKew();
		intWeb();
	}
	
	private void initKew(){

		courseContainer.setVisibility(GONE);
		onlineContainer.setVisibility(GONE);
		weatherContainer.setVisibility(GONE);

		String[] kewTmp = event.getKew();
		for (String kew : kewTmp){
			if (kew.equals("k")) courseContainer.setVisibility(VISIBLE);
			if (kew.equals("e")) onlineContainer.setVisibility(VISIBLE);
			if (kew.equals("w")) weatherContainer.setVisibility(VISIBLE);
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
		this.initCourseParam(infoContainer, null, event.getInfoLink());
		this.infoContainer.setOnClickListener(infoListener);
	}
	
	private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        getContext().startActivity(launchBrowser);
    }
}
