package ch.unibe.sport.DBAdapter;

public class AlreadyRatedException extends Exception {
	private static final long serialVersionUID = 7257083892664206735L;
	
	public AlreadyRatedException(String courseHash){
		super("course: "+courseHash+" already rated");
	}
}