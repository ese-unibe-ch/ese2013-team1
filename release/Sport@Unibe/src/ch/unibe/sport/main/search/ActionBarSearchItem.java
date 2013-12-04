package ch.unibe.sport.main.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import ch.unibe.sport.R;
import ch.unibe.sport.main.IMainTab;
import ch.unibe.sport.utils.Utils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;

public class ActionBarSearchItem {

	private final IMainTab tab;
	private final MenuItem item;
	
	public static final String TAG = "actionSearch";
	
	public ActionBarSearchItem(IMainTab tab, Menu menu, int id) {
		this.tab = tab;
		item = menu.add(0, id, 0, R.string.menu_search_title);
		init();
	}
	
	/**
	 * Hacks actionbar to create custom stylish searchview in actionview
	 */
	@SuppressLint({"InlinedApi","NewApi"}) @SuppressWarnings("deprecation")
	private void init(){
		item.setIcon(R.drawable.ic_action_action_search);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
		
		LayoutInflater inflater = (LayoutInflater) tab.getView().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewGroup searchActionView = (ViewGroup) inflater.inflate(R.layout.search_icon_actionview, null);
		final SearchView searchView = (SearchView) searchActionView.findViewById(R.id.search);
	    
		SearchAutoComplete searchEditText = searchView.getQueryTextView();
		searchEditText.setTextColor(Color.WHITE);
		searchEditText.setTag(TAG);
		
		ImageView closeButton = searchView.getCloseButton();
		closeButton.setImageResource(R.drawable.ic_action_content_backspace);
		
		View searchPlate = searchView.getSearchPlate();
		searchPlate.setBackgroundResource(R.drawable.textfield_searchview);
		
		ImageView searchIcon = searchView.getSearchHintIcon();
		searchIcon.setImageResource(R.drawable.ic_action_action_search);
		
	    ImageView more = (ImageView) searchActionView.findViewById(R.id.more);
	    more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				searchView.setFocusable(false);
			    searchView.setFocusableInTouchMode(false);
				AdvancedSearchDialog.show(tab.getView().getContext(), view);
			}
	    });

		SearchManager searchManager = (SearchManager) tab.getView().getContext().getSystemService(Context.SEARCH_SERVICE);
		if (null != searchView) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(((Activity)tab.getView().getContext()).getComponentName()));
			searchView.setIconifiedByDefault(false);
		}
		searchView.setOnQueryTextListener(new SearchQueryTextListener());
		ViewTreeObserver vto = searchActionView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ViewGroup parent = (ViewGroup)searchActionView.getParent();
				for (int i = 0; i < parent.getChildCount();i++){
					if (parent.getChildAt(i) instanceof ViewGroup){
						for (int j = 0; j < ((ViewGroup)parent.getChildAt(i)).getChildCount();j++){
							View view = ((ViewGroup)parent.getChildAt(i)).getChildAt(j);
							String name = view.getClass().getName();
							if (name.contains("ActionBarView")){
								view.setVisibility(View.GONE);
							}
						}
					}
				}
				ViewTreeObserver obs = searchActionView.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		});
		item.setActionView(searchActionView);
	}
	
	public static void onItemSelected(IMainTab tab,MenuItem item){
		SearchView searchView = (SearchView) item.getActionView().findViewById(R.id.search);
		searchView.setFocusable(true);
	    searchView.setFocusableInTouchMode(true);
		if (!searchView.requestFocus()) searchView.requestFocusFromTouch();
		Utils.showKeyboard(tab.getView());
	}
	
	/**
	 * Listener that reacts on text edit in search view in action bar
	 * @author Team 1 2013
	 */
	private class SearchQueryTextListener implements SearchView.OnQueryTextListener{
		@Override
		public boolean onQueryTextChange(String query) {
			tab.filter(query.trim());
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			tab.filter(query.trim());
			return true;
		}
	}

}