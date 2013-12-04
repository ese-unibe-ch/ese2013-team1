package ch.unibe.sport.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Simple associative list, that holds entries as Key:Value.
 * Key can be {@code String} or {@code int}
 * @param <E>
 * @version 1.3 2013-09-22
 * @author Aliaksei Syrel
 */
public class AssociativeList<E> implements Iterable<E>{
	private LinkedList<String> strKeys;
	private LinkedList<Integer> intKeys;
	private LinkedList<E> values;
	private int size = 0;
	
	public boolean invariant(){
		return strKeys != null
				&& intKeys != null
				&& values != null
				&& strKeys.size() == size
				&& intKeys.size() == size
				&& values.size() == size;
	}
	
	public AssociativeList(){
		strKeys = new LinkedList<String>();
		intKeys = new LinkedList<Integer>();
		values = new LinkedList<E>();
		assert invariant();
	}
	
	private AssociativeList(ArrayList<E> values, ArrayList<Integer> intKeys, ArrayList<String> strKeys){
		this.values = new LinkedList<E>(values);
		this.intKeys = new LinkedList<Integer>(intKeys);
		this.strKeys = new LinkedList<String>(strKeys);
		this.size = values.size();
		assert invariant();
	}
	
	public static <E> AssociativeList<E> valueOf(ArrayList<E> values, ArrayList<Integer> intKeys, ArrayList<String> strKeys) {
	    return new AssociativeList<E>(values, intKeys,strKeys);
	}
	
	public AssociativeList<E> copy(){
		return new AssociativeList<E>(new ArrayList<E>(values), new ArrayList<Integer>(intKeys), new ArrayList<String> (strKeys));
	}
	
	public void add(E obj, String key){
		assert obj != null;
		assert key != null;
		assert key.length() > 0;
		assert (strKeys.contains(key) == false);
		strKeys.add(key);
		intKeys.add(null);
		values.add(obj);
		size++;
		assert invariant();
	}
	public void add(E obj, int key){
		assert obj != null;
		assert (strKeys.contains(key) == false);
		strKeys.add(null);
		intKeys.add(key);
		values.add(obj);
		size++;
		assert invariant();
	}
	public E get(String key){
		assert invariant();
		if (!strKeys.contains(key)) return null;
		return values.get(strKeys.indexOf(key));
	}
	public E get(int key){
		assert invariant();
		if (!intKeys.contains(key)) return null;
		return values.get(intKeys.indexOf(key));
	}
	
	public E getAt(int index){
		assert invariant();
		assert index >= 0;
		assert index < size;
		return values.get(index);
	}
	
	public int indexOf(E obj){
		assert obj != null;
		return this.values.indexOf(obj);
	}
	
	public int indexOfKey(String key){
		assert invariant();
		assert key != null;
		assert key.length() > 0;
		return this.strKeys.indexOf(key);
	}
	
	public int indexOfKey(int key){
		assert invariant();
		return this.intKeys.indexOf(key);
	}
	
	public int getSize(){
		assert invariant();
		return size;
	}
	
	public boolean containsKey(int key){
		assert invariant();
		return intKeys.contains(key);
	}
	
	public boolean containsKey(String key){
		assert invariant();
		assert key != null;
		assert key.length() > 0;
		return strKeys.contains(key);
	}
	
	public E replace(E obj, int key){
		assert obj != null;
		assert intKeys.contains(key);
		int index = intKeys.indexOf(key);
		return this.values.set(index, obj);
	}
	
	public E replace(E obj, String key){
		assert obj != null;
		assert strKeys.contains(key);
		int index = strKeys.indexOf(key);
		return this.values.set(index, obj);
	}

	public ArrayList<E> getValues(){
		return new ArrayList<E>(this.values);
	}
	
	public E remove(int key){
		if (this.intKeys.contains(key)){
			int index = intKeys.indexOf(key);
			intKeys.remove(index);
			size--;
			assert invariant();
			return this.values.remove(index);
		}
		else return null;
	}
	
	
	public Integer[] getIntKeysArray(){
		Integer[] array = Utils.arrayListToArray(this.intKeys);
		if (array == null && this.intKeys != null){
			return new Integer[this.intKeys.size()];
		}
		else return array;
	}
	
	public String[] getStringKeysArray(){
		String[] array =  Utils.arrayListToArray(this.strKeys);
		if (array == null && this.strKeys != null){
			return new String[this.strKeys.size()];
		}
		else return array;
	}
	
	public String toString(){
		String str = "[";
		for (int i = 0 ; i < size; i++){
			str += "'"+((strKeys.get(i) != null) ? strKeys.get(i) : intKeys.get(i))+"':'"+values.get(i)+((i != size-1) ? "', ": "'");
		}
		str += "]";
		return str;
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> valuesIterator = values.iterator();
        return valuesIterator; 
	}
}
