package ch.unibe.sport.DBAdapter.tables;

import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.DBAdapter.DBAdapter.OnOpenedListener;
import ch.unibe.sport.utils.Print;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

public class Table implements ITable, OnOpenedListener {
	public static final String TAG =  Table.class.getName();
	
	private final String tableName;
	private final String createScript;
	private final String[] tableStructure;
	protected final DBAdapter db;
	protected final Context context;
		
	protected boolean invariant(){
		return this.createScript != null
				&& this.tableName != null
				&& this.tableStructure != null
				&& this.createScript.length() > 0
				&& this.tableName.length() > 0
				&& this.tableStructure.length > 0
				&& this.db != null;
	}
	
	/*------------------------------------------------------------
	-------------------- C O N S T R U C T O R S -----------------
	------------------------------------------------------------*/
	
	protected Table(Context context,String tableName, String createScript, String[] tableStructure){
		this.tableName = tableName;
		this.createScript = createScript;
		this.tableStructure = tableStructure;
		this.context = context;
		this.db = DBAdapter.INST;
		this.db.setOnOpenedListener(this);
		assert invariant();
	}
	
	private boolean isExist(){
		db.open(context,TAG);
		Cursor cursor = db.getDB().rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + this.tableName + "'", null);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
            	cursor.close();
                db.close(TAG);
            	return true;
            }
        }
        cursor.close();
        db.close(TAG);
        return false;
	}
	
	/*------------------------------------------------------------
	------------------------- G E T T E R S ----------------------
	------------------------------------------------------------*/
	
	@Override
	public String getName(){
		assert invariant();
		return this.tableName;
	}
	
	protected DBAdapter getDB(){
		assert invariant();
		return this.db;
	}
	
	public Context getContext(){
		return this.context;
	}
	
	/*------------------------------------------------------------
	------------------------- A C T I O N S ----------------------
	------------------------------------------------------------*/
	/**
	 * 
	 */
	@Override
	public void create() throws TableAlreadyExistsException, QuerySyntaxException{
		assert invariant();
		if (isExist())	throw new TableAlreadyExistsException(this);
		db.open(context,TAG);
		db.getDB().execSQL("PRAGMA foreign_keys=ON;");
		db.getDB().execSQL(createScript);
		if (!isExist()) {
			db.close(TAG);
			throw new QuerySyntaxException(this,createScript);
		}
		db.close(TAG);
		assert invariant();
	}
	/**
	 * 
	 */
	@Override
	public void drop() throws TableNotExistsException, QuerySyntaxException{
		assert invariant();
		if (!isExist()) throw new TableNotExistsException(this);
		String query = "DROP TABLE IF EXISTS " + this.tableName;
		db.open(context,TAG);
		db.exec(query);
		if (isExist()) {
			db.close(TAG);
			throw new QuerySyntaxException(this,query);
		}
		assert invariant();
	}
	
	@Override
	public void clear() throws TableNotExistsException{
		assert invariant();
		if (!this.isExist()) throw new TableNotExistsException(this);
		String query = "DELETE FROM " + this.tableName;
		db.open(context,TAG);
		db.getDB().execSQL(query);
		db.close(TAG);
		assert invariant();
	}
	/*------------------------------------------------------------
	---------------------- I N S E R T I O N S -------------------
	------------------------------------------------------------*/
	/**
	 * 
	 */
	@Override
	public void insert() throws TableNotExistsException{};
	
	/**
	 * Simple, safe but slow SQLite insertion
	 */
	@Override
	public long insert(int[] paramIDs, String[] paramValues){
		assert invariant();
		assert paramIDs != null;
		assert paramIDs.length > 0;
		assert paramValues != null;
		assert paramValues.length > 0;
		assert paramIDs.length == paramValues.length;
		ContentValues contentValues = new ContentValues();
		for (int i = 0; i < paramIDs.length; i++){
			assert paramIDs[i] < this.tableStructure.length;
			assert paramIDs[i] >= 0;
			contentValues.put(this.tableStructure[paramIDs[i]], paramValues[i]);
		}
		assert contentValues.size() > 0;
		db.open(context,TAG);
		long index = db.getDB().insert(this.tableName, null,contentValues);
		db.close(TAG);
		return index;
	}
	
	/**
	 * The fastes possible Android SQLite insertion method for big amount of data.
	 * @return 
	 */
	@Override
	public long[] bulkInsert(int[] paramIDs, Object[][] paramValues, String dataType) throws TableNotExistsException{
		assert invariant();
		assert paramIDs != null;
		assert paramIDs.length > 0;
		assert paramValues != null;
		assert paramValues.length > 0;
		assert paramIDs.length == paramValues[0].length;
		assert dataType.equals("int") || dataType.equals("String");
		int type = dataType.equals("int") ? 0 : 1;
		int resultLength = paramValues.length;
		long[] resultIDs = new long[resultLength];
		
		if (!this.isExist()) throw new TableNotExistsException(this);
		db.open(context,TAG);
		db.getDB().beginTransaction();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ");
			sql.append(this.tableName);
			sql.append(" (");
			StringBuilder names = new StringBuilder();
			StringBuilder params = new StringBuilder();
			for (int i = 0; i < paramIDs.length; i++){
				if (i != 0) {
					names.append(',');
					params.append(',');
				}
				names.append(this.tableStructure[paramIDs[i]]);
				params.append('?');
			}
			sql.append(names);
			sql.append(") VALUES(");
			sql.append(params);
			sql.append(')');
			SQLiteStatement statement = db.getDB().compileStatement(sql.toString());
			for (int i = 0; i < paramValues.length; i++){
				for (int j = 1; j <= paramIDs.length; j++){
					if (type == 0) statement.bindLong(j, ((Integer)paramValues[i][j-1]).longValue());
					else statement.bindString(j, (String)paramValues[i][j-1]);
				}
				resultIDs[i] = statement.executeInsert();
			}
			db.getDB().setTransactionSuccessful();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			db.getDB().endTransaction();
		}
		db.close(TAG);
		return resultIDs;
	}
	

	@Override
	public void updateByID(int keyID, Object keyValue, int[] paramIDs, String[] paramValues) {
		assert paramIDs.length > 0;
		assert paramValues.length > 0;
		assert paramIDs.length == paramValues.length;
		db.open(context,TAG);
		ContentValues args = new ContentValues();
		for (int i = 0; i < paramIDs.length; i++){
			args.put(tableStructure[paramIDs[i]], paramValues[i]);
		}
		db.getDB().update(tableName, args, tableStructure[keyID]+" = ?", new String[]{""+keyValue});
		db.close(TAG);
	}
	
	@Override
	public void removeByID(int keyID, Object keyValue) {
		db.open(context,TAG);
		db.getDB().delete(tableName, tableStructure[keyID]+" = ?", new String[]{""+keyValue});
		db.close(TAG);
	}
	
	public void removeByID(int keyID, int[] keyValues){
		assert keyID >= 0;
		assert keyValues != null;
		if (keyValues.length == 0) return;
		db.open(context,TAG);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("DELETE FROM ");
		strBuilder.append(tableName);
		strBuilder.append(" WHERE ");
		strBuilder.append(tableStructure[keyID]);
		strBuilder.append(" IN (");
		strBuilder.append(Print.toString(keyValues, ','));
		strBuilder.append(")");
		db.getDB().execSQL(strBuilder.toString());
		db.close(TAG);
	}
	/*------------------------------------------------------------
	---------------------- S E L E C T I O N S -------------------
	------------------------------------------------------------*/
	// code sqlite selections here
	/*------------------------------------------------------------
	----------------------- P R O T E C T E D --------------------
	------------------------------------------------------------*/
	/**
	 * 
	 * @param cursor
	 * @return
	 */
	public static String[][] getResultString(Cursor cursor) {
		int rowNum = cursor.getCount();
		int colsNum = cursor.getColumnCount();
		if (rowNum < 1) {
			cursor.close();
			return new String[0][0];
		}
		String[][] result = new String[rowNum][colsNum];
		if (cursor.moveToFirst()) {
			int row = 0;
            do {
            	for (int col = 0; col < colsNum; col++){
            		result[row][col] = cursor.getString(col);
            	}
                row++;
            } while (cursor.moveToNext());
        }
		cursor.close();
		return result;
	}
	/**
	 * 
	 * @param cursor
	 * @return
	 */
	public static int[][] getResultInt(Cursor cursor) {
		int rowNum = cursor.getCount();
		int colsNum = cursor.getColumnCount();
		if (rowNum < 1) {
			cursor.close();
			return new int[0][0];
		}
		int[][] result = new int[rowNum][colsNum];
		if (cursor.moveToFirst()) {
			int row = 0;
            do {
            	for (int col = 0; col < colsNum; col++){
            		result[row][col] = cursor.getInt(col);
            	}
                row++;
            } while (cursor.moveToNext());
        }
		cursor.close();
		return result;
	}

	@Override
	public void onOpened() {}

	@Override
	public void onClosed() {}
}
