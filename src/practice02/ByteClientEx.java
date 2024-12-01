package practice02;

public class ByteClientEx {
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new ByteClient(serverAddress, serverPort);
	}
}
