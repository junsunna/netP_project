package lab06;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FindImage extends JFrame {

 public FindImage() {
     super("16장의 카드의 뒷면에 숨겨진 이미지 찾기");
     setSize(500, 400); 
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     Container contentPane = getContentPane();

     contentPane.add(new NorthPanel(), BorderLayout.NORTH);
     contentPane.add(new CenterPanel(), BorderLayout.CENTER);
     contentPane.add(new SouthPanel(), BorderLayout.SOUTH);

     // 공백을 두는게 핵심 로직
     // 동 서에 레이아웃을 추가해서 공백을 확보함.
     JLabel eastLabel = new JLabel();
     eastLabel.setOpaque(true);
     eastLabel.setPreferredSize(new java.awt.Dimension(50, 0)); 

     contentPane.add(eastLabel, BorderLayout.EAST);

     JLabel westLabel = new JLabel();
     westLabel.setOpaque(true);
     westLabel.setPreferredSize(new java.awt.Dimension(50, 0));
     contentPane.add(westLabel, BorderLayout.WEST);

     // 프레임을 화면에 표시
     setVisible(true);
 }

 class NorthPanel extends JPanel {
     public NorthPanel() {
         setBackground(Color.YELLOW);
         setLayout(new FlowLayout()); 
         add(new JLabel("숨겨진 이미지 찾기"));
     }
 }

 class CenterPanel extends JPanel {
     public CenterPanel() {
         // 4x4 그리드에 마진을 10,10으로 둔다.
         setLayout(new GridLayout(4, 4, 10, 10));
         setBackground(Color.WHITE); // 흰색으로.. 문제와 똑같이 구성

     
         // 총 16개의 패널 추가, 숫자는 i번으로 0 ~ 15
         for (int i = 0; i < 16; i++) {
             JLabel L_pane = new JLabel(String.valueOf(i));
             
             L_pane.setOpaque(true); 
             L_pane.setBackground(Color.GREEN); 
             L_pane.setForeground(Color.BLACK); 
             
             add(L_pane); // 패널에 추가
         }
     }
 }

 class SouthPanel extends JPanel {
     public SouthPanel() {
         setBackground(Color.YELLOW);
         setLayout(new FlowLayout());
         add(new JButton("실행 시작"));
     }
 }

 public static void main(String[] args) {
     new FindImage(); // 프레임 생성
 }
}