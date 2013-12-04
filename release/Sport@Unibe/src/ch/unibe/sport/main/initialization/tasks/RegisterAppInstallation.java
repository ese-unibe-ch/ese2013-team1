package ch.unibe.sport.main.initialization.tasks;

import android.content.Context;
import android.widget.Toast;
import ch.unibe.sport.DBAdapter.restApi.RegisterUUID;
import ch.unibe.sport.DBAdapter.restApi.RegisterUUIDRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.config.System;
import ch.unibe.sport.main.initialization.IInitializationTask;
import ch.unibe.sport.main.initialization.InitializationCallback;
import ch.unibe.sport.main.initialization.InitializationException;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

public class RegisterAppInstallation implements IInitializationTask{

	public static final String TAG = RegisterAppInstallation.class.getName();
	private static final String JSON_REGISTER_UUID_KEY = "json_register_uuid_key";
	
	private final RegisterUUIDRequestListener registerListener = new RegisterUUIDRequestListener();
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
	private final Context context;
	
	private RegisterUUIDRequest registerRequest;
	private InitializationCallback mCallback;
	
	public RegisterAppInstallation(Context context){
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
        spiceManager.addListenerIfPending(RegisterUUID.class, JSON_REGISTER_UUID_KEY, registerListener);
        spiceManager.getFromCache(RegisterUUID.class, JSON_REGISTER_UUID_KEY, DurationInMillis.ALWAYS_RETURNED, registerListener);
	}

	@Override
	public boolean isCompleted() {
		return Config.INST.SYSTEM.checkUUIDInitialized();
	}
	
	private void initUUID(){
		if (!isCompleted()){
			registerRequest = new RegisterUUIDRequest(System.generateUUID());
	        spiceManager.execute(registerRequest, JSON_REGISTER_UUID_KEY, DurationInMillis.ALWAYS_EXPIRED, registerListener);
		}
	}
	
	private class RegisterUUIDRequestListener implements RequestListener<RegisterUUID>, RequestProgressListener {

		private final int MaxFailCounter = 10;
		private int failCounter = 0;

		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			Print.log(TAG,"fail");
			failAction();
			spiceManager.shouldStop();
		}

		@Override
		public void onRequestSuccess(RegisterUUID register) {
			Print.log(TAG,"requestSuccess");
			if (done) return;
			if (register == null || !register.isRegistered() || register.getUUID()==null){
				failAction();
				return;
			}
			
			done = true;
			Print.log(TAG,register.getUUID());
			Config.INST.SYSTEM.saveUUID(register.getUUID());
			
			spiceManager.shouldStop();
			mCallback.onTaskCompleted(RegisterAppInstallation.this, null);
		}
		
		private void failAction(){
			failCounter++;
			if (failCounter <= MaxFailCounter){
				initUUID();
			}
			else {
				Toast.makeText(context, "Tries limit exceeded", Toast.LENGTH_SHORT).show();
				mCallback.onTaskCompleted(RegisterAppInstallation.this, new InitializationException("Tries limit exceeded"));
				spiceManager.shouldStop();
			}
		}

		@Override public void onRequestProgressUpdate(RequestProgress arg0) {}
	}
}
