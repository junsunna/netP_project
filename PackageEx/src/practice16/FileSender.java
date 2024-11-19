package practice16;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FileSender {
	private JFrame frame;
	
	private JTextArea t_display;
	private JTextField t_input;
	
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private JButton b_send;
	
	private String serverAddress;
	private int serverPort;
	
//    private Reader in;
	private OutputStream out;
	private Socket socket;
	
	public FileSender(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
		frame = new JFrame("File Sender");
		
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
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendFile();
			}
		};
		
		// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
		t_input = new JTextField(30);
		t_input.addActionListener(listener);
		
		b_send = new JButton("보내기");
		// 보내기 버튼에 액션 리스너 추가하여 메시지 처리
		b_send.addActionListener(listener);
		
		panel.add(t_input, BorderLayout.CENTER);
		panel.add(b_send, BorderLayout.EAST);
		panel.add(createControlPanel(), BorderLayout.SOUTH);
		
		return panel;
	}

	private void sendMessage() {
		// 메시지 가져오기		
		String message = t_input.getText();
		if (message.isEmpty()) return;
		
		try {
//			((BufferedWriter)out).write(message + "\n");

			((DataOutputStream)out).writeUTF(message + "\n");
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
	
	private void sendFile() {
		String filename = t_input.getText().strip();
		if (filename.isEmpty()) return;
		
		File file = new File(filename);
		if (!file.exists()) {
			t_display.append(">> 파일이 존재하지 않습니다: " + filename + "\n");
			return;
		}
		
//		((PrintWriter)out).println(filename);
		
		BufferedInputStream bis = null;
		
		try {
			((DataOutputStream)out).writeUTF(file.getName());
			
			bis = new BufferedInputStream(new FileInputStream(file));

			byte[] buffer = new byte[1024];
			int nRead;
		
			while ((nRead = bis.read(buffer)) != -1) {
				out.write(buffer, 0, nRead);
			}
			
			out.close();
			
			printDisplay(">> 전송을 완료했습니다: " + filename);
			t_input.setText("");
		} catch (FileNotFoundException e) {
			printDisplay(">> 파일이 존재하지 않습니다: " + e.getMessage() + "\n");
			return;
		} catch (IOException e) {
			printDisplay(">> 파일을 읽을 수 없습니다. " + e.getMessage() + "\n");
			return;
		} finally {
			try {
				if (bis != null) bis.close();
			} catch (IOException e) {
				printDisplay(">> 파일을 닫을 수 없습니다. " + e.getMessage() + "\n");
				return;
			}
		}
	}
	
	private void printDisplay(String msg) {
		t_display.append(msg + "\n");
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}
	
//	private void receiveMessage() {
//		try {
//			String inMsg = ((BufferedReader)in).readLine();
//			t_display.append("서버:\t" + inMsg + "\n");
//		} catch (IOException e) {
//			System.err.println("클라이언트 일반 수신 오류> "+ e.getMessage());
//		}
//	}
	
	private JPanel createControlPanel() {
		b_connect = new JButton("접속하기");
		b_disconnect = new JButton("접속끊기");
		b_exit = new JButton("종료하기");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 3));
		panel.add(b_connect);
		panel.add(b_disconnect);
		panel.add(b_exit);
		b_connect.setEnabled(true);
		b_exit.setEnabled(true);
		b_disconnect.setEnabled(false);
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
//			out = new PrintWriter(new BufferedWriter(
//					new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
			
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
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
		new FileSender(serverAddress, serverPort);
	}
}
