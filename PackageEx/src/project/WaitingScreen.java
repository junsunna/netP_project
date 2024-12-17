package project;

import javax.swing.*;
import java.awt.*;

public class WaitingScreen extends JPanel {
    private JLabel waitingLabel;
    private JButton cancelButton;

    public WaitingScreen() {
        setLayout(null);
        initializeComponents();
    }

    private void initializeComponents() {
        // 대기 중인 상태를 표시하는 레이블
        waitingLabel = new JLabel("대기 중... 다른 플레이어를 기다리고 있습니다.", SwingConstants.CENTER);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        waitingLabel.setBounds(100, 200, 400, 50);
        add(waitingLabel);

        // 취소 버튼
        cancelButton = new JButton("취소");
        cancelButton.setBounds(250, 300, 100, 30);
        add(cancelButton);

        // 버튼 리스너 추가
        cancelButton.addActionListener(e -> onCancel());
    }

    private void onCancel() {
        // 취소 버튼 클릭 시 동작
        JOptionPane.showMessageDialog(this, "대기 취소됨. 메인 화면으로 돌아갑니다.");
        ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose(); // 현재 창 닫기
    }
}
