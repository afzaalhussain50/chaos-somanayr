package chat;

public class Message {
	public String value;
	public String who;
	public long when;
	
	public Message(String value, String who, long when) {
		super();
		this.value = value;
		this.when = when;
		this.who = who;
	}
}
