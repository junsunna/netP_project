package practice10;

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
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScoreClientGUI {
	private JFrame frame;
	
//	private JTextArea t_display;
	private JTextField t_name;
	
	private JLabel l_grade;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
//	private JButton b_send;
	
	private String serverAddress;
	private int serverPort;
	
    private Reader in;
	private Writer out;
	private Socket socket;
	
	public ScoreClientGUI(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		frame = new JFrame("ScoreClient GUI");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(100,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	private void buildGUI() {
		frame.add(createInputPanel());
		frame.add(createControlPanel(), BorderLayout.SOUTH);
		
	}

//	private JPanel createDisplayPanel() {
//		t_display = new JTextArea(10, 30);
//		
//		// 편집 불가 상태
//		t_display.setEditable(false);
//		// 자동 줄바꿈
//		t_display.setLineWrap(true);
//		t_display.setWrapStyleWord(true);
//		
//		// JScrollPane 스크롤 연결
//		JScrollPane scrollPane = new JScrollPane(t_display);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		
//		//패널 생성 및 구성
//		JPanel panel = new JPanel();
//		panel.setLayout(new BorderLayout());
//		panel.add(scrollPane, BorderLayout.CENTER);
//		
//		return panel;
//	}
	
	private JPanel createInputPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		t_name = new JTextField(10);
		// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
		t_name.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				receiveMessage();
			}
		});
		t_name.setEnabled(false);
		
		l_grade = new JLabel("학점 확인");
		
		panel.add(new JLabel("이름: "));
		panel.add(t_name);
		panel.add(l_grade);
		
		return panel;
	}

	private void sendMessage() {
		// 메시지 가져오기		
		String message = t_name.getText().trim();
		if (message.isEmpty()) return;
		
		try {
			((BufferedWriter)out).write(message + "\n");
			out.flush();
			
			// 화면 출력
//			t_display.append("나: " + message + "\n");
		} catch (IOException e) {
			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
			System.exit(-1);
		}
	}
	
	private void receiveMessage() {
		try {
			String grade = ((BufferedReader)in).readLine();
			l_grade.setText("학점 : " + grade);
		} catch (IOException e) {
			System.err.println("클라이언트 일반 수신 오류> "+ e.getMessage());
		}
	}
	
	private JPanel createControlPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 3));
		
		b_connect = new JButton("접속하기");
		// 버튼에 액션 리스너 추가하여 접속하기 구현
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				
				t_name.setEnabled(true);
				b_exit.setEnabled(false);
				
			}
		});
		
		b_disconnect = new JButton("접속끊기");
		// 버튼에 액션 리스너 추가하여 접속끊기 구현
		b_disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				
				t_name.setEnabled(false);
//				b_send.setEnabled(false);
				b_exit.setEnabled(true);
			}
		});
		
		b_exit = new JButton("종료하기");
		// 버튼에 액션 리스너 추가하여 종료 구현
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
		b_connect.setEnabled(false);
		b_exit.setEnabled(false);
		connectToServer();

		return panel;
	}
	
	private void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
//			out = new PrintWriter(new BufferedWriter(
//					new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
			
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
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
		new ScoreClientGUI(serverAddress, serverPort);
	}
}
