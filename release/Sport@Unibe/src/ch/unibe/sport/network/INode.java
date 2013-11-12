package ch.unibe.sport.network;

public interface INode {

	public void connect(IPointable point);
	public void send(Message message);
}
