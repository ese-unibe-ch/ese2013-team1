package ch.unibe.sport.DBAdapter.tables;

public class TableNotExistsException extends Exception {
	private static final long serialVersionUID = 6226514530917600809L;
	
	public TableNotExistsException(ITable table){
		super("Table "+table.getName()+" doesn't exist!");
	}

}
