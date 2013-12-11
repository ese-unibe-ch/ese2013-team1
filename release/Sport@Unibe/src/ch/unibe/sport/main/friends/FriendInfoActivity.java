package ch.unibe.sport.main.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.GetFriendDataRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.core.User;
import ch.unibe.sport.event.info.EventsListAdapter;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Objeckson;
import ch.unibe.sport.utils.Print;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class FriendInfoActivity extends ProxySherlockFragmentActivity {

	public static final String TAG = FriendInfoActivity.class.getName();
	public static final String FRIEND_ID = "friendID";
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
    private static final String JSON_GET_FRIEND_DATA_KEY = "json_get_friend_data_key";
	
    private ListView listView;
    private TextView nameView;
    
    private EventsListAdapter listAdapter;
    
    private User friend;
    
	public FriendInfoActivity() {
		super(TAG);
	}
	
	@Override public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		/*
		 * Updating course
		 */
		if (adapter.isCourseUpdate()){
			try {
				int courseID = adapter.getCourseID();
				listAdapter.update(courseID);
			} catch (ParamNotFoundException e) {e.printStackTrace();}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setActionBarHomeAsBack();
		initDataLoader();
	}

	public static void show(final Context context,final int friendID) {
		Intent intent = new Intent();
		intent.setClass(context, FriendInfoActivity.class);
		intent.putExtra(FRIEND_ID, friendID);
		context.startActivity(intent);
	}
	
	private void initDataLoader(){
		int friendID = this.getIntent().getIntExtra(FRIEND_ID, 0);
		if (friendID == 0){
			finish();
			return;
		}
		this.enableLogoSpinner();
		this.setActionBarTitle("Loading...");
		GetFriendDataRequest getFriendDatarequest = new GetFriendDataRequest(Config.INST.SYSTEM.UUID,Config.INST.USER.ID,friendID,new Date());
        spiceManager.execute(getFriendDatarequest, JSON_GET_FRIEND_DATA_KEY, DurationInMillis.ALWAYS_EXPIRED, new GetFriendDataRequestListener());
	}
	
	private void onDataLoaded(User user){
		this.disableLogoSpinner();
		if (user == null || user.getUserID() == 0){
			finish();
			return;
		}
		this.friend = user;
		this.setActionBarTitle(friend.getName());
		initView();
	}
	
	private void initView(){
		this.setContentView(R.layout.friend_info_layout);
		listView = (ListView) this.findViewById(R.id.event_list);
		nameView = (TextView) this.findViewById(R.id.name);
		initUserName();
		initListView();
	}
	
	private void initListView(){
		for (Event event : friend.getAttendedEvents()){
			event.update(this);
		}
		this.listAdapter = new EventsListAdapter(this, friend.getAttendedEvents());
		this.listAdapter.setPeriodHidden(true);
		this.listAdapter.setFirstItemPadding((int) this.getResources().getDimension(R.dimen._50dp));
		this.listAdapter.disableAddAndRemove();
		SwingBottomInAnimationAdapter animatedAdapter = new SwingBottomInAnimationAdapter(listAdapter);
		animatedAdapter.setAbsListView(listView);
		listView.setAdapter(animatedAdapter);
	}
	
	private void initUserName(){
		String name = (friend.getUsername() != null && friend.getUsername().length() > 0) ? friend.getUsername() : friend.getNickname();
		this.nameView.setText(name+"'s attended courses:");
	}

	private class GetFriendDataRequestListener implements RequestListener<String>{

		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			onDataLoaded(null);
		}

		@Override
		public void onRequestSuccess(String json) {
			if (done) return;
			done = true;
			if (json == null || json.length() == 0) {
				onDataLoaded(null);
				return;
			}
			Print.log(json);
			User user = Objeckson.fromJson(json, User.class);
			onDataLoaded(user);
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
