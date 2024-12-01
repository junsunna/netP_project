package practice12;

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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class P2PChatServer {
	private JFrame frame;
	
	private int port;
	private ServerSocket serverSocket;
	private JTextArea t_display;
	private JTextField t_input;
	private JButton b_send;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	
	private Thread acceptThread = null;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	
	public P2PChatServer(int port) {
		this.port = port;
		frame = new JFrame("P2P ChatServer");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
//		printDisplay("서버가 시작되었습니다.");
//		startServer();

	}
	
	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		
		JPanel p_input = new JPanel(new GridLayout(2, 0));
		p_input.add(createInputPanel());
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
	
	private JPanel createInputPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
	
		t_input = new JTextField(30);
		// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
		t_input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
//				receiveMessage();
			}
		});
		
		b_send = new JButton("보내기");
		// 보내기 버튼에 액션 리스너 추가하여 메시지 처리
		b_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
//				receiveMessage();
			}
		});
		
		panel.add(t_input, BorderLayout.CENTER);
		panel.add(b_send, BorderLayout.EAST);
		
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
				
				t_input.setEnabled(false);
				b_send.setEnabled(false);
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
			printDisplay("서버가 시작되었습니다.");
			while (acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				printDisplay("클라이언트가 연결되었습니다.");
				
				ClientHandler cHandler = new ClientHandler(clientSocket);
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
		
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		
			t_input.setEnabled(true);
			b_send.setEnabled(true);
		}
		
		private void receiveMessages(Socket cs) {
			try {
//				in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
				in = new BufferedReader(new InputStreamReader(cs.getInputStream(), "UTF-8"));
				out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));
				
				String message;
				while ((message = in.readLine()) != null) {
					printDisplay("클라이언트 메시지: " + message);
//					out.write("'" + message + "'...echo\n");
//					out.flush();
				}
			} catch (IOException e) {
				System.err.println("클라이언트 연결 문제> " + e.getMessage());
			}
			finally {
				printDisplay("클라이언트가 연결을 종료했습니다.");
				System.out.println("클라이언트가 연결을 종료했습니다.");
				try {
					cs.close();
				}
				catch (IOException e) {
					System.err.println("서버 닫기 오류> " + e.getMessage());
					System.exit(-1);
				}
			}
		}
		
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}
	
	
	private void sendMessage() {
		// 메시지 가져오기		
		String message = t_input.getText();
		if (message.isEmpty()) return;
		
		try {
			((BufferedWriter)out).write(message + "\n");
			out.flush();
			
			// 화면 출력
			t_display.append("나: " + message + "\n");
		} catch (IOException e) {
			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
			System.exit(-1);
		}
		
		
		// 메시지 필드 초기화
		t_input.setText("");

	}
	
	public static void main(String[] args) {
		int port = 54321;
		new P2PChatServer(port);
	}
}
