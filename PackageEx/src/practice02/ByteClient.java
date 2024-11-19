package practice02;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ByteClient {
	private JFrame frame;
	private JTextArea t_display;
	private JTextField t_input;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private JButton b_send;
	
	private String serverAddress;
	private int serverPort;
	
    private OutputStream out;
	
	private Socket socket;
	
	public ByteClient(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		frame = new JFrame("ByteClientGUI");
		
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
		t_display.append("나: " + t_input.getText() + "\n");
		int message = Integer.parseInt(t_input.getText());
		System.out.println(message);
		
		// 서버로 메시지 보내기
		try {
			out.write(message);
			out.flush();
		} catch (IOException e1) {
			System.err.println("클라이언트 메시지 전송 실패 " + e1.getMessage());
		}
		
		// 메시지 필드 초기화
		t_input.setText("");
		
		// 화면 출력
 
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
			}
		});
		
		// 버튼에 액션 리스너 추가하여 접속끊기 구현
		b_disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		});
		 
		// 버튼에 액션 리스너 추가하여 종료 구현
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 프로그램 종료
				System.exit(0);
			}
		});

		return panel;
	}
	
	private void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
			out = socket.getOutputStream();
			
			b_connect.setEnabled(false);
			b_disconnect.setEnabled(true);
			b_exit.setEnabled(false);
		} catch (IOException e) {
			System.err.println("클라이언트 접속 오류> " + e.getMessage());
			System.exit(-1);
		}
	}
	
	private void disconnect() {
		try {
			socket.close();
			out.close();
		} catch (IOException e1) {
			System.err.println("클라이언트 닫기 오류> " + e1.getMessage());
			System.exit(-1);
		}
		b_connect.setEnabled(true);
		b_disconnect.setEnabled(false);
		b_exit.setEnabled(true);

	}
}
