package practice03;

import java.io.IOException;
import java.net.ServerSocket;

public class PortTest {
	public static void main(String[] args) {
		System.out.println("포트 스캔 시작");
		
		ServerSocket ss = null;
		for (int i = 0; i < 65535; i++) {
			try {
				ss = new ServerSocket(i);
				ss.close();
			}
			catch (IOException e){
				System.out.println(i + "빈 TCP 포트 사용중");
				
			}
		} 
		System.out.println("포트 스캔 끝");
	}

}
