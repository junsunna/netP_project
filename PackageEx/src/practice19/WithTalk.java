// 1911249 나준선
package practice19;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class WithTalk extends JFrame {
//	private JTextArea t_display;
	private JTextField t_input;
	private JTextPane t_display;
	
	private JButton b_connect, b_disconnect, b_exit, b_send, b_select, b_sendFile;
	private JLabel l_id, l_serverAddress, l_serverPort;
	private JTextField t_userID, t_hostAddr, t_portNum;
	private DefaultStyledDocument document;
	
	private String serverAddress;
	private int serverPort;
	private String uid;
	
	private Socket socket;
	private ObjectOutputStream out;
	private Thread receiveThread = null;
	

	public WithTalk(String serverAddress, int serverPort) {
		super("1911249 With Talk");
		this.serverAddress = serverAddress; 
		this.serverPort = serverPort;
		
		buildGUI();
		
		setSize(500, 330);
		setLocation(100,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	private void buildGUI() {
		add(createDisplayPanel(), BorderLayout.CENTER);
		
		JPanel p_input = new JPanel(new GridLayout(3, 0));
		p_input.add(createInputPanel());
		p_input.add(createInfoPanel());
		p_input.add(createControlPanel());
		add(p_input, BorderLayout.SOUTH);
		
	}
	
	private JPanel createDisplayPanel() {
		JPanel p = new JPanel(new BorderLayout());
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);		
		t_display.setEditable(false);
		
		p.add(new JScrollPane(t_display), BorderLayout.CENTER);
		return p;
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
		
		b_sendFile = new JButton("파일보내기");
		b_sendFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendFile();
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
		
		b_select = new JButton("선택하기");
		b_select.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"JPG & GIF & PNG Images",
						"jpg", "gif", "png");
				chooser.setFileFilter(filter);
				
				int ret = chooser.showOpenDialog(WithTalk.this);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(WithTalk.this,  "파일을 선택하지 않았습니다.");
					return;
				}
				
				t_input.setText(chooser.getSelectedFile().getAbsolutePath());
				sendImage();
			}
		});
		
		panel.add(t_input, BorderLayout.CENTER);
		JPanel p_button = new JPanel(new GridLayout(1,0));
		p_button.add(b_select);
		p_button.add(b_sendFile);
		p_button.add(b_send);
		panel.add(p_button, BorderLayout.EAST);
		
		t_input.setEnabled(false);
		b_select.setEnabled(false);
		b_send.setEnabled(false);
		b_sendFile.setEnabled(false);
		
		return panel;
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
	
	
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 3));
		
		// 버튼에 액션 리스너 추가하여 접속하기 구현
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WithTalk.this.serverAddress = t_hostAddr.getText();
				WithTalk.this.serverPort = Integer.parseInt(t_portNum.getText());
				
				try {
					connectToServer();
					sendUserID();
				} catch (UnknownHostException e1) {
					printDisplay("서버 주소와 포트번호를 확인하세요: " + e1.getMessage());
				} catch (IOException e1) {
					printDisplay("서버와의 연결 오류: " + e1.getMessage());
					return;
				}
				
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				
				t_input.setEnabled(true);
				b_send.setEnabled(true);
				b_exit.setEnabled(false);
				b_sendFile.setEnabled(true);
				b_select.setEnabled(true);
				
				t_userID.setEditable(false);
				t_hostAddr.setEditable(false);
				t_portNum.setEditable(false);
				
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
				b_sendFile.setEnabled(false);
				b_select.setEnabled(false);
				
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

	private void connectToServer() throws UnknownHostException, IOException {
		try {
			socket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			socket.connect(sa, 3000);
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
//			((BufferedWriter)out).write("/uid:" + t_userID.getText() + "\n");
			out.flush();
			
			receiveThread = new Thread(new Runnable() {
				private ObjectInputStream in;
				
				private void receiveMessage() {
					try {
//						String inMsg = ((BufferedReader)in).readLine();
						ObjMsg inMsg = (ObjMsg)in.readObject();
						if (inMsg == null) {
							disconnect();
							printDisplay("서버 연결 끊김");
//							t_display.append("서버 연결 종료\n");
//							receiveThread = null;
//							b_connect.setEnabled(true);
//							b_disconnect.setEnabled(false);
//							
//							t_input.setEnabled(false);
//							b_send.setEnabled(false);
//							b_exit.setEnabled(true);
							return;
						}
						
						switch (inMsg.mode) {
						case ObjMsg.MODE_TX_STRING :
							printDisplay(inMsg.userID + ": " + inMsg.message);
							break;
						case ObjMsg.MODE_TX_IMAGE :
							printDisplay(inMsg.userID + ": " + inMsg.message);
							printDisplay(inMsg.image);
							break;
						case ObjMsg.MODE_TX_FILE :
							String fileName = inMsg.filename;
							if (inMsg.filename != null) {
								printDisplay(inMsg.userID + ": " + fileName);
								
								File file = new File(fileName);
								BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
								for (byte[] chunk : inMsg.file) {
									bos.write(chunk);
								}
								bos.close();	
							}
							else {
								printDisplay("수신한 파일의 이름이 없습니다.");
							}
							
							break;
						}
						
						
						
					} catch (IOException e) {
//						System.err.println("클라이언트 일반 수신 오류> "+ e.getMessage());
						printDisplay("연결을 종료했습니다.");
					} catch (ClassNotFoundException e) {
						printDisplay("잘못된 객체가 전달되었습니다.");
					}
				}
				
				@Override
				public void run() {
					try {
						in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					} catch (IOException e) {
						printDisplay("입력 스트림이 열리지 않음");
					}
					
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
		send(new ObjMsg(uid, ObjMsg.MODE_LOGOUT));
		
		try {
			receiveThread = null;
			socket.close();
		} catch (IOException e) {
			System.err.println("클라이언트 닫기 오류> " + e.getMessage());
			System.exit(-1);
		}

	}
	
	private void send(ObjMsg msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
		}
	}
	
	private void sendMessage() {
		// 메시지 가져오기		
		String message = t_input.getText();
		if (message.isEmpty()) return;
		
//		try {
//			((BufferedWriter)out).write(message + "\n");
//			out.flush();
//		} catch (IOException e) {
//			System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
//			System.exit(-1);
//		}
		send(new ObjMsg(uid, ObjMsg.MODE_TX_STRING, message));
		
		// 메시지 필드 초기화
		t_input.setText("");
	}
	private void sendImage() {
		String filename = t_input.getText().strip();
		if (filename.isEmpty()) return;
		
		File file = new File(filename);
		if (!file.exists()) {
			printDisplay(">> 파일이 존재하지 않습니다: " + filename);
			return;
		}
		
		ImageIcon icon = new ImageIcon(filename);
		send(new ObjMsg(uid, ObjMsg.MODE_TX_IMAGE, file.getName(), icon));
		
		t_input.setText("");
	}
	
	private void sendFile() {
		String filename = t_input.getText().strip();
		if (filename.isEmpty()) return;
		
		File file = new File(filename);
		if (!file.exists()) {
			printDisplay(">> 파일이 존재하지 않습니다: " + filename + "\n");
			return;
		}
		
		Vector<byte[]> vbyte = new Vector<>();
		
		BufferedInputStream bis = null;
		
		try {
			bis = new BufferedInputStream(new FileInputStream(file));

			byte[] buffer = new byte[1024];
			int nRead;
		
			while ((nRead = bis.read(buffer)) != -1) {
				byte[] temp = new byte[nRead];
				System.arraycopy(buffer, 0, temp, 0, nRead);
				vbyte.add(temp);
			}
			
			send(new ObjMsg(uid, ObjMsg.MODE_TX_FILE, filename, vbyte));
			printDisplay(">> 전송을완료했습니다: " + filename + "\n");
			t_input.setText("");
		} catch (UnsupportedEncodingException e) {
			printDisplay(">> 인코딩 형식을 알 수 없습니다: " + e.getMessage() + "\n");
			return;
		} catch (FileNotFoundException e) {
			printDisplay(">> 파일이 존재하지 않습니다: " + e.getMessage() + "\n");
			return;
		} catch (IOException e) {
			printDisplay(">> 파일을 읽을 수 없습니다: " + e.getMessage() + "\n");
		}finally {
			try {
				if (bis != null) bis.close();
			} catch (IOException e) {
				printDisplay(">> 파일을 닫을 수 없습니다. " + e.getMessage() + "\n");
				return;
			}
		}
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
	}
	
	
	private void sendUserID() {
		uid = t_userID.getText();
		send(new ObjMsg(uid, ObjMsg.MODE_LOGIN));
	}
	
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new WithTalk(serverAddress, serverPort);
	}
}
