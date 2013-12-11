package ch.unibe.sport.main.friends;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.widget.view.NotificationButton;

public class FriendsSocialPagerPanelAdapter {
	
	private LinearLayout panel;
	private AssociativeList<NotificationButton> buttons;
	private NotificationButton selectedButton;
	private OnPagePanelSwitchListener mOnPagePanelSwitchListener;
	
	public interface OnPagePanelSwitchListener {
		public void onPanelSwitch(int page);
	}
	
	public FriendsSocialPagerPanelAdapter(LinearLayout panel){
		this.panel = panel;
		buttons = new AssociativeList<NotificationButton>();
	}
	
	public void addButton(NotificationButton button){
		if (buttons.size() == 0) selectButton(button);
		this.buttons.add(button, buttons.size());
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				NotificationButton button = (NotificationButton) v;
				if (button.equals(selectedButton)) return;
				selectButton(button);
				int id = buttons.indexOf(button);
				if (mOnPagePanelSwitchListener != null && id >= 0){
					mOnPagePanelSwitchListener.onPanelSwitch(id);
				}
			}
		});
		this.panel.addView(button);
	}
	
	public void selectButton(int index){
		if (selectedButton != null) selectedButton.setUnselected();
		NotificationButton button = buttons.getAt(index);
		if (button != null){
			button.setSelected();
		}
		selectedButton = button;
	}
	
	private void selectButton(NotificationButton button){
		if (selectedButton != null) selectedButton.setUnselected();
		button.setSelected();
		selectedButton = button;
	}
	
	public void setOnPagePanelSwitchListener(OnPagePanelSwitchListener l){
		this.mOnPagePanelSwitchListener = l;
	}
	
}
