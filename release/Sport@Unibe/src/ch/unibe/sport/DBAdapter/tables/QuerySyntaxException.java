package ch.unibe.sport.DBAdapter.tables;

public class QuerySyntaxException extends Exception {
	private static final long serialVersionUID = -3108804981145953767L;
	
	public QuerySyntaxException(ITable table,String query){
		super("Error compiling query '" + query +"' in table " + table.getName() + ". Check your syntax.");
	}
}
