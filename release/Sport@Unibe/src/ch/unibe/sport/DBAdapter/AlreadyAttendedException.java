package ch.unibe.sport.DBAdapter;

public class AlreadyAttendedException extends Exception {
	private static final long serialVersionUID = 7257083892664206735L;
	
	public AlreadyAttendedException(String courseHash){
		super("course: "+courseHash+" already attended");
	}
}