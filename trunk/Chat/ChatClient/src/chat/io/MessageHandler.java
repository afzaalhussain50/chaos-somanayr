package chat.io;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import chat.ChatManager;
import chat.Message;


public class MessageHandler implements Runnable{
	private static final char END_MESSAGE = '`';
	private static final char ESCAPE_CHARACTER = '\\';
	private static final char INDICATOR_MESSAGE = 'M';
	private Socket socket;
	private BufferedWriter writer;
	public MessageHandler(String username, String host, int port) throws UnknownHostException, IOException{
		socket = new Socket(host, port);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		write(username);
		new Thread(this).start();
	}

	public void sendMessage(String s) throws IOException{
		write('M' + pack(System.currentTimeMillis()) + '\n' + sanitize(s));
	}

	private String sanitize(String s) {
		return s.
				replace("" + ESCAPE_CHARACTER, ESCAPE_CHARACTER + "" + ESCAPE_CHARACTER).
				replace("" + END_MESSAGE, ESCAPE_CHARACTER + "" + END_MESSAGE);
	}

	private void write(String s) throws IOException{
		writer.write(s + END_MESSAGE);
		writer.flush();
	}

	public void close() throws IOException{
		socket.close();
	}

	@Override
	public void run() {
		try {
			InputStream is = socket.getInputStream();
			StringBuffer buf = new StringBuffer();
			int i;
			boolean isEscaped = false;
			while(!socket.isClosed() && (i = is.read()) != -1){
				char c = (char)i;
				if(c == END_MESSAGE && !isEscaped){
					handleMessage(buf.toString());
					buf.setLength(0);
					isEscaped = false;
				} else if(c == ESCAPE_CHARACTER){
					if(isEscaped){
						buf.append('\\');
						isEscaped = false;
					} else {
						isEscaped = true;
					}
				} else {
					if(isEscaped){
						System.err.println("Syntax error in message handling. Message: " + buf.toString() + "; character: " + c);
						isEscaped = false;
					}
					buf.append(c);
				}

			}
		} catch (IOException e) {
			if(!socket.isClosed())
				e.printStackTrace();
			System.exit(0);
		}
	}

	private void handleMessage(String string) {
		int x = string.indexOf('\n');
		String header = string.substring(0, x);
		String message = string.substring(x + 1);
		if(header.charAt(0) == INDICATOR_MESSAGE){
			long when = unpack(header.substring(1, 9));
			String who = header.substring(9);
			ChatManager.getDefaultRoom().addMessage(new Message(message, who, when));
		}
	}

	public static long unpack(String s){
		long val = 0;
		for(int i = 0; i < 8; i++){
			val |= ((long)s.charAt(i)) << (i * 8);
		}
		return val;
	}

	public static String pack(long l){
		char[] c = new char[8];
		for(int i = 0; i < 8; i++){
			c[i] = (char) ((l >> (i * 8)) & 0xFF);
		}
		return new String(c);
	}
}
