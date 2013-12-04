package ch.unibe.sport.main.friends;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import ch.unibe.sport.R;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.main.IMainTab;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.Point;

public class FriendsTabView extends ScrollView implements IMainTab, IFilterable, IPointable{
	public static final String TAG = FriendsTabView.class.getName();
	
	private Point point;
	private LinearLayout root;
	
	public FriendsTabView(Context context) {
		super(context);
	}
	
	public FriendsTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void process(Message message) {
		
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
		root = (LinearLayout) this.findViewById(R.id.container);
		
		initView();
	}
	
	private void initView(){
		root.addView(new RegisterEnterUserDataView(getContext()));
	}
	
	@Override
	public void initMenu(Menu menu) {
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
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

}
