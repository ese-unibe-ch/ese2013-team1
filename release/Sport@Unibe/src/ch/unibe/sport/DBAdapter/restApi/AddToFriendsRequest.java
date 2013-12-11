package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class AddToFriendsRequest extends SpringAndroidSpiceRequest<AddToFriends>{

	private String uuid;
	private int senderUserID;
	private int receiverUserID;

	public AddToFriendsRequest(String uuid, int receiverUserID, int senderUserID) {
		super(AddToFriends.class);
		this.uuid = uuid;
		this.receiverUserID = receiverUserID;
		this.senderUserID = senderUserID;
	}

	@Override
	public AddToFriends loadDataFromNetwork() throws Exception {
		String request = "http://api.unisport.hut.by/action.php?do=sendFriendRequest&uuid="+uuid+"&userID="+receiverUserID+"&friendID="+senderUserID;
		//Print.log(request);
		return getRestTemplate().getForObject(request, AddToFriends.class);
	}

	public String createCacheKey() {
		return "addToFriends."+senderUserID+"-"+receiverUserID;
	}

}
