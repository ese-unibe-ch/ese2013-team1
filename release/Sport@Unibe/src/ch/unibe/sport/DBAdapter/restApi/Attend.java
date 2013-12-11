package ch.unibe.sport.DBAdapter.restApi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 * @author Team 1
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attend{
	private static final String ATTENDED = "ATTENDED";
	public static final String OK = "OK";
	
	private boolean error = false;
	private String message = "";
	private String result = "";
	
	public void setResult(String result){
		this.result = result;
		error = result != null && !result.equals(OK);
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public String getResult(){
		return result;
	}
	
	public boolean isAttended(){
		return isError() && getMessage().equals(ATTENDED);
	}
	
	public boolean isError(){
		return error;
	}
}
