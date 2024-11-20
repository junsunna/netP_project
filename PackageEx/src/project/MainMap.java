package project;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;



public class MainMap {
	private ImageIcon i_base, tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8 , tile9, tile10;
	private JLabel back_base, b_tile1, b_tile2, b_tile3, b_tile4, b_tile5, b_tile6, b_tile7, b_tile8, b_tile9, b_tile10;
	private List<Rectangle> platformBounds; // 발판 충돌 범위를 저장하는 리스트
	public static final String basePath = "images/tile/static";
	MainMap() {
		platformBounds = new ArrayList<>();
		initTile();
		initPlatforms();
	}
	public List<Rectangle> getPlatforms() {
		return platformBounds;
	}
	private void initTile() {
		i_base = new ImageIcon("images/background/b_base.png");
		tile1 = new ImageIcon(basePath + "/01.png");
		tile2 = new ImageIcon(basePath + "/02.png");
		tile3 = new ImageIcon(basePath + "/03.png");
		tile4 = new ImageIcon(basePath + "/04.png");
		tile5 = new ImageIcon(basePath + "/05.png");
		tile6 = new ImageIcon(basePath + "/06.png");
		tile7 = new ImageIcon(basePath + "/07.png");
		tile8 = new ImageIcon(basePath + "/08.png");
		tile9 = new ImageIcon(basePath + "/09.png");
		tile10 = new ImageIcon(basePath + "/10.png");
	}
	
	public JLabel createTile() {
		back_base = new JLabel(i_base);
		back_base.setBounds(0, 0, i_base.getIconWidth(), i_base.getIconHeight());
		back_base.setLayout(null);
		
		b_tile1 = new JLabel(tile1);
		b_tile1.setBounds(0, 400, tile1.getIconWidth(), tile1.getIconHeight());
		back_base.add(b_tile1);
		
		b_tile2 = new JLabel(tile2);
		b_tile2.setBounds(0, 50, tile2.getIconWidth(), tile2.getIconHeight());
		back_base.add(b_tile2);

		b_tile3 = new JLabel(tile3);
		b_tile3.setBounds(330, 530, tile3.getIconWidth(), tile3.getIconHeight());
		back_base.add(b_tile3);
		
		b_tile4 = new JLabel(tile4);
		b_tile4.setBounds(400, 100, tile4.getIconWidth(), tile4.getIconHeight());
		back_base.add(b_tile4);
		
		b_tile5 = new JLabel(tile5);
		b_tile5.setBounds(1300, 550, tile5.getIconWidth(), tile5.getIconHeight());
		back_base.add(b_tile5);
		
		b_tile6 = new JLabel(tile6);
		b_tile6.setBounds(1500, 100, tile6.getIconWidth(), tile6.getIconHeight());
		back_base.add(b_tile6);
		
		b_tile7 = new JLabel(tile7);
		b_tile7.setBounds(850, 400, tile7.getIconWidth(), tile7.getIconHeight());
		back_base.add(b_tile7);
		
		b_tile8 = new JLabel(tile8);
		b_tile8.setBounds(1750, 500, tile8.getIconWidth(), tile8.getIconHeight());
		back_base.add(b_tile8);

		b_tile9 = new JLabel(tile9);
		b_tile9.setBounds(700, 100, tile9.getIconWidth(), tile9.getIconHeight());
		back_base.add(b_tile9);

		b_tile10 = new JLabel(tile10);
		b_tile10.setBounds(1100, 200, tile10.getIconWidth(), tile10.getIconHeight());
		back_base.add(b_tile10);
		
		return back_base;
	}
	
	private void addPlatform(int x, int y, int width, int height) {
        Rectangle platform = new Rectangle(x, y, width, height);
        platformBounds.add(platform);
    }
	private void initPlatforms() {
	    // Platform 1
		addPlatform(0, 500, 100, 150); // 첫 번째 계단
		addPlatform(100, 400, 100, 150); // 두 번째 계단
		addPlatform(200, 400, 50, 100); // 세 번째 계단
	    // Platform 2
	    addPlatform(0, 100, 100, 100); // 첫 번째 계단

	    // Platform 3
	    addPlatform(330, 530, 150, 50); // 첫 번째 계단
	    addPlatform(200, 400, 50, 100); // 두 번째 계단

	    // Platform 4
	    addPlatform(0, 100, 100, 100); // 첫 번째 계단
	    addPlatform(200, 400, 50, 100); // 두 번째 계단
	    addPlatform(440, 210, 250, 100); // 세 번째 계단
	    addPlatform(495, 160, 150, 100);
	    addPlatform(540, 110, 50, 100);

	    // Platform 5
	    addPlatform(1300, 550, 150, 50); // 첫 번째 계단
	    addPlatform(1400, 300, 100, 50); // 두 번째 계단
	    addPlatform(1100, 200, 400, 100); // 세 번째 계단

	    // Platform 6
	    addPlatform(1500, 100, 100, 50); // 첫 번째 계단

	    // Platform 7
	    addPlatform(850, 400, 150, 50); // 첫 번째 계단
	    addPlatform(950, 450, 150, 50); // 두 번째 계단
	    addPlatform(1050, 500, 150, 50); // 세 번째 계단

	    // Platform 8
	    addPlatform(1750, 500, 50, 50); // 첫 번째 계단

	    // Platform 9
	    addPlatform(700, 100, 100, 50); // 첫 번째 계단

	    // Platform 10
	    addPlatform(1100, 200, 400, 100); // 첫 번째 계단
	}
}
