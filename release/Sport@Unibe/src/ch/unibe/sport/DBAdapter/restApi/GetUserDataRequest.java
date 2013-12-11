package ch.unibe.sport.DBAdapter.restApi;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.os.Build;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * 
 * @author Team 1
 *
 */
public class GetUserDataRequest extends SpringAndroidSpiceRequest<String>{
	private String uuid;
	private int userID;
	
	public GetUserDataRequest(String uuid, int userID) {
		super(String.class);
		this.uuid = uuid.toString();
		this.userID = userID;
	}
	
	@Override
	public String loadDataFromNetwork() throws Exception {
		String url = "http://api.unisport.hut.by/action.php?do=getUserData&uuid="+uuid+"&userID="+userID;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}

		HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
		urlConnection.setRequestProperty("Content-Type", "application/json");
		String result = IOUtils.toString(urlConnection.getInputStream());
		urlConnection.disconnect();
		return result;
	}
}
