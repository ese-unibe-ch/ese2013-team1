package ch.unibe.sport.DBAdapter.restApi;

import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class RegisterUUIDRequest extends SpringAndroidSpiceRequest<RegisterUUID>{
	
	private String uuid;
	
    public RegisterUUIDRequest(String uuid) {
        super(RegisterUUID.class);
        Print.log(uuid);
        this.uuid = uuid;
    }

    @Override
    public RegisterUUID loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject("http://api.unisport.hut.by/action.php?do=registerUUID&uuid="+uuid, RegisterUUID.class );
    }
}
