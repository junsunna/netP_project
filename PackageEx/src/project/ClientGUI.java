package project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class ClientGUI extends JFrame {
<<<<<<< Updated upstream
    private JLabel backgroundLabel;
    private JButton b_gameStart, b_sound;
    private JPanel gamePanel;
    private JLabel character;
    private int characterX = 0;
    private int characterY = 300;
    private int mapOffsetX = 0;
    private final int mapWidth = 1800; // 맵 너비
    private final int screenWidth = 700; // 화면 너비
    private final int screenHeight = 700; // 화면 높이
    private ArrayList<ImageIcon> characterImages; // 캐릭터 이미지 리스트
    private int currentImageIndex = 0; // 현재 이미지 인덱스

    public ClientGUI() {
        super("Client GUI");

        setSize(screenWidth, screenHeight);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildInitialScreen();
        setVisible(true);
=======
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
>>>>>>> Stashed changes

        setFocusable(true);
        requestFocusInWindow();
    }

<<<<<<< Updated upstream
    private void buildInitialScreen() {
        ImageIcon i_startB = new ImageIcon("A:\\Program Files\\Project\\images\\background\\b_start.png");
        backgroundLabel = new JLabel(i_startB);
        backgroundLabel.setBounds(0, 0, i_startB.getIconWidth(), i_startB.getIconHeight());
        backgroundLabel.setLayout(null);
        add(backgroundLabel);

        // 플레이 버튼 설정
        ImageIcon i_play = new ImageIcon("A:\\Program Files\\Project\\images\\button\\playButton.png");
        b_gameStart = new JButton(i_play);
        b_gameStart.setBounds(260, 300, i_play.getIconWidth(), i_play.getIconHeight());
        b_gameStart.setBorderPainted(false);
        b_gameStart.setContentAreaFilled(false);
        backgroundLabel.add(b_gameStart);

        // 스피커 버튼 설정
        ImageIcon i_sound = new ImageIcon("A:\\Program Files\\Project\\images\\button\\soundButton.png");
        b_sound = new JButton(i_sound);
        b_sound.setBounds(600, 20, i_sound.getIconWidth(), i_sound.getIconHeight());
        b_sound.setBorderPainted(false);
        b_sound.setContentAreaFilled(false);
        backgroundLabel.add(b_sound);

        // 플레이 버튼 클릭 시 캐릭터 선택 화면으로 전환
        b_gameStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCharacterSelection();
            }
        });

        // 스피커 버튼 클릭 시 이벤트 처리
        b_sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("스피커 버튼 클릭됨!"); // 예: 사운드 설정 토글
            }
        });
    }

    private void showCharacterSelection() {
        // 캐릭터 선택 버튼 생성
        ImageIcon i_bearButton = new ImageIcon("A:\\Program Files\\Project\\images\\button\\b_bear.png");
        ImageIcon i_rabbitButton = new ImageIcon("A:\\Program Files\\Project\\images\\button\\b_rabbit.png");
        JButton b_bear = new JButton(i_bearButton);
        JButton b_rabbit = new JButton(i_rabbitButton);

        b_bear.setBounds(100, 400, i_bearButton.getIconWidth(), i_bearButton.getIconHeight());
        b_rabbit.setBounds(400, 400, i_rabbitButton.getIconWidth(), i_rabbitButton.getIconHeight());
        b_bear.setBorderPainted(false);
        b_bear.setContentAreaFilled(false);
        b_rabbit.setBorderPainted(false);
        b_rabbit.setContentAreaFilled(false);

        // 기존 버튼과 이미지를 제거하고 캐릭터 선택 버튼을 추가
        backgroundLabel.remove(b_gameStart);
        backgroundLabel.add(b_bear);
        backgroundLabel.add(b_rabbit);

        backgroundLabel.revalidate();
        backgroundLabel.repaint();

        // 캐릭터 선택 버튼 클릭 이벤트 설정
        b_bear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGameWithCharacter(
                    "곰돌이",
                    "A:\\Program Files\\Project\\images\\background\\background1.png",
                    "A:\\Program Files\\Project\\images\\character\\FAT ANIMAL TEDDY\\Animation PNG\\TEDDY\\NUDE\\03-Walk\\01-Walk\\FA_TEDDY_Walk_"
                );
            }
        });

        b_rabbit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGameWithCharacter(
                    "토끼",
                    "A:\\Program Files\\Project\\images\\background\\background1.png",
                    "A:\\Program Files\\Project\\images\\character\\MascotBunnyCharacter\\Bunny1\\02-Run\\__Bunny1_Run_"
                );
            }
        });
    }

    private void startGameWithCharacter(String characterName, String backgroundImagePath, String characterImagePath) {
        SwingUtilities.invokeLater(() -> {
            remove(backgroundLabel);
            loadCharacterImages(characterName, characterImagePath);
            buildGame(backgroundImagePath);
            revalidate();
            repaint();
        });
    }

    private void buildGame(String backgroundImagePath) {
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon(backgroundImagePath);
                Image background = backgroundIcon.getImage();
                // 배경 이미지를 전체 패널 크기에 맞게 그림
                g.drawImage(background, 0, 0, mapWidth, screenHeight, null);
            }
        };
        gamePanel.setLayout(null);
        gamePanel.setPreferredSize(new Dimension(mapWidth, screenHeight));

        character = new JLabel(characterImages.get(0));
        character.setBounds(characterX, characterY, 200, 200);
        gamePanel.add(character);

        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setBounds(0, 0, screenWidth, screenHeight);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setContentPane(scrollPane);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void loadCharacterImages(String characterName, String characterImagePath) {
        characterImages = new ArrayList<>();
        int imageCount = characterName.equals("곰돌이") ? 12 : 8; // 곰돌이는 12개, 토끼는 8개

        for (int i = 0; i < imageCount; i++) {
            String filePath = characterImagePath + String.format("%03d.png", i);
            File imageFile = new File(filePath);

            if (!imageFile.exists()) {
                System.out.println("이미지 파일이 존재하지 않습니다: " + filePath);
                continue;
            }

            ImageIcon icon = new ImageIcon(filePath);
            if (icon.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath);
            } else {
                Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                characterImages.add(new ImageIcon(scaledImage));
            }
        }

        if (characterImages.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                if (characterX > 0) {
                    characterX -= 10;
                    if (characterX < mapOffsetX + screenWidth / 2 && mapOffsetX > 0) {
                        mapOffsetX = Math.max(0, mapOffsetX - 10);
                    }
                    updateCharacterPosition();
                    updateCharacterImage();
                }
                break;
            case KeyEvent.VK_D:
                if (characterX < mapWidth - 200) {
                    characterX += 10;
                    if (characterX > mapOffsetX + screenWidth / 2 && mapOffsetX < mapWidth - screenWidth) {
                        mapOffsetX = Math.min(mapWidth - screenWidth, mapOffsetX + 10);
                    }
                    updateCharacterPosition();
                    updateCharacterImage();
                }
                break;
            case KeyEvent.VK_W:
                if (characterY > 0) characterY -= 10;
                updateCharacterPosition();
                break;
            case KeyEvent.VK_S:
                if (characterY < screenHeight - 200) characterY += 10;
                updateCharacterPosition();
                break;
        }
    }

    private void updateCharacterPosition() {
        character.setBounds(characterX - mapOffsetX, characterY, 200, 200);
        gamePanel.scrollRectToVisible(new Rectangle(mapOffsetX, 0, screenWidth, screenHeight));
        gamePanel.repaint();
    }

    private void updateCharacterImage() {
        currentImageIndex = (currentImageIndex + 1) % characterImages.size();
        character.setIcon(characterImages.get(currentImageIndex));
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
=======
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
>>>>>>> Stashed changes
}
