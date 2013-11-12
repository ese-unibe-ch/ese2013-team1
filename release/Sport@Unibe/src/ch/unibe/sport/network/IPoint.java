package ch.unibe.sport.network;

public interface IPoint {

	public void connect(INode node);
	public void connect(IProxyable proxy);
	public void disconnect(INode node);
	public void send(Message message);

}
