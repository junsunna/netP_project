package practice04;

public class intClientEx {
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		IntClient client = new IntClient(serverAddress, serverPort);
		client.start();
	}
}
