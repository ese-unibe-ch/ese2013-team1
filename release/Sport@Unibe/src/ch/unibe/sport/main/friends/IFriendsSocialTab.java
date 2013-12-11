package ch.unibe.sport.main.friends;

import com.octo.android.robospice.SpiceManager;

import ch.unibe.sport.core.User;
import ch.unibe.sport.widget.view.NotificationButton;
import android.view.View;

public interface IFriendsSocialTab {
	public View getView();
	public NotificationButton getNotificationButton();
	public View getOptionsPanel();
	public void setOnOptionPanelListener(OnOptionPanelListener l);
	public void setUser(User user);
	public void setSpiceManager(SpiceManager spiceManager);
	public void initialize();
}
