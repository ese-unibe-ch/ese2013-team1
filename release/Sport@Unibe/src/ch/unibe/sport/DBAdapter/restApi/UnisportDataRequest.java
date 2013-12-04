package ch.unibe.sport.DBAdapter.restApi;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.os.Build;

import com.octo.android.robospice.request.SpiceRequest;

public class UnisportDataRequest extends SpiceRequest<String> {

	public UnisportDataRequest() {
		super(String.class);
	}

	@Override
	public String loadDataFromNetwork() throws Exception {
		String url = "http://api.unisport.hut.by/unisport.php";

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
