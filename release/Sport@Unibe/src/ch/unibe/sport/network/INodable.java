package ch.unibe.sport.network;

public interface INodable {

	public void connect(IPointable point);
	public String tag();
	public void process(Message message);

}
