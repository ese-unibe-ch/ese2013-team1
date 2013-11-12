package ch.unibe.sport.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ch.unibe.sport.R;
import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;

public class SportInfoActivity extends ProxySherlockFragmentActivity {
	public static final String TAG = SportInfoActivity.class.getName();

	public static final String SPORT_ID_PARAM_NAME = "sportID";
	
	private SportInfoController viewController;
	
	public SportInfoActivity() {
		super(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setActionBarHomeAsBack();
		this.setContentView(R.layout.sport_info_layout);
		viewController = new SportInfoController(this);
		viewController.connect(this);
	}
	
	public static void show(final Context context,final int sportID) {
		Intent intent = new Intent();
		intent.setClass(context, SportInfoActivity.class);
		intent.putExtra(SPORT_ID_PARAM_NAME, sportID);
		context.startActivity(intent);
	}
	
	@Override public void process(Message message) {}

}
