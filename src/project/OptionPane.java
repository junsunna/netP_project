package project;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class OptionPane {
	private static final String dBasePath = "images/tile/dynamic/";
	private static final String bBasePath = "images/button/";
	private JLayeredPane layeredPane;
    private JLabel[] heartLabels; // 하트 라벨 배열
    protected JButton b_setting, b_continue, b_exit, b_sound;
    private JFrame mainFrame;

    public void setMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }
	OptionPane() {
		// 1. JLayeredPane 생성
		layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        
        // 2. 하트 라벨 생성
        heartLabels = new JLabel[3];
        for (int i = 0; i < heartLabels.length; i++) {
            heartLabels[i] = new JLabel(new ImageIcon(dBasePath + "18.png")); // 기본 이미지
            heartLabels[i].setBounds(10 + i * 50, 10, 40, 35); // 좌측 상단에 나란히 배치
            layeredPane.add(heartLabels[i], Integer.valueOf(2)); // 상위 레이어
        }
        
        // 버튼 추가
        JButton b_setting = new JButton(new ImageIcon(bBasePath + "setting.png"));
        b_setting.setBorderPainted(false);  // 버튼 테두리 제거
        b_setting.setContentAreaFilled(false);  // 버튼 배경 제거
        b_setting.setBounds(970, 20, 80, 80); // 버튼 위치와 크기 설정
        b_setting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel darkBackground = new JPanel();
                darkBackground.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                darkBackground.setBackground(new Color(0, 0, 0, 150)); // 반투명한 검정색
                darkBackground.setLayout(null);
                
            	b_continue = new JButton(new ImageIcon(bBasePath + "continue.png"));
            	b_exit = new JButton(new ImageIcon(bBasePath + "exit.png"));
            	b_sound = new JButton(new ImageIcon(bBasePath + "sound.png"));
            	
            	int centerX = layeredPane.getWidth() / 2 - 100; // 버튼 위치 계산
                int centerY = layeredPane.getHeight() / 2 - 100;

                b_continue.setBounds(centerX, centerY - 60, 200, 50); // 첫 번째 버튼
                b_sound.setBounds(centerX, centerY, 200, 50); // 두 번째 버튼
                b_exit.setBounds(centerX, centerY + 60, 200, 50); // 세 번째 버튼

                b_continue.setBorderPainted(false);  // 버튼 테두리 제거
                b_continue.setContentAreaFilled(false);  // 버튼 배경 제거
                b_sound.setBorderPainted(false);  // 버튼 테두리 제거
                b_sound.setContentAreaFilled(false);  // 버튼 배경 제거
                b_exit.setBorderPainted(false);  // 버튼 테두리 제거
                b_exit.setContentAreaFilled(false);  // 버튼 배경 제거
                
                // 버튼 동작 정의
                b_continue.addActionListener(ev -> {
                    layeredPane.remove(darkBackground); // 어두운 배경 제거
                    if (mainFrame != null) {
//                        mainFrame.getGlassPane().setVisible(false); // GlassPane 비활성화
                        mainFrame.requestFocusInWindow(); // 메인 프레임에 포커스
                    }
                    layeredPane.repaint();
                });

                b_exit.addActionListener(ev -> {
                    System.exit(0); // 프로그램 종료
                });

                b_sound.addActionListener(ev -> {
                    System.out.println("사운드 설정");
                });

                // 어두운 배경 패널에 버튼 추가
                darkBackground.add(b_continue);
                darkBackground.add(b_sound);
                darkBackground.add(b_exit);

                // LayeredPane에 추가
                layeredPane.add(darkBackground, Integer.valueOf(3)); // 어두운 배경을 최상위 레이어로 추가
                layeredPane.repaint();
            }
        });
        layeredPane.add(b_setting, Integer.valueOf(2)); // 버튼을 상위 레이어에 추가
	}
	
	public JLayeredPane getPane() {
		return layeredPane;
	}
	
    // 하트 이미지를 동적으로 변경하는 메서드
    public void updateHeart(int index) {
        if (index >= 0 && index < heartLabels.length) {
            heartLabels[index].setIcon(new ImageIcon(dBasePath + "19.png"));
            layeredPane.repaint(); // 변경 사항 반영
        }
    }
}
