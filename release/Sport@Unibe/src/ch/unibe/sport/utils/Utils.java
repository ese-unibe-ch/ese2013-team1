package ch.unibe.sport.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.parse.codec.binary.Hex;

import ch.unibe.sport.config.Config;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Collection of useful static methods
 * @version 1.4 2013-08-29
 * @author Aliaksei Syrel
 */
public class Utils {
	
	/**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
	    Random random = new Random();
	    int randomNum = random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	/**
	 * Calculates signum-function of long number
	 */
	public static final int signum(long value) { 
		if (value > 0) return 1; 
		if (value < 0) return -1 ; 
		else return 0; 
	}
	
	public static final int compare(int _this, int _that){
		if (_this < _that) return -1;
		if (_this > _that) return 1;
		return 0;
	}
	/**
	 * Incredibly fast function to convert numbers from string representation to integer.
	 * Supports negative numbers, that should start with '-'
	 * For performance, no parsingError exceptions are throwen.
	 * @param str - string to be converted in integer
	 * @return integer converted from string
	 */
	public static final int Int(String str){
		int n = 0;
		int j = 0;
		boolean negative = false;
		if (str.charAt(0)=='-'){
			j = 1;
			negative = true;
		}
		for (int i = j; i < str.length();i++){
			n = n * 10 +(str.charAt(i)-'0');
		}
		return n*((negative)?-1:1);
	}
	
	/**
	 * 
	 * @param str
	 * @param del
	 * @param num
	 * @return
	 */
	public static final int[] split(String str,char del,int num){
		int[] array = new int[num];
		int k = 0;
		for (int i = 0; i < str.length();i++){
			int ch = str.charAt(i);
			if (ch == del) k++;
			else array[k] = array[k]*10+(str.charAt(i)-'0');
		}
		return array;
	}
	
	/**
	 * Converts integer to byteArray. Usually used in Color objects
	 * @param value
	 * @return
	 */
	public static final byte[] intToByteArray(int value) {
		return new byte[] {
			(byte)(value >>> 24),
			(byte)(value >>> 16),
			(byte)(value >>> 8),
			(byte)value
		};
	}
	
	/**
	 * Transposes n*m matrix.
	 * @param array n*m matrix to be transposed
	 * @return transposed m*n matrix
	 */
	public static final int[][] transpose(int[][] array){
		if (array == null) return new int[0][0];
		if (array.length == 0) return new int[0][0];
		if (array[0].length == 0) return new int[0][array.length];
		int[][] result = new int[array[0].length][array.length];
		for (int i = 0; i < array.length; i++){
			for (int j = 0; j < array[0].length; j++){
				result[j][i] = array[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Transposes n*m matrix.
	 * @param array n*m matrix to be transposed
	 * @return transposed m*n matrix
	 */
	public static final String[][] transpose(String[][] array){
		if (array == null) return new String[0][0];
		if (array.length == 0) return new String[0][0];
		if (array[0].length == 0) return new String[0][array.length];
		String[][] result = new String[array[0].length][array.length];
		for (int i = 0; i < array.length; i++){
			for (int j = 0; j < array[0].length; j++){
				result[j][i] = array[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Returns n-row from multi-dim String array
	 * @param array - array from to get row
	 * @param row - row index
	 * @return row
	 */
	public static final String[] getRow(String[][] array, int row){
		assert row >= 0 && (row < array.length || row == 0);
		if (array == null) return null;
		if (array.length == 0) return new String[0];
		String[] result = new String[array[row].length];
		System.arraycopy(array[row], 0, result, 0, array[row].length);
		return result;
	}
	
	/**
	 * Returns n-row from multi-dim integer array
	 * @param array - array from to get row
	 * @param row - row index
	 * @return row
	 */
	public static final int[] getRow(int[][] array, int row){
		assert row >= 0 && (row < array.length || row == 0);
		if (array == null) return null;
		if (array.length == 0) return new int[0];
		int[] result = new int[array[row].length];
		System.arraycopy(array[row], 0, result, 0, array[row].length);
		return result;
	}
	
	/**
	 * Converts generic arrayList to array of generic objects
	 * Returns null if arrayList is null or the number of elements in list is 0,
	 * because it's impossible(or hard) to get class when there is no elements
	 * @param list to be converted in array
	 * @return array from list
	 *
	 */
	@SuppressWarnings("unchecked")
	public static final <E> E[] arrayListToArray(List<E> list){
		if (list == null) return null;
		if (list.size() == 0) return null;
		E[] array = null;
		for (int i = 0, length = list.size(); i < length; i++){
			if (list.get(i) != null){
				array = (E[]) Array.newInstance(list.get(i).getClass(), list.size());
				break;
			}
		}
		if (array == null) return null;
		for (int i = 0, length = list.size(); i < length; i++){
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static final Object[] objectArrayListToArray(ArrayList<Object> list){
		if (list == null) return null;
		if (list.size() == 0) return null;
		Object[] array = new Object[list.size()];
		for (int i = 0, length = list.size(); i < length; i++){
			array[i] = list.get(i);
		}
		return array;
	}
	
	/**
	 * Converts Interger[] array to elementary int[] array
	 * @param array
	 * @return
	 */
	public static final int[] toInt(Integer[] array){
		if (array == null) return new int[0];
		int[] intArray = new int[array.length];
		for (int i = 0,length = array.length; i < length; i++){
			intArray[i] = array[i];
		}
		return intArray;
	}
	
	/**
	 * Converts Interger[] array to elementary int[] array
	 * @param array
	 * @return
	 */
	public static final Integer[] toInteger(int[] array){
		if (array == null) return new Integer[0];
		Integer[] intArray = new Integer[array.length];
		for (int i = 0,length = array.length; i < length; i++){
			intArray[i] = array[i];
		}
		return intArray;
	}
	
	public static final ArrayList<String> objectArrayToStringList(Object[] array){
		return new ArrayList<String>(Arrays.asList(Arrays.copyOf(array, array.length, String[].class)));
	}
	
	public static final ArrayList<Integer> objectArrayToIntegerList(Object[] array){
		return new ArrayList<Integer>(Arrays.asList(Arrays.copyOf(array, array.length, Integer[].class)));
	}
	
	
	public static final ArrayList<Integer> intArrayToArrayList(int[] array){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : array){
			list.add(i);
		}
		return list;
	}
	/**
	 * This method convets dp unit to equivalent device specific value in pixels. 
	 * 
	 * @param dp A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param dpi A value of display density dpi
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	//public static float convertDpToPx(float dp,int dpi){
	//	return dp * (dpi / 160f);
	//}
	
	public static float convertDpToPx(Context context,float dp){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param dpi A value of display density dpi
	 * @return A float value to represent db equivalent to px value
	 */
	public static float converPxToDp(int px, int dpi){
		return  px / (dpi / 160f);
	}
	
	public static float converPxToDp(Context context,int px){
		return  px / (getDpi(context) / 160f);
	}
	
	public static int getDpi(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}
	/**
	 * Returns string associated with resource id
	 * @param context
	 * @param resID
	 * @return
	 */
	public static String getString(Context context,int resID){
		if (context == null) return "";
		return context.getResources().getString(resID);
	}
	
	/**
	 * Hides keyboard if any view is focused
	 * @param view
	 */
	public static void hideKeyboard(Activity activity){
		if (activity.getCurrentFocus() != null){
	    	InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	public static void hideKeyboardImplicit(Activity activity){
		if (activity == null) return;
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	/**
	 * Show keyboard if any view is focused
	 * @param view
	 */
	public static void showKeyboard(View view){
		if (view != null){
	    	InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    	inputMethodManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	    }
	}
	
	/**
	 * Shows keyboard and focuses cursor in edit text
	 * @param editText - where to set focus
	 * @author Aliaksei Syrel
	 */
	public static void showKeyboardInOnCreate(final EditText editText) {
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		ViewTreeObserver viewTreeObserver = editText.getViewTreeObserver();
		/*
		 * Hack, that allows to "show" keyboard in onCreate method.
		 * Adding OnGlobalLayoutListener allows to postdelay keyboard appearence
		 * after all views were created and allocate on display
		 */
		viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ViewTreeObserver viewTreeObserver = editText.getViewTreeObserver();
				/*
				 * Delay hack. We are letting android to finish drawing processes
				 * to avoid lags and cancelling of keyboard showing process
				 */
				editText.postDelayed(new Runnable() {
					@Override
					public void run() {
						InputMethodManager keyboard = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						keyboard.showSoftInput(editText, 0);
					}
				}, 50);
				/*
				 * New api method since Jelly Bean
				 */
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					viewTreeObserver.removeOnGlobalLayoutListener(this);
				} else {
					viewTreeObserver.removeGlobalOnLayoutListener(this);  // for compatibility with api < 11
				}
			}
		});
	}
	
	/**
	 * Checks if phone has internet connection
	 * @param context
	 * @return true if wifi or mobile internet turn on
	 */
	public static boolean haveNetworkConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	
	/**
	 * Finds view by res id
	 * @param res
	 * @return
	 */
	public static final View findView(View root,String res) {
		int resID = root.getContext().getResources().getIdentifier(res, "id", Config.PACKAGE_NAME);
		View v = root.findViewById(resID);
		return v;
	}
	
	/**
	 * Finds view by res id
	 * @param activity
	 * @return
	 */
	public static final View findView(Activity activity,String res) {
		int resID = activity.getResources().getIdentifier(res, "id", Config.PACKAGE_NAME);
		View v = activity.findViewById(resID);
		return v;
	}
	
	public static final int getResId(Context context,String res) {
		return context.getResources().getIdentifier(res, "id", Config.PACKAGE_NAME);
	}
	
	public static final int getDrawableResId(Context context,String res) {
		return context.getResources().getIdentifier(res, "drawable", Config.PACKAGE_NAME);
	}
	
	/**
	 * Returns hex representation of string's md5 hash
	 * @param str
	 * @return
	 */
	public static String md5(String str){
		if (str == null) return "null";
		byte[] bytesOfString;
		try {
			bytesOfString = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return "null";
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "null";
		}
		byte[] thedigest = md.digest(bytesOfString);
		return Hex.encodeHexString(thedigest);
	}
}
