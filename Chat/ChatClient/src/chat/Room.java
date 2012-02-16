package chat;

import java.util.LinkedList;

public class Room {
	
	public static int MAX_HISTORY = 10;
	
	private LinkedList<Message> history = new LinkedList<Message>();
	
	private RoomManager rm;
	
	public Room(RoomManager rm) {
		super();
		this.rm = rm;
	}

	public void addMessage(Message m){
		history.addFirst(m);
		if(history.size() > MAX_HISTORY)
			history.removeLast();
		rm.messageAdded(m);
	}
}
