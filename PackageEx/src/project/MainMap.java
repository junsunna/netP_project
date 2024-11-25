package project;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;



public class MainMap {
	private ImageIcon i_base, tile1, tile2, tile3, tile4, tile5, 
	tile6, tile7, tile8 , tile9, tile10, tile11, tile12;
	
	private ImageIcon id_tile1, id_tile2, id_tile3, id_tile4, id_tile5
	, id_tile6, id_tile7, id_tile8, id_tile9, id_tile10, id_tile11
	, id_tile12, id_tile13, id_tile14, id_tile15, id_tile16, id_tile17;
	
	
	private JLabel back_base, s_tile1, s_tile2, s_tile3, s_tile4, 
	s_tile5, s_tile6, s_tile7, s_tile8, s_tile9, s_tile10, s_tile11, s_tile12;
	private JLabel d_tile1, d_tile2, d_tile3, d_tile4,	
	d_tile5, d_tile6, d_tile7, d_tile8, d_tile9, d_tile10, d_tile11, d_tile12
	, d_tile13, d_tile14, d_tile15, d_tile16, d_tile17;
	
	private List<Rectangle> platformBounds; // 발판 충돌 범위를 저장하는 리스트
	private static final String sBasePath = "images/tile/static";
	private static final String dBasePath = "images/tile/dynamic";
	
	MainMap() {
		platformBounds = new ArrayList<>();
		back_base = new JLabel(i_base);
		back_base.setBounds(0, 0, i_base.getIconWidth(), i_base.getIconHeight());
		back_base.setLayout(null);
		initTile();
		createStaticTile();
		createDynamicTile();
		initPlatforms();
	}
	public List<Rectangle> getPlatforms() {
		return platformBounds;
	}
	
	public JLabel getMainMap() {
		return back_base;
	}
	private void initTile() {
		i_base = new ImageIcon("images/background/b_base.png");
		tile1 = new ImageIcon(sBasePath + "/01.png");
		tile2 = new ImageIcon(sBasePath + "/02.png");
		tile3 = new ImageIcon(sBasePath + "/03.png");
		tile4 = new ImageIcon(sBasePath + "/04.png");
		tile5 = new ImageIcon(sBasePath + "/05.png");
		tile6 = new ImageIcon(sBasePath + "/06.png");
		tile7 = new ImageIcon(sBasePath + "/07.png");
		tile8 = new ImageIcon(sBasePath + "/08.png");
		tile9 = new ImageIcon(sBasePath + "/09.png");
		tile10 = new ImageIcon(sBasePath + "/10.png");
		tile11 = new ImageIcon(sBasePath + "/11.png");
		tile12 = new ImageIcon(sBasePath + "/12.png");
		
		id_tile1 = new ImageIcon(dBasePath + "/01.png");
		id_tile2 = new ImageIcon(dBasePath + "/02.png");
		id_tile3 = new ImageIcon(dBasePath + "/03.png");
		id_tile4 = new ImageIcon(dBasePath + "/04.png");
		id_tile5 = new ImageIcon(dBasePath + "/05.png");
		id_tile6 = new ImageIcon(dBasePath + "/06.png");
		id_tile7 = new ImageIcon(dBasePath + "/07.png");
		id_tile8 = new ImageIcon(dBasePath + "/07.png");
		id_tile9 = new ImageIcon(dBasePath + "/07.png");
		id_tile10 = new ImageIcon(dBasePath + "/07.png");
		id_tile11 = new ImageIcon(dBasePath + "/07.png");
		id_tile12 = new ImageIcon(dBasePath + "/07.png");
		id_tile13 = new ImageIcon(dBasePath + "/07.png");
		id_tile14 = new ImageIcon(dBasePath + "/07.png");
		id_tile15 = new ImageIcon(dBasePath + "/07.png");
		id_tile16 = new ImageIcon(dBasePath + "/07.png");
		id_tile17 = new ImageIcon(dBasePath + "/07.png");
	}
	
	public void createStaticTile() {		
		s_tile1 = new JLabel(tile1);
		s_tile1.setBounds(0, 400, tile1.getIconWidth(), tile1.getIconHeight());
		back_base.add(s_tile1);
		
		s_tile2 = new JLabel(tile2);
		s_tile2.setBounds(0, 50, tile2.getIconWidth(), tile2.getIconHeight());
		back_base.add(s_tile2);

		s_tile3 = new JLabel(tile3);
		s_tile3.setBounds(341, 535, tile3.getIconWidth(), tile3.getIconHeight());
		back_base.add(s_tile3);
		
		s_tile4 = new JLabel(tile4);
		s_tile4.setBounds(400, 100, tile4.getIconWidth(), tile4.getIconHeight());
		back_base.add(s_tile4);
		
		s_tile5 = new JLabel(tile5);
		s_tile5.setBounds(1300, 550, tile5.getIconWidth(), tile5.getIconHeight());
		back_base.add(s_tile5);
		
		s_tile6 = new JLabel(tile6);
		s_tile6.setBounds(1500, 100, tile6.getIconWidth(), tile6.getIconHeight());
		back_base.add(s_tile6);
		
		s_tile7 = new JLabel(tile7);
		s_tile7.setBounds(850, 400, tile7.getIconWidth(), tile7.getIconHeight());
		back_base.add(s_tile7);
		
		s_tile8 = new JLabel(tile8);
		s_tile8.setBounds(1750, 500, tile8.getIconWidth(), tile8.getIconHeight());
		back_base.add(s_tile8);

		s_tile9 = new JLabel(tile9);
		s_tile9.setBounds(800, 88, tile9.getIconWidth(), tile9.getIconHeight());
		back_base.add(s_tile9);

		s_tile10 = new JLabel(tile10);
		s_tile10.setBounds(1100, 200, tile10.getIconWidth(), tile10.getIconHeight());
		back_base.add(s_tile10);
		
		s_tile11 = new JLabel(tile11);
		s_tile11.setBounds(0, 650, tile11.getIconWidth(), tile11.getIconHeight());
		back_base.add(s_tile11);
		
		s_tile12 = new JLabel(tile12);
		s_tile12.setBounds(1000, 650, tile12 .getIconWidth(), tile12.getIconHeight());
		back_base.add(s_tile12);
	}
	
	public void createDynamicTile() {
		d_tile1 = new JLabel(id_tile1);
		d_tile1.setBounds(0, 400, id_tile1.getIconWidth(), id_tile1.getIconHeight());
		back_base.add(d_tile1);
		
		d_tile2 = new JLabel(id_tile2);
		d_tile2.setBounds(0, 50, id_tile2.getIconWidth(), id_tile2.getIconHeight());
		back_base.add(d_tile2);

		d_tile3 = new JLabel(id_tile3);
		d_tile3.setBounds(341, 535, id_tile3.getIconWidth(), id_tile3.getIconHeight());
		back_base.add(d_tile3);
		
		d_tile4 = new JLabel(id_tile4);
		d_tile4.setBounds(400, 100, id_tile4.getIconWidth(), id_tile4.getIconHeight());
		back_base.add(d_tile4);
		
		d_tile5 = new JLabel(id_tile5);
		d_tile5.setBounds(1300, 550, id_tile5.getIconWidth(), id_tile5.getIconHeight());
		back_base.add(d_tile5);
		
		d_tile6 = new JLabel(id_tile6);
		d_tile6.setBounds(1500, 100, id_tile6.getIconWidth(), id_tile6.getIconHeight());
		back_base.add(d_tile6);
		
		d_tile7 = new JLabel(id_tile7);
		d_tile7.setBounds(850, 400, id_tile7.getIconWidth(), id_tile7.getIconHeight());
		back_base.add(d_tile7);
		
		d_tile8 = new JLabel(id_tile8);
		d_tile8.setBounds(1750, 500, id_tile8.getIconWidth(), id_tile8.getIconHeight());
		back_base.add(d_tile8);

		d_tile9 = new JLabel(id_tile9);
		d_tile9.setBounds(800, 88, id_tile9.getIconWidth(), id_tile9.getIconHeight());
		back_base.add(d_tile9);

		d_tile10 = new JLabel(id_tile10);
		d_tile10.setBounds(1100, 200, id_tile10.getIconWidth(), id_tile10.getIconHeight());
		back_base.add(d_tile10);
		
		d_tile11 = new JLabel(id_tile11);
		d_tile11.setBounds(0, 650, id_tile11.getIconWidth(), id_tile11.getIconHeight());
		back_base.add(d_tile11);
		
		d_tile12 = new JLabel(id_tile12);
		d_tile12.setBounds(1000, 650, id_tile12.getIconWidth(), id_tile12.getIconHeight());
		back_base.add(d_tile12);
		
		d_tile13 = new JLabel(id_tile13);
		d_tile13.setBounds(1000, 650, id_tile13.getIconWidth(), id_tile13.getIconHeight());
		back_base.add(d_tile13);
		
		d_tile14 = new JLabel(id_tile14);
		d_tile14.setBounds(1000, 650, id_tile14.getIconWidth(), id_tile14.getIconHeight());
		back_base.add(d_tile14);
		
		d_tile15 = new JLabel(id_tile15);
		d_tile15.setBounds(1000, 650, id_tile15.getIconWidth(), id_tile15.getIconHeight());
		back_base.add(d_tile15);
		
		d_tile16 = new JLabel(id_tile16);
		d_tile16.setBounds(1000, 650, id_tile16.getIconWidth(), id_tile16.getIconHeight());
		back_base.add(d_tile16);
		
		d_tile17 = new JLabel(id_tile17);
		d_tile17.setBounds(1000, 650, id_tile17.getIconWidth(), id_tile17.getIconHeight());
		back_base.add(d_tile17);
	}
	
	private void addPlatform(int x, int y, int width, int height) {
        Rectangle platform = new Rectangle(x, y, width, height);
        platformBounds.add(platform);
    }
	private void initPlatforms() {
	    // Platform 1
		addPlatform(0, 500, 100, 150); // 첫 번째 계단
		addPlatform(120, 400, 50, 120); // 두 번째 계단
		addPlatform(200, 400, 30, 90); // 세 번째 계단
	    // Platform 2
	    addPlatform(0, 100, 100, 100); // 첫 번째 계단

	    // Platform 3
	    addPlatform(350, 530, 150, 20); // 첫 번째 계단
 
	    // Platform 4
	    addPlatform(0, 100, 100, 100); // 첫 번째 계단
	    addPlatform(420, 300, 330, 50); // 세 번째 계단
	    addPlatform(470, 200, 210, 100);
	    addPlatform(520, 150, 100, 50);
	    addPlatform(560, 100, 20, 50);

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
	    addPlatform(800, 88, 80, 20); // 첫 번째 계단

	    // Platform 10
	    addPlatform(1100, 200, 400, 100); // 첫 번째 계단
	    addPlatform(1400, 300, 100, 50);
	    addPlatform(1400, 350, 400, 100);
	    
	    // Platform 11
	    addPlatform(100, 650, 450, 50);
	    
	 // Platform 12
	    addPlatform(950 , 650, 850, 50);
	}
}
