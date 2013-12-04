package ch.unibe.sport.main.initialization;

import ch.unibe.sport.R;
import android.view.ViewGroup;
import android.widget.TextView;

public class InitializationViewHolder {
	
	public final TextView loadingStatusButton;
	public final TextView loadingStatusButtonHelper;
	public final ViewGroup loadingStatusButtonContainer;
	
	public InitializationViewHolder(InitializationActivity init){
		loadingStatusButton = (TextView) init.findViewById(R.id.status_button);
		loadingStatusButtonHelper = (TextView) init.findViewById(R.id.status_button_helper);
		loadingStatusButtonContainer = (ViewGroup) init.findViewById(R.id.status_button_container);
	}
	
}
