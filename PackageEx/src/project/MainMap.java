package project;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMap {
    private ImageIcon i_base;
    private final Map<Integer, ImageIcon> tileIcons = new HashMap<>();
    private final Map<Integer, ImageIcon> bulletIcons = new HashMap<>();
    private Platform platform;
    private JPanel back_base;
    private final Map<Integer, JLabel> tiles = new HashMap<>();
    private final Map<Integer, JLabel> bulletTiles = new HashMap<>();
    
    // 화면 크기 및 맵 크기 상수
    private final int PANEL_WIDTH = 715;
    private final int MAP_WIDTH = 1800;
    private final int CENTER_THRESHOLD = PANEL_WIDTH / 2;
    
    private int coin = 0;
    private int bulletId = 30;
    private int bulletIndex = 30;
    
    private Map<Integer, Rectangle> platformBounds;
    private static final String dBasePath = "images/tile/dynamic";
    private int mapX = 0; // 현재 맵의 X 위치
    private int currentTileIndex = 7;
    private javax.swing.Timer waveTimer;
    
    private int bulletTimer = 9;
    private SoundPlayer coinSound, actionSound, endingSound;
    
    public MainMap() {
        platformBounds = new HashMap<>();
        initTile();
        
        // 맵 패널 설정
        back_base = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // [핵심 수정] mapX가 아니라 0, 0에 그려야 합니다.
                // 이유: updateCamera()에서 이미 패널 자체(this)를 mapX만큼 이동시켰기 때문입니다.
                i_base.paintIcon(this, g, 0, 0); 
            }
        };

        back_base.setBounds(0, 0, i_base.getIconWidth(), i_base.getIconHeight());
        back_base.setLayout(null);
        
        createDynamicTile();
        initPlatforms();
        dynamicTimer();
        
        coinSound = new SoundPlayer();
        coinSound.loadSound("audios/coin.wav");
        actionSound = new SoundPlayer();
        actionSound.loadSound("audios/action.wav");
        endingSound = new SoundPlayer();
        endingSound.loadSound("audios/ending.wav");
    }
    
    /**
     * [핵심 로직] 카메라(맵) 위치 업데이트
     * ClientGUI에서 계산한 리더 캐릭터의 X좌표(leaderX)를 받아 맵을 이동시킵니다.
     */
    public void updateCamera(int leaderX) {
        // 1. 맵의 목표 X 좌표 계산
        // 캐릭터가 화면 중앙(CENTER_THRESHOLD)보다 오른쪽에 있으면 맵을 왼쪽(-)으로 이동
        int targetMapX = -(leaderX - CENTER_THRESHOLD);

        // 2. 맵 이동 범위 제한 (Clamping)
        // 맵의 시작점(0)보다 더 오른쪽으로 갈 수 없음
        if (targetMapX > 0) {
            targetMapX = 0;
        }
        
        // 맵의 끝부분보다 더 왼쪽으로 갈 수 없음
        // (배경 이미지 너비 - 화면 너비) 만큼만 이동 가능
        int minMapX = -(MAP_WIDTH - PANEL_WIDTH);
        if (targetMapX < minMapX) {
            targetMapX = minMapX;
        }

        // 3. 실제 패널 이동 및 변수 업데이트
        this.mapX = targetMapX; 
        back_base.setLocation(mapX, 0); // 패널 위치 이동
        // (참고: paintIcon을 쓰는 경우 repaint()가 필요할 수 있으나, setLocation으로 패널 자체를 움직이므로 보통 자동 갱신됨)
    }

    // --- 이하 기존 로직 동일 ---

    public Map<Integer, JLabel> getBullet() {
        return bulletTiles;
    }
    
    public Map<Integer, Rectangle> getPlatforms() {
        return platformBounds;
    }
    
    public JPanel getMainMap() {
        return back_base;
    }

    private void initTile() {
        i_base = new ImageIcon("images/background/b_base.png");
        for (int i = 1; i <= 22; i++) {
            tileIcons.put(i, new ImageIcon(dBasePath + "/" + String.format("%02d.png", i)));
        }
        for (int i = 1; i <= 7; i++) {
            tileIcons.put(22 + i, new ImageIcon(dBasePath + "/" + String.format("04.png")));
        }
    }
    
    public void createDynamicTile() {
        addTile(1, 650, 600, false);
        addTile(2, 800, 600, false);
        addTile(5, 550, 650, true);
        addTile(6, 550, 650, false);
        addTile(7, 1650, 250, true);
        addTile(8, 1650, 285, false);
        addTile(9, 1650, 285, false);
        addTile(10, 1650, 285, false);
        addTile(11, 1650, 285, false);
        addTile(12, 1650, 250, false);
        addTile(13, 1000, 600, true);
        addTile(14, 1000, 600, false);
        addTile(15, 1000, 600, false);
        addTile(16, 1745, 450, true);
        addTile(17, 1745, 450, false);
        addTile(18, 1200, 150, true);
        addTile(19, 1200, 150, false);
        addTile(21, 1475, 600, true);
        addTile(22, 936, 150, false);
        
        addTile(23, 390, 480, true);
        addTile(24, 650, 550, true);
        addTile(25, 800, 550, true);
        addTile(26, 795, 40, true);
        addTile(27, 936, 100, true);
        addTile(28, 1525, 45, true);
        addTile(29, 1750, 600, true);
    }
    
    private void addTile(int id, int x, int y, boolean isVisible) {
        JLabel tile = new JLabel(tileIcons.get(id));
        tile.setBounds(x, y, tileIcons.get(id).getIconWidth(), tileIcons.get(id).getIconHeight());
        tile.setVisible(isVisible);
        tiles.put(id, tile);
        back_base.add(tile);
    }
    
    public void shotBullet() {
        Thread moveThread = new Thread(() -> {
            bulletIcons.put(bulletId, new ImageIcon(dBasePath + "/" + String.format("20.png")));
            JLabel tile = new JLabel(bulletIcons.get(bulletId));
            tile.setBounds(80, 69, bulletIcons.get(bulletId).getIconWidth(), bulletIcons.get(bulletId).getIconHeight());
            tile.setVisible(true);
            bulletTiles.put(bulletId, tile);
            back_base.add(tile);
            bulletId++;

            int tileSpeed = 1;
            int delay = 5;

            while (tile.getX() < back_base.getWidth()) {
                try {
                    int newX = tile.getX() + tileSpeed;
                    tile.setBounds(newX, tile.getY(), tile.getWidth(), tile.getHeight());
                    
                    // back_base.repaint(); // 성능상 과도한 호출 주의
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            back_base.remove(tile);
            bulletTiles.remove(bulletIndex++);
            back_base.repaint();
        });

        moveThread.start();
    }

    private void dynamicTimer() {
        waveTimer = new javax.swing.Timer(300, new ActionListener() {
            boolean toggle = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                bulletTimer++;
                tiles.get(5).setVisible(toggle);
                tiles.get(6).setVisible(!toggle);
                toggle = !toggle;
                if (bulletTimer == 10) {
                    shotBullet();
                    bulletTimer = 0;
                }
                back_base.repaint();
            }
        });
        waveTimer.start();
    }
    
    public void dynamicSwitch() {
        new Thread(() -> actionSound.playSound()).start();
        switchTile();
        waveTimer = new javax.swing.Timer(300, new ActionListener() {
            int toggle = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                tiles.get(13).setVisible(toggle % 3 == 0);
                tiles.get(14).setVisible(toggle % 3 == 1);
                tiles.get(15).setVisible(toggle % 3 == 2);
                toggle++;
                if (toggle >= 3) {
                    waveTimer.stop();
                }
                back_base.repaint();
            }
        });
        waveTimer.start();
    }
    
    public void dynamicButton(int index) {
        new Thread(() -> actionSound.playSound()).start();
        if (index == 17) {
            removeTileByIndex(21);
            removePlatform(30);
        } else if (index == 19) {
            switchTile2();
            removePlatform(29);
        }
        tiles.get(index - 1).setVisible(false);
        tiles.get(index).setVisible(true);
        back_base.repaint();
    }
    
    private void switchTile() {
        tiles.get(1).setVisible(true);
        tiles.get(2).setVisible(true);
        addPlatform(27, 660, 600, 40, 50);
        addPlatform(28, 810, 600, 40, 50);
    }
    
    private void switchTile2() {
        tiles.get(22).setVisible(true);
        addPlatform(31, 946, 150, 40, 40);
    }
    
    public void reachCoin(int index) {
        if (platformBounds.containsKey(index)) {
            new Thread(() -> coinSound.playSound()).start();
        }
        removeCoin(index);
    }
    
    private void removeCoin(int index) {
        coin++;
        tiles.get(index - 9).setVisible(false);
        removePlatform(index);
    }
    
    public int getCoin() {
        return coin;
    }

    private JLabel getTileByIndex(int index) {
        return tiles.get(index);
    }
    
    private void removeTileByIndex(int index) {
        tiles.get(index).setVisible(false);
    }
    
    public void reachDoorAnimation() {
        new Thread(() -> endingSound.playSound()).start();
        Thread animationThread = new Thread(() -> {
            while (true) {
                try {
                    JLabel currentTile = getTileByIndex(currentTileIndex);
                    if (currentTile != null) {
                        currentTile.setVisible(false);
                    }

                    currentTileIndex++;
                
                    JLabel nextTile = getTileByIndex(currentTileIndex);
                    if (nextTile == getTileByIndex(13)) {
                        tiles.get(12).setVisible(true);
                        break;
                    }

                    nextTile.setVisible(true);
                    back_base.repaint();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        animationThread.start();
    }
    
    private void addPlatform(int id, int x, int y, int width, int height) {
        platform = new Platform(id, x, y, width, height);
        platformBounds.put(id, platform);
    }
    
    private void initPlatforms() {
        addPlatform(1, 0, 500, 100, 150);
        addPlatform(2, 120, 400, 50, 120);
        addPlatform(3, 200, 400, 30, 90);
        addPlatform(4, 0, 100, 100, 100); 
        addPlatform(5, 350, 530, 150, 20);
        addPlatform(6, 420, 300, 330, 50);
        addPlatform(7, 470, 200, 210, 100);
        addPlatform(8, 520, 150, 100, 50);
        addPlatform(9, 560, 100, 20, 50);
        addPlatform(20, 1110, 200, 380, 100);
        addPlatform(12, 1400, 300, 100, 50); 
        addPlatform(21, 1400, 350, 400, 90); 
        addPlatform(14, 1500, 100, 100, 50);
        addPlatform(15, 850, 370, 90, 50);
        addPlatform(16, 1050, 450, 40, 50);
        addPlatform(17, 1220, 550, 50, 50);
        addPlatform(11, 1280, 565, 40, 10);
        addPlatform(18, 1755, 450, 50, 60); 
        addPlatform(19, 800, 92, 50, 20);
        addPlatform(22, 100, 650, 450, 50);
        addPlatform(23, 950 , 650, 850, 50);
        addPlatform(24, 550 , 660, 400, 50);
        addPlatform(25, 1670 , 270, 30, 40);
        addPlatform(26, 1010 , 600, 30, 20);
        addPlatform(29, 1225 , 170, 15, 10);
        addPlatform(30, 1510 , 630, 150, 10);
        addPlatform(32, 420, 480, 1, 1);
        addPlatform(33, 680, 550, 1, 1);
        addPlatform(34, 830 , 550, 1, 1);
        addPlatform(35, 820, 40, 1, 1);
        addPlatform(36, 965 , 100, 1, 1);
        addPlatform(37, 1555 , 45, 1, 1);
        addPlatform(38, 1780 , 600, 1, 1);
    }
    
    public void removePlatform(int id) {
        if (platformBounds.containsKey(id)) {
            platformBounds.remove(id);
        }
    }
}