package ch.unibe.sport.DBAdapter;

import java.util.ArrayList;

import android.content.Context;
import ch.unibe.sport.DBAdapter.tables.ITable;
import ch.unibe.sport.DBAdapter.tables.QuerySyntaxException;
import ch.unibe.sport.DBAdapter.tables.TableAlreadyExistsException;
import ch.unibe.sport.DBAdapter.tables.TableNotExistsException;
import ch.unibe.sport.utils.Print;

/**
 * 
 * @author Team 1
 */
public class DBUpdate{
	public static final String TAG = DBUpdate.class.getName();
	private DBStructure structure;
	
	private boolean invariant(){
		return this.structure != null;
	}
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	public DBUpdate(Context context){
		structure = new DBStructure(context);
	}
	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	/**
	 * Performs database full format
	 * @return
	 * @throws TableNotExistsException
	 * @throws TableAlreadyExistsException
	 * @throws QuerySyntaxException
	 */
	public boolean dbFullFormat() throws TableNotExistsException, TableAlreadyExistsException, QuerySyntaxException{
		Print.log(TAG,"Starting full format...");
		Print.log(TAG,"Beginning deleting all tables...");
		dbTablesDrop();
		Print.log(TAG," + All Tables deleted!");
		Print.log(TAG,"Beginning creating all tables...");
		dbTablesCreate();
		Print.log(TAG," + All Tables created!");
		Print.log(TAG,"Beginning inserting all data in tables...");
		dbTablesInsert();
		Print.log(TAG," + All data inserted!");
		Print.log(TAG," * Full format completed!");
		return true;
	}

	/**
	 * Creates all defined tables from dbStructure class.</br>
	 * @throws TableAlreadyExistsException 
	 * @throws QuerySyntaxException 
	 */
	public void dbTablesCreate() throws TableAlreadyExistsException, QuerySyntaxException{
		assert invariant();
		ArrayList<ITable> tables = structure.getTables();
		for (ITable table : tables){
			table.create();
		}
		assert invariant();
	}
	
    /**
     * Inserts default data in all defined tables.</br>
     * <b><i>Uses fast 'bulk' insert method.</i></b>
     * @return {@code True} if operation was successful, otherwise {@code False}
     * @throws TableNotExistsException 
     */
	public void dbTablesInsert() throws TableNotExistsException{
		assert invariant();
		ArrayList<ITable> tables = structure.getTables();
		for (ITable table : tables){
			table.insert();
		}
		assert invariant();
	}
	
    /**
     * Deletes all tables in database, according to dbStructure class.</br>
     * @throws TableNotExistsException 
     * @throws QuerySyntaxException 
     */
    public void dbTablesDrop() throws TableNotExistsException, QuerySyntaxException{
    	assert invariant();
		ArrayList<ITable> tables = structure.getTables();
		for (ITable table : tables){
			table.drop();
		}
		assert invariant();
    }
}
