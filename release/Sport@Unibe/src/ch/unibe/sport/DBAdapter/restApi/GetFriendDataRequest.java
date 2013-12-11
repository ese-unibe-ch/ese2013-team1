package ch.unibe.sport.DBAdapter.restApi;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.os.Build;
import ch.unibe.sport.utils.Date;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class GetFriendDataRequest extends SpringAndroidSpiceRequest<String> {
	private String uuid;
	private int userID;
	private int friendID;
	private Date date;
	
	public GetFriendDataRequest(String uuid, int userID, int friendID, Date date) {
		super(String.class);
		this.uuid = uuid.toString();
		this.userID = userID;
		this.friendID = friendID;
		this.date = date;
	}
	
	@Override
	public String loadDataFromNetwork() throws Exception {
		String url = "http://api.unisport.hut.by/action.php?do=getFriendData&uuid="+uuid+"&userID="+userID+"&friendID="+friendID+"&date="+date.toInt();

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
