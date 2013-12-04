package ch.unibe.sport.DBAdapter.restApi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Attend {
	private static final String OK = "OK";
	private static final String ATTENDED = "ATTENDED";
	
	private boolean error = false;
	
	private String message = "";
	
	public void setResult(String result){
		error = result != null && !result.equals(OK);
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setData(String data){
		
	}
	
	public boolean isError(){
		return error;
	}
	
	public boolean isAttended(){
		return error && message.equals(ATTENDED);
	}
}
