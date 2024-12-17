package project;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    private int coin = 0;
	private int bulletId = 30;
	private int bulletIndex = 30;
	
	private Map<Integer, Rectangle> platformBounds; // 발판 충돌 범위를 저장하는 리스트
	private static final String dBasePath = "images/tile/dynamic";
	private int mapX = 0;
	private int currentTileIndex = 7; // 초기값은 7번 타일
	private javax.swing.Timer waveTimer;
	
	private int bulletTimer = 9;
    private SoundPlayer coinSound, actionSound, endingSound;
	
	MainMap() {
		platformBounds = new HashMap<>();
		initTile();
        back_base = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 배경을 그리는 코드 추가 (필요하다면)
                i_base.paintIcon(this, g, mapX, 0);
            }
        };

		back_base.setBounds(0, 0, i_base.getIconWidth(), i_base.getIconHeight());
		back_base.setLayout(null);
		
		createDynamicTile();
		initPlatforms();
		dynamicTimer();
		coinSound = new SoundPlayer();
		coinSound.loadSound("audios/coin.wav"); // 사운드 파일 경로 설정
		actionSound = new SoundPlayer();
		actionSound.loadSound("audios/action.wav"); // 사운드 파일 경로 설정
		endingSound = new SoundPlayer();
		endingSound.loadSound("audios/ending.wav"); // 사운드 파일 경로 설정
	}
	
	public Map<Integer, JLabel> getBullet() {
		return bulletTiles;
	}
	
	// 배경을 이동시키는 메서드
    public void moveBackground(int moveAmount) {
        mapX += moveAmount;

        // 배경이 맵의 끝을 넘어가지 않도록 제한
        if (mapX > 0) {
            mapX = 0;  // 맵이 오른쪽 끝으로 이동하는 것을 방지
        }

        if (mapX < -(i_base.getIconWidth() - back_base.getWidth())) {
            mapX = -(i_base.getIconWidth() - back_base.getWidth());  // 맵이 왼쪽 끝으로 이동하는 것을 방지
        }

        back_base.repaint();  // 화면 갱신
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
//        addTile(20, 80, 69, true);
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

            int tileSpeed = 1; // 타일 이동 속도
            int delay = 5; // 이동 간격 (ms)

            while (tile.getX() < back_base.getWidth()) {
                try {
                    // 현재 위치 갱신
                    int newX = tile.getX() + tileSpeed;
                    tile.setBounds(newX, tile.getY(), tile.getWidth(), tile.getHeight());

                    
                    // 화면 갱신
                    back_base.repaint();

                    // 지연 시간
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    System.out.println("타일 이동 스레드가 중단되었습니다.");
                    break;
                }
            }
            
            // 맵 밖으로 나가면 타일 삭제
            System.out.println("20번 타일이 맵 밖으로 나가 삭제됩니다.");
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
        	// 가시 타일 플랫폼 지우기
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
	                // 현재 타일 숨기기
	                JLabel currentTile = getTileByIndex(currentTileIndex);
	                if (currentTile != null) {
	                    System.out.println("현재 타일 숨김: " + currentTileIndex);
	                    currentTile.setVisible(false);
	                }

	                // 다음 타일 활성화
	                currentTileIndex++;
	        	
	                JLabel nextTile = getTileByIndex(currentTileIndex);
	                if (nextTile == getTileByIndex(13)) {
	                	tiles.get(12).setVisible(true);
	                    break; // `nextTile`이 null이면 스레드 종료
	                }

	                System.out.println("다음 타일 활성화: " + currentTileIndex);
	                nextTile.setVisible(true);

	                // 화면 갱신
	                back_base.repaint();

	                // 타일 전환 간격 (예: 500ms)
	                Thread.sleep(200);
	            } catch (InterruptedException e) {
	                System.out.println("애니메이션 중단됨: " + e.getMessage());
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
	    // 맨 왼쪽 아래 블럭
		addPlatform(1, 0, 500, 100, 150); // 첫 번째 계단
		addPlatform(2, 120, 400, 50, 120); // 두 번째 계단
		addPlatform(3, 200, 400, 30, 90); // 세 번째 계단
	    
		// 총 블럭
	    addPlatform(4, 0, 100, 100, 100); 

	    // Platform 3
	    addPlatform(5, 350, 530, 150, 20); // 첫 번째 계단
 
	    // Platform 4
	    addPlatform(6, 420, 300, 330, 50); // 세 번째 계단
	    addPlatform(7, 470, 200, 210, 100);
	    addPlatform(8, 520, 150, 100, 50);
	    addPlatform(9, 560, 100, 20, 50);

	    // ㄱㄴ자 블록	
	    addPlatform(20, 1110, 200, 380, 100); // 위
	    addPlatform(12, 1400, 300, 100, 50);  // 중간
	    addPlatform(21, 1400, 350, 400, 90);  // 아래

	    // Platform 6
	    addPlatform(14, 1500, 100, 100, 50); // 첫 번째 계단

	    // Platform 7
	    addPlatform(15, 850, 370, 90, 50); // 첫 번째 계단
	    addPlatform(16, 1050, 450, 40, 50); // 두 번째 계단
	    addPlatform(17, 1220, 550, 50, 50); // 세 번째 계단
	    addPlatform(11, 1280, 565, 40, 10); // 첫 번째 계단
	    
	    // 가시 버튼
	    addPlatform(18, 1755, 450, 50, 60); 

	    // Platform 9
	    addPlatform(19, 800, 92, 50, 20); // 첫 번째 계단

	    // Platform 10
	    
	    // Platform 11
	    addPlatform(22, 100, 650, 450, 50);
	    
	    // Platform 12
	    addPlatform(23, 950 , 650, 850, 50);
	    
	    // Platform 13
	    addPlatform(24, 550 , 660, 400, 50);
	    
	    //Platform 14
	    addPlatform(25, 1670 , 270, 30, 40);
	    
	    //Platform 레버
	    addPlatform(26, 1010 , 600, 30, 20);
	    
	    // 버튼2
	    addPlatform(29, 1225 , 170, 15, 10);
	    
	    // 가시
	    addPlatform(30, 1510 , 630, 150, 10);
	    
	    // 코인 1
	    addPlatform(32, 420, 480, 1, 1);
	    // 코인 2
	    addPlatform(33, 680, 550, 1, 1);
	    // 코인 3
	    addPlatform(34, 830 , 550, 1, 1);
	    // 코인 4
	    addPlatform(35, 820, 40, 1, 1);
	    // 코인 5
	    addPlatform(36, 965 , 100, 1, 1);
	    // 코인 6
	    addPlatform(37, 1555 , 45, 1, 1);
	    // 코인 7
	    addPlatform(38, 1780 , 600, 1, 1);
        
	}
	public void removePlatform(int id) {
		if (platformBounds.containsKey(id)) {
		        platformBounds.remove(id);
	    }
	}
}
