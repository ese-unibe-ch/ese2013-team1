package ch.unibe.sport.network;

import ch.unibe.sport.core.User;
import ch.unibe.sport.utils.Date;

public class MessageAdapter {
	
	private Message message;
	
	public MessageAdapter(Message message){
		this.message = message;
	}
	
	public boolean isSentAddFriend(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.START_SENT_ADD_FRIEND)) return false;
		return true;
	}
	
	public boolean isContinueLoading(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.CONTINUE_LOADING)) return false;
		return true;
	}
	
	public boolean isCourseUpdate(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.COURSE_UPDATE)) return false;
		return true;
	}
	
	public boolean isCourseFavoriteUpdate(){
		if (!isCourseUpdate()) return false;
		if (!this.message.containsKey(MessageBuilder.COURSE_UPDATE_PARAM)) return false;
		if (!this.message.get(MessageBuilder.COURSE_UPDATE_PARAM).equals(MessageBuilder.COURSE_FAVORITE)) return false;
		return true;
	}
	
	public boolean isCourseAttendedUpdate(){
		if (!isCourseUpdate()) return false;
		if (!this.message.containsKey(MessageBuilder.COURSE_UPDATE_PARAM)) return false;
		if (!this.message.get(MessageBuilder.COURSE_UPDATE_PARAM).equals(MessageBuilder.COURSE_ATTENDED)) return false;
		return true;
	}
	
	public boolean isCourseRatedUpdate(){
		if (!isCourseUpdate()) return false;
		if (!this.message.containsKey(MessageBuilder.COURSE_UPDATE_PARAM)) return false;
		if (!this.message.get(MessageBuilder.COURSE_UPDATE_PARAM).equals(MessageBuilder.COURSE_RATED)) return false;
		return true;
	}
	
	public boolean isCourseBGColorUpdate(){
		if (!isCourseUpdate()) return false;
		if (!this.message.containsKey(MessageBuilder.COURSE_UPDATE_PARAM)) return false;
		if (!this.message.get(MessageBuilder.COURSE_UPDATE_PARAM).equals(MessageBuilder.COURSE_BG_COLOR)) return false;
		return true;
	}
	
	public boolean getCourseFavoriteUpdate() throws ParamNotFoundException {
		String param = MessageBuilder.COURSE_FAVORITE;
		if (!isCourseFavoriteUpdate()) throw new ParamNotFoundException(param);
		return (Boolean)this.message.get(param);
	}
	
	public boolean getCourseAttendedUpdate() throws ParamNotFoundException {
		String param = MessageBuilder.COURSE_ATTENDED;
		if (!isCourseAttendedUpdate()) throw new ParamNotFoundException(param);
		return (Boolean)this.message.get(param);
	}
	
	public User getUser() throws ParamNotFoundException {
		String param = MessageBuilder.USER;
		if (!isSentAddFriend()) throw new ParamNotFoundException(param);
		return (User)this.message.get(param);
	}
	
	public int getCourseRatedUpdate() throws ParamNotFoundException {
		String param = MessageBuilder.COURSE_RATED;
		if (!isCourseRatedUpdate()) throw new ParamNotFoundException(param);
		return (Integer)this.message.get(param);
	}
	
	public int getCourseID() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.COURSE_ID)) throw new ParamNotFoundException(MessageBuilder.COURSE_ID);
		return (Integer)this.message.get(MessageBuilder.COURSE_ID);
	}
	
	public boolean isStartActivity(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.START_ACTIVITY)) return false;
		return true;
	}
	
	public boolean isFinishActivity(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.FINISH_ACTIVITY)) return false;
		return true;
	}
	
	public String getActivityTag() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.ACTIVITY_TAG)) throw new ParamNotFoundException(MessageBuilder.ACTIVITY_TAG);
		return (String) this.message.get(MessageBuilder.ACTIVITY_TAG);
	}
	
	public boolean isMainActivitySwitchTab(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.MAIN_ACTIVITY_SWITCH_TAB)) return false;
		return true;
	}
	
	public String getMainActivityTabTag() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.MAIN_ACTIVITY_TAB_TAG)) throw new ParamNotFoundException(MessageBuilder.MAIN_ACTIVITY_TAB_TAG);
		return (String) this.message.get(MessageBuilder.MAIN_ACTIVITY_TAB_TAG);
	}
	
	public boolean getMainActivityTabSwitchSmooth() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.MAIN_ACTIVITY_SWITCH_TAB_SMOOTH)) throw new ParamNotFoundException(MessageBuilder.MAIN_ACTIVITY_SWITCH_TAB_SMOOTH);
		return (Boolean) this.message.get(MessageBuilder.MAIN_ACTIVITY_SWITCH_TAB_SMOOTH);
	}
	
	public boolean isSwitchFragment(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.FRAGMENT_SWITCH)) return false;
		return true;
	}
	
	public String getFragmentTag() throws ParamNotFoundException{
		String param = MessageBuilder.FRAGMENT_TAG;
		if (!this.message.containsKey(param)) throw new ParamNotFoundException(param);
		return (String) this.message.get(param);
	}
	
	public boolean isBackButtonEvent(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.BACK_BUTTON_EVENT)) return false;
		return true;
	}
	
	public String getClassName() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.CLASS_NAME)) throw new ParamNotFoundException(MessageBuilder.CLASS_NAME);
		return (String) this.message.get(MessageBuilder.CLASS_NAME);
	}
	
	public Date getDate() throws ParamNotFoundException{
		if (!this.message.containsKey(MessageBuilder.DATE)) throw new ParamNotFoundException(MessageBuilder.DATE);
		return new Date((Integer) this.message.get(MessageBuilder.DATE));
	}
	
	public boolean isSlideMenuItemActiveSwitch(){
		if (!this.message.containsKey(Message.ACTION)) return false;
		if (!this.message.get(Message.ACTION).equals(MessageBuilder.SLIDE_MENU_ITEM_ACTIVE_SWITCH)) return false;
		return true;
	}
	
	public String getSlideMenuItemTag() throws ParamNotFoundException{
		String param = MessageBuilder.SLIDE_MENU_ITEM_TAG;
		if (!this.message.containsKey(param)) throw new ParamNotFoundException(param);
		return (String) this.message.get(param);
	}
	
	public int getCourseBGColor() throws ParamNotFoundException{
		String param = MessageBuilder.COURSE_BG_COLOR;
		if (!this.message.containsKey(param)) throw new ParamNotFoundException(param);
		return (Integer) this.message.get(param);
	}
}
