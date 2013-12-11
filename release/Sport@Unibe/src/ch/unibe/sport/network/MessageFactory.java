package ch.unibe.sport.network;

import java.util.LinkedList;

import ch.unibe.sport.core.User;
import ch.unibe.sport.event.info.EventAttendDialog;
import ch.unibe.sport.event.info.EventChangeColorDialog;
import ch.unibe.sport.event.info.EventInfoActivity;
import ch.unibe.sport.event.info.EventRatingDialog;
import ch.unibe.sport.event.info.ItemMenu;
import ch.unibe.sport.info.SportInfoController;
import ch.unibe.sport.main.favorites.FavoritesListView;
import ch.unibe.sport.main.friends.FriendInfoActivity;
import ch.unibe.sport.main.friends.FriendsListView;
import ch.unibe.sport.main.search.AdvancedSearchResultFragment;

public class MessageFactory {
	
	public static Message sentAddFriend(String tag, User user){
		MessageBuilder msgBuilder = new MessageBuilder(tag);
		msgBuilder.startSentAddFriend().putUser(user);
		
		msgBuilder.addReceiver(FriendsListView.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateCourse(String tag,int courseID){
		MessageBuilder msgBuilder = new MessageBuilder(tag);
		msgBuilder.startCourseUpdate().putCourseID(courseID);
		
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(EventInfoActivity.TAG);
		msgBuilder.addReceiver(FriendInfoActivity.TAG);
		
		return msgBuilder.getMessage();
	}
	
	public static Message continueLoading(String tag,String receiver){
		MessageBuilder msgBuilder = new MessageBuilder(tag);
		msgBuilder.startContinueLoading();
		msgBuilder.addReceiver(receiver);
		return msgBuilder.getMessage();
	}
	
	public static Message finishActivities(String tag, LinkedList<String> activities){
		MessageBuilder msgBuilder = new MessageBuilder(tag);
		msgBuilder.startActivityFinish();
		for (String activity : activities){
			msgBuilder.addReceiver(activity);
		}
		return msgBuilder.getMessage();
	}
	
	public static Message updateFavoriteFromItemMenu(int courseID, boolean favorite){
		MessageBuilder msgBuilder = new MessageBuilder(ItemMenu.TAG);
		msgBuilder.startCourseUpdate().putFavorite(favorite).putCourseID(courseID);
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		msgBuilder.addReceiver(FriendInfoActivity.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(EventInfoActivity.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateFavoriteFromCourseInfoActivity(int courseID, boolean favorite){
		MessageBuilder msgBuilder = new MessageBuilder(EventInfoActivity.TAG);
		msgBuilder.startCourseUpdate().putFavorite(favorite).putCourseID(courseID);
		msgBuilder.addReceiver(SportInfoController.TAG);

		msgBuilder.addReceiver(FriendInfoActivity.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		return msgBuilder.getMessage(); 
	}
	
	public static Message updateAttendedFromAttendDialog(int courseID, boolean attended){
		MessageBuilder msgBuilder = new MessageBuilder(EventAttendDialog.TAG);
		msgBuilder.startCourseUpdate().putAttended(attended).putCourseID(courseID);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateRatedFromRatingDialog(int courseID, int rating){
		MessageBuilder msgBuilder = new MessageBuilder(EventRatingDialog.TAG);
		msgBuilder.startCourseUpdate().putRating(rating).putCourseID(courseID);
		msgBuilder.addReceiver(EventInfoActivity.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateBGColorFromChangeColorDialog(int courseID, int color){
		MessageBuilder msgBuilder = new MessageBuilder(EventChangeColorDialog.TAG);
		msgBuilder.startCourseUpdate().putCourseID(courseID).putBGColor(color);
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		return msgBuilder.getMessage();
	}
}
