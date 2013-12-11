package ch.unibe.sport.DBAdapter.restApi;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.os.Build;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class SearchNewFriendsRequest extends SpringAndroidSpiceRequest<String>{

	private String uuid;
	private int userID;
	private String query;
	
	public SearchNewFriendsRequest(String uuid, int userID, String query) {
		super(String.class);
		this.uuid = uuid.toString();
		this.userID = userID;
		this.query = query.toString();
	}
	
	@Override
	public String loadDataFromNetwork() throws Exception {
		String url = "http://api.unisport.hut.by/action.php?do=findNewFriends&uuid="+uuid+"&userID="+userID+"&query="+query;

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
