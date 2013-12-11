package ch.unibe.sport.main.initialization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import ch.unibe.sport.R;
import ch.unibe.sport.main.MainActivity;
import ch.unibe.sport.main.initialization.tasks.LoadUnisportData;
import ch.unibe.sport.main.initialization.tasks.RegisterAppInstallation;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.MessageFactory;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import ch.unibe.sport.utils.Print;

public class InitializationActivity extends ProxySherlockFragmentActivity {

	public static final String TAG = InitializationActivity.class.getName();
	
	private InitializationAdapter mAdapter;
	private InitializationViewController controller;
	
	public InitializationActivity() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.initialization_layout);
		init();
	}
	
	public static void show(final Context context) {
		if (context == null) return;
		Intent intent = new Intent();
		intent.setClass(context, InitializationActivity.class);
		context.startActivity(intent);
	}
	
	/*------------------------------------------------------------
	---------------------------- I N I T -------------------------
	------------------------------------------------------------*/
	private void init(){
		 controller = new InitializationViewController(this);
		 controller.setStatusButtonLoading();
		 
		 mAdapter = new InitializationAdapter();
		 mAdapter.setInitializationProgressListener(new MyInitializationProgressListener());
		 mAdapter.setBreakOnError(true);
		 mAdapter.addTask(new RegisterAppInstallation(this));
		 mAdapter.addTask(new LoadUnisportData(this));
		 mAdapter.execute();
		 
	}
	
	private class MyInitializationProgressListener implements InitializationProgressListener {

		@Override
		public void onTaskCompleted(IInitializationTask task, int index, int count, InitializationException exception) {
			Print.log(index+"/"+count+" exception = "+ ((exception == null) ? "null": exception.getMessage()));
		}

		@Override
		public void onAllCompleted(int successfull, int count) {
			Print.log(successfull+"/"+count);
			if (successfull==count){
				send(MessageFactory.continueLoading(TAG,MainActivity.TAG));
				controller.setStatusButtonCompleted();
			}
			else {
				controller.setStatusButtonError();
			}
		}
	 }
	
	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/* disabling back button */
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
	@Override public void process(Message message) {}
}
