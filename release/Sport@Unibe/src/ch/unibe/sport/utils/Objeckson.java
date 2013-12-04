package ch.unibe.sport.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONParser;
import org.json.simple.ParseException;

public class Objeckson {
		
	/**
	 * Returns instantiated {@code Class<E> clazz} object from Json {@code String}.
	 * If provided class structure doesn't match json structure returns null.
	 * If mismatch is just additional parameter that doesn't have setter in provided class,
	 * than this parameter is skipped. If some parameters are missing in json, they wouldn't be
	 * initialized. Class, that must be deserialized should have empty consructor.
	 * <br>
	 * <b>Example:</b>
	 * <pre> public class Foo {
	 *	private String hash;
	 *	private String[] names;
	 *	private Bar bar;	
	 *
	 *	public void setHash(String hash){
	 *		this.hash = hash;
	 *	}
	 *
	 *	public void setBar(Bar bar){
	 *		this.bar = bar;
	 *	}
	 *
	 *	public void setNames(String[] names){
	 *		this.names = names;
	 *	}
	 * }
	 * 
	 * public class Bar {
	 * 	private String address;
	 * 
	 * 	public void setAddress(String address){
	 * 		this.address = address;
	 * 	}
	 * 
	 * }
	 * 
	 * To instantiate class Foo corresponding json string should be passed:
	 * String json = {"hash":"becw6-nc3q-qc3r4","bar":{"address":"Groovestreet, 1"},"names":["Pedro","Muller","Brown"]};
	 * Usage:
	 * Foo foo = Objeckson.fromJson(json,Foo.class);
	 * </pre>
	 * Names of fields doesn't play a role. But user should name setters correctly.
	 * If parameter with name `address` is passed, than setter should be named setAddress(...),
	 * if parameter `phone` : setter should be setPhone(...); 
	 * 
	 * @param json - {@code String} encoded in json format to be deserialized into object
	 * @param clazz - {@code Class<E>} class that should be instantiated from json
	 * @return new object from json string
	 * 
	 * @author Team 1
	 * @version 1.2 2013-11-22
	 */
	public static <E> E fromJson(String json, Class<E> clazz){
		JSONParser parser = new JSONParser();
		Object object = null;
		try {
			object = parser.parse(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (object != null && object.getClass().getName().equals(JSONObject.class.getName())) {
			try {
				return fromObject((JSONObject)object,clazz);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else {
			Print.err("JSON root object should be JSONObject but was "+object.getClass().getName());
		}
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <E> Object fromArray(JSONArray arr, Class<E[]> parameterClass)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		Class<E> arrayElementType = (Class<E>) parameterClass.getComponentType();
		
		Object parameterArray = Array.newInstance(arrayElementType, arr.size());
		
		if (arr.size() == 0) return parameterArray;
		
		int i = 0;
		ListIterator<Object> iterator = arr.listIterator();
		Object entry;
		while(iterator.hasNext()){
			entry = iterator.next();
			if (entry.getClass().getName().equals(JSONObject.class.getName())) {
				Array.set(parameterArray,i,fromObject((JSONObject)entry, arrayElementType));
			}
			else if (entry.getClass().getName().equals(JSONArray.class.getName())){
				/*
				 * Checking if method really takes as parameter array,
				 * otherwise pass this method
				 */
				if (!arrayElementType.isArray()){
					Print.err("Method's  parameter "+arrayElementType.getName()+" isn't array");
					continue;
				}
				Array.set(parameterArray,i,fromArray((JSONArray) entry, (Class<E[]>) arrayElementType));
			}
			else {
				try {
					Array.set(parameterArray,i,fromClass(entry, arrayElementType));
				} catch(IllegalArgumentException argumentException){
					Print.err("Illegal argument: Can't cast object "+entry+" to "+arrayElementType);
				}
			}
			
			i++;
		}
		
		return parameterArray;
	}
	
	@SuppressWarnings("unchecked")
	public static <E> E fromObject(JSONObject obj, Class<E> clazz)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		/*
		 *Creating a new instance of generic object 
		 */
		E instance = clazz.newInstance();
		/*
		 * If parsed object is null, nothing to do here
		 */
		if (obj == null){
			Print.err("Object is null while working with "+clazz.getName());
			return instance;
		}
		
		/*
		 * Building methods hashMap for faster method finding
		 */
		Method[] methodArray = clazz.getMethods();
		HashMap<String,Method> methods = new HashMap<String,Method>();
		for (int i = methodArray.length-1; i >= 0; i--){
			methods.put(methodArray[i].getName(), methodArray[i]);
		}
		
		/** -- preallocate pointers to help GC -- **/
		Set<String> keySet = obj.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		String key;
		String setMethodName;
		Method setMethod;
		Class<?>[] types;
		Class<?> parameterClass;
		Object parameterObject;
		String parameterObjectClassName;
		/**---------------------------------------**/
		
		while(keyIterator.hasNext()){
			/* clazz field that we whant to set */
			key = keyIterator.next();
			/* generation of possible standart setter method name */
			setMethodName = generateSetMethodName(key);
			/* method, that will we used to set field */
			setMethod = methods.get(setMethodName);
			if (setMethod != null){
				types = setMethod.getParameterTypes();
				/*
				 * If setter doesn't have exact one parameter something is wrong
				 * and we just pass this method
				 */						
				if (types.length != 1) {
					Print.err("Method "+setMethod.getName()+" doesn't have exact one parameter");
					continue;
				}
				
				/*
				 * Class, that we should instanciate and pass to the setter method
				 */
				parameterClass = types[0];
				
				/*
				 * object that we should pass to set method
				 * it can be JSONObject, JSONArray or primitive type
				 */
				parameterObject = obj.get(key);
				
				/* parameter object can be also null */
				if (parameterObject == null){
					/* then invoking method with null parameter */
					try {
						setMethod.invoke(instance, (Object)null);
					}
					catch (IllegalArgumentException e){
						/* it means that, method accepts primitive type, which can't be null, so skipping */
					}
					continue;
				}
				
				/*
				 * Name of object's class, that we should pass as argument in setter
				 */
				parameterObjectClassName = parameterObject.getClass().getName();
				
				/* action if object is JSONObject */
				if (parameterObjectClassName.equals(JSONObject.class.getName())) {
					setMethod.invoke(instance, fromObject((JSONObject)parameterObject, parameterClass));
				}
				/* action if object is JSONArray */
				else if (parameterObjectClassName.equals(JSONArray.class.getName())){
					/*
					 * Checking if method really takes as parameter array,
					 * otherwise pass this method
					 */
					if (!parameterClass.isArray()){
						Print.err("Method's "+setMethod.getName()+" parameter isn't array");
						continue;
					}
					
					setMethod.invoke(instance,new Object[]{fromArray((JSONArray) parameterObject, (Class<E[]>) parameterClass)});
				}
				else {
					try {
						setMethod.invoke(instance, fromClass(parameterObject, parameterClass));
					} catch(IllegalArgumentException argumentException){
						Print.err("Illegal argument: Can't cast "+parameterObject.getClass().getName()+" to "+parameterClass);
					}
				}
			}
			else {
				Print.err("Method "+setMethodName + " in class "+clazz+" not found");
			}
		}
		return instance;
	}
	
	//TODO implement type casting between primitive java types
	@SuppressWarnings("unchecked")
	public static <E> E fromClass(Object object, Class<E> clazz) throws InstantiationException, IllegalAccessException{
		/* simple check for null of castObject */
		if (object == null){
			return (E) null;
		}
		E instance = (E)null;
		try {			
			instance = clazz.cast(object);
			
		}
		catch(ClassCastException castExceptionn){
			String clazzName = clazz.getName();
			String objectClazzName = object.getClass().getName();
			
			if ((clazzName.equals(Caster.INT) || clazzName.equals(Caster.INTEGER))){
				if (Caster.ToInteger.isCastable(objectClazzName)){
					return (E) Caster.ToInteger.cast(object, objectClazzName);
				}
			}
			
			else if (clazzName.equals(Caster.DOUBLE) || clazzName.equals(Caster.CDOUBLE)){
				if (Caster.ToDouble.isCastable(objectClazzName)){
					return (E) Caster.ToDouble.cast(object, objectClazzName);
				}
			}
			
			else if (clazzName.equals(Caster.FLOAT) || clazzName.equals(Caster.CFLOAT)){
				if (Caster.ToFloat.isCastable(objectClazzName)){
					return (E) Caster.ToFloat.cast(object, objectClazzName);
				}
			}
			
			
			else if ((clazzName.equals(Caster.SHORT) || clazzName.equals(Caster.CSHORT))){
				if (Caster.ToShort.isCastable(objectClazzName)){
					return (E) Caster.ToShort.cast(object, objectClazzName);
				}
			}
			
			else if ((clazzName.equals(Caster.BOOLEAN) || clazzName.equals(Caster.CBOOLEAN))){
				if (Caster.ToBoolean.isCastable(objectClazzName)){
					return (E) Caster.ToBoolean.cast(object, objectClazzName);
				}
			}
			
			else {
				Print.err("Can't cast "+object.getClass().getName()+" object "+object+" to "+clazz.getName());
			}
		}
		return instance;
	}
	
	private static String generateSetMethodName(String key){
		return new StringBuilder().append("set").append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString();
	}
	
	private static class Caster {
		private static final String SHORT = Short.TYPE.getName();
		private static final String CSHORT = Short.class.getName();
		private static final String INT = Integer.TYPE.getName();
		private static final String INTEGER = Integer.class.getName();
		private static final String LONG = Long.TYPE.getName();
		private static final String CLONG = Long.class.getName();
		private static final String DOUBLE = Double.TYPE.getName();
		private static final String CDOUBLE = Double.class.getName();
		private static final String FLOAT = Float.TYPE.getName();
		private static final String CFLOAT = Float.class.getName();
		private static final String BOOLEAN = Boolean.TYPE.getName();
		private static final String CBOOLEAN = Boolean.class.getName();
		
		private static final LinkedList<String> numericCastable;
		static {
			numericCastable = new LinkedList<String>(Arrays.asList(
					new String[]{
							SHORT,
							CSHORT,
							INT,
							LONG,
							CLONG,
							DOUBLE,
							CDOUBLE,
							FLOAT,
							CFLOAT,
							INTEGER,
							BOOLEAN,
							CBOOLEAN
					}));
		}
		
		private static class ToShort {
			
			private static boolean isCastable(String clazzFrom){
				return numericCastable.contains(clazzFrom);
			}
			
			private static Short cast(Object obj,String clazzFrom){
				if (clazzFrom.equals(SHORT)) return ((Short)obj).shortValue();
				else if (clazzFrom.equals(CSHORT)) return ((Short)obj);
				else if (clazzFrom.equals(Caster.INT)) return ((Integer)obj).shortValue();
				else if (clazzFrom.equals(INTEGER)) return ((Integer)obj).shortValue();
				else if (clazzFrom.equals(LONG)) return ((Long)obj).shortValue();
				else if (clazzFrom.equals(CLONG)) return ((Long)obj).shortValue();
				else if (clazzFrom.equals(DOUBLE)) return ((Double)obj).shortValue();
				else if (clazzFrom.equals(CDOUBLE)) return ((Double)obj).shortValue();
				else if (clazzFrom.equals(FLOAT)) return ((Float)obj).shortValue();
				else if (clazzFrom.equals(CFLOAT)) return ((Float)obj).shortValue();
				else if (clazzFrom.equals(BOOLEAN)) return (short) ((Boolean)obj).compareTo(true);
				else if (clazzFrom.equals(CBOOLEAN)) return (short) ((Boolean)obj).compareTo(true);
				else return 0;
			}
		}

		private static class ToInteger {
			
			private static boolean isCastable(String clazzFrom){
				return numericCastable.contains(clazzFrom);
			}
			
			private static Integer cast(Object obj,String clazzFrom){
				if (clazzFrom.equals(SHORT)) return ((Short)obj).intValue();
				else if (clazzFrom.equals(CSHORT)) return ((Short)obj).intValue();
				else if (clazzFrom.equals(Caster.INT)) return (Integer)obj;
				else if (clazzFrom.equals(INTEGER)) return (Integer)obj;
				else if (clazzFrom.equals(LONG)) return ((Long)obj).intValue();
				else if (clazzFrom.equals(CLONG)) return ((Long)obj).intValue();
				else if (clazzFrom.equals(DOUBLE)) return ((Double)obj).intValue();
				else if (clazzFrom.equals(CDOUBLE)) return ((Double)obj).intValue();
				else if (clazzFrom.equals(FLOAT)) return ((Float)obj).intValue();
				else if (clazzFrom.equals(CFLOAT)) return ((Float)obj).intValue();
				else if (clazzFrom.equals(BOOLEAN)) return ((Boolean)obj).compareTo(true);
				else if (clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj).compareTo(true);
				else return 0;
			}
		}
		
		private static class ToFloat {
			
			private static boolean isCastable(String clazzFrom){
				return numericCastable.contains(clazzFrom);
			}
			
			private static Float cast(Object obj,String clazzFrom){
				if (clazzFrom.equals(SHORT)) return ((Short)obj).floatValue();
				else if (clazzFrom.equals(CSHORT)) return ((Short)obj).floatValue();
				else if (clazzFrom.equals(Caster.INT)) return ((Integer)obj).floatValue();
				else if (clazzFrom.equals(INTEGER)) return ((Integer)obj).floatValue();
				else if (clazzFrom.equals(LONG)) return ((Long)obj).floatValue();
				else if (clazzFrom.equals(CLONG)) return ((Long)obj).floatValue();
				else if (clazzFrom.equals(DOUBLE)) return ((Double)obj).floatValue();
				else if (clazzFrom.equals(CDOUBLE)) return ((Double)obj).floatValue();
				else if (clazzFrom.equals(FLOAT)) return ((Float)obj);
				else if (clazzFrom.equals(CFLOAT)) return ((Float)obj);
				else if (clazzFrom.equals(BOOLEAN)) return (float) ((Boolean)obj).compareTo(true);
				else if (clazzFrom.equals(CBOOLEAN)) return (float) ((Boolean)obj).compareTo(true);
				else return 0f;
			}
		}
		
		private static class ToDouble {
			
			private static boolean isCastable(String clazzFrom){
				return numericCastable.contains(clazzFrom);
			}
			
			private static Double cast(Object obj,String clazzFrom){
				if (clazzFrom.equals(SHORT)) return ((Short)obj).doubleValue();
				else if (clazzFrom.equals(CSHORT)) return ((Short)obj).doubleValue();
				else if (clazzFrom.equals(Caster.INT)) return ((Integer)obj).doubleValue();
				else if (clazzFrom.equals(INTEGER)) return ((Integer)obj).doubleValue();
				else if (clazzFrom.equals(LONG)) return ((Long)obj).doubleValue();
				else if (clazzFrom.equals(CLONG)) return ((Long)obj).doubleValue();
				else if (clazzFrom.equals(DOUBLE)) return ((Double)obj);
				else if (clazzFrom.equals(CDOUBLE)) return ((Double)obj);
				else if (clazzFrom.equals(FLOAT)) return ((Float)obj).doubleValue();
				else if (clazzFrom.equals(CFLOAT)) return ((Float)obj).doubleValue();
				else if (clazzFrom.equals(BOOLEAN)) return (double) ((Boolean)obj).compareTo(true);
				else if (clazzFrom.equals(CBOOLEAN)) return (double) ((Boolean)obj).compareTo(true);
				else return 0d;
			}
		}

		private static class ToBoolean {

			private static boolean isCastable(String clazzFrom){
				return numericCastable.contains(clazzFrom);
			}

			private static Boolean cast(Object obj,String clazzFrom){
				if (clazzFrom.equals(SHORT)) return ((Short)obj).shortValue() != 0;
				else if (clazzFrom.equals(CSHORT)) return ((Short)obj).shortValue() != 0;
				else if (clazzFrom.equals(Caster.INT)) return ((Integer)obj).intValue() != 0;
				else if (clazzFrom.equals(INTEGER)) return ((Integer)obj).intValue() != 0;
				else if (clazzFrom.equals(LONG)) return ((Long)obj).longValue() != 0;
				else if (clazzFrom.equals(CLONG)) return ((Long)obj).longValue() != 0;
				else if (clazzFrom.equals(DOUBLE)) return ((Double)obj).doubleValue() != 0;
				else if (clazzFrom.equals(CDOUBLE)) return ((Double)obj).doubleValue() != 0;
				else if (clazzFrom.equals(FLOAT)) return ((Float)obj).floatValue() != 0;
				else if (clazzFrom.equals(CFLOAT)) return ((Float)obj).floatValue() != 0;
				else if (clazzFrom.equals(BOOLEAN)) return ((Boolean)obj);
				else if (clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj);
				else return false;
			}
		}
	}
	
}
