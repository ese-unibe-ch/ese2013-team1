package ch.unibe.sport.network;

import java.util.ArrayList;

import ch.unibe.sport.utils.Print;

public class Point implements IPoint {

	private final ArrayList<INode> nodes;
	private IProxy proxy;
	private IPointable pointable;
	
	private Point(IPointable pointable){
		this.nodes = new ArrayList<INode>();
		this.pointable = pointable;
	}
	
	public static Point initialize(IPointable pointable){
		return new Point(pointable);
	}
	
	@Override
	public void connect(INode node) {
		assert node != null;
		if (!this.nodes.contains(node)) this.nodes.add(node);
	}

	@Override
	public void connect(IProxyable proxy) {
		this.proxy = proxy.getProxy();
		this.proxy.connect(this);
	}

	public void disconnect(){
		if (proxy != null){
			proxy = null;
		}
		
		//TODO disconnect nodes
	}
	
	@Override
	public void disconnect(INode node) {
		assert node != null;
		if (this.nodes.contains(node)) this.nodes.remove(node);
		node = null;
	}

	@Override
	public void send(Message message) {
		if (message == null) return;
		if (message.isInTrace(pointable.tag())) return;
		message.addToTrace(pointable.tag());
		if (message.isReceiver(pointable.tag())){
			pointable.process(message);
		}
		for (INode node : nodes){
			node.send(message);
		}
		
		if (proxy == null) {
			Print.err("["+pointable.tag()+"] Proxy is null");
		}
		else {
			proxy.send(message);
		}
	}

}
