package practice01;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HelloEx2 {
	private JFrame frame;
	
	public HelloEx2() {
		frame = new JFrame("HelloEx2");
		
		buildGUI();
		
		frame.setSize(200, 100);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void buildGUI() {
		frame.add(createInputPanel(), BorderLayout.CENTER);
		
		JLabel label = new JLabel("Hello World");
		frame.add(label, BorderLayout.SOUTH);
	}
	
	private JPanel createInputPanel() {
		JTextField text = new JTextField(10);
		JButton button = new JButton("확인");

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(text);
		panel.add(button);
		
		panel.setBackground(Color.orange);
		return panel;
	}

}
