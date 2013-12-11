package ch.unibe.sport.DBAdapter.restApi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterUser {
	public static final String OK = "OK";
	
	private boolean error = false;
	private String message = "";
	private String result = "";
	private int data;
	
	public void setData(int data) {
		this.data = data;
	}
	
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
	
	public int getUserID(){
		return data;
	}
	
	public boolean isError(){
		return error;
	}
	
}
