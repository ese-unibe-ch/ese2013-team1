package ch.unibe.sport.main;

import java.util.List;

import ch.unibe.sport.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // This should be called first thing
        super.onCreate(savedInstanceState);
    }
	
	 /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    }
    
	/**
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            addPreferencesFromResource(R.xml.settings);
        }
    }
    
    public static final void show(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, SettingsActivity.class);
		context.startActivity(intent);
	}
}
