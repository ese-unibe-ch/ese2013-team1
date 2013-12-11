package ch.unibe.sport.main.friends;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.GetUserDataRequest;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.User;
import ch.unibe.sport.main.friends.FriendsSocialPagerPanelAdapter.OnPagePanelSwitchListener;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.utils.Objeckson;
import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class FriendsSocialView extends RelativeLayout implements IPage, OnOptionPanelListener {

	private ViewPager pager;
	private FriendsSocialPagerAdapter pageAdapter;
	private FriendsListView friendsList;
	private FriendsNewsFeed friendsNews;
	private LinearLayout pagerPanel;
	private LinearLayout optionsPanel;
	private ImageView optionsButton;
	private FriendsSocialPagerPanelAdapter pagerPanelAdapter;
	private SpiceManager spiceManager;

	private boolean updateLock;
	
	private GetUserDataRequest getUserDataRequest;
    private static final String JSON_GET_USER_DATA_KEY = "json_get_user_data_key";
    
	public FriendsSocialView(Context context) {
		super(context);
	}
	
	public FriendsSocialView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FriendsSocialView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void initialize(){
		View.inflate(getContext(), R.layout.friends_social_page, this);
		this.pager = (ViewPager) this.findViewById(R.id.friends_pager);
		this.pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int page) {
				initOptionsPanel(page);
				pagerPanelAdapter.selectButton(page);
			}
			
			@Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override public void onPageScrollStateChanged(int arg0) {}
		});
		this.pagerPanel = (LinearLayout) this.findViewById(R.id.pager_panel);
		this.optionsPanel = (LinearLayout) this.findViewById(R.id.options_panel);
		this.optionsButton = (ImageView) this.findViewById(R.id.options_button);
		this.optionsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (optionsPanel.getTag() != null) return;
				optionsPanel.setTag(new Object());
				if (optionsPanel.getVisibility() == GONE){
					showOptionsPanel();
				}
				else {
					hideOptionsPanel();
				}
			}
		});
		this.pagerPanelAdapter = new FriendsSocialPagerPanelAdapter(pagerPanel);
		this.pagerPanelAdapter.setOnPagePanelSwitchListener(new OnPagePanelSwitchListener(){
			@Override
			public void onPanelSwitch(int page) {
				switchPage(page);
			}
		});
		this.pageAdapter = new FriendsSocialPagerAdapter();
		this.friendsList = new FriendsListView(getContext());
		this.friendsList.setSpiceManager(spiceManager);
		this.friendsList.initialize();
		this.friendsList.setOnOptionPanelListener(this);
		this.friendsNews = new FriendsNewsFeed(getContext());
		this.friendsNews.initialize();
		this.pagerPanelAdapter.addButton(this.friendsList.getNotificationButton());
		this.pagerPanelAdapter.addButton(this.friendsNews.getNotificationButton());
		this.pageAdapter.addTab(friendsList);
		this.pageAdapter.addTab(friendsNews);
		this.pager.setAdapter(pageAdapter);
		switchPage(0);
		initUserData();
	}
	
	private void switchPage(int page) {
		if (page < pageAdapter.getCount()){
			pager.setCurrentItem(page);
			initOptionsPanel(page);
		}
	}

	private void initOptionsPanel(int page) {
		optionsPanel.removeAllViews();
		View options = pageAdapter.getTab(page).getOptionsPanel();
		if (options == null){
			optionsButton.setVisibility(GONE);
		}
		else {
			optionsButton.setVisibility(VISIBLE);
			optionsPanel.addView(options);
		}
	}
	
	private void showOptionsPanel(){
		Animation anim = new TranslateAnimation(0, 0, pagerPanel.getHeight(), 0);
		anim.setDuration(150);
		anim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				optionsPanel.setTag(null);
			}

			@Override public void onAnimationRepeat(Animation animation) {}
			@Override public void onAnimationStart(Animation animation) {
				optionsPanel.setVisibility(VISIBLE);
			}
			
		});
		optionsPanel.startAnimation(anim);
	}
	
	private void hideOptionsPanel(){
		Animation anim = new TranslateAnimation(0, 0, 0, pagerPanel.getHeight());
		anim.setDuration(150);
		anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				optionsPanel.setTag(null);
				optionsPanel.setVisibility(GONE);
			}

			@Override public void onAnimationRepeat(Animation animation) {}
			@Override public void onAnimationStart(Animation animation) {}
			
		});
		optionsPanel.setVisibility(VISIBLE);
		optionsPanel.startAnimation(anim);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		int viewCoordinates[] = new int[2];
		optionsPanel.getLocationOnScreen(viewCoordinates);
		float x = event.getRawX() + optionsPanel.getLeft() - viewCoordinates[0];
		float y = event.getRawY() + optionsPanel.getTop() - viewCoordinates[1];
		if (event.getAction() == MotionEvent.ACTION_UP
				&& (x < optionsPanel.getLeft() || x >= optionsPanel.getRight()
				|| y < optionsPanel.getTop() || y > optionsPanel.getBottom()) ) {
			if (optionsPanel.getTag() != null) return super.dispatchTouchEvent(event);
			if (optionsPanel.getVisibility() == VISIBLE){
				optionsPanel.setTag(new Object());
				hideOptionsPanel();
			}
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void setOnPageSwitchRequestListener(OnPageSwitchRequestListener l) {}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void onOptionPanelHide() {
		hideOptionsPanel();
	}

	@Override
	public void onOptionPanelShow() {
		showOptionsPanel();
	}

	private void initUserData(){
		if (updateLock) return;
		updateLock = true;
		getUserDataRequest = new GetUserDataRequest(Config.INST.SYSTEM.UUID, Config.INST.USER.ID);
		spiceManager.execute(getUserDataRequest, JSON_GET_USER_DATA_KEY, DurationInMillis.ALWAYS_EXPIRED, new GetUserDataRequestListener());
	}
	
	private void initUser(User user){
		updateLock = false;
		if (user == null) return;
		this.friendsList.setUser(user);
		this.friendsNews.setUser(user);
	}
	
	private class GetUserDataRequestListener implements RequestListener<String>{
		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			initUser(null);
		}

		@Override
		public void onRequestSuccess(String json) {
			if (done) return;
			done = true;
			if (json == null || json.length() == 0) {
				initUser(null);
				return;
			}
			Print.log(json);
			User user = Objeckson.fromJson(json, User.class);
			initUser(user);
		}
	}
	
	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
	}

	@Override
	public void connect(IPointable point) {
		this.friendsList.connect(point);
	}

	public void update() {
		initUserData();
	}

}
