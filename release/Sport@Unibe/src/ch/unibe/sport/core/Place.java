package ch.unibe.sport.core;

import android.content.Context;
import ch.unibe.sport.DBAdapter.tables.EventPlaces;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.bulker.BulkKey;
import ch.unibe.sport.utils.bulker.BulkParam;
import ch.unibe.sport.utils.bulker.BulkTable;

@BulkTable(EventPlaces.NAME)
public class Place {
	public static final String TAG = Place.class.getName();
	@BulkKey
	private String pid;
	@BulkParam
	private String placeName;
	@BulkParam
	private double lat;
	@BulkParam
	private double lon;
	
	public Place(){}
	
	public Place(Context context, String placeID){
		this.pid = placeID;
		initData(context);
	}
	
	private void initData(Context context){
		if (this.pid == null || this.pid.length() <= 0){
			Print.err(TAG,"intervalID is invalid: "+this.pid);
			return;
		}
		String[] data = new EventPlaces(context).getData(pid);
		this.placeName = data[EventPlaces.PLACE_NAME];
		try {
			this.lat = Double.parseDouble(data[EventPlaces.LAT]);
			this.lon = Double.parseDouble(data[EventPlaces.LON]);
		}
		catch (NumberFormatException e){}
	}
	
	public String getPlaceID() {
		return pid;
	}
	public void setPlaceID(String pid) {
		this.pid = pid;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
}
