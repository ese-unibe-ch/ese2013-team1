package ch.unibe.sport.network;

public interface IPointable {

	public String tag();
	public void process(Message message);
	public void send(Message message);
	public void connect(IProxyable proxy);
	public IPoint getPoint();

}
