package ch.unibe.sport.DBAdapter.restApi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import ch.unibe.sport.core.User;
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindUserList {
	private User[] users = new User[0];

	public User[] getUsers() {
		return users;
	}

	public void setUsers(User[] users) {
		this.users = users;
	}
	
	public int size(){
		return users.length;
	}
}
