package ch.unibe.sport.DBAdapter.tasks;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.DBUpdate;
import ch.unibe.sport.DBAdapter.tables.QuerySyntaxException;
import ch.unibe.sport.DBAdapter.tables.TableAlreadyExistsException;
import ch.unibe.sport.DBAdapter.tables.TableNotExistsException;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.taskmanager.ObservableAsyncTask;
import android.content.Context;

public class InitDBTask extends ObservableAsyncTask<Context,Void,Void> {
	public static final String TAG = InitDBTask.class.getName();
	
	@Override
	protected Void doInBackground(Context... context) {
		if (!Config.INST.DATABASE.INIT){
			DBAdapter.INST.beginTransaction(context[0],TAG);
			DBUpdate db = new DBUpdate(context[0]);
			try {
				db.dbFullFormat();
			} catch (TableAlreadyExistsException e){
				e.printStackTrace();
				DBAdapter.INST.endTransaction(TAG);
				return null;
			} catch (QuerySyntaxException e) {
				e.printStackTrace();
				DBAdapter.INST.endTransaction(TAG);
				return null;
			} catch (TableNotExistsException e) {
				e.printStackTrace();
				DBAdapter.INST.endTransaction(TAG);
				return null;
			}
			DBAdapter.INST.endTransaction(TAG);
			Config.INST.DATABASE.setDatabaseInitialized();
			return null;
		}
		else {
			return null;
		}
	}

}
