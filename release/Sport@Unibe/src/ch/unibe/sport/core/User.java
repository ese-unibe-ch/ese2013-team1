package ch.unibe.sport.core;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private int userID;
	private String nickname;
	private String username;
	private String picture;
	
	private transient User[] myFriends = new User[0];
	private transient User[] myFriendRequests = new User[0];
	private transient User[] friendRequestsToMe = new User[0];
	private transient Event[] attendedEvents;
	private transient News[] news;
	public User(){}
	
	private User(Parcel parcel) {
		this.userID = parcel.readInt();
		this.nickname = parcel.readString();
		this.username = parcel.readString();
		this.picture = parcel.readString();
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel parcel) {
			return new User(parcel);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
	
	public int getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		try{this.userID = Integer.parseInt(userID);}
		catch(NumberFormatException e) {}
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
		nameCache = null;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
		nameCache = null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(userID);
		dest.writeString(nickname);
		dest.writeString(username);
		dest.writeString(picture);
	}
	
	private String nameCache;
	public String getName(){
		if (nameCache != null) return nameCache;
		if (this.username == null || this.username.length() == 0) {
			nameCache = this.nickname;
			return this.nameCache;
		}
		else {
			nameCache = this.username + " ("+this.nickname+")";
			return nameCache;
		}
	}

	public User[] getMyFriends() {
		return myFriends;
	}

	public void setMyFriends(User[] myFriends) {
		this.myFriends = myFriends;
	}

	public User[] getMyFriendRequests() {
		return myFriendRequests;
	}

	public void setMyFriendRequests(User[] myFriendRequests) {
		this.myFriendRequests = myFriendRequests;
	}

	public User[] getFriendRequestsToMe() {
		return friendRequestsToMe;
	}

	public void setFriendRequestsToMe(User[] friendRequestsToMe) {
		this.friendRequestsToMe = friendRequestsToMe;
	}

	public Event[] getAttendedEvents() {
		return attendedEvents;
	}

	public void setAttendedEvents(Event[] attendedEvents) {
		this.attendedEvents = attendedEvents;
	}

	public News[] getNews() {
		return news;
	}

	public void setNews(News[] news) {
		this.news = news;
	}
}
