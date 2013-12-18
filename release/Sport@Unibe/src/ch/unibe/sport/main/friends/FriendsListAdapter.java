package ch.unibe.sport.main.friends;

import java.util.ArrayList;

import android.content.Context;
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
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.widget.layout.SeparatedListAdapter;

public class FriendsListAdapter extends SeparatedListAdapter implements OnNotifyDataSetChangedListener {
	public abstract static class SectionAdapter extends BaseAdapter{
		private String sectionName;
		public SectionAdapter(String sectionName){
			this.sectionName = sectionName;
		}
		
		public String getSectionName(){
			return this.sectionName;
		}
	}
	
	
	public static class FriendRequestsToMeAdapter extends SectionAdapter {

		private AssociativeList<User> friendRequestsToMe;
		private ArrayList<Integer> acceptedFriends;
		private ArrayList<Integer> waitingToAcceptFriends;

		private ArrayList<Integer> canceledFriends;
		private ArrayList<Integer> waitingToCancelFriends;
		
		private RelativeLayout tmpView;
		private FriendRequestsToMeViewHolder viewHolder;
		private OnCheckedChangeListener mOnAcceptedCheckedChangeListener;
		private OnCheckedChangeListener mOnCanceledCheckedChangeListener;
		private OnNotifyDataSetChangedListener mOnNotifyDataSetChangedListener;
		
		
		public FriendRequestsToMeAdapter(User[] users){
			super("Friend requests to you:");
			this.acceptedFriends = new ArrayList<Integer>();
			this.waitingToAcceptFriends = new ArrayList<Integer>();
			this.canceledFriends = new ArrayList<Integer>();
			this.waitingToCancelFriends = new ArrayList<Integer>();
			friendRequestsToMe = new AssociativeList<User>();
			for (User u : users){
				friendRequestsToMe.add(u,u.getUserID());
			}
		}
		
		public void setOnNotifyDataSetChangedListener(OnNotifyDataSetChangedListener l){
			this.mOnNotifyDataSetChangedListener = l;
		}
		
		@Override
		public int getCount() {
			return friendRequestsToMe.size();
		}

		@Override
		public Object getItem(int position) {
			return friendRequestsToMe.getAt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public boolean addToAcceptedWaiting(int userID){
			if (!waitingToAcceptFriends.contains(Integer.valueOf(userID))){
				this.waitingToAcceptFriends.add(Integer.valueOf(userID));
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
				return true;
			}
			return false;
		}
		
		public void removeFromActeptedWaiting(int userID) {
			this.waitingToAcceptFriends.remove(Integer.valueOf(userID));
			if (mOnNotifyDataSetChangedListener != null){
				mOnNotifyDataSetChangedListener.onDataSetChanged();
			}
		}
		
		public boolean addToAccepted(int userID){
			if (!acceptedFriends.contains(Integer.valueOf(userID))){
				this.acceptedFriends.add(Integer.valueOf(userID));
				User user = this.friendRequestsToMe.remove(userID);
				this.waitingToAcceptFriends.remove(Integer.valueOf(userID));
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
					mOnNotifyDataSetChangedListener.onAddFriend(user);
				}
				this.acceptedFriends.remove(Integer.valueOf(userID));
				return true;
			}
			return false;
		}
		
		public boolean isAccepted(int userID){
			return this.acceptedFriends.contains(Integer.valueOf(userID));
		}
		
		public boolean addToCanceledWaiting(int userID){
			if (!waitingToCancelFriends.contains(Integer.valueOf(userID))){
				this.waitingToCancelFriends.add(Integer.valueOf(userID));
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
				return true;
			}
			return false;
		}
		
		public void removeFromCanceledWaiting(int userID) {
			this.waitingToCancelFriends.remove(Integer.valueOf(userID));
			if (mOnNotifyDataSetChangedListener != null){
				mOnNotifyDataSetChangedListener.onDataSetChanged();
			}
		}
		
		public boolean addToCanceled(int userID){
			if (!canceledFriends.contains(Integer.valueOf(userID))){
				this.canceledFriends.add(Integer.valueOf(userID));
				this.friendRequestsToMe.remove(userID);
				this.waitingToCancelFriends.remove(Integer.valueOf(userID));
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
				this.canceledFriends.remove(Integer.valueOf(userID));
				return true;
			}
			return false;
		}
		
		public boolean isCanceled(int userID){
			return this.canceledFriends.contains(Integer.valueOf(userID));
		}
		
		public void setOnAcceptedClickListener(OnCheckedChangeListener l){
			mOnAcceptedCheckedChangeListener = l;
		}
		
		public void setOnCanceledClickListener(OnCheckedChangeListener l){
			mOnCanceledCheckedChangeListener = l;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			tmpView = (RelativeLayout) View.inflate(parent.getContext(), R.layout.friends_list_request_to_me_entry, null);
			viewHolder = new FriendRequestsToMeViewHolder(tmpView);
			viewHolder.nameView.setText(friendRequestsToMe.getAt(position).getName());
			initAcceptButton(viewHolder,friendRequestsToMe.getAt(position));
			initCancelButton(viewHolder,friendRequestsToMe.getAt(position));
			return tmpView;
		}
		
		private void initAcceptButton(FriendRequestsToMeViewHolder holder,User user){
			holder.acceptButton.setOnCheckedChangeListener(null);
			holder.acceptButton.setTag(user);
			if (this.acceptedFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.acceptButton.setClickable(false);
				holder.acceptButton.setChecked(true);
				holder.acceptButton.setText("Accepted");
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(false);
				holder.cancelButton.setOnCheckedChangeListener(null);
			}
			else if (this.waitingToAcceptFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.acceptButton.setClickable(false);
				holder.acceptButton.setChecked(true);
				holder.acceptButton.setText(Html.fromHtml("<i>Accepting...<i>"));
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(false);
				holder.cancelButton.setOnCheckedChangeListener(null);
			}
			else {
				if (this.canceledFriends.contains(Integer.valueOf(user.getUserID()))
						|| this.waitingToCancelFriends.contains(Integer.valueOf(user.getUserID()))){
					holder.acceptButton.setClickable(false);
					holder.acceptButton.setChecked(false);
				}
				else {
					holder.acceptButton.setClickable(true);
					holder.acceptButton.setChecked(false);
					holder.acceptButton.setOnCheckedChangeListener(mOnAcceptedCheckedChangeListener);
				}
				holder.acceptButton.setText(Html.fromHtml("<u>Accept<u>"));
			}
		}
		
		private void initCancelButton(FriendRequestsToMeViewHolder holder,User user){
			holder.cancelButton.setOnCheckedChangeListener(null);
			holder.cancelButton.setTag(user);
			if (this.canceledFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(true);
				holder.cancelButton.setText("Rejected");
				holder.acceptButton.setClickable(false);
				holder.acceptButton.setChecked(false);
				holder.acceptButton.setOnCheckedChangeListener(null);
			}
			else if (this.waitingToCancelFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(true);
				holder.cancelButton.setText(Html.fromHtml("<i>Rejecting...<i>"));
				holder.acceptButton.setClickable(false);
				holder.acceptButton.setChecked(false);
				holder.acceptButton.setOnCheckedChangeListener(null);
			}
			else {
				if (this.acceptedFriends.contains(Integer.valueOf(user.getUserID()))
						|| this.waitingToAcceptFriends.contains(Integer.valueOf(user.getUserID()))){
					holder.cancelButton.setClickable(false);
					holder.cancelButton.setChecked(false);
				}
				else {
					holder.cancelButton.setClickable(true);
					holder.cancelButton.setChecked(false);
					holder.cancelButton.setOnCheckedChangeListener(mOnCanceledCheckedChangeListener);
				}
				holder.cancelButton.setText(Html.fromHtml("<u>Reject<u>"));
			}
		}
		
		private class FriendRequestsToMeViewHolder {
			private final TextView nameView;
			private final ToggleButton acceptButton;
			private final ToggleButton cancelButton;
			
			private FriendRequestsToMeViewHolder(ViewGroup view){
				nameView = (TextView) view.findViewById(R.id.name);
				acceptButton = (ToggleButton) view.findViewById(R.id.accept_request);
				cancelButton = (ToggleButton) view.findViewById(R.id.cancel_request);
			}
		}
	}
	
	public static class MyFriendsAdapter extends SectionAdapter {

		private AssociativeList<User> myFriends;
		private OnNotifyDataSetChangedListener mOnNotifyDataSetChangedListener;
		
		public MyFriendsAdapter(User[] users){
			super("Friends:");
			myFriends = new AssociativeList<User>();
			for (User u : users){
				myFriends.add(u,u.getUserID());
			}
		}
		
		public void setOnNotifyDataSetChangedListener(OnNotifyDataSetChangedListener l){
			this.mOnNotifyDataSetChangedListener = l;
		}
		
		@Override
		public int getCount() {
			return myFriends.size();
		}

		@Override
		public Object getItem(int position) {
			return myFriends.getAt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout view = (RelativeLayout) View.inflate(parent.getContext(), R.layout.friends_list_entry, null);
			((TextView)view.findViewById(R.id.name)).setText(myFriends.getAt(position).getName());
			view.setTag(Integer.valueOf(myFriends.getAt(position).getUserID()));
			return view;
		}

		public void addFriend(User user) {
			if (!myFriends.containsKey(user.getUserID())){
				myFriends.add(user, user.getUserID());
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
			}
		}
	}
	
	public static class MyFriendRequestsAdapter extends SectionAdapter  {

		private AssociativeList<User> myFriendRequests;
		private ArrayList<Integer> canceledFriends;
		private ArrayList<Integer> waitingToCancelFriends;

		private RelativeLayout tmpView;
		private FriendMyRequestViewHolder viewHolder;
		private OnCheckedChangeListener mOnCanceledCheckedChangeListener;
		private OnNotifyDataSetChangedListener mOnNotifyDataSetChangedListener;
		
		public MyFriendRequestsAdapter(User[] users){
			super("Your friend requests:");
			this.canceledFriends = new ArrayList<Integer>();
			this.waitingToCancelFriends = new ArrayList<Integer>();
			myFriendRequests = new AssociativeList<User>();
			for (User u : users){
				myFriendRequests.add(u,u.getUserID());
			}
		}
		
		public void addMyFriendRequest(User user) {
			if (!myFriendRequests.containsKey(user.getUserID())){
				myFriendRequests.add(user,user.getUserID());
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
			}
		}
		
		public void setOnNotifyDataSetChangedListener(OnNotifyDataSetChangedListener l){
			this.mOnNotifyDataSetChangedListener = l;
		}
		
		@Override
		public int getCount() {
			return myFriendRequests.size();
		}

		@Override
		public Object getItem(int position) {
			return myFriendRequests.getAt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public boolean addToWaiting(int userID){
			if (!waitingToCancelFriends.contains(Integer.valueOf(userID))){
				this.waitingToCancelFriends.add(Integer.valueOf(userID));
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
				return true;
			}
			return false;
		}
		
		public void removeFromWaiting(int userID) {
			this.waitingToCancelFriends.remove(Integer.valueOf(userID));
			if (mOnNotifyDataSetChangedListener != null){
				mOnNotifyDataSetChangedListener.onDataSetChanged();
			}
		}
		
		public boolean addToCanceled(int userID){
			if (!canceledFriends.contains(Integer.valueOf(userID))){
				this.canceledFriends.add(Integer.valueOf(userID));
				this.waitingToCancelFriends.remove(Integer.valueOf(userID));
				this.myFriendRequests.remove(userID);
				if (mOnNotifyDataSetChangedListener != null){
					mOnNotifyDataSetChangedListener.onDataSetChanged();
				}
				this.canceledFriends.remove(Integer.valueOf(userID));
				return true;
			}
			return false;
		}
		
		public boolean isCanceled(int userID){
			return this.canceledFriends.contains(Integer.valueOf(userID));
		}
		
		public void setOnCanceledClickListener(OnCheckedChangeListener l){
			mOnCanceledCheckedChangeListener = l;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			tmpView = (RelativeLayout) View.inflate(parent.getContext(), R.layout.friends_list_my_request, null);
			viewHolder = new FriendMyRequestViewHolder(tmpView);
			viewHolder.nameView.setText(myFriendRequests.getAt(position).getName());
			initCancelButton(viewHolder,myFriendRequests.getAt(position));
			return tmpView;
		}
		
		private void initCancelButton(FriendMyRequestViewHolder holder,User user){
			holder.cancelButton.setOnCheckedChangeListener(null);
			holder.cancelButton.setTag(user);
			if (this.canceledFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(true);
				holder.cancelButton.setText("Canceled");
			}
			else if (this.waitingToCancelFriends.contains(Integer.valueOf(user.getUserID()))){
				holder.cancelButton.setClickable(false);
				holder.cancelButton.setChecked(true);
				holder.cancelButton.setText(Html.fromHtml("<i>Canceling...<i>"));
			}
			else {
				holder.cancelButton.setClickable(true);
				holder.cancelButton.setChecked(false);
				holder.cancelButton.setText(Html.fromHtml("<u>Cancel<u>"));
				holder.cancelButton.setOnCheckedChangeListener(mOnCanceledCheckedChangeListener);
			}
		}
		
		private class FriendMyRequestViewHolder {
			private final TextView nameView;
			private final ToggleButton cancelButton;
			
			private FriendMyRequestViewHolder(ViewGroup view){
				nameView = (TextView) view.findViewById(R.id.name);
				cancelButton = (ToggleButton) view.findViewById(R.id.cancel);
			}
		}	
	}
	
	private FriendRequestsToMeAdapter friendsRequestsToMeAdapter;
	private MyFriendsAdapter myFriendsAdapter;
	private MyFriendRequestsAdapter myFriendRequestsAdapter;
		
	public FriendsListAdapter(Context context,User user) {
		super(context);
		if (user.getFriendRequestsToMe().length > 0){
			friendsRequestsToMeAdapter = new FriendRequestsToMeAdapter(user.getFriendRequestsToMe());
			friendsRequestsToMeAdapter.setOnNotifyDataSetChangedListener(this);
			this.addSection(friendsRequestsToMeAdapter.getSectionName(), friendsRequestsToMeAdapter);
		}
		this.myFriendsAdapter = new MyFriendsAdapter(user.getMyFriends());
		this.myFriendsAdapter.setOnNotifyDataSetChangedListener(this);
		this.addSection(myFriendsAdapter.getSectionName(), myFriendsAdapter);
		if (user.getMyFriendRequests().length > 0){
			myFriendRequestsAdapter = new MyFriendRequestsAdapter(user.getMyFriendRequests());
			myFriendRequestsAdapter.setOnNotifyDataSetChangedListener(this);
			//this.addSection(myFriendRequestsAdapter.getSectionName(), myFriendRequestsAdapter);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean requestToMeAddToAcceptedWaiting(int userID) {
		return friendsRequestsToMeAdapter.addToAcceptedWaiting(userID);
	}

	public boolean requestToMeIsAcceptedFriend(int userID) {
		return friendsRequestsToMeAdapter.isAccepted(userID);
	}

	public void requestToMeRemoveFromAcceptedWaiting(int userID) {
		friendsRequestsToMeAdapter.removeFromActeptedWaiting(userID);
	}

	public boolean requestToMeAddToAcceptedFriends(int userID) {
		return friendsRequestsToMeAdapter.addToAccepted(userID);
	}

	public boolean requestToMeAddToCanceledWaiting(int userID) {
		return friendsRequestsToMeAdapter.addToCanceledWaiting(userID);
	}
	
	public boolean requestToMeIsCanceledFriend(int userID) {
		return friendsRequestsToMeAdapter.isCanceled(userID);
	}
	
	public void requestToMeRemoveFromCanceledWaiting(int userID) {
		friendsRequestsToMeAdapter.removeFromCanceledWaiting(userID);
	}

	public boolean requestToMeAddToCanceledFriends(int userID) {
		return friendsRequestsToMeAdapter.addToCanceled(userID);
	}	
	
	public boolean myRequestAddToCanceledWaiting(int userID) {
		return myFriendRequestsAdapter.addToWaiting(userID);
	}

	public boolean myRequestIsCanceledFriend(int userID) {
		return myFriendRequestsAdapter.isCanceled(userID);
	}

	public void myRequestRemoveFromCanceledWaiting(int userID) {
		myFriendRequestsAdapter.removeFromWaiting(userID);
	}

	public void myRequestAddToCanceledFriends(int userID) {
		myFriendRequestsAdapter.addToCanceled(userID);
	}

	public void setOnRequestToMeAcceptedListener(OnCheckedChangeListener l) {
		if (friendsRequestsToMeAdapter != null){
			friendsRequestsToMeAdapter.setOnAcceptedClickListener(l);
		}
	}

	public void setOnRequestToMeCanceledListener(OnCheckedChangeListener l) {
		if (friendsRequestsToMeAdapter != null){
			friendsRequestsToMeAdapter.setOnCanceledClickListener(l);
		}
	}

	private OnCheckedChangeListener mOnMyRequestCanceledListener;
	public void setOnMyRequestCanceledListener(OnCheckedChangeListener l) {
		mOnMyRequestCanceledListener = l;
		if (myFriendRequestsAdapter != null){
			myFriendRequestsAdapter.setOnCanceledClickListener(l);
		}
	}

	@Override
	public void onDataSetChanged() {
		if (friendsRequestsToMeAdapter != null && friendsRequestsToMeAdapter.getCount() == 0){
			this.removeSection(friendsRequestsToMeAdapter.getSectionName());
			friendsRequestsToMeAdapter = null;
		}
		if (myFriendRequestsAdapter != null && myFriendRequestsAdapter.getCount() == 0){
			this.removeSection(myFriendRequestsAdapter.getSectionName());
			myFriendRequestsAdapter = null;
		}
		this.notifyDataSetChanged();
	}

	public void addMyFriendRequest(User user) {
		if (myFriendRequestsAdapter == null){
			myFriendRequestsAdapter = new MyFriendRequestsAdapter(new User[]{user});
			myFriendRequestsAdapter.setOnCanceledClickListener(mOnMyRequestCanceledListener);
			myFriendRequestsAdapter.setOnNotifyDataSetChangedListener(this);
			//this.addSection(myFriendRequestsAdapter.getSectionName(), myFriendRequestsAdapter);
			//notifyDataSetChanged();
		}
		else {
			//myFriendRequestsAdapter.addMyFriendRequest(user);
		}
	}

	@Override
	public void onAddFriend(User user) {
		this.myFriendsAdapter.addFriend(user);
	}

}
