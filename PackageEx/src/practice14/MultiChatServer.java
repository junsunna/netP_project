package practice14;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MultiChatServer {
	private JFrame frame;
	
	private int port;
	private String address;
	private ServerSocket serverSocket;
	private JTextArea t_display;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	
	private Thread acceptThread = null;
	
	private BufferedReader in;
	
	private Vector<ClientHandler> users;
	
	public MultiChatServer(int port, String address) {
		this.port = port;
		this.address = address;
		users = new Vector<>(); 
		frame = new JFrame("Multi Chat Server");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
//		startServer();

	}
	
	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		
		JPanel p_input = new JPanel(new GridLayout(1, 0));
//		p_input.add(createInputPanel());
		p_input.add(createControlPanel());
		frame.add(p_input, BorderLayout.SOUTH);
		
	}
	private JPanel createDisplayPanel() {
		t_display = new JTextArea(10, 30);
		// 편집 불가 상태
		t_display.setEditable(false);
		// 자동 줄바꿈
		t_display.setLineWrap(true);
		t_display.setWrapStyleWord(true);
		
		// JScrollPane 스크롤 연결
		JScrollPane scrollPane = new JScrollPane(t_display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		//패널 생성 및 구성
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		
		// 버튼에 액션 리스너 추가하여 접속하기 구현
		b_connect = new JButton("서버 시작");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				connectToServer();
//				startServer();
				
				acceptThread = new Thread(new Runnable() {
					@Override
					public void run() {
						startServer();
					}
				});
				acceptThread.start();
				
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				
				b_exit.setEnabled(false);
				
			}
		});
		
		// 버튼에 액션 리스너 추가하여 접속끊기 구현
		b_disconnect = new JButton("서버 종료");
		b_disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				

				b_exit.setEnabled(true);
			}
		});
		
		b_exit = new JButton("종료");
		panel.add(b_exit, BorderLayout.CENTER);
		
		// 버튼에 액션 리스너 추가하여 종료 구현
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 프로그램 종료
				System.exit(0);
			}
		});
		
		panel.add(b_connect);
		panel.add(b_disconnect);
		panel.add(b_exit);
		b_connect.setEnabled(true);
		b_disconnect.setEnabled(false);
		b_exit.setEnabled(true);
		
		return panel;
	}
	
	public void startServer() {
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			printDisplay("서버가 시작되었습니다: " + address);
			while (acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				printDisplay("클라이언트가 연결되었습니다.");
				
				ClientHandler cHandler = new ClientHandler(clientSocket);
				users.add(cHandler);
				cHandler.start();
			}
		} catch (SocketException e) {
//			System.out.println("서버 소켓 종료: " + e.getMessage());
			printDisplay("서버 소켓 종료");
		}
		catch (IOException e) {
			System.err.println("서버 오류> " + e.getMessage());
			System.exit(-1);
		}
		finally {
			try {
				if (clientSocket != null) clientSocket.close();
				if (serverSocket != null) serverSocket.close();
			}
			catch (IOException e) {
				System.err.println("서버 닫기 오류> " + e.getMessage());
				System.exit(-1);
			}
		}
	}

	private void disconnect() {
		try {
			acceptThread = null;
			serverSocket.close();
		} catch (IOException e1) {
			System.err.println("서버 소켓 닫기 오류> " + e1.getMessage());
			System.exit(-1);
		}

	}
	
	private void printDisplay(String msg) {
		t_display.append(msg + "\n");
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}

	private class ClientHandler extends Thread {
		private Socket clientSocket;
		private BufferedWriter out;
		private String uid;
		private String header_id = "/uid";
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		private void receiveMessages(Socket cs) {
			try {
//				in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
				in = new BufferedReader(new InputStreamReader(cs.getInputStream(), "UTF-8"));
				out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));
				
				String message;
				while ((message = in.readLine()) != null) {
					if (message.contains(header_id)) {
						uid = message.replace(header_id, "");
						printDisplay("새 참가자: " + uid);
						printDisplay("현재 참가자 수 : " + Integer.toString(users.size()));
						continue;
					}
					printDisplay(uid + ": " + message);
					broadcasting(message);
				}
			} catch (IOException e) {
				System.err.println("클라이언트 연결 문제> " + e.getMessage());
			}
			finally {
				printDisplay(uid + "가 연결을 종료했습니다.");
				System.out.println("클라이언트가 연결을 종료했습니다.");
				users.remove(this);
				try {
					cs.close();
				} 
				catch (IOException e) {
					System.err.println("서버 닫기 오류> " + e.getMessage());
					System.exit(-1);
				}
			}
		}
		
		private void sendMessage(String msg, String sUid) {
			try {
				out.write(sUid + ": "+ msg + "\n");
				out.flush();
			} catch (IOException e) {
				System.err.println("메시지 전송 오류> " + e.getMessage());
			}
		}
		
		private void broadcasting(String msg) {
			for (ClientHandler user : users) {
				user.sendMessage(msg, uid);
			}
		}
		
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}
	
	

	public static void main(String[] args) {
		int port = 54321;
		String address = "localhost";
		new MultiChatServer(port, address);
	}
}
