package ch.unibe.sport.slidingmenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.widget.TextView;
import ch.unibe.sport.R;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;

public class AboutDialog extends BaseDialog {

	public static final String TAG = AboutDialog.class.getName();
	
	private TextView aboutText;
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public AboutDialog() {
		super(TAG);
	}

	public static void show(Context context){	
		Intent intent = new Intent(context, AboutDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	/*------------------------------------------------------------
	--------------------------- I N I T --------------------------
	------------------------------------------------------------*/
	private void initView(){
		this.addView(R.layout.about_page);
		this.aboutText = (TextView)this.findViewById(R.id.about_text);
		this.aboutText.setText(Html.fromHtml(getString(R.string.about_page_text)));
		this.aboutText.setMovementMethod(LinkMovementMethod.getInstance());
		this.hideOkCancel();
		this.showClose();
		this.setTitle(R.string.slider_menu_about);
		this.setTitleColor(0xFFd4080e);
		this.setTitleTypeface(Typeface.BOLD);
		this.setTitleGravity(Gravity.CENTER_HORIZONTAL);
	}
}
