package ch.unibe.sport.DBAdapter.restApi;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class LoginUserRequest extends SpringAndroidSpiceRequest<LoginUser>{
	private String nickname;
	private String password;
	
    public LoginUserRequest(String nickname, String password) {
        super(LoginUser.class);
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    public LoginUser loadDataFromNetwork() throws Exception {
    	String request = "http://api.unisport.hut.by/action.php?do=loginUser&nickname="+nickname+"&password="+password;
        return getRestTemplate().getForObject(request, LoginUser.class);
    }
}
