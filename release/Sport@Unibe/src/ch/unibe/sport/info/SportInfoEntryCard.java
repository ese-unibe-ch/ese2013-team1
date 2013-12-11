package ch.unibe.sport.info;

import android.content.Context;
import android.widget.AbsListView;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.event.info.EventMainInfoCardRelative;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.widget.layout.CollapsableLayout;
import ch.unibe.sport.widget.layout.HiddableLayout;

/**
 * 
 * @author Team 1 2013
 */
public class SportInfoEntryCard extends CollapsableLayout {

	private HiddableEventInfo hiddableEventInfo;
	private HeaderView header;
	
	public SportInfoEntryCard(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		this.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
		
		header = new HeaderView(getContext());
		this.setTitleView(header);
		
		hiddableEventInfo = new HiddableEventInfo(getContext());
		hiddableEventInfo.setProgressBarShown(false);
		this.setHiddable(hiddableEventInfo);
	}
	
	
	public void setEvent(Event event){
		header.setEvent(event);
		hiddableEventInfo.setEvent(event);
		/* uncomment to allow auto expand if event is in favorites */
		//if (!hiddableEventInfo.isInitialized())hiddableEventInfo.setVisible(event.isFavorite());
	}
	
	public void connect(IPointable point){
		hiddableEventInfo.connect(point);
	}
	
	
	/**
	 * 
	 * @author Team 1 2013
	 */
	private class HiddableEventInfo extends HiddableLayout {
		
		private EventMainInfoCardRelative eventInfo;
		private Event event;
		private IPointable point;
		
		public HiddableEventInfo(Context context) {
			super(context);
		}

		@Override
		protected void doInInitialization() {
			eventInfo = new EventMainInfoCardRelative(getContext());
			eventInfo.setBackgroundResource(0);
			eventInfo.showFavoritesButton();
			eventInfo.hideMenuButton();
			eventInfo.setEvent(event);
			eventInfo.setMarginTop(0);
			eventInfo.setMarginBottom(0);
			eventInfo.hideHeader();
			if (point != null)eventInfo.connect(point);
			this.addView(eventInfo);
		}

		public void setEvent(Event event) {
			//TODO copy course
			this.event = event;
			if (eventInfo != null)eventInfo.setEvent(event);
		}
		
		public void connect(IPointable point){
			this.point = point;
			if (eventInfo != null)eventInfo.connect(point);
		}

		@Override protected void onPreInitialize() {}
		@Override protected void onPostInitialize() {}
	}

}
