package ch.unibe.sport.DBAdapter.tasks;

import android.content.Context;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.tables.FavoriteCourses;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;

public class AddOrRemoveCourseFromFavoritesTask extends ObservableAsyncTask<Context,Void,Boolean> {
	public static final String TAG =  AddOrRemoveCourseFromFavoritesTask.class.getName();

	private int courseID;
	
	public AddOrRemoveCourseFromFavoritesTask(int courseID){
		this.courseID = courseID;
	}
	
	@Override
	protected Boolean doInBackground(Context... params) {
		Context context = params[0];
		DBAdapter.INST.beginTransaction(context,TAG);
		FavoriteCourses favoriteDB = new FavoriteCourses(context);
		boolean favorite = favoriteDB.isFavorite(courseID);
		if (favorite){
			favoriteDB.remove(courseID);
		}
		else {
			favoriteDB.add(courseID);
		}
		DBAdapter.INST.endTransaction(TAG);
		return !favorite;
	}

}
