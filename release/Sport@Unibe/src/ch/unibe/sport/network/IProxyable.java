package ch.unibe.sport.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public interface IProxyable {
	public String tag();
	public void sendBroadcast(Intent intent);
	public void process(Message message);
	public Intent registerReceiver(BroadcastReceiver mReceiver, IntentFilter ifilt);
	public void unregisterReceiver(BroadcastReceiver mReceiver);
	public void send(Message message);
	public void connect(IPoint point);
	public IProxy getProxy();
	public Activity getActivity();

}
