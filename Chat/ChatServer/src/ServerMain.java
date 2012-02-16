import java.io.IOException;
import java.util.Scanner;


public class ServerMain {

	public static void main(String[] args) {
		try {
			System.out.print("Port: ");
			int port = Integer.parseInt(new Scanner(System.in).nextLine());
			new Server(port);
			System.out.println("Opened server on port " + port + ".");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
