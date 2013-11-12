package ch.unibe.sport.slidingmenu;

import java.util.ArrayList;

import ch.unibe.sport.R;
import ch.unibe.sport.favorites.FavoritesListView;
import ch.unibe.sport.main.SportsListView;
import ch.unibe.sport.network.IPoint;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageAdapter;
import ch.unibe.sport.network.MessageBuilder;
import ch.unibe.sport.network.ParamNotFoundException;
import ch.unibe.sport.network.PointSherlockFragment;
import ch.unibe.sport.widget.layout.list.simple.Entry;
import ch.unibe.sport.widget.layout.list.simple.EntryView;
import ch.unibe.sport.widget.layout.list.simple.IEntry;
import ch.unibe.sport.widget.layout.list.simple.List;
import android.content.Context;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuFragment extends PointSherlockFragment {

	public static final String TAG = SlidingMenuFragment.class.getName();
	private static ViewGroup layout;
	private Context context;
	private Menu menu;
	
	public SlidingMenuFragment() {
		super(TAG);
	}


	@Override
	public View onCreated(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		/* hack that fixes stupid android fragment's recreation issue in pager */
		if (layout != null) {
			ViewGroup parent = (ViewGroup) layout.getParent();
			if (parent != null)
				parent.removeView(layout);
		}
		try {
			layout = (ViewGroup)inflater.inflate(R.layout.sliding_menu_fragment, null);
		} catch(InflateException e) {
			/* view is already there */
		}
		context = getActivity();
		initView();
		return layout;
	}
	
	private void initView(){
		menu = new Menu(context);
		layout.addView(menu.getView());
	}
	

	@Override
	public void process(Message message) {
		MessageAdapter adapter = new MessageAdapter(message);
		if (adapter.isSlideMenuItemActiveSwitch()){
			String tag = null;
			try {
				tag = adapter.getSlideMenuItemTag();
			} catch (ParamNotFoundException e) {
				e.printStackTrace();
			}
			if (tag != null) menu.switchActive(tag);
		}
	}

	private class Menu extends List {
		
		ArrayList<Item> items;
		/**
		 * Defines MainActivity switch tab action after click on entry in menu
		 */
		private OnEntryClickListener mainTabSwitcher;
		
		private Menu(Context context) {
			super(context);
		}

		@Override
		protected void onCreateList() {
			items = new ArrayList<Item>();
			mainTabSwitcher = new OnEntryClickListener(){
				@Override
				public void onEntryClickListener(IEntry entry) {
					MessageBuilder msg = new MessageBuilder(TAG);
					msg.startMainActivitySwitchTab();
					msg.putMainActivityTabTag(((Item) entry).getTag());
					msg.putMainActivityTabSwitchSmooth(false);
					send(msg.getMessage());
				}
			};
			
			items.add(new Item(getContext(),"").hideLine());
			items.add(new SportsList(getContext()));
			items.add(new FavoritesList(getContext()).hideLine());
			items.add(new Item(getContext(),"").hideLine());
			//items.add(new Options(getContext()));
			items.add(new About(getContext()).hideLine());
			
			initItems();
		}
		
		private void initItems(){
			if (items == null) return;
			for (Item item : items){
				this.add(item);
			}
		}
		
		private void switchActive(String tag){
			if (items == null) return;
			if (tag == null) return;
			for (Item item : items){
				if (item.getTag() != null && item.getTag().equals(tag)){
					item.setActive(true);
				}
				else item.setActive(false);
			}
		}
		
		private class SportsList extends Item {
			public SportsList(Context context) {
				super(context, "Sports List");
				this.setDefaultIcon(R.drawable.ic_list_sport_normal);
				setActiveIcon(R.drawable.ic_list_sport_active);
				this.setTag(SportsListView.TAG);
				this.setOnEntryClickListener(mainTabSwitcher);
			}
		}
		
		/*private class Live extends Item {
			public Live(Context context) {
				super(context, "Live");
				this.setDefaultIcon(R.drawable.ic_list_calendar_normal);
				setActiveIcon(R.drawable.ic_list_calendar_active);
				this.setTag(FavoritesGridFragment.TAG);
				this.setOnEntryClickListener(mainTabSwitcher);
			}
		}*/
		
		private class FavoritesList extends Item {
			public FavoritesList(Context context) {
				super(context, "Favorites");
				setDefaultIcon(R.drawable.ic_list_favorites_normal);
				setActiveIcon(R.drawable.ic_list_favorites_active);
				this.setTag(FavoritesListView.TAG);
				this.setOnEntryClickListener(mainTabSwitcher);
			}
		}
		
		/*private class Options extends Item {
			public Options(Context context) {
				super(context,context.getResources().getString(R.string.slider_menu_options));
				this.setOnEntryClickListener(new OnEntryClickListener(){
					@Override
					public void onEntryClickListener(IEntry entry) {
						SettingsActivity.show(getContext());
					}
				});
			}
		}*/
		
		private class About extends Item {
			public About(Context context) {
				super(context,context.getResources().getString(R.string.slider_menu_about));
				this.setOnEntryClickListener(new OnEntryClickListener(){
					@Override
					public void onEntryClickListener(IEntry entry) {
						AboutDialog.show(getContext());
					}
				});
			}
		}
	}
	
	
	private class Item extends Entry {
		private ItemView itemView;
		
		private String tag = "";
		
		public Item(Context context,String name) {
			super(context,name);
			this.itemView = new ItemView(context,this);
			this.setHeaderView(itemView);
		}

		protected Item hideLine(){
			this.itemView.hideLine();
			return this;
		}
		
		protected void setDefaultIcon(int resID){
			this.itemView.setDefaultIcon(resID);
		}
		
		protected void setActiveIcon(int resID){
			this.itemView.setActiveIcon(resID);
		}
		
		protected void setActive(boolean active){
			this.itemView.setActive(active);
		}
		
		protected void setTag(String tag){
			this.tag = tag;
		}
		
		protected String getTag() {
			return tag;
		}
	}
	
	private class ItemView extends EntryView {
		
		private int defaultIcon;
		private int activeIcon;
		private boolean mActive;
		private ImageView icon;
		private TextView text;
		private View line;
		private IEntry entry;
		
		public ItemView(Context context, IEntry entry) {
			super(context, entry, true);
			this.entry = entry;
			initView();
		}
		
		private void initView(){
			this.setLayout(R.layout.sliding_menu_entry);
			text = (TextView) this.findViewById(R.id.text);
			text.setText(entry.getName());
			icon = (ImageView) this.findViewById(R.id.icon);
			line = this.findViewById(R.id.line);
		}
		
		private void hideLine(){
			line.setVisibility(GONE);
		}
		
		private void setDefaultIcon(int resID) {
			this.defaultIcon = resID;
			this.icon.setImageResource(resID);
			this.icon.setVisibility(VISIBLE);
		}
		
		private void setActiveIcon(int resID){
			this.activeIcon = resID;
		}
		
		private void setActive(boolean active){
			if (active && !mActive && this.activeIcon > 0){
				this.icon.setImageResource(activeIcon);
				this.mActive = true;
			}
			else if (!active && mActive && this.defaultIcon > 0){
				this.icon.setImageResource(defaultIcon);
				this.mActive = false;
			}
		}
	}
	
	/**
	 * Prevents fragment recreation memory leak.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.context = null;
		this.menu = null;
	}


	@Override
	public IPoint getPoint() {
		// TODO Auto-generated method stub
		return null;
	}
}
