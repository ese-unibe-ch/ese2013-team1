package ch.unibe.sport.core;

import java.sql.Timestamp;

import ch.unibe.sport.utils.Date;

public class News {
	
	private int userID;
	private String nickname;
	private String username;
	private String picture;
	private String hash;
	private String eventName;
	private Date date;
	private Timestamp stamp;
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		try {this.userID = Integer.parseInt(userID);}
		catch(NumberFormatException e){}
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(String date) {
		try {this.date = new Date(Integer.parseInt(date));}
		catch(NumberFormatException e){}
	}
	public Timestamp getStamp() {
		return stamp;
	}
	public void setStamp(String stamp) {
		if (stamp != null){
			this.stamp = Timestamp.valueOf(stamp);
		}
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
}
