package ch.unibe.sport.main.search;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;


public class AdvancedSearchResultActivity extends ProxySherlockFragmentActivity{

	public static final String TAG = AdvancedSearchResultActivity.class.getName();
	
	public AdvancedSearchResultActivity() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setActionBarHomeAsBack();
		if (savedInstanceState == null) {
			AdvancedSearchResultFragment searchResult = AdvancedSearchResultFragment.newInstance();
			searchResult.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, searchResult).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Initalizes search item in actionbar menu */
		return true; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:{
				return super.onOptionsItemSelected(item);
			}
		}
	}
	
	public static void show(Context context, String searchQuery){
		Intent intent = new Intent();
		intent.setClass(context, AdvancedSearchResultActivity.class);
		intent.putExtra(AdvancedSearchResultFragment.JSON_QUERY, searchQuery);
		context.startActivity(intent);
	}
	
	@Override
	public void process(Message message) {}
}
