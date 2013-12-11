package ch.unibe.sport.main.friends;

import ch.unibe.sport.core.User;

public interface OnNotifyDataSetChangedListener {
	public void onDataSetChanged();
	public void onAddFriend(User user);
}
