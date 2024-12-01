package practice17;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
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
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ImgSender extends JFrame{
//	private JFrame frame;
	
//	private JTextArea t_display;
	private JTextPane t_display;
	private JTextField t_input;
	private DefaultStyledDocument document;
	
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private JButton b_send;
	private JButton b_select;
	
	private String serverAddress;
	private int serverPort;
	
//    private Reader in;
	private OutputStream out;
	private Socket socket;
	
	public ImgSender(String serverAddress, int serverPort) {
		super("Img Sender");
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
				
		buildGUI();
		
		setSize(400, 300);
		setLocation(100,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	private void buildGUI() {
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createInputPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel createDisplayPanel() {
		document = new DefaultStyledDocument(); 
		t_display = new JTextPane(document);
		// 편집 불가 상태
		t_display.setEditable(false);
		// 자동 줄바꿈
//		t_display.setLineWrap(true);
//		t_display.setWrapStyleWord(true);
		
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
				sendImage();
			}
		};
		
		// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
		t_input = new JTextField(30);
		t_input.addActionListener(listener);
		
		b_send = new JButton("보내기");
		// 보내기 버튼에 액션 리스너 추가하여 메시지 처리
		b_send.addActionListener(listener);
		
		b_select = new JButton("선택하기");
		b_select.addActionListener(new ActionListener() {
			
			JFileChooser chooser = new JFileChooser();
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"JPG & GIF & PNG Images",  // 파일 이름에 창에 출력될 문자열
						"jpg", "gif", "png");      // 파일 필터로 사용되는 확장자
				
				chooser.setFileFilter(filter);
				
				int ret = chooser.showOpenDialog(ImgSender.this);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(ImgSender.this, "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				t_input.setText(chooser.getSelectedFile().getAbsolutePath());
			}
			
		});
		
		panel.add(t_input, BorderLayout.CENTER);
//		panel.add(b_send, BorderLayout.EAST);
		JPanel p_button = new JPanel(new GridLayout(1, 0));
		p_button.add(b_select);
		p_button.add(b_send);
		panel.add(p_button, BorderLayout.EAST);
		
		t_input.setEnabled(false);
		b_select.setEnabled(false);
		b_send.setEnabled(false);
		
		panel.add(createControlPanel(), BorderLayout.SOUTH);
		
		return panel;
	}

//	private void sendMessage() {
//		// 메시지 가져오기		
//		String message = t_input.getText();
//		if (message.isEmpty()) return;
//		
//		try {
////			((BufferedWriter)out).write(message + "\n");
//
//			((DataOutputStream)out).writeUTF(message + "\n");
//			out.flush();
//			
//			// 화면 출력
//			t_display.append("나: " + message + "\n");
//		} catch (IOException e) {
//			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
//			System.exit(-1);
//		}
//		t_input.setText("");
//	}
	
	private void sendImage() {
		String filename = t_input.getText().strip();
		if (filename.isEmpty()) return;
		
		File file = new File(filename);
		if (!file.exists()) {
			printDisplay(">> 파일이 존재하지 않습니다: " + filename);
			return;
		}
		
//		((PrintWriter)out).println(filename);
		
		BufferedInputStream bis = null;
		
		try {
			((DataOutputStream)out).writeUTF(file.getName());
			((DataOutputStream)out).writeLong(file.length());
			
			bis = new BufferedInputStream(new FileInputStream(file));

			byte[] buffer = new byte[1024];
			int nRead;
		
			while ((nRead = bis.read(buffer)) != -1) {
				out.write(buffer, 0, nRead);
			}
			
//			out.close();
			out.flush();
			
			printDisplay(">> 전송을 완료했습니다: " + filename);
			t_input.setText("");
			
			ImageIcon icon = new ImageIcon(filename);
			printDisplay(icon);
			
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
	
	private void printDisplay(ImageIcon icon) {
		t_display.setCaretPosition(t_display.getDocument().getLength());
		
		if (icon.getIconWidth() > 400) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
		}
		t_display.insertIcon(icon);
		
		printDisplay("");
		t_input.setText("");
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
				b_select.setEnabled(true);
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
				b_select.setEnabled(false);
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
		new ImgSender(serverAddress, serverPort);
	}
}
