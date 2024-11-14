package project;

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
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ClientGUI extends JFrame {
//	private JTextArea t_display;
	private JLabel backgroundLabel, back_cake, back_choice;
	private JTextField t_input;
	private JTextPane t_display;
	private ImageIcon i_cake, i_choice;
	
	private JButton b_gameStart, b_sound;
	private DefaultStyledDocument document;
	
	private String serverAddress;
	private int serverPort;
	private String uid;
	
	private Socket socket;
	private ObjectOutputStream out;
	private Thread receiveThread = null;
	

	public ClientGUI(String serverAddress, int serverPort) {
		super("Client GUI");
		this.serverAddress = serverAddress; 
		this.serverPort = serverPort;

		setLayout(null);
		
		buildGUI();
		
		setSize(715, 738);
		setLocation(100,300);

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	private void buildGUI() {
		createDisplayPanel();
		createControlPanel();
	}
	
	private void createDisplayPanel() {
		ImageIcon i_startB = new ImageIcon("images/background/b_start.png");
		backgroundLabel = new JLabel(i_startB);
        backgroundLabel.setBounds(0, 0, i_startB.getIconWidth(), i_startB.getIconHeight());
        backgroundLabel.setLayout(null);
        add(backgroundLabel);
        
        i_cake = new ImageIcon("images/background/b_cake.png");
        back_cake = new JLabel(i_cake);
        back_cake.setBounds(20, 20, i_cake.getIconWidth(), i_cake.getIconHeight());
        backgroundLabel.add(back_cake);
        
        ImageIcon i_play = new ImageIcon("images/button/playButton.png");
        ImageIcon i_sound = new ImageIcon("images/button/soundButton.png");
        
        b_gameStart = new JButton(i_play);
        b_gameStart.setBounds(260, 300, i_play.getIconWidth(), i_play.getIconHeight());
        b_gameStart.setBorderPainted(false);  // 버튼 테두리 제거
        b_gameStart.setContentAreaFilled(false);  // 버튼 배경 제거


        b_sound = new JButton(i_sound);
        b_sound.setBounds(520, 20, i_play.getIconWidth(), 100);
        b_sound.setBorderPainted(false);  // 버튼 테두리 제거
        b_sound.setContentAreaFilled(false);  // 버튼 배경 제거
        b_sound.setFocusPainted(false);
//        
        backgroundLabel.add(b_gameStart);
        backgroundLabel.add(b_sound); 
        
        setVisible(true);
	}
	
//	private JPanel createInputPanel() {
//		
//		return panel;
//	}

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
	
	
	private void createControlPanel() {
		b_gameStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon i_bearButton = new ImageIcon("images/button/b_bear.png");
				ImageIcon i_rabbitButton = new ImageIcon("images/button/b_rabbit.png");
				i_choice = new ImageIcon("images/background/b_choice.png");
				
				back_choice = new JLabel(i_choice);
				JButton b_bear = new JButton(i_bearButton);
				JButton b_rabbit = new JButton(i_rabbitButton);
				
				back_choice.setBounds(230, 20, i_choice.getIconWidth(), i_choice.getIconHeight());
				b_bear.setBounds(50, 240, i_bearButton.getIconWidth(), i_bearButton.getIconHeight());
				b_rabbit.setBounds(500, 240, i_rabbitButton.getIconWidth(), i_rabbitButton.getIconHeight());
				
				b_bear.setBorderPainted(false);  // 버튼 테두리 제거
				b_bear.setContentAreaFilled(false);  // 버튼 배경 제거
		        
				b_rabbit.setBorderPainted(false);  // 버튼 테두리 제거
				b_rabbit.setContentAreaFilled(false);  // 버튼 배경 제거
				
				
				backgroundLabel.add(b_bear);
				backgroundLabel.add(b_rabbit);
				backgroundLabel.add(back_choice);
				backgroundLabel.remove(b_gameStart);
				backgroundLabel.remove(back_cake);
				
				backgroundLabel.revalidate();
				backgroundLabel.repaint();
				
				b_bear.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
							try {
							connectToServer();
	//						sendUserID();
						} catch (UnknownHostException e1) {
							printDisplay("서버 주소와 포트번호를 확인하세요: " + e1.getMessage());
						} catch (IOException e1) {
							printDisplay("서버와의 연결 오류: " + e1.getMessage());
							return;
						}
					}
				});
				b_rabbit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
							try {
							connectToServer();
	//						sendUserID();
						} catch (UnknownHostException e1) {
							printDisplay("서버 주소와 포트번호를 확인하세요: " + e1.getMessage());
						} catch (IOException e1) {
							printDisplay("서버와의 연결 오류: " + e1.getMessage());
							return;
						}
					}
				});
			}
			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					connectToServer();
////					sendUserID();
//				} catch (UnknownHostException e1) {
//					printDisplay("서버 주소와 포트번호를 확인하세요: " + e1.getMessage());
//				} catch (IOException e1) {
//					printDisplay("서버와의 연결 오류: " + e1.getMessage());
//					return;
//				}
//				
//				b_gameStart.setEnabled(false);
//			}
		});
		b_sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("사운드 설정을 변경합니다!");
            }
        });
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
						ObjMessage inMsg = (ObjMessage)in.readObject();
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
						case ObjMessage.MODE_TX_STRING :
							printDisplay(inMsg.userID + ": " + inMsg.message);
							break;
						case ObjMessage.MODE_TX_IMAGE :
							printDisplay(inMsg.userID + ": " + inMsg.message);
							printDisplay(inMsg.image);
							break;
						case ObjMessage.MODE_TX_FILE :
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
		send(new ObjMessage(uid, ObjMessage.MODE_LOGOUT));
		
		try {
			receiveThread = null;
			socket.close();
		} catch (IOException e) {
			System.err.println("클라이언트 닫기 오류> " + e.getMessage());
			System.exit(-1);
		}

	}
	
	private void send(ObjMessage msg) {
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
		send(new ObjMessage(uid, ObjMessage.MODE_TX_STRING, message));
		
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
		send(new ObjMessage(uid, ObjMessage.MODE_TX_IMAGE, file.getName(), icon));
		
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
			
			send(new ObjMessage(uid, ObjMessage.MODE_TX_FILE, filename, vbyte));
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
	
	
//	private void sendUserID() {
//		uid = t_userID.getText();
//		send(new ObjMessage(uid, ObjMessage.MODE_LOGIN));
//	}
	
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new ClientGUI(serverAddress, serverPort);
	}
}
