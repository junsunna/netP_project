package project;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

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
	
    private Vector<ClientHandler> users = new Vector<>();
    private Map<String, ClientHandler> clientMap = new ConcurrentHashMap<>();
    private List<ClientHandler> waitingClients = Collections.synchronizedList(new ArrayList<>()); // 대기 중인 클라이언트 목록

	public ServerGUI(int port) {
		super("BunnyBearServer");
		
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
	
	public class ClientHandler extends Thread {
	    private Socket clientSocket;
	    private DataOutputStream out;
	    private DataInputStream in;
	    private String uid; // uniqueUid
	    private String baseUid; // 기본 UID
	    private ClientHandler pairedClient; // 페어링된 상대 클라이언트

	    public ClientHandler(Socket clientSocket) {
	        this.clientSocket = clientSocket;
	    }

	    private void receiveMessages() {
	        try {
	            in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
	            out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

	            // BaseUid 요청 및 UID 생성
	            baseUid = in.readUTF().trim();
	            uid = baseUid + "-" + UUID.randomUUID().toString().substring(0, 8);
	            printDisplay("클라이언트 연결됨: " + uid);

	            clientMap.put(uid, this); // 클라이언트 추가

	            // 대기열에 추가 및 매칭 시도
	            synchronized (waitingClients) {
	                waitingClients.add(this);
	                checkForPairing();
	            }

	            // 메시지 수신 및 전달
	            String message;
	            while ((message = in.readUTF()) != null) {
	                if (pairedClient != null) {
	                    // 페어링된 상대방이 있을 경우 메시지를 전달
	                    sendToPairedClient(message);
	                } else {
	                    printDisplay("페어링되지 않은 클라이언트가 메시지를 보냈습니다: " + message);
	                }
	            }
	        } catch (IOException e) {
	            printDisplay("클라이언트 연결 오류: " + e.getMessage());
	        } finally {
	            cleanup();
	        }
	    }

	    private void checkForPairing() {
	        synchronized (waitingClients) {
	            for (ClientHandler client : waitingClients) {
	                if (!client.equals(this) && isOppositeBase(client)) {
	                    try {
	                        // 페어링되면 서로의 pairedClient를 설정
	                        this.pairedClient = client;
	                        client.pairedClient = this;

	                        // 페어링 및 게임 시작
	                        notifyPairingAndStartGame(client);
	                        waitingClients.remove(client);
	                        waitingClients.remove(this);

	                        break; // 페어링 후 루프 종료
	                    } catch (IOException e) {
	                        printDisplay("짝 연결 오류: " + e.getMessage());
	                    }
	                }
	            }
	        }
	    }

	    private boolean isOppositeBase(ClientHandler client) {
	        return (this.baseUid.equals("Rabbit") && client.baseUid.equals("Bear")) ||
	               (this.baseUid.equals("Bear") && client.baseUid.equals("Rabbit"));
	    }

	    private void notifyPairingAndStartGame(ClientHandler pairedClient) throws IOException {
	        this.pairedClient = pairedClient; // 페어링된 상대 저장

	        // 상대에게도 게임 시작 신호 전송
	        out.writeUTF("GAME_START");
	        pairedClient.out.writeUTF("GAME_START");

	        out.flush();
	        pairedClient.out.flush();

	        printDisplay("짝 연결됨: " + uid + " <-> " + pairedClient.uid + " | 게임 시작");
	    }

	    private void sendToPairedClient(String message) {
	        if (pairedClient != null) {
	            try {
	                pairedClient.out.writeUTF(message);
	                pairedClient.out.flush();
	                printDisplay(uid + " -> " + pairedClient.uid + ": " + message);
	            } catch (IOException e) {
	                printDisplay("메시지 전달 오류: " + e.getMessage());
	            }
	        } else {
	            printDisplay("페어링되지 않은 클라이언트에게 메시지를 전달할 수 없습니다.");
	        }
	    }

	    private void cleanup() {
	        try {
	            clientMap.remove(uid);
	            synchronized (waitingClients) {
	                waitingClients.remove(this);
	            }
	            if (pairedClient != null) {
	                pairedClient.pairedClient = null; // 상대방의 페어링도 해제
	            }
	            clientSocket.close();
	            printDisplay("클라이언트 연결 종료: " + uid);
	        } catch (IOException e) {
	            printDisplay("클라이언트 종료 오류: " + e.getMessage());
	        }
	    }

	    @Override
	    public void run() {
	        receiveMessages();
	    }
	}

	public static void main(String[] args) {
		int port = 54321;
		new ServerGUI(port);
	}
}
