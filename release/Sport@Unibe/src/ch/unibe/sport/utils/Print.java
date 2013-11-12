package ch.unibe.sport.utils;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

/**
 * Collection of print functions for fast debuging prints.
 * Classic debug in eclipse is good, but not usefull when we want
 * to get a content of some array in object that was extended from android's view
 * because there are a lot of private parameters, that prevent from easy finding
 * of neccessary array
 * 
 * @version 1.1 2013-09-01
 * @author Team 1 2013
 */
public class Print {
	public static void p(int[] array){
		StringBuilder str = new StringBuilder();
		str.append('[');
		str.append(toString(array, ','));
		str.append(']');
		log(str);
	}
	
	public static void p(int[][] array){
		if (array.length == 0) log("[empty]");
		for (int i = 0, length = array.length; i < length; i++){
			p(array[i]);
		}
	}
	
	public static void p(String[] array) {
		StringBuilder str = new StringBuilder();
		str.append('[');
		str.append(toString(array, ','));
		str.append(']');
		log(str);
	}
	
	public static void p(String[][] array){
		if (array.length == 0) log("[empty]");
		for (int i = 0, length = array.length; i < length; i++){
			p(array[i]);
		}
	}
	
	public static <E> void p(ArrayList<E> list,Object delimiter){
		log(toString(list,delimiter));
	}
	
	public static String toString(int[] array,Object delimiter){
		if (array == null) return "null";
		if (array.length == 0) return "empty";
		if (array.length == 1) return ""+array[0];
		StringBuilder str = new StringBuilder();
		for (int i = 0,length = array.length;i < length; i++){
			if (i != 0) str.append(delimiter);
			str.append(array[i]);
		}
		return str.toString();
	}
	
	public static String toString(String[] array,Object delimiter){
		if (array == null) return "null";
		if (array.length == 0) return "empty";
		if (array.length == 1) return ""+array[0];
		StringBuilder str = new StringBuilder();
		for (int i = 0,length = array.length;i < length; i++){
			if (i != 0) str.append(delimiter);
			str.append(array[i]);
		}
		return str.toString();
	}
	
	public static <E> String toString(ArrayList<E> list, Object delimiter){
		if (list == null) return "null";
		if (list.size() == 0) return "empty";
		StringBuilder str = new StringBuilder();
		for (int i = 0,length = list.size();i < length; i++){
			if (i != 0) str.append(delimiter);
			str.append(list.get(i).toString());
		}
		return str.toString();
	}
	
	public static void log(Object o){
		if (o == null){
			log("null");
			return;
		}
		System.out.println(o.toString());
	}
	
	public static void log(String tag,Object o){
		if (o == null){
			log("["+((tag!=null)?tag:'?')+"] null");
			return;
		}
		StringBuilder str = new StringBuilder();
		str.append('[');
		if (tag != null){
			String[] tmp = tag.split("\\.");
			if (tmp.length > 0)str.append(tmp[tmp.length-1]);
			else str.append(tag);
		}
		else str.append('?');
		str.append(']').append(' ');
		str.append((o!=null)?o.toString(): ' ');
		System.out.println(str.toString());
	}
	
	public static void err(Object o){
		if (o == null){
			err("[error] null");
			return;
		}
		System.err.println("[error] "+o.toString());
	}
	
	public static void err(String tag,Object o){
		if (o == null){
			log("[error] null");
			return;
		}
		StringBuilder str = new StringBuilder();
		str.append('[');
		if (tag != null){
			String[] tmp = tag.split("\\.");
			if (tmp.length > 0)str.append(tmp[tmp.length-1]);
			else str.append(tag);
		}
		else str.append('?');
		str.append(']').append(' ');
		str.append((o!=null)?o.toString(): ' ');
		System.err.println(str.toString());
	}
	
	public static void toast(Context context,String msg){
		Toast toast = Toast.makeText(context, msg,Toast.LENGTH_LONG);
		toast.show();
	}
}
