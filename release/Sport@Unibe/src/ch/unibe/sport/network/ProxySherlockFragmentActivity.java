package ch.unibe.sport.network;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import ch.unibe.sport.R;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.utils.Print;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.Parse;

public abstract class ProxySherlockFragmentActivity extends SherlockFragmentActivity implements IProxyable {

	public static final String TAG = ProxySherlockFragmentActivity.class.getName();
	
	private final String tag;
	private FrameLayout mActionBar;
	private LinearLayout mActionBarHome;
	private ImageView mActionIcon;
	private ToggleButton mActionLogo;
	
	private Proxy proxy;
	
	public ProxySherlockFragmentActivity(String activityTag){
		this.tag = activityTag;
	}
	
	@Override
	public String tag() {
		return tag;
	}
	
	@Override
	public Activity getActivity(){
		return this;
	}
	
	@Override
	public void send(Message message){
		proxy.send(message);
	}
	
	@Override
	public void connect(IPoint point){
		proxy.connect(point);
	}
	
	@Override
	public IProxy getProxy() {
		return proxy;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		Parse.initialize(this, "u1SoZrTuTj6Yv6CsJR3Qlgb2L3Uhw9eCV5MpnQfW", "36t3hEVmB97vzw7cdg2VfI3RUgbIOOwelsFjetUS");
		proxy = Proxy.initialize(this);		// must run first
		initConfig();				// must run after proxy
		initReadyToRunCheck();		// must run after configuration
	}
	
	private void initActionBar(){
		ActionBar actionBar = getSupportActionBar();
		/* we have no actionbar, nothing to do here */
		if (actionBar == null) return;

		
		mActionBar = (FrameLayout) getLayoutInflater().inflate(R.layout.actionbar_layout, null);
		mActionBarHome = (LinearLayout) mActionBar.findViewById(R.id.ab_home);
		mActionIcon = (ImageView) mActionBar.findViewById(R.id.ab_action_icon);
		mActionLogo = (ToggleButton) mActionBar.findViewById(R.id.ab_logo);
		
		/* here goes dirty hack to fully change action bar */
		hackActionBar(actionBar);
	}

	public void enableLogoSpinner(){
		mActionLogo.setChecked(true);
	}
	
	public void disableLogoSpinner(){
		mActionLogo.setChecked(false);
	}

	public ViewGroup getActionBarHome(){
		return this.mActionBarHome;
	}
	
	public ImageView getActionIcon(){
		return this.mActionIcon;
	}
	
	public void setActionBarIconResource(int resId){
		if (this.mActionIcon == null) return;
		this.mActionIcon.setImageResource(resId);
		this.mActionIcon.setVisibility(View.VISIBLE);
	}
	
	public void setActionBarHomeOnClickListener(OnClickListener l){
		if (mActionBarHome == null) return;
		this.mActionBarHome.setOnClickListener(l);
		setActionBarHomeBackgroundResource(R.drawable.actionbar_home_bg);
	}
	
	public void setActionBarHomeAsBack(){
		setActionBarHomeOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setActionBarIconResource(R.drawable.ic_action_navigation_previous_item);
	}
	
	public void setActionBarHomeBackgroundResource(int resId){
		if (mActionBarHome == null) return;
		this.mActionBarHome.setBackgroundResource(resId);
	}
	
	public void hideActionBar(){
		this.getSupportActionBar().hide();
	}
	
	/**
	 * Hacks current action bar layout, and changes it with our custom,
	 * saving all main actionbar features, such as actionviews and menu icons
	 * @param actionBar
	 */
	private void hackActionBar(ActionBar actionBar) {
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		
		actionBar.setCustomView(mActionBar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		ViewGroup absView = (ViewGroup) mActionBar.getParent();
		absView.setBackgroundResource(R.drawable.actionbar_bg);
		mActionBar.setLayoutParams(absView.getLayoutParams());
	}			
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	
	private void initConfig(){
		if (!Config.INST.INIT){
			Config.INST.init(this);
		}
	}
	
	private void initReadyToRunCheck() {
		/* make sure only one instance is running */
		if (!Config.INST.isRunning(this,tag)){
			Config.INST.addToRuningActivitiesList(this.tag);
		}
		else {
			finish();
		}
	}
	
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	@Override
    protected void onResume() {
        Print.log("Resume: "+this.tag);
        super.onResume();
    }
	
	@Override
	protected void onPause(){
        Print.log("Pause: "+this.tag);
		super.onPause();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		proxy.unInitReceiver();
		Config.INST.removeFromRuningActivitiesList(this.tag);
		super.onDestroy();
	}
}
