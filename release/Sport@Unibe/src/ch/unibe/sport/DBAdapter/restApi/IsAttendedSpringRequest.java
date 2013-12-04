package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Date;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class IsAttendedSpringRequest extends SpringAndroidSpiceRequest<IsAttended> {

	private String uuid;
	private String hash;
	private Date date;
	
	public IsAttendedSpringRequest(String uuid, String hash, Date date) {
		super(IsAttended.class);
		this.uuid = uuid;
		this.hash = hash;
		this.date = date;
	}

	@Override
	public IsAttended loadDataFromNetwork() throws Exception {
		return getRestTemplate().getForObject("http://api.unisport.hut.by/action.php?do=isAttended&uuid="+uuid+"&hash="+hash+"&date="+date.toInt(), IsAttended.class);
	}

}
