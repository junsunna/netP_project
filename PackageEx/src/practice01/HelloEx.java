package practice01;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class HelloEx {
	private JFrame frame;
	
	public HelloEx() {
		frame = new JFrame("HelloEx");
		
		buildGUI();
		
		
		frame.setSize(200, 80);
		frame.setLocation(500, 300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void buildGUI() {
		JTextField text = new JTextField();
		JButton button = new JButton("확인");
		
		JLabel label = new JLabel("Hello World");
		
		frame.add(text, BorderLayout.CENTER);
		frame.add(button, BorderLayout.EAST);
		frame.add(label, BorderLayout.SOUTH);
	}
}
