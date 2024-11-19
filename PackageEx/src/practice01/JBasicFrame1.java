package practice01;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class JBasicFrame1 {
	private JFrame frame;
	
	public JBasicFrame1() {
		frame = new JFrame();
		frame.setTitle("Frame Test1");
		// frame.setTitle("Frame Test1");
	
		// 프레임 구성
		buildGUI();
		
//		frame.setSize(200, 300);
//		frame.setLocation(100, 200);
		frame.setBounds(100, 200, 200, 300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		
		JButton btn;
		for (int i = 0; i < 5; i++) {
			btn = new JButton("" + (i + 1));
//			btn.setBounds(0, 40 * i, 100, 30);
			frame.add(btn);
		}
	}

}
