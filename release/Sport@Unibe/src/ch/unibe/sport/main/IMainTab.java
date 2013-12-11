package ch.unibe.sport.main;

import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;

public interface IMainTab extends IFilterable{
	public void initMenu(Menu menu);
	public View getView();
	public boolean onOptionsItemSelected(MenuItem item);
	public String getTitle();
	public void collapseActionBar();
	public void setSpiceManager(SpiceManager spiceManager);
}