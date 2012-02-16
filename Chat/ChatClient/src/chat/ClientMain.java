package chat;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import chat.io.MessageHandler;

public class ClientMain {
	
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException{
		ChatManager.setDefaultRoom(new Room(new RoomManager() {
			
			@Override
			public void messageAdded(Message m) {
				Date d = new Date(m.when);
				System.out.println(d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + " " + m.who + " : " + m.value);
			}
		}));
		MessageHandler handler = new MessageHandler(get("Username: "), get("Host: "), Integer.parseInt(get("Port: ")));
		while(true){
			String s = scan.nextLine();
			if(s.equals("Close stream")){
				handler.close();
				break;
			}
			handler.sendMessage(s);
		}
	}
	
	public static String get(String s){
		System.out.print(s);
		return scan.nextLine();
	}
}
