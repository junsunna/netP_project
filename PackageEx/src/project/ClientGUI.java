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

        setFocusable(true);
        requestFocusInWindow();
    }

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
}
