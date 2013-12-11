package ch.unibe.sport.DBAdapter.restApi;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class RegisterUserRequest extends SpringAndroidSpiceRequest<RegisterUser> {
	private String uuid;
	private String nickname;
	private String password;
	
    public RegisterUserRequest(String uuid,String nickname, String password) {
        super(RegisterUser.class);
        this.uuid = uuid;
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    public RegisterUser loadDataFromNetwork() throws Exception {
    	String request = "http://api.unisport.hut.by/action.php?do=registerUser&uuid="+uuid+"&nickname="+nickname+"&password="+password;
        return getRestTemplate().getForObject(request, RegisterUser.class);
    }
}
