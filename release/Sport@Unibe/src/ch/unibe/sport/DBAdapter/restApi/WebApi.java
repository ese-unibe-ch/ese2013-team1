package ch.unibe.sport.DBAdapter.restApi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.content.Context;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.utils.Json;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

public class WebApi {
	private static final String ROOT_URL = "http://api.unisport.hut.by/action.php";
	
	private static final String DO = "do";
	
	private static final String UUID = "uuid";
	private static final String HASH = "hash";
	private static final String DATE = "date";
	public static final String SHARE = "share";
	public static final String RATING = "rating";
	
	private static final String RESULT = "result";
	private static final String MESSAGE = "message";
	private static final String DATA = "data";
	private static final String ERROR = "ERROR";
	private static final String OK = "OK";
	private static final String YES = "true";
	private static final String NO = "false";

	public static final String ATTENDED = "ATTENDED";
	public static final String RATED = "RATED";
	
	/**
	 * Adds course to attended on server
	 * @param context
	 * @param hash
	 * @param date
	 * @param share
	 * @throws Exception is course is already attended or something went wrong
	 */
	public static final void addToAttended(Context context,String hash, int date, boolean share) throws Exception {
		if (context == null || !Utils.haveNetworkConnection(context)) throw new Exception("no connection");
		StringBuilder request = new StringBuilder();
		request.append(ROOT_URL).append('?').append(DO).append('=').append("addToAttended");
		request.append('&').append(UUID).append('=').append(Config.INST.SYSTEM.UUID);
		request.append('&').append(HASH).append('=').append(hash);
		request.append('&').append(DATE).append('=').append(date);
		request.append('&').append(SHARE).append('=').append((share) ? 1 : 0);
		
		String answer = Json.getInputStream(request.toString());
		Print.log(answer);
		JSONObject root = Json.parseJson(answer);
		
		String result = (String) root.get(RESULT);
		if (result == null) throw new Exception("result is null");
		if (!result.equals(OK)) {
			if (result.equals(ERROR)){
				String message = (String) root.get(MESSAGE);
				throw new Exception((message != null) ? message : "unexpected error");
			}
			throw new Exception("unexpected error");
		}
	}
	
	/**
	 * Checks if course is attended.
	 * @param context
	 * @param hash
	 * @param date
	 * @return attendance data or null if course isn't attended
	 * @throws Exception
	 */
	public static final AssociativeList<String> isAttended(Context context, String hash, int date) throws Exception{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new Exception("no connection");
		StringBuilder request = new StringBuilder();
		request.append(ROOT_URL).append('?').append(DO).append('=').append("isAttended");
		request.append('&').append(UUID).append('=').append(Config.INST.SYSTEM.UUID);
		request.append('&').append(HASH).append('=').append(hash);
		request.append('&').append(DATE).append('=').append(date);
		
		String answer = Json.getInputStream(request.toString());
		Print.log(answer);
		JSONObject root = Json.parseJson(answer);
		
		String result = (String) root.get(RESULT);
		if (result == null) throw new Exception("result is null");
		if (!result.equals(YES) && !result.equals(NO)) {
			if (result.equals(ERROR)){
				String message = (String) root.get(MESSAGE);
				throw new Exception((message != null) ? message : "unexpected error");
			}
			throw new Exception("unexpected error");
		}
				
		if (result.equals(YES)){
			JSONArray dataArray = (JSONArray) root.get(DATA);
			if (dataArray == null || dataArray.size() != 1) throw new Exception("data array is null or wrong");
			
			JSONObject dataMap = (JSONObject) dataArray.get(0);
			if (dataMap == null) throw new Exception("data is null");		
			
			AssociativeList<String> data = new AssociativeList<String>();
			data.add((String)dataMap.get(UUID), UUID);
			data.add((String)dataMap.get(HASH), HASH);
			data.add((String)dataMap.get(DATE), DATE);
			data.add((String)dataMap.get(SHARE), SHARE);
			
			return data;
		}
		
		return null;
	}
	
	
	public static final void rate(Context context, String hash, int rating) throws Exception{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new Exception("no connection");
		StringBuilder request = new StringBuilder();
		request.append(ROOT_URL).append('?').append(DO).append('=').append("rate");
		request.append('&').append(UUID).append('=').append(Config.INST.SYSTEM.UUID);
		request.append('&').append(HASH).append('=').append(hash);
		request.append('&').append(RATING).append('=').append(rating);
		
		String answer = Json.getInputStream(request.toString());
		Print.log(answer);
		JSONObject root = Json.parseJson(answer);
		
		String result = (String) root.get(RESULT);
		if (result == null) throw new Exception("result is null");
		if (!result.equals(OK)) {
			if (result.equals(ERROR)){
				String message = (String) root.get(MESSAGE);
				throw new Exception((message != null) ? message : "unexpected error");
			}
			throw new Exception("unexpected error");
		}
	}
	
	public static final AssociativeList<String> isRated(Context context, String hash) throws Exception{
		if (context == null || !Utils.haveNetworkConnection(context)) throw new Exception("no connection");
		StringBuilder request = new StringBuilder();
		request.append(ROOT_URL).append('?').append(DO).append('=').append("isRated");
		request.append('&').append(UUID).append('=').append(Config.INST.SYSTEM.UUID);
		request.append('&').append(HASH).append('=').append(hash);
		
		String answer = Json.getInputStream(request.toString());
		Print.log(answer);
		JSONObject root = Json.parseJson(answer);
		
		String result = (String) root.get(RESULT);
		if (result == null) throw new Exception("result is null");
		if (!result.equals(YES) && !result.equals(NO)) {
			if (result.equals(ERROR)){
				String message = (String) root.get(MESSAGE);
				throw new Exception((message != null) ? message : "unexpected error");
			}
			throw new Exception("unexpected error");
		}
				
		if (result.equals(YES)){
			JSONObject dataMap = (JSONObject) root.get(DATA);
			if (dataMap == null) throw new Exception("data is null");		
			
			AssociativeList<String> data = new AssociativeList<String>();
			data.add((String)dataMap.get(UUID), UUID);
			data.add((String)dataMap.get(HASH), HASH);
			data.add((String)dataMap.get(RATING), RATING);
			
			return data;
		}
		
		return null;
	}
}
