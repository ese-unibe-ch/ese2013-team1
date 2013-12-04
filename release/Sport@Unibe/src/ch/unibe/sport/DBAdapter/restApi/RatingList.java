package ch.unibe.sport.DBAdapter.restApi;

import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingList {

	private String result;
	private LinkedHashMap<String,Integer> data;
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public void setData(LinkedHashMap<String,Integer> data){
		this.data = data;
	}
	
	public int[] getRating(){
		if (data == null) return null;
		int[] rating = new int[5];
		
		for (int i = 1; i <=5 ;i++){
			rating[i-1] = this.data.get(""+i);
		}
		return rating;
	}
}