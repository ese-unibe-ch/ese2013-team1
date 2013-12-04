package ch.unibe.sport.main.sports;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.info.SportInfoActivity;
import ch.unibe.sport.main.IFilterable;
import ch.unibe.sport.main.IMainTab;
import ch.unibe.sport.main.search.ActionBarSearchItem;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.network.IProxyable;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.Point;
import ch.unibe.sport.utils.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SportsListView extends ListView  implements IFilterable, IMainTab, IPointable {
	public static final String TAG = SportsListView.class.getName();

	private Sports sportsDB;
	private String[][] sportsData;
	private SportsListAdapter listAdapter;
	
	private Point point;
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			showSportInfo(listAdapter.getIndex(position));
		}
	};
	
	public SportsListView(Context context) {
		super(context);
	}
	
	public SportsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SportsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	/**
	 * Starts initializing process of sorts list
	 */
	public void initialize(){
		this.point = Point.initialize(this);
		sportsDB = new Sports(getContext());
		sportsData = sportsDB.getData();
		
		initView();
	}
	
	/**
	 * Initializes list adapter from sports data
	 */
	private void initView() {
		if (sportsData.length == 0){
			initEmptyView("empty");
			return;
		}
		listAdapter = new SportsListAdapter(getContext(),sportsData);
		setAdapter(listAdapter);
		this.setOnItemClickListener(itemClickListener);
	}
	
	private void initEmptyView(String msg){
		setAdapter(null);
		TextView empty = new TextView(getContext());
		empty.setText(msg);
		this.setEmptyView(empty);
	}

	private void showSportInfo(int index) {
		final int courseID = Utils.Int(sportsData[index][Sports.SID]);
		SportInfoActivity.show(getContext(),courseID);
	}
	
	@Override
	public void initMenu(Menu menu) {
		if (menu == null) return;
		menu.clear();
		/* Initalizes search item in actionbar menu */
		new ActionBarSearchItem(this, menu, R.id.menu_search);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_search: {
				ActionBarSearchItem.onItemSelected(this,item);
				return true;
			}
			default: {
				return false;
			}
		}
	}

	@Override
	public boolean isFilterExists() {
		if (listAdapter == null) {
			listAdapter = (SportsListAdapter) this.getAdapter();
		}
		return listAdapter != null && listAdapter.getFilter() != null;
	}

	@Override
	public void filter(String prefix) {
		if (isFilterExists())listAdapter.getFilter().filter(prefix);
	}

	@Override
	public String getTitle() {
		return "Sports List";
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public void process(Message message) {}

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
	

}
