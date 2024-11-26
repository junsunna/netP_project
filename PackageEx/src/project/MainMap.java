package project;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class MainMap {
	private ImageIcon i_base;
	private ImageIcon id_tile1, id_tile2, id_tile3, id_tile4, id_tile5
	, id_tile6, id_tile7, id_tile8, id_tile9, id_tile10, id_tile11
	, id_tile12, id_tile13, id_tile14, id_tile15, id_tile16, id_tile17;
	
	private JPanel back_base;
	private JLabel d_tile1, d_tile2, d_tile3, d_tile4,	
	d_tile5, d_tile6, d_tile7, d_tile8, d_tile9, d_tile10, d_tile11, d_tile12
	, d_tile13, d_tile14, d_tile15, d_tile16, d_tile17;
	
	private List<Rectangle> platformBounds; // 발판 충돌 범위를 저장하는 리스트
	private static final String dBasePath = "images/tile/dynamic";
	private int mapX = 0;
	MainMap() {
		platformBounds = new ArrayList<>();
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
	
	public List<Rectangle> getPlatforms() {
		return platformBounds;
	}
	
	public JPanel getMainMap() {
		return back_base;
	}
	private void initTile() {
		i_base = new ImageIcon("images/background/b_base.png");
		
		id_tile1 = new ImageIcon(dBasePath + "/01.png");
		id_tile2 = new ImageIcon(dBasePath + "/02.png");
		id_tile3 = new ImageIcon(dBasePath + "/03.png");
		id_tile4 = new ImageIcon(dBasePath + "/04.png");
		id_tile5 = new ImageIcon(dBasePath + "/05.png");
		id_tile6 = new ImageIcon(dBasePath + "/06.png");
		id_tile7 = new ImageIcon(dBasePath + "/07.png");
		id_tile8 = new ImageIcon(dBasePath + "/08.png");
		id_tile9 = new ImageIcon(dBasePath + "/09.png");
		id_tile10 = new ImageIcon(dBasePath + "/10.png");
		id_tile11 = new ImageIcon(dBasePath + "/11.png");
		id_tile12 = new ImageIcon(dBasePath + "/12.png");
		id_tile13 = new ImageIcon(dBasePath + "/13.png");
		id_tile14 = new ImageIcon(dBasePath + "/14.png");
		id_tile15 = new ImageIcon(dBasePath + "/15.png");
		id_tile16 = new ImageIcon(dBasePath + "/16.png");
		id_tile17 = new ImageIcon(dBasePath + "/17.png");
	}
	
	public void createDynamicTile() {
		d_tile1 = new JLabel(id_tile1);
		d_tile1.setBounds(650, 600, id_tile1.getIconWidth(), id_tile1.getIconHeight());
		back_base.add(d_tile1);
		
		d_tile2 = new JLabel(id_tile2);
		d_tile2.setBounds(800, 600, id_tile2.getIconWidth(), id_tile2.getIconHeight());
		back_base.add(d_tile2);

//		d_tile3 = new JLabel(id_tile3);
//		d_tile3.setBounds(341, 535, id_tile3.getIconWidth(), id_tile3.getIconHeight());
//		back_base.add(d_tile3);
		
//		d_tile4 = new JLabel(id_tile4);
//		d_tile4.setBounds(400, 100, id_tile4.getIconWidth(), id_tile4.getIconHeight());
//		back_base.add(d_tile4);
//		
		d_tile5 = new JLabel(id_tile5);
		d_tile5.setBounds(550, 650, id_tile5.getIconWidth(), id_tile5.getIconHeight());
		back_base.add(d_tile5);
//		
//		d_tile6 = new JLabel(id_tile6);
//		d_tile6.setBounds(1500, 100, id_tile6.getIconWidth(), id_tile6.getIconHeight());
//		back_base.add(d_tile6);
//		
//		d_tile7 = new JLabel(id_tile7);
//		d_tile7.setBounds(850, 400, id_tile7.getIconWidth(), id_tile7.getIconHeight());
//		back_base.add(d_tile7);
//		
//		d_tile8 = new JLabel(id_tile8);
//		d_tile8.setBounds(1750, 500, id_tile8.getIconWidth(), id_tile8.getIconHeight());
//		back_base.add(d_tile8);
//
//		d_tile9 = new JLabel(id_tile9);
//		d_tile9.setBounds(800, 88, id_tile9.getIconWidth(), id_tile9.getIconHeight());
//		back_base.add(d_tile9);
//
//		d_tile10 = new JLabel(id_tile10);
//		d_tile10.setBounds(1100, 200, id_tile10.getIconWidth(), id_tile10.getIconHeight());
//		back_base.add(d_tile10);
//		
//		d_tile11 = new JLabel(id_tile11);
//		d_tile11.setBounds(0, 650, id_tile11.getIconWidth(), id_tile11.getIconHeight());
//		back_base.add(d_tile11);
//		
//		d_tile12 = new JLabel(id_tile12);
//		d_tile12.setBounds(1000, 650, id_tile12.getIconWidth(), id_tile12.getIconHeight());
//		back_base.add(d_tile12);
//		
//		d_tile13 = new JLabel(id_tile13);
//		d_tile13.setBounds(1000, 650, id_tile13.getIconWidth(), id_tile13.getIconHeight());
//		back_base.add(d_tile13);
//		
//		d_tile14 = new JLabel(id_tile14);
//		d_tile14.setBounds(1000, 650, id_tile14.getIconWidth(), id_tile14.getIconHeight());
//		back_base.add(d_tile14);
//		
//		d_tile15 = new JLabel(id_tile15);
//		d_tile15.setBounds(1000, 650, id_tile15.getIconWidth(), id_tile15.getIconHeight());
//		back_base.add(d_tile15);
//		
//		d_tile16 = new JLabel(id_tile16);
//		d_tile16.setBounds(1000, 650, id_tile16.getIconWidth(), id_tile16.getIconHeight());
//		back_base.add(d_tile16);
//		
//		d_tile17 = new JLabel(id_tile17);
//		d_tile17.setBounds(1000, 650, id_tile17.getIconWidth(), id_tile17.getIconHeight());
//		back_base.add(d_tile17);
	}
	
	private void addPlatform(int id, int x, int y, int width, int height) {
        Platform platform = new Platform(id, x, y, width, height);
        platformBounds.add(platform);
    }
	private void initPlatforms() {
	    // Platform 1
		addPlatform(1, 0, 500, 100, 150); // 첫 번째 계단
		addPlatform(2, 120, 400, 50, 120); // 두 번째 계단
		addPlatform(3, 200, 400, 30, 90); // 세 번째 계단
	    // Platform 2
	    addPlatform(4, 0, 100, 100, 100); // 첫 번째 계단

	    // Platform 3
	    addPlatform(5, 350, 530, 150, 20); // 첫 번째 계단
 
	    // Platform 4
	    addPlatform(6, 420, 300, 330, 50); // 세 번째 계단
	    addPlatform(7, 470, 200, 210, 100);
	    addPlatform(8, 520, 150, 100, 50);
	    addPlatform(9, 560, 100, 20, 50);

	    // Platform 5
	    addPlatform(10, 1320, 550, 50, 50); // 첫 번째 계단
	    addPlatform(11, 1350, 570, 45, 10); // 첫 번째 계단
	    addPlatform(12, 1400, 300, 100, 50); // 두 번째 계단
	    addPlatform(13, 1100, 200, 400, 100); // 세 번째 계단

	    // Platform 6
	    addPlatform(14, 1500, 100, 100, 50); // 첫 번째 계단

	    // Platform 7
	    addPlatform(15, 850, 400, 130, 50); // 첫 번째 계단
	    addPlatform(16, 950, 450, 130, 50); // 두 번째 계단
	    addPlatform(17, 1050, 500, 130, 50); // 세 번째 계단

	    // Platform 8
	    addPlatform(18, 1750, 500, 50, 50); // 첫 번째 계단

	    // Platform 9
	    addPlatform(19, 800, 92, 50, 20); // 첫 번째 계단

	    // Platform 10
	    addPlatform(20, 1100, 200, 400, 100); // 첫 번째 계단
	    addPlatform(21, 1400, 350, 400, 100);
	    
	    // Platform 11
	    addPlatform(22, 100, 650, 450, 50);
	    
	    // Platform 12
	    addPlatform(23, 950 , 650, 850, 50);
	    
	    // Platform 13
	    addPlatform(24, 550 , 660, 400, 50);
	}
}
