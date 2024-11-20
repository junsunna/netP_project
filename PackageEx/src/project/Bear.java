package project;


import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Bear implements Moveable{
    public static final String LEFT_MOVE = "LEFT_MOVE";
    public static final String RIGHT_MOVE = "RIGHT_MOVE";
    public static final String LEFT_JUMP = "LEFT_JUMP";
    public static final String RIGHT_JUMP = "RIGHT_JUMP";
    public static final String ACTIVE = "ACTIVE";
    public static final String LEFT_IDLE = "LEFT_IDLE";
    public static final String RIGHT_IDLE = "RIGHT_IDLE";
    public static final String basePath = "images/move/bear";
    public static final String LEFT_AIR_MOVE = "LEFT_AIR_MOVE";
    public static final String RIGHT_AIR_MOVE = "RIGHT_AIR_MOVE";
    private Point position;
    
	Direction direction;
	boolean left;
	boolean right;
	boolean up;
	boolean down;
	boolean idle;

	private final int SPEED = 3;
	private final int JUMPSPEED = 1;
    
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
    private int rightIdleIndex = 0; // 오른쪽 기본 인덱스
    private int leftIdleIndex = 0; // 왼쪽 기본 인덱스
    private int rightJumpIndex = 0; // 오른쪽 점프 인덱스
    private int leftJumpIndex = 0; // 왼쪽 점프 인덱스
	private boolean isActive = false;

    public Bear() {
    	this.position = new Point(100, 495);
    	character = new JLabel(); // JLabel 초기화
	    character.setBounds(position.x, position.y, 200, 200); // 초기 위치 설정
    	loadMoveImage();
    	left = false;
    	right = false;
    	up = false;
    	down = false;
    	idle = true;
    	direction = Direction.RIGHT;
    	idle();

    }
    public JLabel getCharacter() {
        return character;
    }

    public boolean getIsActive() {
    	return isActive;
    }
    
    public void initIndex() {
    	rightMoveIndex = 0; // 현재 이미지 인덱스
        leftMoveIndex = 0; // 현재 이미지 인덱스
        rightIdleIndex = 0; // 오른쪽 기본 인덱스
        leftIdleIndex = 0; // 왼쪽 기본 인덱스
    }
    
    private void loadMoveImage() {
        // 캐릭터 이미지 파일들을 로드하고 크기 조정
        i_rightMove = new ArrayList<>();
        i_leftMove = new ArrayList<>();
        i_leftIdle = new ArrayList<>();
        i_rightIdle = new ArrayList<>();
        i_rightJump = new ArrayList<>();
        i_leftJump = new ArrayList<>();
        
        // 12개의 걷기 밑 기본 애니메이션 이미지를 로드
        for (int i = 1; i <= 12; i++) { // 0부터 11까지
        	String filePath = basePath + "/move_left/left" + String.format("%02d.png", i);
            ImageIcon leftMove = new ImageIcon(filePath);
            String filePath1 = basePath + "/move_right/right" + String.format("%02d.png", i);
            ImageIcon rightMove = new ImageIcon(filePath1);  
            
            String filePath2 = basePath + "/idle_left/left" + String.format("%02d.png", i);
            ImageIcon leftIdle = new ImageIcon(filePath2);
            String filePath3 = basePath + "/idle_right/right" + String.format("%02d.png", i);
            ImageIcon rightIdle = new ImageIcon(filePath3); 

            if (leftMove.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath);
            } else {
            	i_leftMove.add(leftMove);
            }
            if (rightMove.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath1);
            } else {
                i_rightMove.add(rightMove);
            }
            if (leftIdle.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath2);
            } else {
            	i_leftIdle.add(leftIdle);
            }
            if (rightIdle.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath3);
            } else {
            	i_rightIdle.add(rightIdle);
            }
        }
        // 10개의 점프 이미지 로드
        for (int i = 1; i <= 10; i++) {
        	String filePath4 = basePath + "/jump_left/jump" + String.format("%02d.png", i);
        	ImageIcon leftJump = new ImageIcon(filePath4);
        	String filePath5 = basePath + "/jump_right/jump" + String.format("%02d.png", i);
        	ImageIcon rightJump = new ImageIcon(filePath5);
        	
        	if (leftJump.getIconWidth() == -1) {
        		System.out.println("이미지를 로드할 수 없습니다: " + filePath4);
        	} else {
        		i_leftJump.add(leftJump);
        	}
        	if (rightJump.getIconWidth() == -1) {
        		System.out.println("이미지를 로드할 수 없습니다: " + filePath5);
        	} else {
        		i_rightJump.add(rightJump);
        	}
        }

        // 이미지가 로드되지 않을 경우 예외 방지
        if (i_rightMove.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
        if (i_leftMove.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
        if (i_leftIdle.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
        if (i_rightIdle.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
        if (i_leftJump.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
        if (i_rightJump.isEmpty()) {
            System.out.println("이미지가 로드되지 않았습니다. 파일 경로를 확인하세요.");
        }
    }
    

 // 캐릭터 위치를 업데이트하고 화면과 동기화
    private void updateCharacterPosition() {
        character.setBounds(position.x - mapOffsetX, position.y, 200, 200); // 캐릭터 위치 설정
//        gamePanel.scrollRectToVisible(new Rectangle(mapOffsetX, 0, screenWidth, 700)); // 화면 스크롤 동기화
//        gamePanel.repaint(); // 패널 다시 그리기
    }
	@Override
	public void up() {
		if(!up && !down) {
			up = true;
			new Thread(() -> {
				if (direction == Direction.RIGHT) {
					for (int i = 0; i < 120; i++) {
						position.y = position.y - (JUMPSPEED);
	                    updateCharacterPosition();
	                    rightJumpIndex = i % (i_rightJump.size() / 2);
	                    character.setIcon(i_rightJump.get(rightJumpIndex));
						try {
							Thread.sleep(5);
						} catch (Exception e) {
							System.out.println("위쪽 이동중 인터럽트 발생 : " + e.getMessage());
						}
					}
				} 
				else {
					for (int i = 0; i < 120; i++) {
						position.y = position.y - (JUMPSPEED);
	                    updateCharacterPosition();
	                    leftJumpIndex = i % (i_leftJump.size() / 2);
	                    character.setIcon(i_leftJump.get(leftJumpIndex));
						try {
							Thread.sleep(5);
						} catch (Exception e) {
							System.out.println("위쪽 이동중 인터럽트 발생 : " + e.getMessage());
						}
					}
				}
				up = false;
				down();
				
			}).start();
		}
	}

	@Override
	public void down() {
		if (!down) {
			down = true;
			new Thread(() -> {
				if (down) {
					if (direction == Direction.RIGHT) {
						for (int i = 0; i < 120; i++) {
							position.y = position.y + (JUMPSPEED);
		                    updateCharacterPosition();
		                    rightJumpIndex = (i % (i_rightJump.size() / 2) + 5);
		                    character.setIcon(i_rightJump.get(rightJumpIndex));
							try {
								Thread.sleep(5);
							} catch (Exception e) {
								System.out.println("위쪽 이동중 인터럽트 발생 : " + e.getMessage());
							}
						}
					} 
					else {
						for (int i = 0; i < 120; i++) {
							position.y = position.y + (JUMPSPEED);
		                    updateCharacterPosition();
		                    leftJumpIndex = (i % (i_leftJump.size() / 2) + 5);
		                    character.setIcon(i_leftJump.get(leftJumpIndex));
							try {
								Thread.sleep(5);
							} catch (Exception e) {
								System.out.println("위쪽 이동중 인터럽트 발생 : " + e.getMessage());
							}
						}
					}
					down = false;
				}
			}).start();
		}
	}

	@Override
	public void left() {
		if (!left) {
			left = true;
			direction = Direction.LEFT;
			
			new Thread(() -> {
				while (left) {
					position.x = position.x - SPEED;
					updateCharacterPosition();
					
					leftMoveIndex = (leftMoveIndex + 1) % i_leftMove.size();
					character.setIcon(i_leftMove.get(leftMoveIndex));

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("왼쪽 이동중 인터럽트 발생 : " + e.getMessage());
					}
				}
			}).start();
		}
	}

	@Override
	public void right() {
		if (!right) {
			right = true;
			direction = Direction.RIGHT;
			
			new Thread(() -> {
				while (right) {
					position.x = position.x + SPEED;
					updateCharacterPosition();
					
					rightMoveIndex = (rightMoveIndex + 1) % i_rightMove.size();
					character.setIcon(i_rightMove.get(rightMoveIndex));
					
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
			}).start();
		}
	}
	@Override
	public void idle() {
		new Thread(() -> {
			while (idle) {
				if (direction == Direction.LEFT && !up && !down && !right && !left) {
			          updateCharacterPosition();
			          leftIdleIndex = (leftIdleIndex + 1) % i_leftIdle.size();
			          character.setIcon(i_leftIdle.get(leftIdleIndex));
					} else if (direction == Direction.RIGHT && !up && !down && !right && !left) {
			          updateCharacterPosition();
			          rightIdleIndex = (rightIdleIndex + 1) % i_rightIdle.size();
			          character.setIcon(i_rightIdle.get(rightIdleIndex));
					}
				try {
					Thread.sleep(30);
				} catch (Exception e) {
					System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
				}
			}
		}).start();

		
	}
}
