package ch.unibe.sport.DBAdapter.tasks;

import android.content.Context;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.EventFavorite;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;

public class AddOrRemoveEventFromFavoritesTask extends ObservableAsyncTask<Context,Void,Boolean> {
	public static final String TAG =  AddOrRemoveEventFromFavoritesTask.class.getName();

	private String hash;
	
	public AddOrRemoveEventFromFavoritesTask(String hash){
		this.hash = hash;
	}
	
	@Override
	protected Boolean doInBackground(Context... params) {
		Context context = params[0];
		DBAdapter.INST.beginTransaction(context,TAG);
		EventFavorite favoriteDB = new EventFavorite(context);
		boolean favorite = favoriteDB.isFavorite(hash);
		if (favorite){
			favoriteDB.remove(hash);
		}
		else {
			favoriteDB.add(hash);
		}
		DBAdapter.INST.endTransaction(TAG);
		return !favorite;
	}

}
