package ch.unibe.sport.network;

public interface IMember {
	public void process(Message message);
	public String getMemberTag();
}
