package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class SetUsernameRequest extends SpringAndroidSpiceRequest<SetUsername> {
	private String uuid;
	private int userID;
	private String username;
	
	public SetUsernameRequest(String uuid, int userID, String username) {
        super(SetUsername.class);
        this.uuid = uuid;
        this.userID = userID;
        this.username = username;
    }

    @Override
    public SetUsername loadDataFromNetwork() throws Exception {
    	String request = "http://api.unisport.hut.by/action.php?do=setUsername&uuid="+uuid+"&userID="+userID+"&username="+username;
    	//Print.log(request);
        return getRestTemplate().getForObject(request, SetUsername.class);
    }
}
