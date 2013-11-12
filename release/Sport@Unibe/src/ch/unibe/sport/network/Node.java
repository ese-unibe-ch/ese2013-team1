package ch.unibe.sport.network;

public class Node implements INode {
	
	private IPoint point;
	private INodable nodable;

	private Node(INodable nodable){
		this.nodable = nodable;
	}
	
	public static Node initialize(INodable nodable){
		return new Node(nodable);
	}
	
	@Override
	public void connect(IPointable point) {
		this.point = point.getPoint();
		this.point.connect(this);
	}

	@Override
	public void send(Message message) {
		if (message == null) return;
		if (message.isDelivered()) return;
		if (message.isInTrace(nodable.tag())) return;
		if (message.isReceiver(nodable.tag())){
			nodable.process(message);
		}
		if (point != null && message.getSender().equals(nodable.tag())){
			point.send(message);
		}
	}
}
