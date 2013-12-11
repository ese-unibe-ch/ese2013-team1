package ch.unibe.sport.main.friends;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.AcceptFriend;
import ch.unibe.sport.DBAdapter.restApi.AcceptFriendRequest;
import ch.unibe.sport.DBAdapter.restApi.CancelFriend;
import ch.unibe.sport.DBAdapter.restApi.CancelFriendRequest;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.User;
import ch.unibe.sport.network.INodable;
import ch.unibe.sport.network.INode;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.Node;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.widget.view.NotificationButton;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class FriendsListView extends ListView implements IFriendsSocialTab, INodable  {

	public static final String TAG = FriendsListView.class.getName();
	
	private FriendsListAdapter listAdapter;
	private NotificationButton notificationButton;
	private LinearLayout optionsPanel;
	private SpiceManager spiceManager;
	
	private OnOptionPanelListener mOnOptionPanelListener;
    private INode node;
	
	public FriendsListView(Context context) {
		super(context);
	}
	
	public FriendsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FriendsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		if (adapter.isSentAddFriend()){
			try {
				User user = adapter.getUser();
				listAdapter.addMyFriendRequest(user);
			} catch (ParamNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void initialize(){
		this.node = Node.initialize(this);
		this.setPadding((int)Utils.convertDpToPx(getContext(), 10), (int)Utils.convertDpToPx(getContext(), 5), (int)Utils.convertDpToPx(getContext(), 10), 0);
		notificationButton = new NotificationButton(getContext());
		notificationButton.setImageResource(R.drawable.friends_icon);
		notificationButton.setPadding(5f);
		optionsPanel = (LinearLayout) View.inflate(getContext(), R.layout.friends_list_options_panel, null);
		optionsPanel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		optionsPanel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (mOnOptionPanelListener != null){
					mOnOptionPanelListener.onOptionPanelHide();
				}
				FriendsFindNewDialog.show(getContext());
			}
		});
		this.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View item, int arg2, long arg3) {
				FriendInfoActivity.show(getContext(), (Integer)item.getTag());
			}
		});
	}
	
	@Override
	public void setUser(User user) {
		listAdapter = new FriendsListAdapter(getContext(),user);
		listAdapter.setOnRequestToMeAcceptedListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				buttonView.setChecked(false);
				initRequestToMeAccept((User) buttonView.getTag());
			}
			
		});
		listAdapter.setOnRequestToMeCanceledListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				buttonView.setChecked(false);
				initRequestToMeCancel((User) buttonView.getTag());
			}
			
		});
		listAdapter.setOnMyRequestCanceledListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				buttonView.setChecked(false);
				initMyRequestCancel((User) buttonView.getTag());
			}
			
		});
		SwingBottomInAnimationAdapter animatedAdapter = new SwingBottomInAnimationAdapter(listAdapter);
		animatedAdapter.setAbsListView(this);
		this.setAdapter(animatedAdapter);
		notificationButton.setNotifications(user.getFriendRequestsToMe().length);
	}
	
	@Override
	public View getView() {
		return this;
	}

	@Override
	public NotificationButton getNotificationButton() {
		return notificationButton;
	}

	@Override
	public View getOptionsPanel() {
		return optionsPanel;
	}

	@Override
	public void setOnOptionPanelListener(OnOptionPanelListener l) {
		mOnOptionPanelListener = l;
	}
	
	private void initRequestToMeAccept(User user){
		AcceptFriendRequest acceptFriendRequest = new AcceptFriendRequest(Config.INST.SYSTEM.UUID,Config.INST.USER.ID,user.getUserID());
		if(listAdapter.requestToMeAddToAcceptedWaiting(user.getUserID()) && !listAdapter.requestToMeIsAcceptedFriend(user.getUserID())){
			spiceManager.execute(acceptFriendRequest, acceptFriendRequest.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new AcceptFriendRequestListener(user));
		}
	}
	
	private void initRequestToMeCancel(User user){
		CancelFriendRequest cancelFriendRequest = new CancelFriendRequest(Config.INST.SYSTEM.UUID,Config.INST.USER.ID,user.getUserID());
		if(listAdapter.requestToMeAddToCanceledWaiting(user.getUserID()) && !listAdapter.requestToMeIsCanceledFriend(user.getUserID())){
			spiceManager.execute(cancelFriendRequest, cancelFriendRequest.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestToMeCancelListener(user));
		}
	}
	
	private void initMyRequestCancel(User user){
		CancelFriendRequest cancelFriendRequest = new CancelFriendRequest(Config.INST.SYSTEM.UUID,Config.INST.USER.ID,user.getUserID());
		if(listAdapter.myRequestAddToCanceledWaiting(user.getUserID()) && !listAdapter.myRequestIsCanceledFriend(user.getUserID())){
			spiceManager.execute(cancelFriendRequest, cancelFriendRequest.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MyRequestCancelListener(user));
		}
	}
	
	private class AcceptFriendRequestListener implements RequestListener<AcceptFriend>{
		private boolean done = false;
		private User user;
		
		public AcceptFriendRequestListener(User user) {
			this.user = user;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			listAdapter.requestToMeRemoveFromAcceptedWaiting(user.getUserID());
			done = true;
		}

		@Override
		public void onRequestSuccess(AcceptFriend accept) {
			if (done) return;
			done = true;
			listAdapter.requestToMeRemoveFromAcceptedWaiting(user.getUserID());
			if (accept == null) {
				return;
			}
			Print.log("error: "+accept.isError());
			if (!accept.isError()){
				listAdapter.requestToMeAddToAcceptedFriends(user.getUserID());
				notificationButton.minusNotification();
			}
			else {
				Toast.makeText(getContext(), "Error accepting request: "+accept.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class RequestToMeCancelListener implements RequestListener<CancelFriend>{
		private boolean done = false;
		private User user;
		
		public RequestToMeCancelListener(User user) {
			this.user = user;
		}
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			listAdapter.requestToMeRemoveFromCanceledWaiting(user.getUserID());
			done = true;
		}

		@Override
		public void onRequestSuccess(CancelFriend cancel) {
			if (done) return;
			done = true;
			listAdapter.requestToMeRemoveFromCanceledWaiting(user.getUserID());
			if (cancel == null) {
				return;
			}
			Print.log("error: "+cancel.isError());
			if (!cancel.isError()){
				listAdapter.requestToMeAddToCanceledFriends(user.getUserID());
				notificationButton.minusNotification();
			}
			else {
				Toast.makeText(getContext(), "Error canceling request: "+cancel.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class MyRequestCancelListener implements RequestListener<CancelFriend>{
		private boolean done = false;
		private User user;
		
		public MyRequestCancelListener(User user) {
			this.user = user;
		}
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			listAdapter.myRequestRemoveFromCanceledWaiting(user.getUserID());
			done = true;
		}

		@Override
		public void onRequestSuccess(CancelFriend cancel) {
			if (done) return;
			done = true;
			Print.log(cancel);
			listAdapter.myRequestRemoveFromCanceledWaiting(user.getUserID());
			if (cancel == null) {
				return;
			}
			Print.log("error: "+cancel.isError());
			if (!cancel.isError()){
				listAdapter.myRequestAddToCanceledFriends(user.getUserID());
			}
			else {
				Toast.makeText(getContext(), "Error canceling request: "+cancel.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
	}

	@Override
	public void connect(IPointable point) {
		node.connect(point);
	}

	@Override
	public String tag() {
		return TAG;
	}

}
