package ch.unibe.sport.main;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.InitDBTask;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.favorites.FavoritesListView;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.MessageBuilder;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.AssociativeList;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

/**
 * Main activity - that holds all tabs and controlls their switching
 * @author Team 1 2013
 *
 */

public class MainActivity extends ProxySherlockFragmentActivity implements IPullToRefresh {
	public static final String TAG = MainActivity.class.getName();

	private Context context;
	private ViewPager pager;
	private MainActivityPagerAdapter adapter;
	private PullToRefreshAttacher pullToRefreshAttacher;
	private AssociativeList<IMainTab> tabViews;
	private PagerSlidingTabStrip tabs;
	private SlidingMenu slidingMenu;

	private static Menu mMenu;

	public MainActivity() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		// checking start configuration and launching actual app loading
		initReadyToRunCheck();
	}

	/*------------------------------------------------------------
	------------------------- M E S S A G E ----------------------
	------------------------------------------------------------*/

	/**
	 * Processes received message
	 */
	@Override
	public void process(Message message) {
		message.printTrace();
		// creating message adapter for easy message process
		MessageAdapter msgAdapter = new MessageAdapter(message);
		// if we need to start activity
		if (msgAdapter.isMainActivitySwitchTab()){
			switchTabAction(msgAdapter);
		}
	}

	/**
	 * Get's page tag from message and switches main activity tab
	 * @param msgAdapter - adapter of received message
	 */
	private void switchTabAction(MessageAdapter msgAdapter) {
		String pageTag = null;
		boolean smooth = false;
		try {
			// getting page tag
			pageTag = msgAdapter.getMainActivityTabTag();
			// if we need to switch tab with animation
			smooth = msgAdapter.getMainActivityTabSwitchSmooth();
		} catch (ParamNotFoundException e) {
			e.printStackTrace();
		}
		if (pageTag != null){
			// switching tab
			setCurrentPage(pageTag,smooth);
			// closing sliding menu
			slidingMenu.showContent(true);
		}
	}

	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Starting initialize check. If database isn't configured properly,
	 * runs database init task.
	 */
	private void initReadyToRunCheck() {	
		if (!Config.INST.DATABASE.INIT){
			InitDBTask initDBTask = new InitDBTask();
			initDBTask.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Void>(){
				@Override
				public void onTaskCompleted(AsyncTask<Context,Void,Void> task) {
					initView();
				}
			});
			initDBTask.execute(context);
		}
		else initView();
	}

	/**
	 * Initializes left sliding menu.
	 */
	private void initSlidingMenu(){
		// creating menu
		slidingMenu = new SlidingMenu(context);
		// attaching to MainActivity
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		// reinitializing display configuration to get correct display width and height
		Config.INST.DISPLAY.reInit();
		// user can see 1/3 of main activity on the right side of screen
		slidingMenu.setBehindOffset(Config.INST.DISPLAY.WIDTH/3);
		// enabling fade while opening and closing menu
		slidingMenu.setFadeEnabled(true);
		// setting fade degree, maximum 80% fade
		slidingMenu.setFadeDegree(0.8f);
		// setting shadow width from resource
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// setting shadow drawable
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		// setting menu layout, where sliding menu fragment is situated
		slidingMenu.setMenu(R.layout.sliding_menu);
		// setting navigation drawer icon
		this.setActionBarIconResource(R.drawable.ic_navigation_drawer);
		// setting click listener for home part of action bar
		this.setActionBarHomeOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				slidingMenu.showMenu(true);
			}
		});
	}
	
	private void initView(){
		initSlidingMenu();
		//pullToRefreshAttacher = PullToRefreshAttacher.get(this);
		setContentView(R.layout.main_layout);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		tabViews = new AssociativeList<IMainTab>();
		
		SportsListView sportList = new SportsListView(this);
		sportList.initialize();
		
		FavoritesListView favoritesList = new FavoritesListView(this);
		favoritesList.initialize();
		favoritesList.connect(this);
		
		tabViews.add(sportList,SportsListView.TAG);
		tabViews.add(favoritesList,FavoritesListView.TAG);
		
		
		adapter = new MainActivityPagerAdapter(tabViews.getValues());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		
		pager.setPageTransformer(true, AnimationsViewPager.getAnimation(this.context,9));
		tabs.setOnPageChangeListener(new MainPageChangeAdapter(){
			@Override
			public void onPageSelected(int page) {
				onPageSwitched(page);
			}
		});
		
		this.setCurrentPage(SportsListView.TAG, true);
	}

	/**
	 * Returns pullToRefreshAttacher, so subfragments can attach themselfs.
	 */
	@Override
	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return pullToRefreshAttacher;
	}
	
	private void onPageSwitched(int page) {
		if (page == 0) slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		else slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		IMainTab fragmentTab = tabViews.getAt(page);
		if (fragmentTab != null) {
			notifySlidingMenuSwitchTab(fragmentTab.getClass().getName());
			fragmentTab.initMenu(mMenu);
		}
	}

	/**
	 * Switchs current tab in pager. 
	 * @param tag - page's tag.
	 * @param smooth - true if animation is requared, otherwise false
	 */
	private void setCurrentPage(String tag, boolean smooth){
		if (tag == null || tag.length() == 0) return;
		// getting fragment index, it's actually page index
		int index = tabViews.indexOfKey(tag);
		if (index < 0 || index > adapter.getCount()) return;
		// switching page in pager
		if (index == 0) slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		pager.setCurrentItem(index,smooth);
		// notifying sliding menu, that page was changed
		notifySlidingMenuSwitchTab(tag);
	}
	
	/**
	 * Returns Fragment associated with current page
	 * @return IMainFragmentTab
	 */
	private IMainTab getCurrentPage() {
		if (pager == null) return null;
		int page = pager.getCurrentItem();
		IMainTab tab = this.tabViews.getAt(page);
		return tab;
	}
	
	/**
	 * Sends message to sliding menu, that current pae was changed
	 * @param tag
	 */
	private void notifySlidingMenuSwitchTab(String tag) {
		MessageBuilder msg = new MessageBuilder(TAG);
		msg.startSlideMenuItemActiveSwitch();
		msg.putSlideMenuItemTag(tag);
		send(msg.getMessage());
	}

	/**
	 * Tries to hide soft keyboard if user clicks outside focused view (editText)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		View view = getCurrentFocus();
		if (view == null) return super.dispatchTouchEvent(event);
		int viewCoordinates[] = new int[2];
		view.getLocationOnScreen(viewCoordinates);
		float x = event.getRawX() + view.getLeft() - viewCoordinates[0];
		float y = event.getRawY() + view.getTop() - viewCoordinates[1];
		if (event.getAction() == MotionEvent.ACTION_UP
				&& (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) ) {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
		}
		return super.dispatchTouchEvent(event);
	}
	/*------------------------------------------------------------
	---------------------------- M E N U -------------------------
	------------------------------------------------------------*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (slidingMenu.isShown()){
				if (slidingMenu.isMenuShowing()) slidingMenu.showContent(true);
				else slidingMenu.showMenu(true);
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			if (slidingMenu.isShown()){
				if (slidingMenu.isMenuShowing()) {
					slidingMenu.showContent(true);
					return true;
				}
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Creates action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		super.onCreateOptionsMenu(menu);
		IMainTab tab = this.getCurrentPage();
		if (tab != null)tab.initMenu(menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (getCurrentPage().onOptionsItemSelected(item)) return true;
		return super.onOptionsItemSelected(item);
	}

	public PagerSlidingTabStrip getTabs() {
		return tabs;
	}
}
