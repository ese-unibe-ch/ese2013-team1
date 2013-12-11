package ch.unibe.sport.utils.bulker;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteStatement;
import ch.unibe.sport.DBAdapter.DBAdapter;
import ch.unibe.sport.utils.Print;

/**
 * Powered by finely trained slowpokes and some street magic.
 * This is a good example of bad code-style without any OOP patterns and principles.
 * Is wrote using Chack Norris programming methods.
 * If you don't like this code, just think that it is obfuscated for some great
 * and important purposes.
 * @author Team 1 2013
 */
public class Bulker {
	public static final String TAG = Bulker.class.getName();
	
	public static final <E> void insert(Context context,E obj) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, ClassCastException{
		BulkInsert bulkInsert = new BulkInsert();
		buildTables(bulkInsert,obj);
		bulkInsertInDB(context,bulkInsert);
	}
	
	//TODO to be implemented in future
	/*public static final <E> E load(Context context, Class<E> clazz,Object... keyValues) throws InstantiationException, IllegalAccessException{
		if (clazz.isArray()){
			Print.err(TAG,"Class can't be an array, but was: "+clazz);
			return null;
		}
		
		E instance = clazz.newInstance();
		
		String tableName = null;
		BulkTable clazzTable  = clazz.getAnnotation(BulkTable.class);
		if (clazzTable != null){
			tableName = clazzTable.value();
		}
		
		Field[] fields = clazz.getDeclaredFields();
		int keyCounter = 0;
		for (Field field : fields){
			field.setAccessible(true);
			if (field.getAnnotation(BulkKey.class) != null){
				field.set(instance, keyValues[keyCounter]);
				keyCounter++;
			}
		}
		
		ArrayList<>
		
		for (Field field : fields){
			if (field.getAnnotation(BulkParam.class) != null){
				insertParamField(bulkInsert, tableName,field.get(obj),field.getType(),field.getName());
			}
			
			else if (field.getAnnotation(BulkArray.class) != null){
				insertParamArray(bulkInsert, tableName,field.get(obj),field.getType(),field.getName());
			}
			
			else if (field.getAnnotation(BulkRelation.class) != null){
				insertRelation(bulkInsert, field.getAnnotation(BulkRelation.class).value(), obj,
						field.get(obj), field.getType(), field.getName());
			}
		}
		
		return null;
	}*/
	
	private static final void bulkInsertInDB(Context context, BulkInsert bulkInsert){
		
		DBAdapter.INST.open(context,TAG);
		DBAdapter.INST.getDB().beginTransaction();

		String[] tables = bulkInsert.getTables();
		try {
			for (String table : tables){
				bulkInsertTableInDB(bulkInsert.getTable(table));
			}
			DBAdapter.INST.getDB().setTransactionSuccessful();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			DBAdapter.INST.getDB().endTransaction();
		}
		DBAdapter.INST.close(TAG);
		
	}
	
	private static final void bulkInsertTableInDB(BulkInsertTable table){
		if (table == null) {
			Print.err("table is null in bulkInsertTableInDB");
			return;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(table.getTableName());
		sql.append(" (");
		StringBuilder names = new StringBuilder();
		StringBuilder params = new StringBuilder();
		
		
		Iterator<String> keyIterator = table.getInsertMap().keySet().iterator();
		String[] columns = new String[table.getColumnSize()];
		int i = 0;
		while (keyIterator.hasNext()){
			if (i != 0) {
				names.append(',');
				params.append(',');
			}
			columns[i] = keyIterator.next();
			names.append(columns[i]);
			params.append('?');
			i++;
		}
		
		if (columns.length == 0) {
			Print.err("there are no columns");
			return;
		}
		
		int length = table.getInsertMap().get(columns[0]).size();
		for (String column : columns){
			if (table.getInsertMap().get(column).size() != length){
				Print.err("columns length aren't the same");
				return;
			}
		}
		
		sql.append(names);
		sql.append(") VALUES(");
		sql.append(params);
		sql.append(')');
		
		SQLiteStatement statement = DBAdapter.INST.getDB().compileStatement(sql.toString());
		bindBulkValues(table,columns,statement);
		
	}
	
	private static final void bindBulkValues(BulkInsertTable table,String[] columns,SQLiteStatement statement){
		if (columns.length == 0) {
			Print.err("there are no columns");
			return;
		}
		
		int length = table.getInsertMap().get(columns[0]).size();
		Print.log(table.getTableName()+" length = "+length);
		HashMap<String, ArrayList<Object>> insertMap = table.getInsertMap();
		@SuppressWarnings("unchecked")
		Class<? extends Object>[] columnTypes = new Class[columns.length];
		
		int i = 0;
		for (String column : columns){
			columnTypes[i] = table.getTypesMap().get(column);
			i++;
		}
		
		
		Object value;
		for (i = 0; i < length; i++){
			for (int j = 1; j <= columns.length; j++){
				value = insertMap.get(columns[j-1]).get(i);
				if (value == null){
					statement.bindNull(j);
				}
				else if (columnTypes[j-1].equals(Long.class)){
					statement.bindLong(j, (Long)value);
				}
				else if (columnTypes[j-1].equals(Double.class)){
					statement.bindDouble(j, (Double)value);
				}
				else if (columnTypes[j-1].equals(String.class)){
					statement.bindString(j, (String)value);
				}
				else {
					statement.bindNull(j);
				}
			}
			try {
				statement.executeInsert();
			} catch (SQLiteConstraintException e){
				String row = "";
				for (int j = 1; j <= columns.length; j++){
					if (j != 1) row += ", ";
					row += insertMap.get(columns[j-1]).get(i);
				}
				//Print.err("Not unique row at "+table.getTableName()+": "+row);
			}
		}
	}
	
	private static final <E> void buildTables(BulkInsert bulkInsert, E obj) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, ClassCastException{
		Class<?> clazz = obj.getClass();
		if (clazz.isArray()){
			for (int i = 0, length = Array.getLength(obj); i < length; i++){
				buildTables(bulkInsert, Array.get(obj, i));
			}
			return;
		}
		

		String tableName = null;
		BulkTable clazzTable  = clazz.getAnnotation(BulkTable.class);
		if (clazzTable != null){
			tableName = clazzTable.value();
		}
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields){
			/* to access private fields */
			field.setAccessible(true);
			
			if (field.getAnnotation(BulkKey.class) != null){
				insertParamKey(bulkInsert, tableName,field.get(obj),field.getType(),field.getName());
			}
			
			else if (field.getAnnotation(BulkParam.class) != null){
				insertParamField(bulkInsert, tableName,field.get(obj),field.getType(),field.getName());
			}
			
			else if (field.getAnnotation(BulkArray.class) != null){
				insertParamArray(bulkInsert, tableName,field.get(obj),field.getType(),field.getName());
			}
			
			else if (field.getAnnotation(BulkRelation.class) != null){
				insertRelation(bulkInsert, field.getAnnotation(BulkRelation.class).value(), obj,
						field.get(obj), field.getType(), field.getName());
			}
		}
	}
	
	private static final <E> void insertRelation(BulkInsert bulkInsert, String relationTable, Object parentObject,
			Object fieldParam, Class<? extends Object> fieldClazz, String fieldName)
					throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, ClassCastException{
		
		Class<? extends Object> parentClazz = parentObject.getClass();
		BulkTable fieldClazzTable = null;
		if (fieldClazz.isArray()){
			fieldClazzTable = fieldClazz.getComponentType().getAnnotation(BulkTable.class);
		}
		else {
			fieldClazzTable = fieldClazz.getAnnotation(BulkTable.class);
		}
		ArrayList<Field> parentKeys = getKeyFields(parentClazz);
		if (fieldClazzTable != null){
			buildTables(bulkInsert,fieldParam);
			
			ArrayList<Field> fieldKeys = null;
			if (fieldClazz.isArray()){
				fieldKeys = getKeyFields(fieldParam.getClass().getComponentType());
				for (int i = 0, length = Array.getLength(fieldParam); i < length; i++){
					for (Field parentKey : parentKeys){
						bulkInsert.add(relationTable, parentKey.getName(), bulkCast(parentKey.get(parentObject)));
					}
					for (Field fieldKey : fieldKeys){
						bulkInsert.add(relationTable, fieldKey.getName(), bulkCast(fieldKey.get(Array.get(fieldParam,i))));
					}
				}
			}
			else {
				fieldKeys = getKeyFields(fieldParam.getClass());
				for (Field parentKey : parentKeys){
					bulkInsert.add(relationTable, parentKey.getName(), bulkCast(parentKey.get(parentObject)));
				}
				for (Field fieldKey : fieldKeys){
					bulkInsert.add(relationTable, fieldKey.getName(), bulkCast(fieldKey.get(fieldParam)));
				}
			}
		}
		else {
			if (fieldClazz.isArray()){
				insertParamArray(bulkInsert, relationTable,fieldParam,fieldClazz,fieldName);
				
				for (int i = 0, length = Array.getLength(fieldParam); i < length; i++){
					for (Field parentKey : parentKeys){
						bulkInsert.add(relationTable, parentKey.getName(), bulkCast(parentKey.get(parentObject)));
					}
				}
			}
			else {
				insertParamField(bulkInsert, relationTable, fieldParam, fieldClazz, fieldName);
				for (Field parentKey : parentKeys){
					bulkInsert.add(relationTable, parentKey.getName(), bulkCast(parentKey.get(parentObject)));
				}
			}
			return;
		}		
	}
	
	private static final <E> void insertParamArray(BulkInsert bulkInsert, 
			String table, Object fieldParam, Class<? extends Object> fieldClazz, String fieldName) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, ClassCastException{
		if (!fieldClazz.isArray()) {
			Print.err("@BulkArray must be an array");
			return;
		}
		Class<? extends Object> elementClazz = fieldClazz.getComponentType();
		if (Caster.PRIMITIVES.containsKey(elementClazz.getName())){
			for (int i = 0, length = Array.getLength(fieldParam); i < length; i++){
				bulkInsert.add(table, fieldName, bulkCast(Array.get(fieldParam, i)));
			}
			return;
		}
		if (fieldParam == null){
			Print.err("Field param int table " + table+ " is null as class "+fieldClazz.getName());
			return;
		}
		
		for (int i = 0, length = Array.getLength(fieldParam); i < length; i++){
			buildTables(bulkInsert,Array.get(fieldParam, i));
		}
		
	}
	
	private static final <E> void insertParamKey(BulkInsert bulkInsert, 
			String table, Object fieldParam, Class<? extends Object> fieldClazz, String fieldName) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, ClassCastException{
		if (table == null){
			if (Caster.PRIMITIVES.containsKey(fieldClazz.getName())){
				Print.err("Field type in "+fieldName+" is primitive "+fieldClazz.getName()+" while parentTable is null");
				return;
			}
			buildTables(bulkInsert,fieldParam);
			return;
		}
		if (fieldClazz.isArray()) {
			Print.err("@BulkKey field can't be an array");
			return;
		}
		if (fieldParam == null){
			if (Caster.PRIMITIVES.containsKey(fieldClazz.getName())){
				bulkInsert.add(table, fieldName, null);
			}
			else {
				Print.err("Key field "+fieldName+" not a primitive, but "+fieldClazz);
			}
			return;
		}
		if (Caster.PRIMITIVES.containsKey(fieldClazz.getName())){
			try {
				bulkInsert.add(table, fieldName, bulkCast(fieldParam));
				return;
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final <E> void insertParamField(BulkInsert bulkInsert, 
			String table, Object fieldParam, Class<? extends Object> fieldClazz, String fieldName) throws IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, ClassCastException{
		if (table == null){
			if (Caster.PRIMITIVES.containsKey(fieldClazz.getName())){
				Print.err("Field type in "+fieldName+" is primitive "+fieldClazz.getName()+" while parentTable is null");
				return;
			}
			buildTables(bulkInsert,fieldParam);
			return;
		}
		if (fieldClazz.isArray()) {
			Print.err("@BulkParam field can't be an array");
			return;
		}
		
		/* if value is null we'll bind null */
		if (fieldParam == null){
			if (Caster.PRIMITIVES.containsKey(fieldClazz.getName())){
				bulkInsert.add(table, fieldName, null);
			}
			else {
				BulkTable clazzTable = fieldClazz.getAnnotation(BulkTable.class);
				if (clazzTable == null){
					/* not a table itself, but maybe contains tables in field*/
					//TODO not a table
					Print.err(fieldClazz.getName()+" not a table");
					return;
				}
				ArrayList<Field> keys = getKeyFields(fieldClazz);
				if (keys.size() > 0){
					bulkInsert.add(table, keys.get(0).getName(), null);
				}
			}
			return;
		}
		if (Caster.PRIMITIVES.containsKey(fieldParam.getClass().getName())){
			try {
				bulkInsert.add(table, fieldName, bulkCast(fieldParam));
				return;
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		ArrayList<Field> keys = getKeyFields(fieldParam.getClass());
		if (keys.size() > 0){
			for (Field keyField : keys){
				insertParamField(bulkInsert, table,keyField.get(fieldParam),keyField.getType(),keyField.getName());
			}
		}
		buildTables(bulkInsert, fieldParam);
	}
	
	private static final ArrayList<Field> getKeyFields(Class<? extends Object> clazz){
		ArrayList<Field> keyFields = new ArrayList<Field>();
		if (clazz == null) return keyFields;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields){
			field.setAccessible(true);
			BulkKey key = field.getAnnotation(BulkKey.class);
			if (key != null){
				keyFields.add(field);
			}
		}
		return keyFields;
	}
	
	private static final Object bulkCast(Object obj) throws ClassCastException{
		String clazz = obj.getClass().getName();
		if (clazz.equals(Caster.BYTE) || clazz.equals(Caster.CBYTE) || clazz.equals(Caster.SHORT)
				|| clazz.equals(Caster.CSHORT) || clazz.equals(Caster.INT) || clazz.equals(Caster.INTEGER)
				|| clazz.equals(Caster.LONG) || clazz.equals(Caster.CLONG) || clazz.equals(Caster.BOOLEAN)
				|| clazz.equals(Caster.CBOOLEAN)){
			return Caster.toLong(obj);
		}
		else if (clazz.equals(Caster.DOUBLE) || clazz.equals(Caster.CDOUBLE)
				|| clazz.equals(Caster.FLOAT) || clazz.equals(Caster.CFLOAT)){
			return Caster.toDouble(obj);
		}
		else return Caster.toString(obj);
	}
	
	
	private static class BulkInsert {
		
		private HashMap<String,BulkInsertTable> tables;
		
		private BulkInsert(){
			tables = new HashMap<String,BulkInsertTable>();
		}
		
		/**
		 * Returns added table
		 * @param table
		 * @return
		 */
		public BulkInsertTable addTable(String table){
			if (this.tables.containsKey(table)){
				return this.tables.get(table);
			}
			else {
				BulkInsertTable insertTable = new BulkInsertTable(table);
				this.tables.put(table, insertTable);
				return insertTable;
			}
		}
		
		public void add(String table, String key, Object value){
			addTable(table).add(key, value);
		}
		
		@Override
		public String toString(){
			return tables.toString();
		}
		
		public String[] getTables(){
			int size = this.tables.size();
			String[] tables = new String[size];
			Iterator<String> keyIterator = this.tables.keySet().iterator();
			int i = 0;
			while (keyIterator.hasNext()){
				tables[i] = keyIterator.next();
				i++;
			}
			return tables;
		}
		
		public BulkInsertTable getTable(String tableName){
			return this.tables.get(tableName);
		}
		
	}
	
	private static class BulkInsertTable {

		private String table;
		private BulkInsertArray array;
		
		private BulkInsertTable(String table) {
			this.table = table;
			this.array = new BulkInsertArray();
		}
		
		public String getTableName() {
			return table;
		}

		public void add(String key, Object value){
			try {
				this.array.add(key, value);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public String toString(){
			return array.toString();
		}
		
		public int getColumnSize(){
			return array.getInsertMap().size();
		}
		
		public HashMap<String,ArrayList<Object>> getInsertMap(){
			return array.getInsertMap();
		}
		
		public HashMap<String,Class<? extends Object>> getTypesMap(){
			return array.getTypesMap();
		}
		
	}
	
	private static class BulkInsertArray {

		private HashMap<String,ArrayList<Object>> toInsert;
		private HashMap<String,Class<? extends Object>> types;
		
		
		public BulkInsertArray(){
			toInsert = new HashMap<String,ArrayList<Object>>();
			types = new HashMap<String,Class<? extends Object>>();
		}
				
		public void add(String key, Object value) throws ClassCastException{
			if (!this.toInsert.containsKey(key)){
				this.toInsert.put(key, new ArrayList<Object>());
				this.types.put(key, Long.class);
			}
			if (value != null){
				if (value.getClass().equals(this.types.get(key))){
					this.toInsert.get(key).add(value);
				}
				else if (value.getClass().equals(Double.class) && this.types.get(key).equals(Long.class)){
					this.types.put(key, Double.class);
					this.toInsert.get(key).add(value);
				}
				else if (value.getClass().equals(String.class)
						&& (this.types.get(key).equals(Long.class) || this.types.get(key).equals(Double.class))){
					this.types.put(key, String.class);
					this.toInsert.get(key).add(value);
				}
				else if (value.getClass().equals(Long.class) && this.types.get(key).equals(Double.class)){
					this.toInsert.get(key).add(Caster.toDouble(value));
				}
				else if ((value.getClass().equals(Long.class) || value.getClass().equals(Double.class))
						&& this.types.get(key).equals(String.class)){
					this.toInsert.get(key).add(Caster.toString(value));
				}
			}
			else {
				this.toInsert.get(key).add(null);
			}
			
		}
		
		@Override
		public String toString(){
			return toInsert.toString();
		}
		
		public HashMap<String,ArrayList<Object>> getInsertMap(){
			return this.toInsert;
		}
		
		public HashMap<String,Class<? extends Object>> getTypesMap(){
			return this.types;
		}
	}
}
