package practice04;

public class IntServerTEx {

	public static void main(String[] args) {
		int port = 54321;
		
		IntServer server = new IntServer(port);
		server.startServer();
		
		
	}
}
