package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {
    private Bear bear;
    private Rabbit rabbit;
    private MainMap mainMap;
    private JPanel m_map;
    private OptionPane oPane;
    private boolean toggle = true;
    private JLabel backgroundLabel, back_cake;
    private ImageIcon i_cake, i_startB;
    
    // [삭제] Timer gameLoopTimer; <- 더 이상 필요하지 않습니다.

    private JButton b_gameStart, b_sound;
    private SoundPlayer startSound, mainSound, buttonSound;

    int panelWidth = 715;
    int mapWidth = 1800;

    public ClientGUI() {
        super("BunnyBear Local");

        // 사운드 초기화
        startSound = new SoundPlayer();
        startSound.loadSound("audios/startBGM.wav");
        mainSound = new SoundPlayer();
        mainSound.loadSound("audios/mainBGM.wav");
        
        new Thread(() -> {
            startSound.playSound();
            startSound.loopSound();
        }).start();
        
        buttonSound = new SoundPlayer();
        buttonSound.loadSound("audios/button.wav");

        // 맵 및 옵션창 초기화
        mainMap = new MainMap();
        m_map = mainMap.getMainMap();
        oPane = new OptionPane(mainSound);

        // [핵심 수정] 캐릭터 생성 시 'this' (ClientGUI)를 넘겨줍니다.
        // Rabbit과 Bear 생성자도 이에 맞춰 수정되어야 합니다.
        rabbit = new Rabbit(mainMap, oPane, this);
        bear = new Bear(mainMap, oPane, this);

        setLayout(null);
        buildGUI();

        setSize(715, 738);
        setLocation(500, 150);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * [신규] 맵 업데이트 요청 처리 (Mediator 역할)
     * 캐릭터(Bear, Rabbit)가 이동할 때마다 이 메서드를 호출합니다.
     */
    public void requestMapUpdate() {
        if (bear == null || rabbit == null) return;

        // 1. 두 캐릭터의 현재 X 좌표 가져오기
        int bearX = bear.getPosition().x;
        int rabbitX = rabbit.getPosition().x;

        // 2. 더 오른쪽에 있는(리더) 좌표 선택
        int leaderX = Math.max(bearX, rabbitX);

        // 3. MainMap에게 카메라(맵) 이동 명령
        mainMap.updateCamera(leaderX);
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
        b_gameStart.setBorderPainted(false);
        b_gameStart.setContentAreaFilled(false);

        b_sound = new JButton(i_sound);
        b_sound.setBounds(520, 20, i_play.getIconWidth(), 100);
        b_sound.setBorderPainted(false);
        b_sound.setContentAreaFilled(false);
        b_sound.setFocusPainted(false);

        backgroundLabel.add(b_gameStart);
        backgroundLabel.add(b_sound);

        setVisible(true);
    }

    private void createControlPanel() {
        // 게임 시작 버튼 로직
        b_gameStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> buttonSound.playSound()).start();
                startGame();
            }
        });

        // 사운드 토글 버튼
        b_sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> buttonSound.playSound()).start();
                if (toggle) {
                    startSound.stopSound();
                    toggle = false;
                } else {
                    new Thread(() -> {
                        startSound.playSound();
                        startSound.loopSound();
                    }).start();
                    toggle = true;
                }
            }
        });
    }

    private void startGame() {
        startSound.stopSound();

        SwingUtilities.invokeLater(() -> {
            remove(backgroundLabel);

            oPane.setMainFrame(ClientGUI.this);
            JComponent glassPane = (JComponent) getGlassPane();
            glassPane.setLayout(null);
            glassPane.setBounds(0, 0, 1100, 738);
            oPane.getPane().setBounds(0, 0, 1100, 738);
            glassPane.add(oPane.getPane());
            glassPane.setVisible(true);
            add(oPane.getPane());

            bear.setPlayer(true);
            rabbit.setPlayer(true);

            m_map.add(bear.getCharacter());
            m_map.add(rabbit.getCharacter());
            add(m_map);

            setSize(1100, 738);
            revalidate();
            repaint();
            
            // [삭제] startCameraLoop(); <- Timer 관련 호출 제거

            new Thread(() -> {
                mainSound.playSound();
                mainSound.loopSound();
            }).start();

            setupKeyHandler();
            requestFocusInWindow();
        });
    }

    private void setupKeyHandler() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                // Bear (WASD)
                if (!bear.isDead) {
                    bear.idle = false;
                    switch (keyCode) {
                        case KeyEvent.VK_A: bear.left(); break;
                        case KeyEvent.VK_D: bear.right(); break;
                        case KeyEvent.VK_W: bear.up(); break;
                        case KeyEvent.VK_F: 
                            bear.active(10);
                            bear.active(-10);
                            bear.idle();
                            break;
                    }
                }

                // Rabbit (Arrows)
                if (!rabbit.isDead) {
                    rabbit.idle = false;
                    switch (keyCode) {
                        case KeyEvent.VK_LEFT: rabbit.left(); break;
                        case KeyEvent.VK_RIGHT: rabbit.right(); break;
                        case KeyEvent.VK_UP: rabbit.up(); break;
                        case KeyEvent.VK_ENTER: 
                            rabbit.active(90);
                            rabbit.active(-10);
                            break;
                        case KeyEvent.VK_DOWN: rabbit.push(); break;
                    }
                }
                m_map.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();

                // Bear Stop
                bear.initIndex();
                if (keyCode == KeyEvent.VK_A) {
                    bear.left_released();
                    bear.idle();
                } else if (keyCode == KeyEvent.VK_D) {
                    bear.right_released();
                    bear.idle();
                }

                // Rabbit Stop
                rabbit.initIndex();
                if (keyCode == KeyEvent.VK_LEFT) {
                    rabbit.left_released();
                    rabbit.idle();
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    rabbit.right_released();
                    rabbit.idle();
                }
                m_map.repaint();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}