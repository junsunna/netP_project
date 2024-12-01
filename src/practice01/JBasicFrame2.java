package practice01;

import javax.swing.JButton;
import javax.swing.JFrame;

public class JBasicFrame2 extends JFrame{
	public JBasicFrame2() {
		super("Frame Test02");
		
		
		// 프레임 구성
		buildGUI();
		
		this.setBounds(100, 200, 200, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
	}
	private void buildGUI() {
		JButton b = new JButton("확인");
		// Container cp = frame.getContentPane();
		// cp.add(b);
//		frame.add(b);
		
	}

}
