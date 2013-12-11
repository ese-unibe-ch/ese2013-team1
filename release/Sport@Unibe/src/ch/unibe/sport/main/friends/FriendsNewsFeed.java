package ch.unibe.sport.main.friends;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import ch.unibe.sport.R;
import ch.unibe.sport.core.News;
import ch.unibe.sport.core.User;
import ch.unibe.sport.widget.view.NotificationButton;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.octo.android.robospice.SpiceManager;

public class FriendsNewsFeed extends ListView implements IFriendsSocialTab {

	private NotificationButton notificationButton;
	private FriendsNewsFeedAdapter listAdapter;
	
	public FriendsNewsFeed(Context context) {
		super(context);
	}
	
	public FriendsNewsFeed(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FriendsNewsFeed(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void initialize() {
		notificationButton = new NotificationButton(getContext());
		notificationButton.setImageResource(R.drawable.feed_icon);
		notificationButton.setPadding(5f);
		notificationButton.setNotifications(0);
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public NotificationButton getNotificationButton() {
		return notificationButton;
	}

	@Override
	public View getOptionsPanel() {
		return null;
	}

	@Override
	public void setOnOptionPanelListener(OnOptionPanelListener l) {}

	@Override
	public void setUser(User user) {
		News[] news = user.getNews();
		if (news == null) news = new News[0];
		this.listAdapter = new FriendsNewsFeedAdapter(news);
		SwingBottomInAnimationAdapter animatedAdapter = new SwingBottomInAnimationAdapter(listAdapter);
		animatedAdapter.setAbsListView(this);
		this.setAdapter(animatedAdapter);
	}

	@Override
	public void setSpiceManager(SpiceManager spiceManager) {}
}
