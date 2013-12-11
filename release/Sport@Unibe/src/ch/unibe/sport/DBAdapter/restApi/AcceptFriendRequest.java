package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class AcceptFriendRequest extends SpringAndroidSpiceRequest<AcceptFriend> {

	private String uuid;
	private int userID;
	private int friendID;
	
	public AcceptFriendRequest(String uuid, int userID, int friendID) {
		super(AcceptFriend.class);
		this.uuid = uuid;
		this.userID = userID;			// user id from Config.INST.USER.ID
		this.friendID = friendID;
	}

	@Override
	public AcceptFriend loadDataFromNetwork() throws Exception {
		String request = "http://api.unisport.hut.by/action.php?do=acceptFriendRequest&uuid="+uuid+"&userID="+userID+"&friendID="+friendID;
		//Print.log(request);
        return getRestTemplate().getForObject(request, AcceptFriend.class);
	}
	
	public String createCacheKey() {
		return "acceptFriend."+userID+"-"+friendID;
	}

}
