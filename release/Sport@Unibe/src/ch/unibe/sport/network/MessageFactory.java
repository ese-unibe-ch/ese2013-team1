package ch.unibe.sport.network;

import ch.unibe.sport.course.info.CourseAttendDialog;
import ch.unibe.sport.course.info.CourseChangeColorDialog;
import ch.unibe.sport.course.info.CourseInfoActivity;
import ch.unibe.sport.course.info.CourseRatingDialog;
import ch.unibe.sport.course.info.ItemMenu;
import ch.unibe.sport.favorites.FavoritesListView;
import ch.unibe.sport.info.SportInfoController;
import ch.unibe.sport.main.search.AdvancedSearchResultFragment;

public class MessageFactory {
	
	public static Message updateCourse(String tag,int courseID){
		MessageBuilder msgBuilder = new MessageBuilder(tag);
		msgBuilder.startCourseUpdate().putCourseID(courseID);
		
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(CourseInfoActivity.TAG);
		
		return msgBuilder.getMessage();
	}
	
	public static Message updateFavoriteFromItemMenu(int courseID, boolean favorite){
		MessageBuilder msgBuilder = new MessageBuilder(ItemMenu.TAG);
		msgBuilder.startCourseUpdate().putFavorite(favorite).putCourseID(courseID);
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(CourseInfoActivity.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateFavoriteFromCourseInfoActivity(int courseID, boolean favorite){
		MessageBuilder msgBuilder = new MessageBuilder(CourseInfoActivity.TAG);
		msgBuilder.startCourseUpdate().putFavorite(favorite).putCourseID(courseID);
		msgBuilder.addReceiver(SportInfoController.TAG);
		
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		return msgBuilder.getMessage(); 
	}
	
	public static Message updateAttendedFromAttendDialog(int courseID, boolean attended){
		MessageBuilder msgBuilder = new MessageBuilder(CourseAttendDialog.TAG);
		msgBuilder.startCourseUpdate().putAttended(attended).putCourseID(courseID);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateRatedFromRatingDialog(int courseID, int rating){
		MessageBuilder msgBuilder = new MessageBuilder(CourseRatingDialog.TAG);
		msgBuilder.startCourseUpdate().putRating(rating).putCourseID(courseID);
		msgBuilder.addReceiver(CourseInfoActivity.TAG);
		return msgBuilder.getMessage();
	}
	
	public static Message updateBGColorFromChangeColorDialog(int courseID, int color){
		MessageBuilder msgBuilder = new MessageBuilder(CourseChangeColorDialog.TAG);
		msgBuilder.startCourseUpdate().putCourseID(courseID).putBGColor(color);
		msgBuilder.addReceiver(FavoritesListView.TAG);
		msgBuilder.addReceiver(AdvancedSearchResultFragment.TAG);
		msgBuilder.addReceiver(SportInfoController.TAG);
		return msgBuilder.getMessage();
	}
}
