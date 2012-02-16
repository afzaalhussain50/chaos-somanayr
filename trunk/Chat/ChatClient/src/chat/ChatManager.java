package chat;

public class ChatManager {
	private ChatManager(){
		throw new IllegalStateException();
	}
	
	private static Room defaultRoom;

	public static Room getDefaultRoom() {
		return defaultRoom;
	}

	public static void setDefaultRoom(Room defaultRoom) {
		ChatManager.defaultRoom = defaultRoom;
	}
}
