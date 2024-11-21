package project;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ServerGUI extends JFrame {
	private int port;
	private ServerSocket serverSocket = null;
	private DefaultStyledDocument document;
//	private JTextArea t_display;
	private JTextPane t_display;
	private JButton b_connect, b_disconnect, b_exit;
	
	private Thread acceptThread = null;
	
	private Vector<ClientHandler> users = new Vector<ClientHandler>();
	private Map<String, ClientHandler> clientMap = new HashMap<>();  // 클라이언트 ID를 관리
	
	public ServerGUI(int port) {
		super("Server GUI");
		
		this.port = port;
		users = new Vector<>();
		
		buildGUI();
		
		setSize(400, 300);
		setLocation(500,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
//		startServer();

	}
	
	private void buildGUI() {
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);
		
	}
	
	private JPanel createDisplayPanel() {
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		// 편집 불가 상태
		t_display.setEditable(false);
		
		//패널 생성 및 구성
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(t_display), BorderLayout.CENTER);
		
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
	
	private String getLocalAddr() {
		String address = "";
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			address = localAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return address;
		
	}
	
	private void startServer() {
		Socket clientSocket = null;
		try { 
			serverSocket = new ServerSocket(port);
			
			printDisplay("서버가 시작되었습니다: " + getLocalAddr());
			
			while (acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				
				String cAddr = clientSocket.getInetAddress().getHostAddress();
				printDisplay("클라이언트가 연결되었습니다: " + cAddr + "\n");
				
				ClientHandler cHandler = new ClientHandler(clientSocket);
				users.add(cHandler);
				cHandler.start();
			}
		} catch (SocketException e) {
//			System.out.println("서버 소켓 종료: " + e.getMessage());
			printDisplay("서버 소켓 종료" + e.getMessage());
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
//		t_display.append(msg + "\n");
//		t_display.setCaretPosition(t_display.getDocument().getLength());
		int len = t_display.getDocument().getLength();
		
		try {
			document.insertString(len, msg + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		t_display.setCaretPosition(len);
	}
	
	private class ClientHandler extends Thread {
		private Socket clientSocket;
		private DataOutputStream out;
		private DataInputStream in;
		private String uid;
		
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		private void receiveMessages(Socket cs) {
			try {
				in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
				out = new DataOutputStream(new BufferedOutputStream(cs.getOutputStream()));
				
                // 클라이언트 ID 설정
                out.writeUTF("Please provide your UID (e.g., Rabbit or Bear):");
                out.flush();

                // 클라이언트가 보낸 UID 수신
                this.uid = in.readUTF().trim();
                System.out.println("클라이언트 UID: " + uid);

                // 클라이언트 ID를 Map에 추가
                clientMap.put(uid, this);
                
                System.out.println("Client " + uid + " connected and added to clientMap.");
                
	            String receivedMessage;
	            KeyMsg keyMsg;
	            while ((receivedMessage = in.readUTF()) != null) {
	                try {
	                    receivedMessage = receivedMessage.trim().toUpperCase(); // 문자열 정리
	                    keyMsg = KeyMsg.valueOf(receivedMessage); // KeyMsg로 변환
	                    
	                    // 받은 메시지를 브로드캐스트
	                    broadcasting(keyMsg);
	                } catch (IllegalArgumentException e) {
	                    System.err.println("Invalid KeyMsg received: " + receivedMessage);
	                }
	            }
				
				users.removeElement(this);
				printDisplay(uid + " 퇴장, 현재 참가자 수: " + users.size());
			} catch (IOException e) {
				System.err.println("클라이언트 연결 문제> " + e.getMessage());
			} 
			finally {
				printDisplay(uid + "가 연결을 종료했습니다.");
				System.out.println("클라이언트가 연결을 종료했습니다.");
				users.remove(this);
	            clientMap.remove(uid);
				try {
					cs.close();
				} 
				catch (IOException e) {
					System.err.println("서버 닫기 오류> " + e.getMessage());
					System.exit(-1);
				}
			}
		}
		
		private void send(KeyMsg msg) {
		    if (msg == null) {
		        System.err.println("Invalid KeyMsg: null");
		        return;
		    }
			try {
				out.writeUTF(msg.name());
				out.flush();
			} catch (IOException e) {
				System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
			}
		}
        private void sendToSpecificClient(String targetId, KeyMsg msg) {
            ClientHandler targetClient = clientMap.get(targetId);
            System.out.println("Current clientMap: " + clientMap);
            if (targetClient != null) {
                targetClient.send(msg);
            } else if (uid.equals("Bear")) {
                System.err.println("대상 클라이언트를 찾을 수 없습니다: " + targetId);
            }else {
                System.err.println("Both Rabbit and Bear must be connected to send messages.");
            }
        }
		
		
		private void broadcasting(KeyMsg msg) {
	        if (msg == null) {
	            System.err.println("Cannot broadcast null KeyMsg");
	            return;
	        }
            // 이 메서드는 동일한 쌍의 클라이언트에게만 메시지를 보냄
            if (uid.equals("Rabbit")) {
                sendToSpecificClient("Bear", msg);  // Rabbit -> Bear
            } else if (uid.equals("Bear")) {
                sendToSpecificClient("Rabbit", msg);  // Bear -> Rabbit
            }
		}
		
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}
	
	

	public static void main(String[] args) {
		int port = 54321;
		new ServerGUI(port);
	}
}
