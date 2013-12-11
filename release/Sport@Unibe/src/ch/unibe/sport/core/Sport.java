package ch.unibe.sport.core;

import android.content.Context;
import android.util.SparseIntArray;
import ch.unibe.sport.DBAdapter.tables.SportEvents;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.bulker.BulkKey;
import ch.unibe.sport.utils.bulker.BulkParam;
import ch.unibe.sport.utils.bulker.BulkRelation;
import ch.unibe.sport.utils.bulker.BulkTable;

/**
 * Class that handles the sports' information
 * 
 * @author Team 1
 *
 */

@BulkTable(Sports.NAME)
public class Sport {
	public static final String TAG = Sport.class.getName();
	
	@BulkKey
	private int sid;
	@BulkParam
	private String hash;
	@BulkParam
	private String sportName;
	@BulkParam
	private String sportLink;
	@BulkParam
	private String sportImage;
	@BulkParam
	private String descriptionHeader;
	
	@BulkRelation(SportEvents.NAME)
	private Event[] events;
	
	private SparseIntArray eventIDs;
	
	public Sport(){}
	
	public Sport(Context context,int sportID){
		this.sid = sportID;
		eventIDs = new SparseIntArray();
		events = new Event[0];
		initData(context);
	}
	
	private void initData(Context context){
		if (this.sid <= 0){
			Print.err(TAG,"SportID is valid: "+this.sid);
		}
		
		Sports sportDB = new Sports(context);
		String[] data = sportDB.getData(sid);
		this.hash = data[Sports.HASH];
		this.sportLink = data[Sports.SPORT_LINK];
		this.sportName = data[Sports.SPORT_NAME];
		this.sportImage = data[Sports.SPORT_IMAGE];
		this.descriptionHeader = data[Sports.DESCRIPTION_HEADER];
		int[] eventIDs = sportDB.getEventIDs(sid);
		this.events = new Event[eventIDs.length];
		for (int i = 0,length = eventIDs.length; i < length; i++){
			this.events[i] = new Event(context,eventIDs[i]);
			this.eventIDs.put(eventIDs[i], i);
		}
	}
	
	public Event getEvent(int eventID){
		int index = this.eventIDs.get(eventID,-1);
		if (index >=0 && index < events.length) return events[index];
		else return null;
	}
	
	public Event getEventAt(int index){
		if (index >=0 && index < events.length) return events[index];
		else return null;
	}
	
	public int getEventCount(){
		return this.events.length;
	}
	
	public void setSportID(int sportID){
		this.sid = sportID;
	}
	
	public int getSportID(){
		return this.sid;
	}
	
	public String getSportHash() {
		return hash;
	}
	
	public void setSportHash(String sportHash) {
		this.hash = sportHash;
	}
	
	public String getSportName() {
		return sportName;
	}
	
	public void setSportName(String sportName) {
		this.sportName = sportName;
	}
	
	public String getSportLink() {
		return sportLink;
	}
	
	public void setSportLink(String sportLink) {
		this.sportLink = sportLink;
	}
	
	public Event[] getEvents() {
		return events;
	}
	
	public void setEvents(Event[] events) {
		this.events = events;
	}

	public String getSportImage() {
		return sportImage;
	}

	public void setSportImage(String sportImage) {
		this.sportImage = sportImage;
	}

	public String getDescriptionHeader() {
		return descriptionHeader;
	}

	public void setDescriptionHeader(String descriptionHeader) {
		this.descriptionHeader = descriptionHeader;
	}
}
