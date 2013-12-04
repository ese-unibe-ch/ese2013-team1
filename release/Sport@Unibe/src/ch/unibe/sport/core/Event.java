package ch.unibe.sport.core;

import android.content.Context;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.EventAttended;
import ch.unibe.sport.DBAdapter.tables.EventDaysOfWeek;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.DBAdapter.tables.EventKew;
import ch.unibe.sport.DBAdapter.tables.EventPeriods;
import ch.unibe.sport.DBAdapter.tables.EventRating;
import ch.unibe.sport.DBAdapter.tables.Events;
import ch.unibe.sport.DBAdapter.tables.SportEvents;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.utils.bulker.BulkKey;
import ch.unibe.sport.utils.bulker.BulkParam;
import ch.unibe.sport.utils.bulker.BulkRelation;
import ch.unibe.sport.utils.bulker.BulkTable;

@BulkTable(Events.NAME)
public class Event {
	public static final String TAG = Event.class.getName();
	
	@BulkKey
	private int eid;
	@BulkParam
	private String hash;
	@BulkParam
	private String eventName;
	@BulkParam
	private String date;
	@BulkParam
	private String infoLink;
	@BulkParam
	private String registration;
	@BulkParam
	private String registrationLink;
	@BulkParam
	private Interval interval;
	@BulkRelation(EventPeriods.NAME)
	private int[] periods;
	@BulkRelation(EventKew.NAME)
	private String[] kew;
	@BulkRelation(EventDaysOfWeek.NAME)
	private int[] dayOfWeek;
	
	private String sportName;
	private boolean favorite;
	private int attended;
	private int background;
	private int rating;
	
	public Event(){
		this.kew = new String[0];
		this.periods = new int[0];
		this.dayOfWeek = new int[0];
	}
	
	public Event(Context context, int eventID){
		this.eid = eventID;

		DBAdapter.INST.beginTransaction(context, TAG);
		String[] data = new Events(context).getData(eid);
		this.hash = data[Events.HASH];
		initEventData(context, data);
		DBAdapter.INST.endTransaction(TAG);
	}
	
	public Event(Context context, String hash){
		this.hash = hash;
		
		DBAdapter.INST.beginTransaction(context, TAG);
		String[] data = new Events(context).getData(hash);
		this.eid = Utils.Int(data[Events.EID]);
		initEventData(context, data);
		DBAdapter.INST.endTransaction(TAG);
	}
	
	private void initEventData(Context context, String[] data) {
		if (this.eid <= 0){
			Print.err(TAG,"eventID is invalid: "+this.eid);
			return;
		}
		this.eventName = data[Events.EVENT_NAME];
		this.date = data[Events.DATE];
		this.infoLink = data[Events.INFO_LINK];
		this.registration = data[Events.REGISTRATION];
		this.registrationLink = data[Events.REGISTRATION_LINK];
		this.periods = new EventPeriods(context).getPeriods(eid);
		this.dayOfWeek = new EventDaysOfWeek(context).getDaysOfWeek(eid);
		this.kew = new EventKew(context).getKew(eid);
		this.sportName = new SportEvents(context).getSportName(eid);
		if (data[Events.IID].length() > 0){
			this.interval = new Interval(context,data[Events.IID]);
		}
		
		initUserData(context);
	}
	
	private void initUserData(Context context){
		if (this.eid <= 0 || hash == null){
			Print.err(TAG,"eventID or hash is invalid: eid="+this.eid+" hash="+hash);
			return;
		}

		this.attended = new EventAttended(context).isAttended(hash);
		String[] favoriteData = new EventFavorite(context).getData(hash);
		this.favorite = favoriteData.length > 0;
		if (favorite){
			this.setBackground(Utils.Int(favoriteData[EventFavorite.BG_COLOR]));
		}
		this.setRating(new EventRating(context).getRating(hash));
	}
	
	public void update(Context context){
		DBAdapter.INST.beginTransaction(context, TAG);
		initUserData(context);
		DBAdapter.INST.endTransaction(TAG);
	}
	
	public void setDaysOfWeek(int[] daysOfWeek){
		this.dayOfWeek = daysOfWeek;
	}
	
	public int[] getDaysOfWeek(){
		return this.dayOfWeek;
	}
	
	public void setEventID(int eventID){
		this.eid = eventID;
	}
	
	public int getEventID(){
		return this.eid;
	}
	
	public String getEventHash() {
		return hash;
	}
	
	public void setEventHash(String eventHash) {
		this.hash = eventHash;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	public Interval getInterval() {
		return interval;
	}
	
	public void setInterval(Interval interval) {
		this.interval = interval;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public int[] getPeriods() {
		return periods;
	}
	
	public void setPeriods(int[] periods) {
		this.periods = periods;
	}
	
	public String getInfoLink() {
		return infoLink;
	}
	
	public void setInfoLink(String infoLink) {
		this.infoLink = infoLink;
	}
	
	public String getRegistration() {
		return registration;
	}
	
	public void setRegistration(String registration) {
		this.registration = registration;
	}
	
	public String getRegistrationLink() {
		return registrationLink;
	}
	
	public void setRegistrationLink(String registrationLink) {
		this.registrationLink = registrationLink;
	}
	
	public String[] getKew() {
		return kew;
	}
	
	public void setKew(String[] kew) {
		this.kew = kew;
	}
	
	public int getAttended(){
		return this.attended;
	}

	public int getBackground() {
		return background;
	}

	public void setBackground(int background) {
		this.background = background;
	}
	
	public boolean isFavorite(){
		return this.favorite;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public boolean isAttended(){
		return this.attended != 0;
	}
	
	public boolean isRated(){
		return this.rating > 0;
	}
	
	public String getTimeString(){
		return this.interval.toString();
	}
	
	public String getPeriodString(){
		if (this.periods.length == 0) return "";
		StringBuilder period = new StringBuilder();
		int index = 0;
		for (int i = 1; i <= 5; i++){
			if (i != 1) period.append(" | ");
			if (index < periods.length && i == periods[index]){
				period.append(i);
				index++;
			}
			else period.append('-');
		}
		return period.toString();
	}
	
	//TODO find better algorithm
	public String getDaysOfWeekString(){
		if (this.dayOfWeek.length == 0) return "";
		StringBuilder str = new StringBuilder();
		int lastDay = 0;
		for (int i = 0,length = this.dayOfWeek.length; i < length; i++){
			if (i == 0){
				str.append(Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[dayOfWeek[i]-1]);
				lastDay = dayOfWeek[i];
				continue;
			}
			

			if (i == length-1){
				if (this.dayOfWeek[i]-lastDay > 1) str.append("-");
				else str.append(", ");
				str.append(Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[dayOfWeek[i]-1]);
				continue;
			}
			
			if (this.dayOfWeek[i]-this.dayOfWeek[i-1] == 1 && i < length-1){
				continue;
			}
			
			if (this.dayOfWeek[i]-this.dayOfWeek[i-1] > 1){
				if (i > 1){
					if (this.dayOfWeek[i-1]-lastDay > 1) str.append("-");
					else str.append(", ");
					str.append(Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[dayOfWeek[i-1]-1]);
				}
				str.append(", ").append(Config.INST.STRINGS.DAYS_OF_WEEK_SHORT[dayOfWeek[i]-1]);
				lastDay = dayOfWeek[i];
				continue;
			}
		}
		return str.toString();
	}
	
	public String getPlace(){
		return "";
	}
	
	public String getSportName(){
		return this.sportName;
	}
}
