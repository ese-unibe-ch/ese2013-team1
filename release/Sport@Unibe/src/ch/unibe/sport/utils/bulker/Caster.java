package ch.unibe.sport.utils.bulker;
import java.util.HashMap;

public class Caster {
	
	public static final String BYTE = Byte.TYPE.getName();
	public static final String CBYTE = Byte.class.getName();
	public static final String SHORT = Short.TYPE.getName();
	public static final String CSHORT = Short.class.getName();
	public static final String INT = Integer.TYPE.getName();
	public static final String INTEGER = Integer.class.getName();
	public static final String LONG = Long.TYPE.getName();
	public static final String CLONG = Long.class.getName();
	public static final String DOUBLE = Double.TYPE.getName();
	public static final String CDOUBLE = Double.class.getName();
	public static final String FLOAT = Float.TYPE.getName();
	public static final String CFLOAT = Float.class.getName();
	public static final String BOOLEAN = Boolean.TYPE.getName();
	public static final String CBOOLEAN = Boolean.class.getName();
	public static final String CHAR = Character.TYPE.getName();
	public static final String CCHAR = Character.class.getName();
	public static final String STRING = String.class.getName();
	
	public static final String[] TYPES = new String[]{
		BYTE,						// +
		CBYTE,						// +
		SHORT,						// +
		CSHORT,						// +
		INT,						// +
		INTEGER,					// +
		LONG,						// +
		CLONG,						// +
		DOUBLE,						// +
		CDOUBLE,					// +
		FLOAT,						// +
		CFLOAT,						// +
		BOOLEAN,					// +
		CBOOLEAN,					// +
		CHAR,						// +
		CCHAR,						// +
		STRING						// +
	};
	
	public static final HashMap<String,Class<?>> PRIMITIVES;
	static {
		PRIMITIVES = new HashMap<String,Class<?>>();
		PRIMITIVES.put(BYTE, Byte.TYPE);
		PRIMITIVES.put(CBYTE,Byte.class);
		PRIMITIVES.put(SHORT,Short.TYPE);
		PRIMITIVES.put(CSHORT,Short.class);
		PRIMITIVES.put(INT,Integer.TYPE);
		PRIMITIVES.put(INTEGER,Integer.class);
		PRIMITIVES.put(LONG,Long.TYPE);
		PRIMITIVES.put(CLONG,Long.class);
		PRIMITIVES.put(DOUBLE,Double.TYPE);
		PRIMITIVES.put(CDOUBLE,Double.class);
		PRIMITIVES.put(FLOAT,Float.TYPE);
		PRIMITIVES.put(CFLOAT,Float.class);
		PRIMITIVES.put(BOOLEAN,Boolean.TYPE);
		PRIMITIVES.put(CBOOLEAN,Boolean.class);
		PRIMITIVES.put(CHAR,Character.TYPE);
		PRIMITIVES.put(CCHAR,Character.class);
		PRIMITIVES.put(STRING,String.class);
	}
	
	
	/**
	 * Tries to convert object to byte
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Byte toByte(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return (Byte)obj;
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj).byteValue();
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj).byteValue();
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj).byteValue();
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj).byteValue();
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).byteValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return (byte) (((Boolean)obj) ? 1 : 0);
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (byte) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CBYTE +" with value: "+value);
			}
		}
		else {
			try {
				return Byte.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CBYTE +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}
	
	/**
	 * Tries to convert object to short
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Short toShort(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj).shortValue();
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj);
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj).shortValue();
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj).shortValue();
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj).shortValue();
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).shortValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return (short) (((Boolean)obj) ? 1 : 0);
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (short) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CCHAR +" with value: "+value);
			}
		}
		else {
			try {
				return Short.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CCHAR +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}

	/**
	 * Tries to convert object to integer
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Integer toInt(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj).intValue();
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj).intValue();
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj);
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj).intValue();
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj).intValue();
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).intValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj) ? 1 : 0;
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (int) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+INTEGER +" with value: "+value);
			}
		}
		else {
			try {
				return Integer.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+INTEGER +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}
	
	/**
	 * Tries to convert object to long
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Long toLong(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj).longValue();
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj).longValue();
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj).longValue();
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj);
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj).longValue();
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).longValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj) ? 1L : 0L;
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (long) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CLONG +" with value: "+value);
			}
		}
		else {
			try {
				return Long.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CLONG +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}
	
	/**
	 * Tries to convert object to double
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Double toDouble(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj).doubleValue();
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj).doubleValue();
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj).doubleValue();
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj).doubleValue();
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj);
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).doubleValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj) ? 1d : 0d;
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (double) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CDOUBLE+" with value: "+value);
			}
		}
		else {
			try {
				return Double.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CDOUBLE +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}

	/**
	 * Tries to convert object to float
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Float toFloat(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj).floatValue();
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj).floatValue();
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj).floatValue();
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj).floatValue();
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj).floatValue();
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj).floatValue();
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj) ? 1f : 0f;
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			int value = ((Character)obj) - '0';
			if (value >= 0 && value <= 9) return (float) value;
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CFLOAT +" with value: "+value);
			}
		}
		else {
			try {
				return Float.valueOf(obj.toString());
			} catch (NumberFormatException e){
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CFLOAT +" with value: "+obj.toString()+"\n"+e);
			}
		}
	}
	
	/**
	 * Tries to convert object to boolean
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Boolean toBoolean(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return ((Byte)obj) != 0;
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return ((Short)obj) != 0;
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return ((Integer)obj) != 0;
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return ((Long)obj) != 0;
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return ((Double)obj) != 0;
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return ((Float)obj) != 0;
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean)obj);
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) {
			char value = ((Character)obj);
			if (value == '0' || value == 'f'){
				return false;
			}
			else if (value == '1' || value == 't'){
				return true;
			}
			else {
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CBOOLEAN +" with value: "+value);
			}
		}
		else {
			String value = obj.toString().toLowerCase();
			if (value.equals("false") || value.equals("0") || value.equals("f")){
				return false;
			}
			else if (value.equals("true") || value.equals("1") || value.equals("t")){
				return false;
			}
			else{
				throw new ClassCastException("Unable to cast "+clazzFrom+" to "+CBOOLEAN +" with value: "+obj.toString());
			}
		}
	}
	
	/**
	 * Tries to convert object to char
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final Character toChar(Object obj) throws ClassCastException{
		String clazzFrom = obj.getClass().getName();
		if (clazzFrom.equals(BYTE) || clazzFrom.equals(CBYTE)) return Character.forDigit(((Byte)obj),Character.MAX_RADIX);
		else if (clazzFrom.equals(SHORT) || clazzFrom.equals(CSHORT)) return Character.forDigit(((Short)obj),Character.MAX_RADIX);
		else if (clazzFrom.equals(Caster.INT) || clazzFrom.equals(INTEGER)) return Character.forDigit(((Integer)obj),Character.MAX_RADIX);
		else if (clazzFrom.equals(LONG) || clazzFrom.equals(CLONG)) return Character.forDigit(((Long)obj).intValue(),Character.MAX_RADIX);
		else if (clazzFrom.equals(DOUBLE) || clazzFrom.equals(CDOUBLE)) return Character.forDigit(((Double)obj).intValue(),Character.MAX_RADIX);
		else if (clazzFrom.equals(FLOAT) || clazzFrom.equals(CFLOAT)) return Character.forDigit(((Float)obj).intValue(),Character.MAX_RADIX);
		else if (clazzFrom.equals(BOOLEAN) || clazzFrom.equals(CBOOLEAN)) return ((Boolean) obj) ? '1' : '0';
		else if (clazzFrom.equals(CHAR) || clazzFrom.equals(CCHAR)) return (Character) obj;
		else {
			String value = obj.toString().toLowerCase();
			if (value.length() == 0)return 0;
			else return value.charAt(0);
		}
	}
	
	/**
	 * Tries to convert object to String
	 * @param obj
	 * @return
	 * @throws ClassCastException
	 */
	public static final String toString(Object obj) throws ClassCastException{
		return ""+obj;
	}
}
