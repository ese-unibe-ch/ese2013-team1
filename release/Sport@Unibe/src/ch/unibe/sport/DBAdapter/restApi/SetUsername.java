package ch.unibe.sport.DBAdapter.restApi;

public class SetUsername {
public static final String OK = "OK";
	
	private boolean error = false;
	private String message = "";
	private String result = "";
	private String data;
	
	public void setData(String data) {
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
	
	public boolean isError(){
		return error;
	}
}
