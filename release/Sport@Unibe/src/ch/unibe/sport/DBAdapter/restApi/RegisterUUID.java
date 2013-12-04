package ch.unibe.sport.DBAdapter.restApi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterUUID {
	
	private static final String OK = "OK";
	private boolean registered = false;
	private String data;
	
	public void setResult(String result){
		registered = result != null && result.equals(OK);
	}
	
	public void setMessage(String message){
	}
	
	public void setData(String data){
		this.data = data;
	}
	
	public boolean isRegistered(){
		return registered;
	}
	
	public String getUUID(){
		return data;
	}
}
