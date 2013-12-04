package ch.unibe.sport.DBAdapter.restApi;

import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IsAttended {

	private static final String ERROR = "ERROR";
	private static final String YES = "true";
	
	private boolean attended = false;
	private boolean error = false;
	
	private String result;
	
	private String message;
	
	private LinkedHashMap<String,String> data;
	
	public void setResult(String result){
		this.result = result;
		attended = result.equals(YES);
		error = result.equals(ERROR);
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setData(LinkedHashMap<String,String> data){
		this.data = data;
	}
	
	public boolean isError(){
		return error;
	}
	
	public boolean isAttended(){
		return attended;
	}
	
	public String getResult(){
		return result;
	}
}
