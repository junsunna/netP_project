package project;


import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Bear {
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String JUMP = "JUMP";
    public static final String ACTIVE = "ACTIVE";
    public static final String basePath = "images/move/bear";

    private Point position;
    
    private JLabel character;
    private int mapOffsetX = 0;
    private final int mapWidth = 1800;
    private final int screenWidth = 700;
    private ArrayList<ImageIcon> i_rightMove; // 오른쪽 이동
    private ArrayList<ImageIcon> i_leftMove; // 왼쪽 이동
    private ArrayList<ImageIcon> i_leftJump; // 왼쪽 점프
    private ArrayList<ImageIcon> i_rightJump; // 오른쪽 점프
    private ArrayList<ImageIcon> i_leftActive; // 왼쪽 상호작용
    private ArrayList<ImageIcon> i_rightActive; // 오른쪽 상호작용
    private ArrayList<ImageIcon> i_leftDead; // 왼쪽 죽음
    private ArrayList<ImageIcon> i_rightDead; // 오른쪽 죽음
    private ArrayList<ImageIcon> i_leftIdle; // 오른쪽 기본
    private ArrayList<ImageIcon> i_rightIdle; // 왼쪽 기본
    private int rightMoveIndex = 0; // 현재 이미지 인덱스
    private int leftMoveIndex = 0; // 현재 이미지 인덱스


    public Bear() {
    	this.position = new Point(100, 495);
    	character = new JLabel(); // JLabel 초기화
	    character.setBounds(position.x, position.y, 200, 200); // 초기 위치 설정
    	loadRightMoveImage();
    	loadLeftMoveImage();

    }
    public JLabel getCharacter() {
        return character;
    }

    
    private void loadRightMoveImage() {
        // 캐릭터 이미지 파일들을 로드하고 크기 조정
        i_rightMove = new ArrayList<>();

        // 12개의 걷기 애니메이션 이미지를 로드
        for (int i = 1; i <= 12; i++) { // 0부터 11까지
            String filePath = basePath + "/move_right/right" + String.format("%02d.png", i);
            ImageIcon originalIcon = new ImageIcon(filePath);

            // 이미지가 로드되지 않는 경우 디버그 메시지 출력
            if (originalIcon.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath);
            } else {
                // 크기 조정 및 이미지 추가
//                Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                i_rightMove.add(originalIcon);
            }
        }

        // 이미지가 로드되지 않을 경우 예외 방지
        if (i_rightMove.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
    }
    
    private void loadLeftMoveImage() {
        // 캐릭터 이미지 파일들을 로드하고 크기 조정
        i_leftMove = new ArrayList<>();

        // 12개의 걷기 애니메이션 이미지를 로드
        for (int i = 1; i <= 12; i++) { // 1부터 12까지
            String filePath = basePath + "/move_left/left" + String.format("%02d.png", i);
            ImageIcon originalIcon = new ImageIcon(filePath);

            // 이미지가 로드되지 않는 경우 디버그 메시지 출력
            if (originalIcon.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath);
            } else {
                // 크기 조정 및 이미지 추가
//                Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            	i_leftMove.add(originalIcon);
            }
        }

        // 이미지가 로드되지 않을 경우 예외 방지
        if (i_leftMove.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
    }
    
    public void move(String direction) {
    	switch(direction) {
    	case LEFT :
    		if (position.x > 0) {
    			position.x -= 8;
    			
                // 화면이 캐릭터를 따라 이동할지 결정
                if (position.x < mapOffsetX + screenWidth / 2 && mapOffsetX > 0) {
                    mapOffsetX = Math.max(0, mapOffsetX - 10);
                }
    			
                updateCharacterPosition();
                leftMoveIndex = (leftMoveIndex + 1) % i_leftMove.size();
                character.setIcon(i_leftMove.get(leftMoveIndex));
    		}
    		break;
    	case RIGHT :
    		if (position.x > 0) {
    			position.x += 8;
    			
                if (position.x < mapOffsetX + screenWidth / 2 && mapOffsetX > mapWidth - screenWidth) {
                    mapOffsetX = Math.min(mapWidth - screenWidth, mapOffsetX + 10);
                }
                updateCharacterPosition();
                rightMoveIndex = (rightMoveIndex + 1) % i_rightMove.size();
                character.setIcon(i_rightMove.get(rightMoveIndex));
    		}
    		break;
    	case JUMP :
    		break;
    	case ACTIVE :
    		break;
    	}
    }

 // 캐릭터 위치를 업데이트하고 화면과 동기화
    private void updateCharacterPosition() {
        character.setBounds(position.x - mapOffsetX, position.y, 200, 200); // 캐릭터 위치 설정
//        gamePanel.scrollRectToVisible(new Rectangle(mapOffsetX, 0, screenWidth, 700)); // 화면 스크롤 동기화
//        gamePanel.repaint(); // 패널 다시 그리기
    }
}
