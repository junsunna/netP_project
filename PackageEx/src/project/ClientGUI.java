package project;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	private Bear bear;
	private Rabbit rabbit;
	private MainMap mainMap;
	private JLabel m_map;
	
	private JLabel backgroundLabel, back_cake, back_choice;
	private JTextPane t_display;
	private ImageIcon i_cake, i_choice, i_startB;
	
	private JButton b_gameStart, b_sound;
	private DefaultStyledDocument document;
	
	private String serverAddress;
	private int serverPort;
	
	private Socket socket;
	private DataOutputStream out;
	private Thread receiveThread = null;
    private String uid;

	public ClientGUI(String serverAddress, int serverPort) {
		super("Client GUI");
		this.serverAddress = serverAddress; 
		this.serverPort = serverPort;

		setLayout(null);
		buildGUI();
		
		setSize(715, 738);
		setLocation(500,150);

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void buildGUI() {
		createDisplayPanel();
		createControlPanel();
	}
	
	private void createDisplayPanel() {
		i_startB = new ImageIcon("images/background/b_start.png");
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
				    	uid = "Bear";
				        // Bear 객체 생성
				        bear = new Bear();
				    	rabbit = new Rabbit();
				        // 시작 화면 삭제
				        remove(backgroundLabel);
				        // 플레이 화면 생성
				        mainMap = new MainMap();
				        m_map = mainMap.getMainMap();
				        m_map.add(bear.getCharacter());
				        m_map.add(rabbit.getCharacter());
				        add(m_map);
				        
						setSize(1100, 738);
				        revalidate();
				        repaint();				        
				        
				        try {
							connectToServer(rabbit);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				        

		                
				        // 키 이벤트 처리
				        addKeyListener(new KeyAdapter() {
				            @Override
				            public void keyPressed(KeyEvent e) {
				                int keyCode = e.getKeyCode();
				                bear.idle = false;
				                	switch (keyCode) {
				                	 	case KeyEvent.VK_LEFT :
				                	 		send(KeyMsg.KEY_LEFT);
						                    bear.left();
						                    break;
						                case KeyEvent.VK_RIGHT :
						                	send(KeyMsg.KEY_RIGHT);
						                	bear.right();
						                	break;
						                case KeyEvent.VK_SPACE:
						                	send(KeyMsg.KEY_SPACE);
						                    bear.up();
						                    break;
						                case KeyEvent.VK_A:
						                	send(KeyMsg.KEY_A);
						                    bear.dead();
						                    break;
				                	}
				                	m_map.repaint();
				            }
				            @Override
				            public void keyReleased(KeyEvent e) {
				                int keyCode = e.getKeyCode();

				                bear.initIndex();
				                if (keyCode == KeyEvent.VK_LEFT) {
				                	send(KeyMsg.KEY_LEFT_RELEASED);
				                	bear.left_released();
				                	bear.idle();
				                } else if (keyCode == KeyEvent.VK_RIGHT) {
				                	send(KeyMsg.KEY_RIGHT_RELEASED);
				                	bear.right_released();
				                	bear.idle();
				                } 
				                m_map.repaint();
				            }
				        });

				        // 포커스 요청
				        requestFocusInWindow();
				    }

				});
				b_rabbit.addActionListener(new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				        // Bear 객체 생성
				    	uid = "Rabbit";
				        System.out.println("UID set to: " + uid); // UID 확인
				    	rabbit = new Rabbit();
				    	bear = new Bear();
				        // 시작 화면 삭제
				        remove(backgroundLabel);
				        // 플레이 화면 생성
				        mainMap = new MainMap();
				        m_map = mainMap.getMainMap();
				        m_map.add(rabbit.getCharacter());
				        m_map.add(bear.getCharacter());
				        add(m_map);
				        
						setSize(1100, 738);				        
				        revalidate();
				        repaint();
				    	
				        try {
							connectToServer(bear);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

		                
				        // 키 이벤트 처리
				        addKeyListener(new KeyAdapter() {
				            @Override
				            public void keyPressed(KeyEvent e) {
				                int keyCode = e.getKeyCode();
				                rabbit.idle = false;
				                	switch (keyCode) {
				                	 	case KeyEvent.VK_LEFT :
				                	 		send(KeyMsg.KEY_LEFT);
				                	 		rabbit.left();
						                    break;
						                case KeyEvent.VK_RIGHT :
						                	send(KeyMsg.KEY_RIGHT);
						                	rabbit.right();
						                	break;
						                case KeyEvent.VK_SPACE:
						                	send(KeyMsg.KEY_SPACE);
						                	rabbit.up();
						                    break;
						                case KeyEvent.VK_A:
						                	send(KeyMsg.KEY_A);
						                	rabbit.dead();
						                    break;
				                	}
				                	m_map.repaint();
				            }
				            @Override
				            public void keyReleased(KeyEvent e) {
				                int keyCode = e.getKeyCode();
				                rabbit.initIndex();
				                if (keyCode == KeyEvent.VK_LEFT) {
				                	send(KeyMsg.KEY_LEFT_RELEASED);
				                	rabbit.left_released();
				                	rabbit.idle();
				                } else if (keyCode == KeyEvent.VK_RIGHT) {
				                	send(KeyMsg.KEY_RIGHT_RELEASED);
				                	rabbit.right_released();
				                	rabbit.idle();
				                } 
				                m_map.repaint();
				            }
				        });

				        // 포커스 요청
				        requestFocusInWindow();
				    }
				});
			}
		});
		b_sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("사운드 설정을 변경합니다!");
            }
        });
	}
	
	private void connectToServer(Moveable character) throws UnknownHostException, IOException {
	    try {
	        socket = new Socket();
	        SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
	        socket.connect(sa, 3000);
	        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

	        System.out.println("Sending UID: " + uid); // UID 확인
            out.writeUTF(uid);
            out.flush();
	        
	        receiveThread = new Thread(new Runnable() {
	            private DataInputStream in;
	            String inMsg;

	            private void receiveMessage() {
	                try {
	                    inMsg = in.readUTF();
	                    inMsg = inMsg.trim().toUpperCase();
	                    KeyMsg key = KeyMsg.valueOf(inMsg);

	                    switch (key) {
	                        case KEY_LEFT:
	                            character.left();
	                            break;
	                        case KEY_RIGHT:
	                            character.right();
	                            break;
	                        case KEY_SPACE:
	                            character.up();
	                            break;
	                        case KEY_A:
	                            character.dead();
	                            break;
	                        case KEY_LEFT_RELEASED:
	                        	character.left_released();
	                            character.idle();
	                            character.initIndex();
	                            break;
	                        case KEY_RIGHT_RELEASED:
	                        	character.right_released();
	                            character.idle();
	                            character.initIndex();
	                            break;
	                        default:
	                            System.err.println("Unhandled KeyMsg: " + key);
	                            break;
	                    }

	                    m_map.repaint(); // 화면 갱신
	                } catch (IOException e) {
	                    System.err.println("클라이언트 일반 수신 오류> " + e.getMessage());
	                } catch (IllegalArgumentException e) {
	                    System.err.println("Invalid KeyMsg received: " + inMsg);
	                }
	            }

	            @Override
	            public void run() {
	                try {
	                    in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	                } catch (IOException e) {
	                    e.printStackTrace();
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
	    } catch (SocketTimeoutException e) {
	        printDisplay("서버와의 연결 오류: Connect timed out");
	    } catch (IOException e) {
	        printDisplay("서버 연결 오류");
	    }
	}

	
	private void disconnect() {
//		send(new KeyMsg(uid, KeyMsg.MODE_LOGOUT));
		
		try {
			receiveThread = null;
			socket.close();
		} catch (IOException e) {
			System.err.println("클라이언트 닫기 오류> " + e.getMessage());
			System.exit(-1);
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
	

	
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		new ClientGUI(serverAddress, serverPort);
	}
}
