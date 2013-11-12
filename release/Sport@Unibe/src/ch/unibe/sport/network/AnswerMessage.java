package ch.unibe.sport.network;

public class AnswerMessage {
	public final static Message wrongMember;
	public final static Message okMessage;
	public final static Message switchingProtocols;
	static {
		wrongMember = new Message(Message.SYSTEM).put(Message.RESPONSE, Message.CODE_NOT_FOUND);
		okMessage = new Message(Message.SYSTEM).put(Message.RESPONSE, Message.CODE_OK);
		switchingProtocols = new Message(Message.SYSTEM).put(Message.RESPONSE, Message.SWITCHING_PROTOCOLS);
	}
}
