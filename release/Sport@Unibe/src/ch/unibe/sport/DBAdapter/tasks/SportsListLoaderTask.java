package ch.unibe.sport.DBAdapter.tasks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.ParseException;

import ch.unibe.sport.DBAdapter.DBUpdate;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.DBAdapter.tables.QuerySyntaxException;
import ch.unibe.sport.DBAdapter.tables.TableAlreadyExistsException;
import ch.unibe.sport.DBAdapter.tables.TableNotExistsException;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import ch.unibe.sport.utils.Json;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import android.content.Context;

/**
 * Loads courses data from internet
 * @author Aliaksei Syrel
 */
public class SportsListLoaderTask extends ObservableAsyncTask<Void,Void,Boolean>{

	private Context context;
	private String url;
	private Sports coursesDB;

	public SportsListLoaderTask(Context context,String url){
		this.context = context;
		this.url = url;
	}
	
	@Override
	protected Boolean doInBackground(Void... param) {
		coursesDB = new Sports(context);
		try {
			coursesDB.clear();
		} catch (TableNotExistsException e) {
			e.printStackTrace();
			DBUpdate updateDB = new DBUpdate(context);
			try {
				updateDB.dbFullFormat();
			} catch (TableNotExistsException e1) {
			} catch (TableAlreadyExistsException e1) {
				e1.printStackTrace();
				return false;
			} catch (QuerySyntaxException e1) {
			}
		}
		String json;
		try {
			json = Json.getInputStream(url);
		} catch (Exception e1) {
			Print.err("[SportsListLoader] Connection error");
			return false;
		}
		
		if (json.length() == 0) return false;
		JSONObject array = null;
		try {
			array = Json.parseJson(json);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		if (array == null) return false;
		String[][] paramValues = null;
		boolean result = false;
		if(array.containsKey("result")){
			JSONObject jsonObject = (JSONObject) array.get("result");
			Set<Map.Entry<String,String>> set = Json.jsonObjectToSet(jsonObject);
			Iterator<Map.Entry<String, String>> iterator = set.iterator();
			paramValues = new String[set.size()][2];
			int i = 0;
			Map.Entry<String, String> entry = null;
			while (iterator.hasNext()){
				entry = iterator.next();
				paramValues[i][0] = entry.getKey();
				paramValues[i][1] = entry.getValue();
				i++;
			}
			Arrays.sort(paramValues, new Comparator<String[]>() {
	            @Override
	            public int compare(final String[] entry1, final String[] entry2) {
	                final int id1 = Utils.Int(entry1[0]);
	                final int id2 = Utils.Int(entry2[0]);
	                return Utils.compare(id1, id2);
	            }
	        });
			if (paramValues.length > 0){
				result = coursesDB.insertSports(paramValues);
			}
		}
		return result;
	}

}
