package ch.unibe.sport.main.friends;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import ch.unibe.sport.R;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.main.IMainTab;
import ch.unibe.sport.main.search.ActionBarSearchItem;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.Point;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;

public class FriendsTabView extends ScrollView implements IMainTab, IFilterable, IPointable, OnPageSwitchRequestListener {
	public static final String TAG = FriendsTabView.class.getName();
	
	private Point point;
	private FrameLayout root;
	private IPage currentPage;
	private SpiceManager spiceManager;
	
	public FriendsTabView(Context context) {
		super(context);
	}
	
	public FriendsTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void process(Message message) {
		
	}
	
	@Override
	public void onPageSwitch(IPage page,IPage newPage) {
		switchView(page, newPage);
	}
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Starts initializing process of friends view
	 */
	public void initialize(){
		point = Point.initialize(this);
		
		View.inflate(getContext(), R.layout.friends_tab_layout, this);
		this.setLayoutParams(new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setFillViewport(true);
		root = (FrameLayout) this.findViewById(R.id.container);
		
		initView();
	}
	
	private void initView(){
		/**
		 * Checking if it neccessary to register user
		 */
		if (Config.INST.USER.isRegistered()){
			FriendsSocialView socialView = new FriendsSocialView(getContext());
			socialView.setSpiceManager(spiceManager);
			socialView.setOnPageSwitchRequestListener(this);
			socialView.initialize();
			socialView.connect(this);
			root.addView(socialView);
			currentPage = socialView;
		}
		else {
			SignUpView signUp = new SignUpView(getContext());
			signUp.setSpiceManager(spiceManager);
			signUp.setOnPageSwitchRequestListener(this);
			signUp.initialize();
			root.addView(signUp);
			currentPage = signUp;
		}
		
	}
	
	/**
	 * Switches page from oldPage to newPage with transiotion animation
	 * @param oldPage
	 * @param newPage
	 */
	private void switchView(final IPage oldPage, final IPage newPage){
		root.addView(newPage.getView());
		newPage.getView().setVisibility(GONE);
		Animation translateLeftOut = new TranslateAnimation(0f,-oldPage.getView().getWidth(),0f,0f);
		translateLeftOut.setDuration(500);
		translateLeftOut.setFillAfter(true);
		translateLeftOut.setAnimationListener(new AnimationListener() {
			@Override public void onAnimationStart(Animation animation) {}
			@Override public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				root.removeView(oldPage.getView());
			}
		});
		Animation translateLeftIn = new TranslateAnimation(oldPage.getView().getWidth(),0,0f,0f);
		translateLeftIn.setDuration(500);
		translateLeftIn.setFillAfter(true);
		translateLeftIn.setAnimationListener(new AnimationListener() {
			@Override public void onAnimationStart(Animation animation) {
				newPage.getView().setVisibility(VISIBLE);
				newPage.setOnPageSwitchRequestListener(FriendsTabView.this);
				newPage.setSpiceManager(spiceManager);
				newPage.initialize();
				newPage.connect(FriendsTabView.this);
			}
			@Override public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				currentPage = newPage;
			}
		});
		oldPage.getView().startAnimation(translateLeftOut);
		newPage.getView().startAnimation(translateLeftIn);
	}
	
	public IPage getCurrentPage(){
		return this.currentPage;
	}
	
	@Override
	public void initMenu(Menu menu) {
		if (menu == null) return;
		menu.clear();
		MenuItem refresh = menu.add(0, R.id.menu_friends_update, 0, R.string.menu_friends_update);
		refresh.setIcon(R.drawable.ic_action_navigation_refresh);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_friends_update: {
				if (currentPage instanceof FriendsSocialView){
					((FriendsSocialView)currentPage).update();
					return true;
				}
				else {
					return false;
				}
			}
			default: {
				return false;
			}
		}
	}
	
	@Override
	public void filter(String prefix) {
		
	}
	
	/*------------------------------------------------------------
	------------------------ D E F A U L T -----------------------
	------------------------------------------------------------*/
	
	@Override
	public boolean isFilterExists() {
		return false;
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public String getTitle() {
		return "Friends";
	}
	
	@Override
	public void send(Message message) {
		point.send(message);
	}

	@Override
	public void connect(IProxyable proxy) {
		point.connect(proxy);
	}

	@Override
	public IPoint getPoint() {
		return point;
	}
	
	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public void collapseActionBar() {
		
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
	}

	

}
