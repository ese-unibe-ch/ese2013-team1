package ch.unibe.sport.event.info;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.tasks.AddOrRemoveEventFromFavoritesTask;
import ch.unibe.sport.dialog.DialogFloating;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.taskmanager.OnTaskCompletedListener;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.widget.layout.list.simple.Entry;
import ch.unibe.sport.widget.layout.list.simple.EntryView;
import ch.unibe.sport.widget.layout.list.simple.IEntry;
import ch.unibe.sport.widget.layout.list.simple.List;
import ch.unibe.sport.widget.layout.list.simple.List.OnEntryClickListener;

public class ItemMenu extends DialogFloating implements OnEntryClickListener {
	public static final String TAG = ItemMenu.class.getName();
	
	private static final String EVENT_ID = "eventID";
	private static final String EVENT_HASH = "eventHash";
	private static final int REMOVE_FROM_FAVORITES = 1;
	private static final int SHOW_MAP = 2;
	private static final int CHANGE_COLOR = 3;
	
	private boolean favoriteButtonLock = false;
	private int eventID;
	private String eventHash;
	
	public static void show(Context context, View view, int eventID,String eventHash) {
		Bundle extras = new Bundle();
		extras.putInt(EVENT_ID, eventID);
		extras.putString(EVENT_HASH, eventHash);
		DialogFloating.show(context, view, extras,ItemMenu.class);
	}
	
	public ItemMenu() {
		super(TAG);
	}

	/**
	 * onInitElements() method is abstract and called from onCreate().
	 * Usage of overwritable methods in constructors is bad practice in Java.
	 * Best practices say, that in constructors only private and final
	 * methods are allowed. But in this case Activity is called not from
	 * constructor, but from android framework, so in our case it's OK
	 * to have abstract method in abstract class that extends from Activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		eventID = this.getIntent().getIntExtra(EVENT_ID, 0);
		eventHash = this.getIntent().getStringExtra(EVENT_HASH);
		if (eventID == 0){
			finish();
			return;
		}
		Menu menu = new Menu(getContext());
		menu.setOnEntryClickListener(this);
		this.getLayout().addView(menu.getView());
		this.getLayout().setBackgroundResource(R.drawable.card_layout_bg_white);
	}

	@Override
	public void onEntryClickListener(IEntry entry) {
		final int code = ((Item)entry).code;
		switch (code){
			case REMOVE_FROM_FAVORITES:{
				if (!favoriteButtonLock){
					removeFromFavorites();
					finish();
				}
				break;
			}
			case SHOW_MAP:{
				showMap();
				break;
			}
			case CHANGE_COLOR:{
				showColorsDialog();
				break;
			}
		}
	}
	
	private void showColorsDialog() {
		EventChangeColorDialog.show(getContext(),eventID);
		finish();
	}

	private void showMap(){
		MapDialog.show(this, eventID);
		finish();
	}
	
	private void removeFromFavorites(){
		favoriteButtonLock = true;
		AddOrRemoveEventFromFavoritesTask task = new AddOrRemoveEventFromFavoritesTask(eventHash);
		task.setOnTaskCompletedListener(new OnTaskCompletedListener<Context,Void,Boolean>(){
			@Override
			public void onTaskCompleted(AsyncTask<Context,Void,Boolean> task) {
				try {
					send(MessageFactory.updateFavoriteFromItemMenu(eventID, task.get()));
				} catch (InterruptedException e){e.printStackTrace();}catch (ExecutionException e) {e.printStackTrace();}
				favoriteButtonLock = false;
			}
		});
		task.execute(this);
	}
	
	private class Menu extends List {

		private Menu(Context context) {
			super(context);
		}

		@Override
		protected void onCreateList() {
			Item colors = new Item(getContext(),Utils.getString(getContext(), R.string.dialog_item_menu_change_color),CHANGE_COLOR);
			this.add(colors);
			Item remove = new Item(getContext(),Utils.getString(getContext(), R.string.dialog_item_menu_remove_from_favorites),REMOVE_FROM_FAVORITES);
			this.add(remove);
			Item map = new Item(getContext(),Utils.getString(getContext(), R.string.dialog_item_menu_show_on_map),SHOW_MAP);
			map.hideLine();
			this.add(map);
		}
	}
	
	private class Item extends Entry {
		private ItemView itemView;
		private final int code;
		
		private Item(Context context,String name, int code) {
			super(context,name);
			this.itemView = new ItemView(context,this);
			this.setHeaderView(itemView);
			this.code = code;
		}
		
		private void hideLine(){
			this.itemView.hideLine();
		}
	}
	
	 /**
	  * 
	  * @author Team 1 2013
	  *
	  */
	private class ItemView extends EntryView {
		
		private TextView text;
		private View line;
		private IEntry entry;
		
		private ItemView(Context context, IEntry entry) {
			super(context, entry, true);
			this.entry = entry;
			initView();
		}
		
		private void initView(){
			this.setLayout(R.layout.item_menu_entry);
			text = (TextView) this.findViewById(R.id.text);
			text.setText(entry.getName());
			line = this.findViewById(R.id.line);
		}
		
		private void hideLine(){
			line.setVisibility(GONE);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
