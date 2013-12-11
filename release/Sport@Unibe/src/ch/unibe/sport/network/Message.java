package ch.unibe.sport.network;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;
import ch.unibe.sport.utils.AssociativeList;
import ch.unibe.sport.utils.Date;
import ch.unibe.sport.utils.Print;
import ch.unibe.sport.utils.Utils;

/**
 * Message object that can be sent from one activity to another.
 * User of this class can transfer parcelable objects as key:object pair between activities
 * and all other objects included Fragments.
 * Network consists of Proxies(Activities), Points(Fragments) and Nodes (Views).
 * Proxies are connected by default. They use broadcast protocol to communicate.
 * Points connect them selfs to proxies automatically in onCreateView(...)
 * So, the user should only connect Nodes to Points. After that, the full network is built.
 * Message can have several destinations. After message reaches all it's
 * destinations it stops to deliver, but only in context of one activity.
 *  
 * @version 1.0 2013-09-22
 * @author Aliaksei Syrel
 */
public class Message implements Parcelable {

	public static final String SYSTEM = "system";
	public static final String BROADCAST = "ch.unibe.sport.communication.message.broadcast";
	public static final String RESPONSE = "response";
	public static final String ACTION = "action";
	public static final String CODE_NOT_FOUND = "404";
	public static final String CODE_OK = "200";
	public static final String SWITCHING_PROTOCOLS = "101";		// when message goes to another activity
	
	private AssociativeList<Object> data;
	private ArrayList<String> receiverTags;
	private ArrayList<String> trace;
	private String sender;
	
	private boolean invariant(){
		return data != null && receiverTags != null;
	}

	public Message(String sender){
		this.data = new AssociativeList<Object>();
		this.receiverTags = new ArrayList<String>();
		trace = new ArrayList<String>();
		this.sender = sender;
		assert invariant();	
	}

	private Message(Parcel parcel) {
		int dataSize = parcel.readInt();
		if (dataSize == 0){
			this.data = new AssociativeList<Object>();
		}
		else {
			Object[] val = parcel.readArray(getClass().getClassLoader());
			ArrayList<Object> values = new ArrayList<Object>(Arrays.asList(val));
			Object[] intarr = parcel.readArray(getClass().getClassLoader());
			ArrayList<Integer> intKeys = Utils.objectArrayToIntegerList(intarr);
			Object[] strarr = parcel.readArray(getClass().getClassLoader());
			ArrayList<String> strKeys = Utils.objectArrayToStringList(strarr);
			this.data = AssociativeList.valueOf(values,intKeys,strKeys);
		}
		int receiverSize = parcel.readInt();
		if (receiverSize > 0){
			this.receiverTags = Utils.objectArrayToStringList(parcel.readArray(getClass().getClassLoader()));
		}
		int traceSize = parcel.readInt();
		if (traceSize > 0){
			this.trace = Utils.objectArrayToStringList(parcel.readArray(getClass().getClassLoader()));
		}
		this.sender = parcel.readString();
		assert invariant();

	}
	
	private Message (AssociativeList<Object> data,ArrayList<String> receiverTags,ArrayList<String> trace){
		this.data = data;
		this.receiverTags = receiverTags;
		this.trace = trace;
	}
	
	public Message copy(){
		return new Message(data, new ArrayList<String>(receiverTags), new ArrayList<String>(trace));
	}

	public Message put(String key, Object value){
		assert invariant();
		this.data.add(value, key);
		return this;
	}
	
	public Message put(int key, Object value){
		assert invariant();
		this.data.add(value, key);
		return this;
	}

	public Object get(String key){
		assert invariant();
		return this.data.get(key);
	}

	public Object get(int key){
		assert invariant();
		return this.data.get(key);
	}

	public Message addReceiver(String receiver){
		assert invariant();
		assert !this.receiverTags.contains(receiver);
		this.receiverTags.add(receiver);
		return this;
	}

	public boolean isReceiver(String receiver){
		assert invariant();
		return this.receiverTags.contains(receiver);
	}
	
	public boolean containsKey(int key){
		return this.data.containsKey(key);
	}
	
	public boolean containsKey(String key){
		return this.data.containsKey(key);
	}
	
	public void removeReceiver(String receiver){
		assert invariant();
		assert this.receiverTags.contains(receiver);
		this.receiverTags.remove(receiver);
	}
	
	public boolean isDelivered(){
		return (this.receiverTags.size() == 0);
	}
	
	public void addToTrace(String tag){
		assert invariant();
		assert !this.trace.contains(tag);
		this.trace.add(tag);
	}
	
	public boolean isInTrace(String tag){
		assert invariant();
		return trace.contains(tag);
	}

	public void printTrace(){
		Print.p(this.trace, " > ");
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public Message setSender(String sender){
		this.sender = sender;
		return this;
	}
	
	public Message clearTrace(){
		trace = new ArrayList<String>();
		return this;
	}
	
	public Message clearReceivers(){
		this.receiverTags = new ArrayList<String>();
		return this;
	}
	
	public String getSender(){
		return this.sender;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.data.size());
		if (this.data.size() > 0){
			dest.writeArray(Utils.objectArrayListToArray(this.data.getValues()));
			dest.writeArray(this.data.getIntKeysArray());
			dest.writeArray(this.data.getStringKeysArray());
		}
		dest.writeInt(this.receiverTags.size());
		if (this.receiverTags.size() > 0) {
			dest.writeArray(Utils.arrayListToArray(this.receiverTags));
		}
		dest.writeInt(this.trace.size());
		if (this.trace.size() > 0){
			dest.writeArray(Utils.arrayListToArray(this.trace));
		}
		dest.writeString(sender);
	}

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
		public Message createFromParcel(Parcel parcel) {
			return new Message(parcel);
		}

		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Date))
		      return false;
		Message msg = (Message) obj;
		return (this.data.equals(msg.data)
				&& this.receiverTags.equals(msg.receiverTags));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = hash * prime + this.data.hashCode();
		hash = hash * prime + this.receiverTags.hashCode();
		return hash;
	}
}
