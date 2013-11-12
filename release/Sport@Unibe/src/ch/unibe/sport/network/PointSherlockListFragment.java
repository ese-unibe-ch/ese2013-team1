package ch.unibe.sport.network;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import ch.unibe.sport.main.OnFragmentAttachListener;
import ch.unibe.sport.utils.Print;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class PointSherlockListFragment extends SherlockListFragment  implements IPoint,IMember {

	private String tag;
	
	private final ArrayList<INode> nodes;
	private IProxy proxy;

	private OnFragmentAttachListener mOnFragmentAttachListener;
	
	public PointSherlockListFragment(String tag){
		this.nodes = new ArrayList<INode>();
		this.tag = tag;
	}
	
	@Override
	public void connect(IProxyable proxy){
		assert proxy != null;
		this.proxy = proxy.getProxy();
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onCreated(savedInstanceState);
		/* yes, it's not the best solution, but we need to check if parent activity is ProxySherlockFragmentActivity
		 * to avoid type cast exceptions
		 */
		connectToActivity();
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if (mOnFragmentAttachListener != null){
			this.mOnFragmentAttachListener.onAttach(this);
		}
	}
	
	public void setOnFragmentAttachListener(OnFragmentAttachListener l){
		this.mOnFragmentAttachListener = l;
	}
	
	public abstract void onCreated(final Bundle savedInstanceState);

	private void connectToActivity() {
		if (this.getActivity() instanceof ProxySherlockFragmentActivity){
			((ProxySherlockFragmentActivity)this.getActivity()).connect(this);
		}
	}
	
	@Override
	public void connect(INode node) {
		assert node != null;
		if (!this.nodes.contains(node)) this.nodes.add(node);
	}
	
	@Override
	public void disconnect(INode node){
		assert node != null;
		if (this.nodes.contains(node)) this.nodes.remove(node);
		node = null;
	}
	
	/**
	 * Prevents fragment recreation memory leak.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (proxy != null)this.proxy.disconnect(this);
	}

	@Override
	public void send(Message message) {
		if (message == null) return;
		if (message.isInTrace(getMemberTag())) return;
		if (message.isDelivered()) return;
		message.addToTrace(getMemberTag());
		if (message.isReceiver(getMemberTag())){
			process(message);
			message.removeReceiver(getMemberTag());
			if (message.isDelivered()) return;
		}
		for (INode node : nodes){
			node.send(message);
			if (message.isDelivered()) return;
		}
		if (proxy == null) {
			Print.err("[PointListFragment] Proxy is null, reconnecting...");
			connectToActivity();
		}
		if (proxy != null) this.proxy.send(message);
		else Print.err("[PointListFragment] Proxy is null, reconnection failed.");
	}

	@Override
	public String getMemberTag() {
		return tag;
	}
	
}
