package practice14;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MultiTalk {
	private JFrame frame;
	
	private JTextArea t_display;
	private JTextField t_input;
	
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private JButton b_send;
	
	private String serverAddress;
	private int serverPort;
	
    private Reader in;
	private Writer out;
	private Socket socket;
	
	private Thread receiveThread = null;
	
	private JLabel l_id;
	private JLabel l_serverAddress;
	private JLabel l_serverPort;
	
	private JTextField t_userID;
	private JTextField t_hostAddr;
	private JTextField t_portNum;
	
	private boolean isConnected;
	
	public MultiTalk(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		frame = new JFrame("Multi Talk");
		
		buildGUI();
		
		frame.setSize(500, 330);
		frame.setLocation(100,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		
		JPanel p_input = new JPanel(new GridLayout(3, 0));
		p_input.add(createInputPanel());
		p_input.add(createInfoPanel());
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
	
	private JPanel createInfoPanel() {
		JPanel p_info = new JPanel();
		p_info.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		t_userID = new JTextField(7);
		t_hostAddr = new JTextField(12);
		t_portNum = new JTextField(7);
		
		Random rand = new Random();
		int randomNum = rand.nextInt(99) + 1;
		
		t_userID.setText("guest" + randomNum);
		t_hostAddr.setText(serverAddress);
		t_portNum.setText(Integer.toString(serverPort));
		
		l_id = new JLabel("아이디: ");
		l_serverAddress = new JLabel("서버주소: ");
		l_serverPort = new JLabel("포트번호: ");
		
		p_info.add(l_id);
		p_info.add(t_userID);
		
		p_info.add(l_serverAddress);
		p_info.add(t_hostAddr);
		
		p_info.add(l_serverPort);
		p_info.add(t_portNum);
		
		return p_info;
	}
	
	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new BorderLayout());

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

	private void sendMessage() {
		// 메시지 가져오기		
		String message = t_input.getText();
		if (message.isEmpty()) return;
		
		try {
			((BufferedWriter)out).write(message + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
			System.exit(-1);
		}
		
		
		// 메시지 필드 초기화
		t_input.setText("");

	}
	private void printDisplay(String msg) {
		t_display.append(msg + "\n");
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}
	private void receiveMessage() {
		try {
			String inMsg = ((BufferedReader)in).readLine();
			if (inMsg == null) {
				t_display.append("서버 연결 종료\n");
				receiveThread = null;
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				
				t_input.setEnabled(false);
				b_send.setEnabled(false);
				b_exit.setEnabled(true);
				return;
			}
			printDisplay(inMsg);
			
		} catch (IOException e) {
			System.err.println("클라이언트 일반 수신 오류> "+ e.getMessage());
		}
	}
	
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 3));
		
		// 버튼에 액션 리스너 추가하여 접속하기 구현
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sa = t_hostAddr.getText();
				String sp = t_portNum.getText();
				connectToServer(sa, Integer.parseInt(sp));
				if (!isConnected) {
					System.out.println("서버 연결 실패");
					return;
				}
				
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				
				t_input.setEnabled(true);
				b_send.setEnabled(true);
				b_exit.setEnabled(false);
				
			}
		});
		
		// 버튼에 액션 리스너 추가하여 접속끊기 구현
		b_disconnect = new JButton("접속끊기");
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
		
		// 버튼에 액션 리스너 추가하여 종료 구현
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 프로그램 종료
				disconnect();
				System.exit(0);
			}
		});

		panel.add(b_connect);
		panel.add(b_disconnect);
		panel.add(b_exit);
		
		b_disconnect.setEnabled(false);
		
		return panel;
	}

	private void connectToServer(String serverAddress, int serverPort) {
		try {
			socket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			socket.connect(sa, 3000);
			
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
			((BufferedWriter)out).write("/uid:" + t_userID.getText() + "\n");
			out.flush();
			
			receiveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (receiveThread == Thread.currentThread()) {
						receiveMessage();
					}
				}
			});
			receiveThread.start();
			
		} catch (UnknownHostException e) {
			printDisplay("알 수 없는 서버");
			e.printStackTrace();
		} catch (SocketTimeoutException e){
			printDisplay("서버와의 연결 오류:Connect timed out");
		} catch (IOException e) {
			printDisplay("서버 연결 오류");
		} 
	}
	
	private void disconnect() {
		try {
			receiveThread = null;
			socket.close();
		} catch (IOException e1) {
			System.err.println("클라이언트 닫기 오류> " + e1.getMessage());
			System.exit(-1);
		}

	}
	
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new MultiTalk(serverAddress, serverPort);
	}
}
