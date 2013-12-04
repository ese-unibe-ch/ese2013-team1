package ch.unibe.sport.main.initialization.tasks;

import android.content.Context;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import ch.unibe.sport.DBAdapter.restApi.UnisportDataRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.core.Unisport;
import ch.unibe.sport.main.initialization.IInitializationTask;
import ch.unibe.sport.main.initialization.InitializationCallback;
import ch.unibe.sport.main.initialization.InitializationException;
import ch.unibe.sport.utils.Objeckson;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;
import ch.unibe.sport.utils.bulker.Bulker;
import ch.unibe.sport.utils.bulker.ClassCastException;

public class LoadUnisportData implements IInitializationTask {

	public static final String TAG = LoadUnisportData.class.getName();
	private static final String JSON_UISPORT_DATA_KEY = "json_unisport_data_key";
	
	private final LoadUnisportDataRequestListener registerListener = new LoadUnisportDataRequestListener();
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
	private final Context context;
	
	private UnisportDataRequest unisportDataRequest;
	private InitializationCallback mCallback;
	
	public LoadUnisportData(Context context){
		this.context = context;
	}
	
	@Override
	public void execute(InitializationCallback callback, Object... params) {
		this.mCallback = callback;
		if (isCompleted()){
			mCallback.onTaskCompleted(this, null);
			return;
		}
		
		if (!Utils.haveNetworkConnection(context)){
			mCallback.onTaskCompleted(this, new InitializationException("No internet connection"));
			return;
		}
		
		spiceManager.start(context);
        spiceManager.addListenerIfPending(String.class, JSON_UISPORT_DATA_KEY, registerListener);
        spiceManager.getFromCache(String.class, JSON_UISPORT_DATA_KEY, DurationInMillis.ALWAYS_EXPIRED, registerListener);
	}
	
	private void loadData(){
		if (!isCompleted()){
			unisportDataRequest = new UnisportDataRequest();
	        spiceManager.execute(unisportDataRequest, JSON_UISPORT_DATA_KEY, DurationInMillis.ALWAYS_EXPIRED, registerListener);
		}
	}

	@Override
	public boolean isCompleted() {
		return Config.INST.SYSTEM.checkDataUpdated();
	}
	
	private class LoadUnisportDataRequestListener implements RequestListener<String>, RequestProgressListener {

		private final int MaxFailCounter = 10;
		private int failCounter = 0;

		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			Print.log(TAG,"fail");
			failLoad();
			spiceManager.shouldStop();
		}

		@Override
		public void onRequestSuccess(String json) {
			Print.log(TAG,"requestSuccess");
			if (done) return;
			if (json == null){
				failLoad();
				return;
			}
			
			done = true;
			Print.log(TAG,json.length());
			/* deserializing unisport class from json */
			Unisport unisport = Objeckson.fromJson(json.toString(), Unisport.class);
			try {
				/* serializing unisport in database */
				Bulker.insert(context,unisport);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				insertException();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				insertException();
				return;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				insertException();
				return;
			} catch (ClassCastException e) {
				e.printStackTrace();
				insertException();
				return;
			}
			
			Config.INST.SYSTEM.setUnisportDataUpdated();
			
			spiceManager.shouldStop();
			mCallback.onTaskCompleted(LoadUnisportData.this, null);
		}
		
		private void insertException(){
			mCallback.onTaskCompleted(LoadUnisportData.this, new InitializationException("Data insertion error"));
		}
		
		private void failLoad(){
			failCounter++;
			if (failCounter <= MaxFailCounter){
				loadData();
			}
			else {
				Toast.makeText(context, "Tries limit exceeded", Toast.LENGTH_SHORT).show();
				mCallback.onTaskCompleted(LoadUnisportData.this, new InitializationException("Tries limit exceeded"));
				spiceManager.shouldStop();
			}
		}

		@Override public void onRequestProgressUpdate(RequestProgress arg0) {}
	}

}
