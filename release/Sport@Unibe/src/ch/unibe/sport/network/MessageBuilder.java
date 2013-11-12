package ch.unibe.sport.network;

import ch.unibe.sport.main.MainActivity;
import ch.unibe.sport.slidingmenu.SlidingMenuFragment;
import ch.unibe.sport.utils.Date;

public class MessageBuilder {
	
	public static final String COURSE_UPDATE = "course_update";
	public static final String COURSE_UPDATE_PARAM = "course_update_param";
	public static final String COURSE_FAVORITE = "favorite";
	public static final String COURSE_ATTENDED = "attended";
	public static final String COURSE_RATED = "rated";
	public static final String COURSE_BG_COLOR = "bg_color";
	public static final String COURSE_ID = "courseID";
	
	public static final String START_ACTIVITY = "start_activity";
	public static final String ACTIVITY_TAG = "start_activity_tag";
	
	public static final String MAIN_ACTIVITY_SWITCH_TAB = "main_activity_switch_tab";
	public static final String MAIN_ACTIVITY_TAB_TAG = "main_activity_tag";
	public static final String MAIN_ACTIVITY_SWITCH_TAB_SMOOTH = "main_activity_switch_tab_smoth";
	
	public static final String FRAGMENT_SWITCH = "fragment_switch";
	public static final String FRAGMENT_TAG = "switch_fragment_tag";
	
	public static final String BACK_BUTTON_EVENT = "back_button_event";
	
	public static final String CLASS_NAME = "class";
	
	public static final String DATE = "date";
	
	public static final String SLIDE_MENU_ITEM_ACTIVE_SWITCH = "slide_menu_item_active_switch";
	public static final String SLIDE_MENU_ITEM_TAG = "slide_menu_item_tag";
	
	private Message message;
	
	public MessageBuilder(String sender){
		this.message = new Message(sender);
	}
	
	public MessageBuilder startCourseUpdate(){
		this.message.put(Message.ACTION, COURSE_UPDATE);
		return this;
	}
	
	public MessageBuilder startActivityShow(){
		this.message.put(Message.ACTION, START_ACTIVITY);
		return this;
	}
	
	public MessageBuilder putFavorite(boolean isFavorite){
		this.message.put(COURSE_UPDATE_PARAM, COURSE_FAVORITE);
		this.message.put(COURSE_FAVORITE, isFavorite);
		return this;
	}
	
	public MessageBuilder putAttended(boolean isAttanded){
		this.message.put(COURSE_UPDATE_PARAM, COURSE_ATTENDED);
		this.message.put(COURSE_ATTENDED, isAttanded);
		return this;
	}
	
	public MessageBuilder putRating(int rating){
		this.message.put(COURSE_UPDATE_PARAM, COURSE_RATED);
		this.message.put(COURSE_RATED, rating);
		return this;
	}
	
	public MessageBuilder putCourseID(int courseID){
		this.message.put(COURSE_ID, courseID);
		return this;
	}
	
	public MessageBuilder putBGColor(int color){
		this.message.put(COURSE_UPDATE_PARAM, COURSE_BG_COLOR);
		this.message.put(COURSE_BG_COLOR, color);
		return this;
	}
	
	public MessageBuilder addReceiver(String receiver){
		this.message.addReceiver(receiver);
		return this;
	}
	
	public MessageBuilder addReceivers(String[] receivers){
		for (int i = 0, length = receivers.length; i < length; i++){
			this.message.addReceiver(receivers[i]);
		}
		return this;
	}
	
	public MessageBuilder putActivityTag(String tag){
		this.message.put(ACTIVITY_TAG, tag);
		return this;
	}
	
	public MessageBuilder startMainActivitySwitchTab(){
		this.message.put(Message.ACTION, MAIN_ACTIVITY_SWITCH_TAB);
		addReceiver(MainActivity.TAG);
		return this;
	}
	
	public MessageBuilder putMainActivityTabTag(String tag){
		this.message.put(MAIN_ACTIVITY_TAB_TAG, tag);
		return this;
	}
	
	public MessageBuilder putMainActivityTabSwitchSmooth(boolean smooth){
		this.message.put(MAIN_ACTIVITY_SWITCH_TAB_SMOOTH, smooth);
		return this;
	}
	
	public MessageBuilder startFragmentSwitch(){
		this.message.put(Message.ACTION, FRAGMENT_SWITCH);
		return this;
	}
	
	public MessageBuilder startSlideMenuItemActiveSwitch(){
		this.message.put(Message.ACTION, SLIDE_MENU_ITEM_ACTIVE_SWITCH);
		addReceiver(SlidingMenuFragment.TAG);
		return this;
	}
	
	public MessageBuilder putSlideMenuItemTag(String tag){
		this.message.put(SLIDE_MENU_ITEM_TAG, tag);
		return this;
	}
	
	public MessageBuilder putFragmentTag(String tag){
		this.message.put(FRAGMENT_TAG, tag);
		return this;
	}
	
	public MessageBuilder startBackButtonEvent(){
		this.message.put(Message.ACTION, BACK_BUTTON_EVENT);
		return this;
	}
	
	public MessageBuilder putClassName(String cls){
		this.message.put(CLASS_NAME, cls);
		return this;
	}
	
	public MessageBuilder putDate(Date date) {
		this.message.put(DATE, date.toInt());
		return this;
	}
	
	public Message getMessage(){
		return this.message;
	}
	
}
