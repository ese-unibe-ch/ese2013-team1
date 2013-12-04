package ch.unibe.sport.network;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Proxy implements IProxy {

	private ArrayList<IPoint> points;
	private IProxyable proxyable;
	
	private boolean receiverRegistered;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override 
		public void onReceive(Context context, Intent intent) {			
			Message message = (Message) intent.getParcelableExtra(Message.BROADCAST);
			if (message != null){
				if (preDeliverActions(message)) return;
				if (message.isDelivered()) return;
				if (message.getSender().equals(proxyable.tag())) return;
				if (message.isInTrace(proxyable.tag())) return;
				message.addToTrace(proxyable.tag());
				if (message.isReceiver(proxyable.tag())) {
					proxyable.process(message);
					if (message.isDelivered()) return;
				}
				for (IPoint point : points){
					point.send(message);
					if (message.isDelivered()) return;
				}
			}
		}
	};
	
	/**
	 * True to cancel delivering, false otherwise
	 * @param message
	 * @return
	 */
	private boolean preDeliverActions(Message message){
		MessageAdapter adapter = new MessageAdapter(message);
		if (adapter.isFinishActivity() && message.isReceiver(proxyable.tag())){
			if (proxyable.getActivity() != null){
				proxyable.getActivity().finish();
				return true;
			}
		}
		return false;
	}
	
	private Proxy(IProxyable proxyable){
		this.proxyable = proxyable;
		this.points = new ArrayList<IPoint>();
		initReceiver();
	}
	
	public static Proxy initialize(IProxyable proxyable){
		return new Proxy(proxyable);
	}
		
	@Override
	public void connect(IPoint point) {
		assert point != null;
		if (!this.points.contains(point)) {
			this.points.add(point);
		}
	}

	@Override
	public void disconnect(IPoint point) {
		assert point != null;
		if (this.points.contains(point)) this.points.remove(point);
		point = null;
	}
	
	@Override
	public void send(Message message) {
		if (message == null) return;
		if (message.isDelivered()) return;
		if (message.isInTrace(proxyable.tag())) return;
		message.addToTrace(proxyable.tag());
		if (message.isReceiver(proxyable.tag())) {
			proxyable.process(message);
			if (message.isDelivered()) return;
		}
		for (IPoint point : points){
			point.send(message);
			if (message.isDelivered()) return;
		}
		broadcast(message);
	}

	@Override
	public void broadcast(Message message) {
		Intent intent = new Intent(Message.BROADCAST);
		intent.putExtra(Message.BROADCAST, message);
		proxyable.sendBroadcast(intent);
	}
	
	private void initReceiver() {
		if (!receiverRegistered) {
			IntentFilter ifilt = new IntentFilter(Message.BROADCAST); 
			proxyable.registerReceiver(mReceiver, ifilt);
			receiverRegistered = true;
		}
	}
	
	public void unInitReceiver(){
		if (receiverRegistered){
			proxyable.unregisterReceiver(mReceiver);
			receiverRegistered = false;
		}
	}
}
