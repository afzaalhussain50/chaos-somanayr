import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable{

	private static final char END_MESSAGE = '`';

	private static final char ESCAPE_CHARACTER = '\\';

	private static ArrayList<ClientHandler> instances = new ArrayList<ClientHandler>();;

	private Socket socket;
	private String username = null;
	private BufferedWriter writer;

	public ClientHandler(Socket socket) throws IOException {
		super();
		this.socket = socket;
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		instances.add(this);
		new Thread(this).start();
	}

	@Override
	public void run(){
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
			instances.remove(this);
			try {
				forward('M' + pack(System.currentTimeMillis()) + "Server\n" + username + " left the server.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void handleMessage(String string) throws IOException {
		if(username == null){
			username = verify(sanitize(string));
			forward('M' + pack(System.currentTimeMillis()) + "Server\n" + username + " joined the server.");
			sendMessage('M' + pack(System.currentTimeMillis()) + "Server\n" + username + " joined the server.");
			System.out.println(username + " joined the server.");
		} else {
			forward(string.substring(0, 9) + username + string.substring(9));
			System.out.println(username + " sent message " + string.substring(10));
		}
	}

	private String verify(String sanitize) {
		String s = sanitize;
		if(s.equalsIgnoreCase("server")){
			s = "Noob";
		}
		int count = 0;
		boolean b = true;
		while(b){
			b = false;
			for(ClientHandler c : instances){
				if(c.username != null && c.username.equalsIgnoreCase(s)){
					count++;
					s = sanitize + count;
					b = true;
					break;
				}
			}
		}
		return s;
	}

	private void forward(String string) throws IOException {
		for (ClientHandler c : instances) {
			if(c != this)
				c.sendMessage(string);
		}
	}

	private String sanitize(String s) {
		return s.
				replace("" + ESCAPE_CHARACTER, ESCAPE_CHARACTER + "" + ESCAPE_CHARACTER).
				replace("" + END_MESSAGE, ESCAPE_CHARACTER + "" + END_MESSAGE);
	}

	private void sendMessage(String s) throws IOException{
		writer.write(s + END_MESSAGE);
		writer.flush();
	}

	public static String pack(long l){
		char[] c = new char[8];
		for(int i = 0; i < 8; i++){
			c[i] = (char) ((l >> (i * 8)) & 0xFF);
		}
		return new String(c);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientHandler other = (ClientHandler) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}


}
