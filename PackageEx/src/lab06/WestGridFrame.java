package lab06;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WestGridFrame extends JFrame {
    public WestGridFrame() {
        super("West Grid 프레임"); 
        setSize(300, 350);  // 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout()); // 보더 레이아웃 동쪽과 센터
        contentPane.add(new WestPanel(), BorderLayout.WEST);
        contentPane.add(new CenterPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    class WestPanel extends JPanel {
        
        public WestPanel() {
            setLayout(new GridLayout(10, 1)); // 그리드.. 새로 1열 
            Color[] btnColors = { // 버튼색 배열
                Color.RED, Color.LIGHT_GRAY, Color.BLUE, Color.YELLOW,
                Color.CYAN, Color.GRAY, Color.PINK, Color.GREEN,
                Color.ORANGE, Color.MAGENTA
            };

            // 버튼 추가
            for (int i = 0; i < 10; i++) {
                JButton colorButton = new JButton(); 
                colorButton.setBackground(btnColors[i]);
                add(colorButton); 
            }
        }
    }

    class CenterPanel extends JPanel {
        public CenterPanel() {
            setLayout(null); // 배치관리자 없음
            setBackground(Color.WHITE); 

            // 50~200 사이 좌표 범위 랜덤 값 지정하여 라벨 찍어준다.
            for (int i = 0; i < 10; i++) {
                int x = (int)(Math.random() * (200 - 50 + 1)) + 50; 
                int y = (int)(Math.random() * (200 - 50 + 1)) + 50; 
                
                JLabel numberLabel = new JLabel(String.valueOf(i));
                numberLabel.setForeground(Color.RED); 
                numberLabel.setBounds(x, y, 20, 20); 
                
                add(numberLabel); 
            }
        }
    }

    public static void main(String[] args) {
        new WestGridFrame();
    }
}