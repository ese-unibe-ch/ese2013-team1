package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class CancelFriendRequest extends SpringAndroidSpiceRequest<CancelFriend> {

	private String uuid;
	private int userID;
	private int friendID;
	
	public CancelFriendRequest(String uuid, int userID, int friendID) {
		super(CancelFriend.class);
		this.uuid = uuid;
		this.userID = userID;			// user id from Config.INST.USER.ID
		this.friendID = friendID;
	}

	@Override
	public CancelFriend loadDataFromNetwork() throws Exception {
		String request = "http://api.unisport.hut.by/action.php?do=cancelFriendRequest&uuid="+uuid+"&userID="+userID+"&friendID="+friendID;
        //Print.log(request);
		return getRestTemplate().getForObject(request, CancelFriend.class);
	}

	public String createCacheKey() {
		return "cancelFriend."+userID+"-"+friendID;
	}

}
