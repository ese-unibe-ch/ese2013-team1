package ch.unibe.sport.main.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import ch.unibe.sport.R;
import ch.unibe.sport.DBAdapter.restApi.FindUserList;
import ch.unibe.sport.DBAdapter.restApi.SearchNewFriendsRequest;
import ch.unibe.sport.DBAdapter.restApi.UnisportSpiceService;
import ch.unibe.sport.config.Config;
import ch.unibe.sport.dialog.BaseDialog;
import ch.unibe.sport.dialog.Dialog;
import ch.unibe.sport.utils.Objeckson;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class FriendsFindNewDialog extends BaseDialog {

	public static final String TAG = FriendsFindNewDialog.class.getName();

	private EditText friendSearch;
	private final SpiceManager spiceManager = new SpiceManager(UnisportSpiceService.class);
    private SearchNewFriendsRequest searchFriendsRequest;
    private static final String JSON_SEARCH_NEW_FRIENDS_KEY = "json_search_new_friends_key";
    
	private OnClickListener okListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			search();
		}
	};
	
	/*------------------------------------------------------------
	------------------- C O N S T R U C T O R S ------------------
	------------------------------------------------------------*/
	public static void show(Context context){	
		Intent intent = new Intent(context, FriendsFindNewDialog.class);
		intent.putExtra(Dialog.ACTION_ASK, Dialog.ACTION_YES);
		context.startActivity(intent);
	}
	
	public FriendsFindNewDialog() {
		super(TAG);
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
		addView(R.layout.friends_find_new_dialog);
		setTitle(R.string.dialog_find_new_friends_title);
		friendSearch = (EditText) this.findViewById(R.id.friend_search);
		Utils.showKeyboardInOnCreate(friendSearch);
		friendSearch.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 1) enableOkButton();
				else disableOkButton();
			}

			@Override public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before,int count) {}
			
		});
		initButtons();
	}
	
	private void initButtons() {
		this.setOnOkClickListener(okListener);
		this.setOkText(R.string.dialog_find_new_friends_search);
		disableOkButton();
	}
	
	/*------------------------------------------------------------
	--------------------------- S E A R C H ----------------------
	------------------------------------------------------------*/
	private void search(){
		String query = friendSearch.getText().toString();
		this.disableOkButton();
		if (query == null || query.length() <= 1) {
			Toast.makeText(this, "Query length should be > 1", Toast.LENGTH_SHORT).show();
			return;
		}
		
		friendSearch.setEnabled(false);
		this.showFade();
		this.showSpinner();
		this.disallowFinish();
		
        searchFriendsRequest = new SearchNewFriendsRequest(Config.INST.SYSTEM.UUID,Config.INST.USER.ID,query);
        spiceManager.execute(searchFriendsRequest, JSON_SEARCH_NEW_FRIENDS_KEY, DurationInMillis.ALWAYS_EXPIRED, new SearchNewFriendsRequestListener());
	}
	
	private void onSearched(FindUserList users){
		if (users == null || users.size() == 0) {
			Toast.makeText(this, "Nobody found", Toast.LENGTH_SHORT).show();
			this.hideSpinner();
			this.hideFade();
			this.enableOkButton();
			this.friendSearch.setEnabled(true);
			this.allowFinish();
			return;
		}
		finish();
		FriendsFindNewResultActivity.show(this, users.getUsers());
	}
	
	
	private class SearchNewFriendsRequestListener implements RequestListener<String>{

		private boolean done = false;
		
		@Override
		public void onRequestFailure(SpiceException e) {
			if (done) return;
			done = true;
			onSearched(null);
		}

		@Override
		public void onRequestSuccess(String json) {
			if (done) return;
			done = true;
			if (json == null || json.length() == 0) {
				onSearched(null);
				return;
			}
			FindUserList users = Objeckson.fromJson(json, FindUserList.class);
			onSearched(users);
		}
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

}
