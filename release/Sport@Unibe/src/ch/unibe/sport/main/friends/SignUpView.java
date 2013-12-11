package ch.unibe.sport.main.friends;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.LoginUser;
import ch.unibe.sport.DBAdapter.restApi.LoginUserRequest;
import ch.unibe.sport.DBAdapter.restApi.RegisterUser;
import ch.unibe.sport.DBAdapter.restApi.RegisterUserRequest;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.network.IPointable;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class SignUpView extends RelativeLayout implements IPage {

	private EditText nicknameView;
	private EditText passwordView;
	private int defaultPasswordInputType;
	private Button next;
	private Button login;
	private ViewGroup showPassword;
	private View indicatorRed;
	private View indicatorGrey;
	private View rootView;
	
    private static final String JSON_REGISTER_USER_KEY = "json_register_user_key";
    private static final String JSON_LOGIN_USER_KEY = "json_login_user_key";
    private SpiceManager spiceManager;
    
	private OnPageSwitchRequestListener mOnPageSwitchRequestListener;
	public SignUpView(Context context) {
		super(context);
	}
	
	public SignUpView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SignUpView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void initialize(){
		rootView = View.inflate(getContext(), R.layout.friends_welcome_page_nickname, null);
		this.addView(rootView);
		nicknameView = (EditText) this.findViewById(R.id.nickname);
		passwordView = (EditText) this.findViewById(R.id.password);
		defaultPasswordInputType = passwordView.getInputType();
		next = (Button) this.findViewById(R.id.next);
		login = (Button) this.findViewById(R.id.login);
		indicatorRed = this.findViewById(R.id.indicator_red);
		indicatorGrey = this.findViewById(R.id.indicator_grey);
		showPassword = (ViewGroup) this.findViewById(R.id.show_password);
		showPassword.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action){
					case MotionEvent.ACTION_DOWN: {
						passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						break;
					}
					case MotionEvent.ACTION_UP: {
						passwordView.setInputType(defaultPasswordInputType);
						break;
					}
					case MotionEvent.ACTION_OUTSIDE:{
						passwordView.setInputType(defaultPasswordInputType);
						break;
					}
					case MotionEvent.ACTION_MOVE:{
						break;
					}
					default:{
						passwordView.setInputType(defaultPasswordInputType);
					}
				}
				return false;
			}
		});
		
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				register();
			}
		});
		
		login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				login();
			}
			
		});
	}
	
	private void enableControlls(){
		next.setClickable(true);
		next.setEnabled(true);
		login.setClickable(true);
		login.setEnabled(true);
		nicknameView.setEnabled(true);
		passwordView.setEnabled(true);
	}
	
	private void disableControlls(){
		next.setClickable(false);
		next.setEnabled(false);
		login.setClickable(false);
		login.setEnabled(false);
		nicknameView.setEnabled(false);
		passwordView.setEnabled(false);
	}
	
	private void register(){
		disableControlls();
		next.setText("Loading...");
		String nickname = this.nicknameView.getText().toString();
		String password = this.passwordView.getText().toString();
		
		initRegisterRequest(nickname, password);
	}
	
	private void login(){
		disableControlls();
		login.setText("Loading...");
		String nickname = this.nicknameView.getText().toString();
		String password = this.passwordView.getText().toString();
		
		initLoginRequest(nickname, password);
	}
	
	private void initRegisterRequest(String nickname, String password){
		startWaitingAnimation();
		RegisterUserRequest registerRequest = new RegisterUserRequest(Config.INST.SYSTEM.UUID, nickname, password);
		spiceManager.execute(registerRequest, JSON_REGISTER_USER_KEY, DurationInMillis.ALWAYS_EXPIRED, new RegisterUserRequestListener(nickname));
	}
	
	private void initLoginRequest(String nickname, String password){
		startWaitingAnimation();
		LoginUserRequest loginRequest = new LoginUserRequest(nickname, password);
		spiceManager.execute(loginRequest, JSON_LOGIN_USER_KEY, DurationInMillis.ALWAYS_EXPIRED, new LoginUserRequestListener());
	}
		
	private void onRegistered(int userID,String nickname){
		if (Config.INST.USER != null){
			Config.INST.USER.setUserID(userID);
			Config.INST.USER.setUserNickname(nickname);
		}
		if (mOnPageSwitchRequestListener != null){
			RegistrationDoneView registrationDoneView = new RegistrationDoneView(getContext());
			registrationDoneView.setUserID(userID);
			mOnPageSwitchRequestListener.onPageSwitch(this,registrationDoneView);
			return;
		}
	}
	
	private void onLogined(int userID,String hash,String nickname,String username){
		if (Config.INST.USER != null){
			Config.INST.USER.setUserID(userID);
			Config.INST.USER.setUserNickname(nickname);
			Config.INST.USER.setUserUsername(username);
			Config.INST.SYSTEM.saveUUID(hash);
		}
		if (mOnPageSwitchRequestListener != null){
			mOnPageSwitchRequestListener.onPageSwitch(this, new FriendsSocialView(getContext()));
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
	
	/**
	 * 
	 * @author Team 1 2013
	 */
	public static class CircularAnimation extends Animation {

	    private View view;
	    private float cx, cy;           	// center x,y position of circular path
	    private float prevX, prevY;     	// previous x,y position of image during animation
	    private float radius;                // radius of circle
	    private int direction;


	    /**
	     * @param view - View to be animated
	     * @param radius - circle path radius
	     */
	    public CircularAnimation(View view, float radius, int direction){
	        this.view = view;
	        this.radius = radius;
	        this.direction = direction;
	        this.setRepeatCount(Animation.INFINITE);
	        this.setRepeatMode(Animation.INFINITE);
	        this.setDuration(2000);
	    }

	    @Override
	    public boolean willChangeBounds() {
	        return true;
	    }

	    @Override
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {
	        // calculate position of image center
	        int cxImage = width / 2;
	        int cyImage = height / 2;
	        cx = view.getLeft() + cxImage;
	        cy = view.getTop() + cyImage;

	        // set previous position to center
	        prevX = cx;
	        prevY = cy;
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
	        if(interpolatedTime == 0){
	            return;
	        }

	        float angleDegree = (interpolatedTime * 360f) % 360 + 90*(direction+1);
	        float angleRadian = (float) Math.toRadians(angleDegree);

	        // r = radius, cx and cy = center point, a = angle (radians)
	        float x = (float) (cx + direction * radius + radius * Math.cos(angleRadian));
	        float y = (float) (cy + radius * Math.sin(angleRadian));


	        float dx = prevX - x;
	        float dy = prevY - y;

	        prevX = x;
	        prevY = y;

	        transformation.getMatrix().setTranslate(dx, dy);
	    }
	}

	@Override
	public void setOnPageSwitchRequestListener(OnPageSwitchRequestListener l) {
		this.mOnPageSwitchRequestListener = l;
	}

	@Override
	public View getView() {
		return this;
	}
	
	private class RegisterUserRequestListener implements RequestListener<RegisterUser>{
		private String nickname;
		private boolean done;
		
		public RegisterUserRequestListener(String nickname){
			this.nickname = nickname;
		}
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			registerFailed(null);
		}

		@Override
		public void onRequestSuccess(RegisterUser registerUser) {
			if (done) return;
			done = true;
			if (registerUser == null) {
				registerFailed(null);
				return;
			}
			
			int userID = registerUser.getUserID();
			
			if (userID == 0){
				registerFailed(registerUser);
				return;
			}
			onRegistered(userID,nickname);	
		}
		
		private void registerFailed(RegisterUser registerUser){
			Toast.makeText(getContext(), "Failed to register. "+((registerUser!=null)?registerUser.getMessage():""), Toast.LENGTH_SHORT).show();
			finishWaitingAnimation();
			login.setText("Login");
			next.setText("Register");
			enableControlls();
		}
	}
	
	private class LoginUserRequestListener implements RequestListener<LoginUser>{
		private boolean done;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			registerFailed(null);
		}

		@Override
		public void onRequestSuccess(LoginUser loginUser) {
			if (done) return;
			done = true;
			if (loginUser == null) {
				registerFailed(null);
				return;
			}
			
			int userID = loginUser.getUserID();
			String hash = loginUser.getHash();
			String nickname = loginUser.getNickname();
			String username = loginUser.getUsername();
					
			if (userID == 0 || hash == null || hash.length() == 0){
				registerFailed(loginUser);
				return;
			}
			onLogined(userID,hash,nickname,username);	
		}
		
		private void registerFailed(LoginUser loginUser){
			Toast.makeText(getContext(), "Failed to register. "+((loginUser!=null)?loginUser.getMessage():""), Toast.LENGTH_SHORT).show();
			finishWaitingAnimation();
			enableControlls();
		}
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
	}

	@Override
	public void connect(IPointable point) {
		// TODO Auto-generated method stub
		
	}
}
