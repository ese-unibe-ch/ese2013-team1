package ch.unibe.sport.favorites;

import com.actionbarsherlock.view.Menu;

public class ActionBarListMenu {
	//private final SubMenu subMenu;
	
	
	/**
	 * Example of programmatical actionbar submenu
	 * @param menu
	 * @param id
	 */
	public ActionBarListMenu(Menu menu, int id) {
		//this.subMenu = menu.addSubMenu(0, 0, 0, R.string.menu_navigation_title).setIcon(R.drawable.ic_action_core_overflow);
		//init();
	}

	/*private void init() {
	    MenuItem subMenuItem = subMenu.getItem();
	    subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	    
		MenuItem showAsGrid = subMenu.add(Menu.NONE, R.id.menu_favorites_change_grid, 0, R.string.menu_favorites_grid_title);
		showAsGrid.setIcon(R.drawable.ic_action_collections_view_as_grid);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			showAsGrid.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
	}*/
}
