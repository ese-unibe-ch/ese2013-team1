package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Date;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class AttendSpringRequest extends SpringAndroidSpiceRequest<Attend> {

	private String uuid;
	private String hash;
	private Date date;
	private boolean share;
	
	public AttendSpringRequest(String uuid, String hash, Date date,boolean share) {
		super(Attend.class);
		this.uuid = uuid;
		this.hash = hash;
		this.date = date;
		this.share = share;
	}

	@Override
	public Attend loadDataFromNetwork() throws Exception {
		return getRestTemplate().getForObject("http://api.unisport.hut.by/action.php?do=addToAttended&uuid="+uuid+"&hash="+hash+"&date="+date.toInt()+"&share="+((share) ? 1 : 0), Attend.class);
	}

}
