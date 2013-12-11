package ch.unibe.sport.main.friends;

import java.util.ArrayList;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.core.User;

public class FriendsFindNewResultAdapter extends BaseAdapter {

	private User[] users;
	private ArrayList<Integer> addedToFriends;
	private ArrayList<Integer> waitingToAddFriends;
	private RelativeLayout tmpView;
	private OnCheckedChangeListener  mOnCheckedChangeListener;
	
	public FriendsFindNewResultAdapter(User[] users){
		this.addedToFriends = new ArrayList<Integer>();
		this.waitingToAddFriends = new ArrayList<Integer>();
		this.users = users;
	}
	
	@Override
	public int getCount() {
		return users.length;
	}

	@Override
	public Object getItem(int position) {
		return users[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setOnClickListener(OnCheckedChangeListener l){
		mOnCheckedChangeListener = l;
	}
	
	public boolean addToWaiting(int userID){
		if (!waitingToAddFriends.contains(Integer.valueOf(userID))){
			this.waitingToAddFriends.add(Integer.valueOf(userID));
			this.notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public void removeFromWaiting(int userID) {
		this.waitingToAddFriends.remove(Integer.valueOf(userID));
		this.notifyDataSetChanged();
	}
	
	public boolean addToAdded(int userID){
		if (!addedToFriends.contains(Integer.valueOf(userID))){
			this.addedToFriends.add(Integer.valueOf(userID));
			this.notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public boolean isAdded(int userID){
		return this.addedToFriends.contains(Integer.valueOf(userID));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		tmpView = (RelativeLayout)convertView;
		if (tmpView == null) {
			tmpView = (RelativeLayout) View.inflate(parent.getContext(), R.layout.friends_find_list_entry, null);
	    }
		((TextView)tmpView.findViewById(R.id.name)).setText(users[position].getName());
		ToggleButton button = (ToggleButton)tmpView.findViewById(R.id.add);
		button.setTag(users[position]);
		button.setOnCheckedChangeListener(null);
		if (this.addedToFriends.contains(Integer.valueOf(users[position].getUserID()))){
			button.setClickable(false);
			button.setChecked(true);
			button.setText("Request sent");
			tmpView.setBackgroundColor(0x88d5ffd9);
		}
		else if (this.waitingToAddFriends.contains(Integer.valueOf(users[position].getUserID()))){
			button.setClickable(false);
			button.setChecked(true);
			button.setText(Html.fromHtml("<i>Sending request...<i>"));
			tmpView.setBackgroundColor(0);
		}
		else {
			button.setClickable(true);
			button.setChecked(false);
			button.setText(Html.fromHtml("<u>Send friend request<u>"));
			tmpView.setBackgroundColor(0);
			button.setOnCheckedChangeListener(mOnCheckedChangeListener);
		}
		return tmpView;
	}

	

}
