package ch.unibe.sport.network;

public interface IProxy {

	public void connect(IPoint point);
	public void send(Message message);
	public void broadcast(Message message);
	public void disconnect(IPoint point);
}
