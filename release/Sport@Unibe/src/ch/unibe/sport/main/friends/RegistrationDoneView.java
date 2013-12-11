package ch.unibe.sport.main.friends;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.SetUsername;
import ch.unibe.sport.DBAdapter.restApi.SetUsernameRequest;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.main.friends.SignUpView.CircularAnimation;
import ch.unibe.sport.network.IPointable;
import ch.unibe.sport.utils.Print;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RegistrationDoneView extends LinearLayout implements IPage {

	private OnPageSwitchRequestListener mOnPageSwitchRequestListener;
	private Button doneButton;
	private EditText usernameView;
	private View indicatorRed;
	private View indicatorGrey;

	private int userID;
	
    private static final String JSON_SET_USERNAME_KEY = "json_set_username_key";
    private SpiceManager spiceManager;
	
	public RegistrationDoneView(Context context) {
		super(context);
	}
	
	public RegistrationDoneView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RegistrationDoneView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setUserID(int userID){
		this.userID = userID;
	}

	@Override
	public void initialize(){
		View.inflate(getContext(), R.layout.friends_welcome_page_done, this);
		this.doneButton = (Button) this.findViewById(R.id.done);
		this.indicatorRed = this.findViewById(R.id.indicator_red);
		this.indicatorGrey = this.findViewById(R.id.indicator_grey);
		this.usernameView = (EditText) this.findViewById(R.id.username);
		
		this.doneButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (usernameView.getText().toString().length() > 0){
					setUsername();
				}
				else if (mOnPageSwitchRequestListener != null){
					mOnPageSwitchRequestListener.onPageSwitch(RegistrationDoneView.this, new FriendsSocialView(getContext()));
				}
			}
		});
	}
	
	private void enableControlls(){
		doneButton.setText("Start using");
		doneButton.setClickable(true);
		doneButton.setEnabled(true);
		usernameView.setEnabled(true);
	}
	
	private void disableControlls(){
		doneButton.setText("Loading...");
		doneButton.setClickable(false);
		doneButton.setEnabled(false);
		usernameView.setEnabled(false);
	}
	
	private void setUsername(){
		disableControlls();
		String username = this.usernameView.getText().toString();
		
		initSetUsernameRequest(username);
	}
	
	private void initSetUsernameRequest(String username){
		startWaitingAnimation();
		SetUsernameRequest setUsernameRequest = new SetUsernameRequest(Config.INST.SYSTEM.UUID, userID, username);
		spiceManager.execute(setUsernameRequest, JSON_SET_USERNAME_KEY, DurationInMillis.ALWAYS_EXPIRED, new SetUsernameRequestListener(username));
	}
		
	private void onSaved(int userID,String username){
		if (Config.INST.USER != null){
			Config.INST.USER.setUserUsername(username);
		}
		if (mOnPageSwitchRequestListener != null){
			mOnPageSwitchRequestListener.onPageSwitch(this,new FriendsSocialView(getContext()));
			return;
		}
	}
	
	private void startWaitingAnimation(){
		//calculating radius according to dots positions
		int[] locationRed = new int[2];
		indicatorRed.getLocationInWindow(locationRed);
		int[] locationGrey = new int[2];
		indicatorGrey.getLocationInWindow(locationGrey);
		float radius = Math.abs(locationGrey[0] - locationRed[0])/2;
		
		Animation animationRed = new CircularAnimation(indicatorRed, radius, -1);
		indicatorRed.startAnimation(animationRed);
		Animation animationGrey = new CircularAnimation(indicatorGrey, radius, 1);
		indicatorGrey.startAnimation(animationGrey);
	}
	
	private void finishWaitingAnimation(){
		if (indicatorRed != null)indicatorRed.clearAnimation();
		if (indicatorGrey != null)indicatorGrey.clearAnimation();
	}
	
	@Override
	public void setOnPageSwitchRequestListener(OnPageSwitchRequestListener l) {
		mOnPageSwitchRequestListener = l;
	}
	
	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
	}

	@Override
	public void connect(IPointable point) {
		// TODO Auto-generated method stub
	}
	
	private class SetUsernameRequestListener implements RequestListener<SetUsername>{
		private boolean done;
		
		private String username;
		
		public SetUsernameRequestListener(String username) {
			this.username = username;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			saveFailed();
		}

		@Override
		public void onRequestSuccess(SetUsername setUsername) {
			if (done) return;
			done = true;
			if (setUsername == null || setUsername.isError()) {
				if (setUsername != null) Print.err(setUsername.getMessage());
				saveFailed();
				return;
			}
			
			onSaved(userID, username);	
		}
		
		private void saveFailed(){
			Toast.makeText(getContext(), "Failed to save username.", Toast.LENGTH_SHORT).show();
			finishWaitingAnimation();
			enableControlls();
		}
	}
}
