package ch.unibe.sport.DBAdapter.restApi;

public class LoginUser {
public static final String OK = "OK";
	
	private boolean error = false;
	private String message = "";
	private String result = "";
	private String[] data;
	private String hash;
	private String nickname;
	private String username;
	private int userID;
	
	public void setData(String[] data) {
		this.data = data;
		this.setHash(data[1]);
		try{this.setUserID(Integer.parseInt(data[0]));}
		catch (NumberFormatException e){};
		this.setNickname(data[2]);
		this.setUsername(data[3]);
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
		return userID;
	}
	
	public boolean isError(){
		return error;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
