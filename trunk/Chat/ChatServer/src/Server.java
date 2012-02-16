import java.io.IOException;
import java.net.ServerSocket;


public class Server implements Runnable{
	ServerSocket serverSocket;
	public Server(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		new Thread(this).start();
	}
	@Override
	public void run() {
		while(!serverSocket.isClosed()){
			try {
				new ClientHandler(serverSocket.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
