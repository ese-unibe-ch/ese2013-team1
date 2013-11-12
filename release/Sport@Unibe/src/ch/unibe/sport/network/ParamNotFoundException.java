package ch.unibe.sport.network;

public class ParamNotFoundException extends Exception {
	private static final long serialVersionUID = 1686640975944120154L;
	
	public ParamNotFoundException(String param){
		super("Parameter '"+param+"' wasn't found in Message");
	}
}
