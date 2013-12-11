package ch.unibe.sport.main.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import ch.unibe.sport.DBAdapter.restApi.AddToFriends;
import ch.unibe.sport.DBAdapter.restApi.AddToFriendsRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.User;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class FriendsFindNewResultActivity extends ProxySherlockFragmentActivity {

	public static final String TAG = FriendsFindNewResultActivity.class.getName();
	public static final String USERS_KEY = Config.PACKAGE_NAME+".users";
	
	private User[] users;
	private ListView listView;
	private FriendsFindNewResultAdapter adapter;
	
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
	
	public FriendsFindNewResultActivity() {
		super(TAG);
	}

	public static void show(Context context,User[] users){	
		Intent intent = new Intent(context, FriendsFindNewResultActivity.class);
		intent.putExtra(USERS_KEY, users);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setActionBarHomeAsBack();
		Parcelable[] parcels = this.getIntent().getParcelableArrayExtra(USERS_KEY);
		this.setActionBarTitle("Found "+parcels.length +" user"+((parcels.length > 1)?"s":""));
		users = User.CREATOR.newArray(parcels.length);
		for (int i = 0, length = parcels.length;i < length; i++){
			users[i] = (User) parcels[i];
		}
		adapter = new FriendsFindNewResultAdapter(users);
		listView = new ListView(this);
		listView.setPadding((int)Utils.convertDpToPx(this, 10), (int)Utils.convertDpToPx(this, 5), (int)Utils.convertDpToPx(this, 10), 0);
		
		this.setContentView(listView);
		listView.setAdapter(adapter);
		adapter.setOnClickListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				buttonView.setChecked(false);
				initRequest((User) buttonView.getTag());
			}
		});
	}
	
	private void initRequest(User user){
		AddToFriendsRequest addToFriendsRequest = new AddToFriendsRequest(Config.INST.SYSTEM.UUID,user.getUserID(),Config.INST.USER.ID);
		if(adapter.addToWaiting(user.getUserID()) && !adapter.isAdded(user.getUserID())){
			spiceManager.execute(addToFriendsRequest, addToFriendsRequest.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new AddToFriendsRequestListener(user));
		}
	}
	
	@Override
	public void process(Message message) {}
	
	private class AddToFriendsRequestListener implements RequestListener<AddToFriends>{
		private boolean done = false;
		private User user;
		
		public AddToFriendsRequestListener(User user){
			this.user = user;
		}
		
		@Override
		public void onRequestFailure(SpiceException e) {
			Print.log("onRequestFail");
			adapter.removeFromWaiting(user.getUserID());
			done = true;
		}

		@Override
		public void onRequestSuccess(AddToFriends addToFriends) {
			if (done) return;
			done = true;
			adapter.removeFromWaiting(user.getUserID());
			if (addToFriends == null) {
				return;
			}
			Print.log("error: "+addToFriends.isError());
			if (!addToFriends.isError()){
				adapter.addToAdded(user.getUserID());
				send(MessageFactory.sentAddFriend(TAG, user));
			}
		}
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

}
