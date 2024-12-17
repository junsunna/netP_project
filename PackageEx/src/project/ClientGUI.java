package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;

public class ClientGUI extends JFrame {
	private Bear bear;
	private Rabbit rabbit;
	private MainMap mainMap;
	private JPanel m_map;
	private OptionPane oPane;
	private boolean toggle = true;
	private JLabel backgroundLabel, back_cake, back_choice, back_wait;
	private ImageIcon i_cake, i_choice, i_startB;
	
	private JButton b_gameStart, b_sound;
	
	private String serverAddress;
	private int serverPort;
	
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private Thread receiveThread = null;
    private String uid;

    private SoundPlayer startSound, mainSound, buttonSound;

    int panelWidth = 715; 
    int mapWidth = 1800;
    int screenCenterX = panelWidth / 2;
	int mapX = 0; // 맵의 X 좌표
    
	public ClientGUI(String serverAddress, int serverPort) {
		super("BunnyBearClient");
		this.serverAddress = serverAddress; 
		this.serverPort = serverPort;

		startSound = new SoundPlayer();
		startSound.loadSound("audios/startBGM.wav"); // 사운드 파일 경로 설정
		mainSound = new SoundPlayer();
        mainSound.loadSound("audios/mainBGM.wav"); // 사운드 파일 경로 설정
	    new Thread(() -> {
	    	startSound.playSound();     // 사운드 재생
	    	startSound.loopSound(); // 사운드 무한 반복
	    }).start();
		buttonSound = new SoundPlayer();
		buttonSound.loadSound("audios/button.wav"); // 사운드 파일 경로 설정
        
        // 플레이 화면 생성
        mainMap = new MainMap();
        m_map = mainMap.getMainMap();
        oPane = new OptionPane(mainSound);
        
        rabbit = new Rabbit(mainMap, oPane);
        bear = new Bear(mainMap, oPane);
        
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
	
	
	private void createControlPanel() {
		b_gameStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    new Thread(() -> buttonSound.playSound()).start();
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
				
				ImageIcon i_wait = new ImageIcon("images/button/wait.png");
				back_wait = new JLabel(i_wait);
				back_wait.setBounds(200, 280, i_wait.getIconWidth(), i_wait.getIconHeight());
				
				b_bear.addActionListener(new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {

						backgroundLabel.add(back_wait);
						backgroundLabel.setComponentZOrder(back_wait, 0);
						backgroundLabel.revalidate();
						backgroundLabel.repaint();


					    new Thread(() -> buttonSound.playSound()).start();
					    
						

				        new Thread(() -> {
				    	uid = "Bear";

						try {
							
								connectToServer(rabbit);
	
				                // 서버 연결이 완료된 후 UI 업데이트 (Swing 스레드 사용)
				                SwingUtilities.invokeLater(() -> {
							        remove(backgroundLabel);
							    	oPane.setMainFrame(ClientGUI.this); // 현재 프레임 참조 전달
							        JComponent glassPane = (JComponent) getGlassPane(); // GlassPane 가져오기
							        glassPane.setLayout(null); // 레이아웃 설정
							        glassPane.setBounds(0, 0, 1100, 738); // 크기 명시적으로 설정
							        
							        oPane.getPane().setBounds(0, 0, 1100, 738); // OptionPane 크기 설
							        
							        glassPane.add(oPane.getPane()); // OptionPane 추가
							        glassPane.setVisible(true); // GlassPane 활성화
							        
							        add(oPane.getPane());
							    	// 시작 화면 삭제
		
							        bear.setPlayer(true);
		
							    	
							        m_map.add(bear.getCharacter());
							        m_map.add(rabbit.getCharacter());
							        add(m_map);
		
									setSize(1100, 738);
							        revalidate();
							        repaint();	
								    startSound.stopSound();
								    new Thread(() -> {
								        mainSound.playSound();     // 사운드 재생
								        mainSound.loopSound(); // 사운드 무한 반복
								    }).start();
				                });
							} catch (UnknownHostException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
				        }).start(); // 새로운 스레드 시작


	

		                
				        // 키 이벤트 처리
				        addKeyListener(new KeyAdapter() {
				            @Override
				            public void keyPressed(KeyEvent e) {
				            	if (bear.isDead)return; 
				                int keyCode = e.getKeyCode();
				                bear.idle = false;
				                	switch (keyCode) {
				                	 	case KeyEvent.VK_LEFT :
				                	 		send(KeyMsg.KEY_LEFT);
						                    bear.left();
//						                    updateMap();
						                    break;
						                case KeyEvent.VK_RIGHT :
						                	send(KeyMsg.KEY_RIGHT);
						                	bear.right();
//						                	updateMap();
						                	break;
						                case KeyEvent.VK_SPACE:
						                	send(KeyMsg.KEY_SPACE);
						                    bear.up();
						                    break;
						                case KeyEvent.VK_A:
						                	send(KeyMsg.KEY_A);
						                    bear.active(10);
						                    bear.active(-10);
						                    bear.idle();
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
				    	new Thread(() -> buttonSound.playSound()).start();
					    startSound.stopSound();

						backgroundLabel.add(back_wait);
						backgroundLabel.setComponentZOrder(back_wait, 0);
						backgroundLabel.revalidate();
						backgroundLabel.repaint();

					
				    	uid = "Rabbit";



				    	// OptionPane 생성 및 GlassPane 설정

				        new Thread(() -> {
							try {
								connectToServer(bear);
							
	
						        remove(backgroundLabel);
						        
						    	oPane.setMainFrame(ClientGUI.this); // 현재 프레임 참조 전달
						        JComponent glassPane = (JComponent) getGlassPane(); // GlassPane 가져오기
						        glassPane.setLayout(null); // 레이아웃 설정
						        glassPane.setBounds(0, 0, 1100, 738); // 크기 명시적으로 설정
						        
						        oPane.getPane().setBounds(0, 0, 1100, 738); // OptionPane 크기 설
						        
						        glassPane.add(oPane.getPane()); // OptionPane 추가
						        glassPane.setVisible(true); // GlassPane 활성화
						        
						        add(oPane.getPane());
						        // 플레이 화면 생성
	
						    	rabbit.setPlayer(true);
	
						        m_map.add(rabbit.getCharacter());
						        m_map.add(bear.getCharacter());
						        add(m_map);
						        
								setSize(1100, 738);				        
						        revalidate();
						        repaint();	
							} catch (UnknownHostException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	
						    startSound.stopSound();
						    new Thread(() -> {
						        mainSound.playSound();     // 사운드 재생
						        mainSound.loopSound(); // 사운드 무한 반복
						    }).start();
				        }).start();
						
				        // 키 이벤트 처리
				        addKeyListener(new KeyAdapter() {
				            @Override
				            public void keyPressed(KeyEvent e) {
				            	if (rabbit.isDead)return; 
				                int keyCode = e.getKeyCode();
				                rabbit.idle = false;
				                	switch (keyCode) {
				                	 	case KeyEvent.VK_LEFT :
				                	 		send(KeyMsg.KEY_LEFT);
				                	 		rabbit.left();
//				                	 		updateMap();
						                    break;
						                case KeyEvent.VK_RIGHT :
						                	send(KeyMsg.KEY_RIGHT);
						                	rabbit.right();
//						                	updateMap();
						                	
						                	break;
						                case KeyEvent.VK_SPACE:
						                	send(KeyMsg.KEY_SPACE);
						                	rabbit.up();
						                    break;
						                case KeyEvent.VK_A:
						                	send(KeyMsg.KEY_A);
						                	rabbit.active(90);
						                	rabbit.active(-10);
						                    break;
						                case KeyEvent.VK_S:
						                	send(KeyMsg.KEY_S);
						                	rabbit.push();
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
			    new Thread(() -> buttonSound.playSound()).start();
            	if (toggle) {
            		startSound.stopSound();
            		toggle = false;
            	} else {
				    new Thread(() -> {
				    	startSound.playSound();     // 사운드 재생
				    	startSound.loopSound(); // 사운드 무한 반복
				    }).start();
            		toggle = true;
            	}
            	
            }
        });
	}
	
	private void connectToServer(Moveable character) throws UnknownHostException, IOException {
	    try {
	        socket = new Socket();
	        SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
	        socket.connect(sa, 3000);
	        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	        System.out.println("Sending UID: " + uid); // UID 확인
            out.writeUTF(uid);
            out.flush();
            
            String serverMessage;
            while (true) {
                serverMessage = in.readUTF().trim();
                if ("GAME_START".equals(serverMessage)) {
                    System.out.println("GAME_START 메시지 수신. 게임을 시작합니다.");
                    break; // GAME_START 수신 시 루프 종료
                }
            }
	        receiveThread = new Thread(new Runnable() {
	            String inMsg;
	            private void receiveMessage() {
	                try {
	                    inMsg = in.readUTF();
	                    inMsg = inMsg.trim().toUpperCase();
	                    KeyMsg key = KeyMsg.valueOf(inMsg);

	                    switch (key) {
	                        case KEY_LEFT:
	                        	character.setIdle();
	                            character.left();
	                            break;
	                        case KEY_RIGHT:
	                        	character.setIdle();
	                            character.right();
	                            break;
	                        case KEY_SPACE:
	                            character.up();
	                            break;
	                        case KEY_A:
	                            character.active(90);
	                            character.active(-10);
	                            break;
	                        case KEY_LEFT_RELEASED:
	                        	character.left_released();
	                            break;
	                        case KEY_RIGHT_RELEASED:
	                        	character.right_released();
	                            break;
			                case KEY_S:
			                	character.push();
			    
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
//	        printDisplay("알 수 없는 서버");
	        e.printStackTrace();
	    } catch (SocketTimeoutException e) {
//	        printDisplay("서버와의 연결 오류: Connect timed out");
	    } catch (IOException e) {
//	        printDisplay("서버 연결 오류");
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
