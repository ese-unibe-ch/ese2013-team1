package ch.unibe.sport.dialog;

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ch.unibe.sport.R;
import ch.unibe.sport.utils.Utils;

public abstract class BaseDialog extends Dialog {

	private static final int BUTTON_TEXT_COLOR_ON = 0x99222222;
	private static final int BUTTON_TEXT_COLOR_OFF = 0x33222222;
	
	private TextView title;
	private ViewGroup fade;
	private ViewGroup errorContainer;
	private TextView errorView;
	private TextView errorDetailsView;
	private ImageView spinner;
	private AnimationDrawable spinnerAnimation;
	private Button cancel;
	private Button ok;
	private Button close;
	
	private ViewGroup okCancelContainer;
	private ViewGroup closeContainer;
	
	private ViewGroup contentContainer;
	
	private boolean isFinishAllowed = true;
	
	private OnClickListener okListener;
	private OnClickListener finishListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			hideSpinner();
			finish();
		}
	};
	
	public BaseDialog(String tag) {
		super(tag);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	
	private void initView() {
		this.setContentView(R.layout.dialog_layout);
		this.contentContainer = (ViewGroup) this.findViewById(R.id.content);
		this.title = (TextView) this.findViewById(R.id.title);
		this.spinner = (ImageView) this.findViewById(R.id.progress_bar);
		this.spinner.setBackgroundResource(R.drawable.unisport_spinner);
		this.fade = (ViewGroup) this.findViewById(R.id.fade);
		this.errorContainer = (ViewGroup) this.findViewById(R.id.error_container);
		this.errorView = (TextView) this.findViewById(R.id.error_text);
		this.errorDetailsView = (TextView) this.findViewById(R.id.error_text_detail);
		this.cancel = (Button) this.findViewById(R.id.cancel);
		this.ok = (Button) this.findViewById(R.id.ok);
		this.close = (Button) this.findViewById(R.id.close);
		this.okCancelContainer = (ViewGroup) this.findViewById(R.id.ok_cancel_container);
		this.closeContainer = (ViewGroup) this.findViewById(R.id.close_container);
				
		initButtons();
		hideFade();
		hideSpinner();
	}
	
	private void initButtons(){
		cancel.setOnClickListener(finishListener);
	}
				
	/*------------------------------------------------------------
	----------------------- P R O T E C T E D --------------------
	------------------------------------------------------------*/
	protected void setOkText(int resId){
		if (ok == null) return;
		ok.setText(resId);
	}
	
	protected void setCancelText(int resId){
		if (cancel == null) return;
		cancel.setText(resId);
	}
	
	protected void setCloseText(int resId){
		if (close == null) return;
		close.setText(resId);
	}
	
	protected void setOnOkClickListener(OnClickListener l){
		this.okListener = l;
		if (ok != null) ok.setOnClickListener(okListener);
	}
	
	protected void showError(int errorRes, int errorDetailsRes) {
		hideSpinner();
		setTitle(R.string.dialog_title_error);
		/* activity can be finished before asynctask */
		if (errorView == null || errorView == null || errorContainer == null) return;
		errorView.setText(Utils.getString(getContext(), errorRes));
		errorDetailsView.setText(Utils.getString(getContext(), errorDetailsRes));
		errorContainer.setVisibility(View.VISIBLE);
		switchOkCancelToClose();
		this.enableCloseButton();
	}
	
	protected void setTitle(String text){
		/* activity can be finished before asynctask */
		if (this.title == null) return;
		this.title.setText(text);
	}
	
	@Override
	public void setTitleColor(int color){
		/* activity can be finished before asynctask */
		if (this.title == null) return;
		this.title.setTextColor(color);
	}
	
	protected void setTitleTypeface(int typeface){
		/* activity can be finished before asynctask */
		if (this.title == null) return;
		this.title.setTypeface(null,typeface);
	}
	
	protected void setTitleGravity(int gravity){
		/* activity can be finished before asynctask */
		if (this.title == null) return;
		this.title.setGravity(gravity);
	}
	
	protected void setTitleSize(float sp){
		/* activity can be finished before asynctask */
		if (this.title == null) return;
		this.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
	}
	
	protected void disallowFinish(){
		this.isFinishAllowed = false;
		disableCancelButton();
		disableCloseButton();
		this.setFinishOnTouchOutside(false);
	}
	
	protected void allowFinish(){
		this.isFinishAllowed = true;
		enableCancelButton();
		enableCloseButton();
		this.setFinishOnTouchOutside(true);
	}

	protected void disableOkButton(){
		if (ok == null) return;
		ok.setOnClickListener(null);
		ok.setClickable(false);
		ok.setTextColor(BUTTON_TEXT_COLOR_OFF);
	}
	
	protected void enableOkButton(){
		if (ok == null) return;
		ok.setOnClickListener(okListener);
		ok.setClickable(true);
		ok.setTextColor(BUTTON_TEXT_COLOR_ON);
	}
	
	protected void disableCloseButton(){
		if (close == null) return;
		close.setOnClickListener(null);
		close.setClickable(false);
		close.setTextColor(BUTTON_TEXT_COLOR_OFF);
	}
	
	protected void enableCloseButton(){
		if (close == null) return;
		close.setOnClickListener(finishListener);
		close.setClickable(true);
		close.setTextColor(BUTTON_TEXT_COLOR_ON);
	}
	
	protected void disableCancelButton(){
		if (cancel == null) return;
		cancel.setOnClickListener(null);
		cancel.setClickable(false);
		cancel.setTextColor(BUTTON_TEXT_COLOR_OFF);
	}
	
	protected void enableCancelButton(){
		if (cancel == null) return;
		cancel.setOnClickListener(finishListener);
		cancel.setClickable(true);
		cancel.setTextColor(BUTTON_TEXT_COLOR_ON);
	}
	
	protected void hideFade(){
		if (fade == null) return;
		fade.setVisibility(View.GONE);
	}
	
	protected void showFade(){
		if (fade == null) return;
		fade.setVisibility(View.VISIBLE);
	}
	
	protected void hideOkCancel(){
		/* activity can be finished before asynctask */
		if (okCancelContainer == null) return;
		this.okCancelContainer.setVisibility(View.GONE);
	}
	
	protected void showClose(){
		/* activity can be finished before asynctask */
		if (closeContainer == null || close == null) return;
		this.closeContainer.setVisibility(View.VISIBLE);
		this.close.setOnClickListener(finishListener);
	}
	
	protected void hideSpinner(){
		/* activity can be finished before asynctask */
		if (spinner == null) return;
		if (spinnerAnimation == null) spinnerAnimation = (AnimationDrawable)spinner.getBackground();
		if (spinnerAnimation == null) return;
		spinnerAnimation.stop();
		spinner.setVisibility(View.GONE);
	}
	
	protected void showSpinner(){
		/* activity can be finished before asynctask */
		if (spinner == null) return;
		if (spinnerAnimation == null) spinnerAnimation = (AnimationDrawable)spinner.getBackground();
		if (spinnerAnimation == null) return;
		spinner.setVisibility(View.VISIBLE);
		spinnerAnimation.start();
	}
	
	protected void switchOkCancelToClose(){
		this.hideOkCancel();
		this.showClose();
	}
	
	/*------------------------------------------------------------
	-------------------------- P U B L I C -----------------------
	------------------------------------------------------------*/
	public void addView(View view){
		this.contentContainer.addView(view);
	}
	
	public void addView(int resId){
		View view = getView(resId);
		if (view == null || view.getParent() != null) return;
		this.contentContainer.addView(view);
	}
	
	public View getView(int resId){
		return getLayoutInflater().inflate(resId, null);
	}
	
	@Override
	public void setTitle(int titleTextRes){
		setTitle(Utils.getString(getContext(), titleTextRes));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (isFinishAllowed)finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFinishAllowed)finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
