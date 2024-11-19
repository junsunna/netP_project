package practice06;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MsgClientGUI {
	private JFrame frame;
	
	private JTextArea t_display;
	private JTextField t_input;
	
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private JButton b_send;
	
	private String serverAddress;
	private int serverPort;
	
    private PrintWriter out;
	private Socket socket;
	
	public MsgClientGUI(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		frame = new JFrame("MsgClient GUI");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(100,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		frame.add(createInputPanel(), BorderLayout.SOUTH);
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
		t_input = new JTextField(10);
		b_send = new JButton("보내기");

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(t_input, BorderLayout.CENTER);
		panel.add(b_send, BorderLayout.EAST);
		panel.add(createControlPanel(), BorderLayout.SOUTH);
		
		// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
		t_input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				b_send.doClick();
			}
		});
		
		// 보내기 버튼에 액션 리스너 추가하여 메시지 처리
		b_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				
			}
		});
		
		return panel;
	}

	private void sendMessage() {
		// 메시지 가져오기		
//		String textMessage = t_input.getText()+ '\n';
		String textMessage = t_input.getText();
		if (textMessage.isEmpty()) return;
		
		// 서버로 메시지 보내기
		out.println(textMessage);
//		try {
//			out.write(textMessage);
//			out.flush();
//		} catch (IOException e1) {
//			System.err.println("클라이언트 일반 전송 오류 " + e1.getMessage());
//		}
		
		// 화면 출력
		t_display.append("나: " + textMessage + "\n");
		
		// 메시지 필드 초기화
		t_input.setText("");

	}
	
	private JPanel createControlPanel() {
		b_connect = new JButton("접속하기");
		b_disconnect = new JButton("접속끊기");
		b_exit = new JButton("종료하기");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 3));
		panel.add(b_connect);
		panel.add(b_disconnect);
		panel.add(b_exit);
		b_connect.setEnabled(false);
		b_exit.setEnabled(false);
		connectToServer();
		
		// 버튼에 액션 리스너 추가하여 접속하기 구현
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				
				t_input.setEnabled(true);
				b_send.setEnabled(true);
				b_exit.setEnabled(false);
				
			}
		});
		
		// 버튼에 액션 리스너 추가하여 접속끊기 구현
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
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 프로그램 종료
				disconnect();
				System.exit(0);
			}
		});

		return panel;
	}
	
	private void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
			
//			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
//			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//			out = socket.getOutputStream();
		} catch (UnknownHostException e) {
			System.err.println("알 수 없는 서버> " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("클라이언트 접속 오류> " + e.getMessage());
		}

	}
	
	private void disconnect() {
		try {
			out.close();
			socket.close();
		} catch (IOException e1) {
			System.err.println("클라이언트 닫기 오류> " + e1.getMessage());
			System.exit(-1);
		}

	}
	
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new MsgClientGUI(serverAddress, serverPort);
	}
}
