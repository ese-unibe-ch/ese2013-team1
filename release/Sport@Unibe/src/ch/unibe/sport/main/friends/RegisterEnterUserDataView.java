package ch.unibe.sport.main.friends;

import ch.unibe.sport.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class RegisterEnterUserDataView extends RelativeLayout {

	
	private EditText nicknameView;
	private EditText usernameView;
	private Button next;
	
	public RegisterEnterUserDataView(Context context) {
		super(context);
		 init();
	}
	
	public RegisterEnterUserDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 init();
	}
	
	public RegisterEnterUserDataView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		 init();
	}

	private void init(){
		View.inflate(getContext(), R.layout.friends_welcome_page_nickname, this);
		nicknameView = (EditText) this.findViewById(R.id.nickname);
		usernameView = (EditText) this.findViewById(R.id.username);
		next = (Button) this.findViewById(R.id.next);
	}	
	
}
