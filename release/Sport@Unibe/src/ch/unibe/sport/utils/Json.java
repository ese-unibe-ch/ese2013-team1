package ch.unibe.sport.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONParser;
import org.json.simple.ParseException;

public class Json {
	
	public static JSONObject parseJson(String json) throws ParseException{
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(json);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Map.Entry<String,String>> jsonObjectToSet(JSONObject jsonObject){
		return (Set<Map.Entry<String,String>>) jsonObject.entrySet();
	}
	
	public static String getInputStream(String url) throws Exception {
		URL ulr = new URL(url);
		HttpURLConnection httpConn = (HttpURLConnection)ulr.openConnection();
		httpConn.setConnectTimeout(15000);
		InputStream inputStream = httpConn.getInputStream();
		return convertStreamToString(inputStream);
	}

	public static String convertStreamToString(InputStream is) {
		if (is == null) return "";
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	@SuppressWarnings("unchecked")
	public static AssociativeList<String>[] buildAssociativeListOfSubCourses(JSONObject resultObject) {
		AssociativeList<String>[] courses = null;
		if (resultObject.containsKey("result")){
			resultObject = (JSONObject) resultObject.get("result");
			Set<String> keySet = (Set<String>) resultObject.keySet();
			Iterator<String> keySetIterator = keySet.iterator();
			if (keySetIterator.hasNext()){
				String key = keySetIterator.next();
				Print.log("key: "+key);
				JSONArray resultArray = (JSONArray)resultObject.get(key);
				if (resultArray.size() > 0){
					int subCourseCount = resultArray.size();
					courses = new AssociativeList[subCourseCount];
					for (int i = 0; i < subCourseCount; i++){
						courses[i] = new AssociativeList<String>();
						JSONObject courseData = (JSONObject) resultArray.get(i);
						Set<String> courseDataKeySet = courseData.keySet();
						Iterator<String> subCourseKeySetIterator = courseDataKeySet.iterator();
						while (subCourseKeySetIterator.hasNext()){
							String subCourseKey = subCourseKeySetIterator.next();
							courses[i].add((String)courseData.get(subCourseKey), subCourseKey);
						}
					}
				}
			}
		}
		return courses;
	}
}
