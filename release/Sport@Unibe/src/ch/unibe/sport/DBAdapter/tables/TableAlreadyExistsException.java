package ch.unibe.sport.DBAdapter.tables;

public class TableAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 5547956983222344830L;
	
	public TableAlreadyExistsException(ITable table){
		super("Table " + table.getName() +" already exists!");
	}
}
