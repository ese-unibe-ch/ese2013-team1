package ch.unibe.sport.dialog;

import java.util.LinkedList;

import ch.unibe.sport.network.Message;
import ch.unibe.sport.network.ProxySherlockFragmentActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

public abstract class Dialog extends ProxySherlockFragmentActivity {
	
	public Dialog(String tag) {
		super(tag);
	}
	
	public static final int DIALOG_SELECT_INTERVAL = 1;
	
	public static final String ACTION_DO = "do";
	public static final String ACTION_QUESTION = "question";
	public static final String ACTION_ASK = "ask";
	public static final String ACTION_AUTO_HIDE = "autohide";
	public static final String ACTION_HIDDEN = "hidden";
	public static final String ACTION_PROGRESS = "progress";
	public static final String ACTION_QUICK_HIDE = "quick_hide";
	
	public static final String ACTION_YES = "yes";
	public static final String ACTION_NO = "no";
	
	public final static int RESULT_ERROR = -404;
	public final static int RESULT_NOTHING_TO_SELECT = -503;
	
	protected Bundle extras;
	private LinkedList<String> paramKeys;
	
	protected String Do;
	protected String Question;
	/* default values */
	protected boolean Ask = true;
	protected boolean Progress = true;
	protected boolean AutoHide = false;
	protected boolean Hidden = false;
	protected boolean QuickHide = false;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDialog();
	}
	
	private void initDialog(){
		if (initParams()){
			initDo();
			initQuestion();
			initAsk();
			initAutoHide();
			initHidden();
			initProgress();
			initQuickHide();
		}
	}
		
	protected Context getContext(){
		return this;
	}
	
	/**
	 * Initializes params, that was bekommen by dialog.
	 */
	private boolean initParams(){
		extras = getIntent().getExtras();
		if (extras == null) {
			return false;
		}
		paramKeys = new LinkedList<String>(extras.keySet());
		if (paramKeys.size() == 0) return false;
		return true;
	}
	
	/**
	 * Returns {@code String} object holidng parameter value. 
	 * @param key - {@code String} param's key.
	 * @return {@code String} - param value.
	 */
	protected String getParam(String key){
		assert key != null;
		assert key.length() > 0;
		return extras.getString(key);
	}
	
	/**
	 * Gets 'do' parameter. This parameter always should be passed.
	 */
	private void initDo(){
		Do = getParam(ACTION_DO);
	}
	
	/**
	 * Gets 'Question' that will be displayed as title of dialog.
	 * It hepls user to understand what will be task doing.
	 */
	private void initQuestion(){
		if (paramKeys.contains(ACTION_QUESTION)){
			Question = getParam(ACTION_QUESTION);
		}
		else {
			Question = "";
		}
	}
	
	/**
	 * Gets 'Ask' boolean parameter. It says whether user
	 * should be asked if he acepts action.
	 */
	private void initAsk(){
		if (paramKeys.contains(ACTION_ASK)){
			String param = getParam(ACTION_ASK);
			if (param.equals(ACTION_YES)){
				Ask = true;
			}
			else if (param.equals(ACTION_NO)){
				Ask = false;
			}
			else {
				System.err.println(ACTION_ASK+" - Unknown param: "+param);
			}
		}
	}
	
	/**
	 * Gets 'AutoHide' parameter. If AutoHide is {@code True} dialog will hide after task ist complete,
	 * otherwise no.
	 */
	private void initAutoHide(){
		if (paramKeys.contains(ACTION_AUTO_HIDE)){
			String param = getParam(ACTION_AUTO_HIDE);
			if (param.equals(ACTION_YES)){
				AutoHide = true;
			}
			else if (param.equals(ACTION_NO)){
				AutoHide = false;
			}
			else {
				System.err.println(ACTION_AUTO_HIDE+" - Unknown param: "+param);
			}
		}
	}
	/**
	 * Gets 'Hidden' parameter. If Hidden is {@code True} dialog will be hidden,
	 * otherwise no.
	 */
	private void initHidden(){
		if (paramKeys.contains(ACTION_HIDDEN)){
			String param = getParam(ACTION_HIDDEN);
			if (param.equals(ACTION_YES)){
				Hidden = true;
			}
			else if (param.equals(ACTION_NO)){
				Hidden = false;
			}
			else {
				System.err.println(ACTION_HIDDEN+" - Unknown param: "+param);
			}
		}
	}
	
	/**
	 * Gets 'Progress' parameter. If Progress is {@code True} progress bar will be visible,
	 * otherwise no.
	 */
	private void initProgress(){
		if (paramKeys.contains(ACTION_PROGRESS)){
			String param = getParam(ACTION_PROGRESS);
			if (param.equals(ACTION_YES)){
				Progress = true;
			}
			else if (param.equals(ACTION_NO)){
				Progress = false;
			}
			else {
				System.err.println(ACTION_PROGRESS+" - Unknown param: "+param);
			}
		}
	}
	
	/**
	 * Gets 'Progress' parameter. If Progress is {@code True} progress bar will be visible,
	 * otherwise no.
	 */
	private void initQuickHide(){
		if (paramKeys.contains(ACTION_QUICK_HIDE)){
			String param = getParam(ACTION_QUICK_HIDE);
			if (param.equals(ACTION_YES)){
				QuickHide = true;
			}
			else if (param.equals(ACTION_NO)){
				QuickHide = false;
			}
			else {
				System.err.println(ACTION_QUICK_HIDE+" - Unknown param: "+param);
			}
		}
	}

	/*------------------------------------------------------------
	------------------------- D E F A U L T ----------------------
	------------------------------------------------------------*/
	/**
	 * Deactivates close by back button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override public void process(Message message) {}
}
