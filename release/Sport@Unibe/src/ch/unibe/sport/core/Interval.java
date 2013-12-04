package ch.unibe.sport.core;

import android.content.Context;
import ch.unibe.sport.DBAdapter.tables.EventIntervals;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.utils.bulker.BulkKey;
import ch.unibe.sport.utils.bulker.BulkParam;
import ch.unibe.sport.utils.bulker.BulkTable;

@BulkTable(EventIntervals.NAME)
public class Interval {
	public static final String TAG = Interval.class.getName();
	
	@BulkKey
	private String iid;
	@BulkParam
	private int timeFrom;
	@BulkParam
	private int timeTo;
	
	private Time mTimeFrom;
	private Time mTimeTo;
	
	@BulkParam
	private String status;
	
	public Interval(){}
	
	public Interval(Context context, String intervalID){
		this.iid = intervalID;
		initData(context);
	}
	
	public Interval(Time timeFrom, Time timeTo) {
		this.mTimeFrom = timeFrom;
		this.mTimeTo = timeTo;
	}

	private void initData(Context context){
		if (this.iid == null || this.iid.length() <= 0){
			Print.err(TAG,"intervalID is invalid: "+this.iid);
			return;
		}
		String[] data = new EventIntervals(context).getData(iid);
		this.status = data[EventIntervals.STATUS];
		this.timeFrom = Utils.Int(data[EventIntervals.TIME_FROM]);
		this.timeTo = Utils.Int(data[EventIntervals.TIME_TO]);
		this.mTimeFrom = new Time(timeFrom);
		this.mTimeTo = new Time(timeTo);
	}
	
	public void setIntervalID(String intervalID){
		this.iid = intervalID;
	}
	
	public String getIntervalID(){
		return this.iid;
	}
	
	public Time getTimeFrom() {
		return mTimeFrom;
	}
	
	public void setTimeFrom(Time mTimeFrom) {
		this.mTimeFrom = mTimeFrom;
		this.timeFrom = this.mTimeFrom.toMinutes();
	}
	
	public Time getTimeTo() {
		return mTimeTo;
	}
	
	public void setTimeTo(Time mTimeTo) {
		this.mTimeTo = mTimeTo;
		this.timeTo = this.mTimeTo.toMinutes();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String mStatus) {
		this.status = mStatus;
	}
	
	@Override
	public String toString(){
		if (status != null) return status;
		return this.mTimeFrom.toString()+"-"+this.mTimeTo.toString();
	}
	
}
