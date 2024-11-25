package project;


import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;


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
    MainMap mainMap;
    
	Direction direction;
	boolean left;
	boolean right;
	boolean up;
	boolean down;
	boolean idle;
	boolean dead;
	boolean loop;

	private final int SPEED = 4;
	private final int JUMPSPEED = 2;
    
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
    private int rightDeadIndex = 0; // 왼쪽 기본 인덱스
    private int leftDeadIndex = 0; // 왼쪽 기본 인덱스
	private boolean isActive = false;
	private boolean onGround = false;

    public Bear() {
    	this.position = new Point(0, 400);
    	character = new JLabel(); // JLabel 초기화
	    character.setBounds(position.x, position.y, 85, 93); // 초기 위치 설정
    	loadMoveImage();
    	left = false;
    	right = false;
    	up = false;
    	down = false;
    	idle = false;
    	loop = false;
    	direction = Direction.RIGHT;
    	mainMap = new MainMap();
    	idle();
    	gameLoop();
    }
    public JLabel getCharacter() {
        return character;
    }

    public boolean getIsActive() {
    	return isActive;
    }
    
    @Override
    public void initIndex() {
    	rightMoveIndex = 0; // 현재 이미지 인덱스
        leftMoveIndex = 0; // 현재 이미지 인덱스
        rightIdleIndex = 0; // 오른쪽 기본 인덱스
        leftIdleIndex = 0; // 왼쪽 기본 인덱스
        rightDeadIndex = 0; // 왼쪽 기본 인덱스
        leftDeadIndex = 0; // 왼쪽 기본 인덱스
    }
    
    private void loadMoveImage() {
        // 캐릭터 이미지 파일들을 로드하고 크기 조정
        i_rightMove = new ArrayList<>();
        i_leftMove = new ArrayList<>();
        i_leftIdle = new ArrayList<>();
        i_rightIdle = new ArrayList<>();
        i_rightJump = new ArrayList<>();
        i_leftJump = new ArrayList<>();
        i_rightDead = new ArrayList<>();
        i_leftDead = new ArrayList<>();
        ImageIcon o_icon;
        Image s_image;
        // 12개의 걷기 밑 기본 애니메이션 이미지를 로드
        for (int i = 1; i <= 12; i++) { // 0부터 11까지
        	String filePath = basePath + "/move_left/left" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath);
            s_image = o_icon.getImage().getScaledInstance(71, 93, Image.SCALE_SMOOTH);
            ImageIcon leftMove = new ImageIcon(s_image);
            
            String filePath1 = basePath + "/move_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath1);
            s_image = o_icon.getImage().getScaledInstance(71, 93, Image.SCALE_SMOOTH);
            ImageIcon rightMove = new ImageIcon(s_image);  
            
            String filePath2 = basePath + "/idle_left/left" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath2);
            s_image = o_icon.getImage().getScaledInstance(71, 93, Image.SCALE_SMOOTH);
            ImageIcon leftIdle = new ImageIcon(s_image);
            
            String filePath3 = basePath + "/idle_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath3);
            s_image = o_icon.getImage().getScaledInstance(71, 93, Image.SCALE_SMOOTH);
            ImageIcon rightIdle = new ImageIcon(s_image); 

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
        // 2개의 점프 이미지 로드
        for (int i = 1; i <= 2; i++) {
        	String filePath4 = basePath + "/jump_left/jump" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath4);
            s_image = o_icon.getImage().getScaledInstance(85, 93, Image.SCALE_SMOOTH);
        	ImageIcon leftJump = new ImageIcon(s_image);
        	String filePath5 = basePath + "/jump_right/jump" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath5);
            s_image = o_icon.getImage().getScaledInstance(85, 93, Image.SCALE_SMOOTH);
        	ImageIcon rightJump = new ImageIcon(s_image);
        	
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
        int newHeight = 100; // 원하는 높이
        for (int i = 1; i <= 8; i++) {
        	String filePath6 = basePath + "/dead_left/left" + String.format("%02d.png", i);
        	String filePath7 = basePath + "/dead_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath6);
            s_image = o_icon.getImage().getScaledInstance(newHeight, o_icon.getIconWidth() * newHeight / o_icon.getIconHeight(), Image.SCALE_SMOOTH);
        	ImageIcon leftDead = new ImageIcon(s_image);
            o_icon = new ImageIcon(filePath7);
            s_image = o_icon.getImage().getScaledInstance(newHeight, o_icon.getIconWidth() * newHeight / o_icon.getIconHeight(), Image.SCALE_SMOOTH);
        	ImageIcon rightDead = new ImageIcon(s_image);
        	
        	if (leftDead.getIconWidth() == -1) {
        		System.out.println("이미지를 로드할 수 없습니다: " + filePath6);
        	} else {
        		i_leftDead.add(leftDead);
        	}
        	if (rightDead.getIconWidth() == -1) {
        		System.out.println("이미지를 로드할 수 없습니다: " + filePath7);
        	} else {
        		i_rightDead.add(rightDead);
        	}
        }
    }
    
    // 캐릭터 이동
    private boolean moveCharacter(int deltaX) {
        int nextX = position.x + deltaX;
        List<Rectangle> platforms = mainMap.getPlatforms();
        // 좌우 충돌 확인
        for (Rectangle platform : platforms) {
            if (new Rectangle(nextX, position.y, 85, 93).intersects(platform)) {
            	System.out.printf("netx : %d, position.y = %d platform.x = %d platform.y = %d\n", 
            			nextX, position.y, platform.x, platform.y);
                // 충돌 발생: 이동 제한
                return false;
            }
        }

        // 충돌 없으면 이동
        return true;
    }

 // 캐릭터 위치를 업데이트하고 화면과 동기화
    private void updateCharacterPosition() {
        character.setBounds(position.x - mapOffsetX, position.y, 85, 93); // 캐릭터 위치 설정
//        gamePanel.scrollRectToVisible(new Rectangle(mapOffsetX, 0, screenWidth, 700)); // 화면 스크롤 동기화
//        gamePanel.repaint(); // 패널 다시 그리기
    }
    
    private boolean upCharacter() {
        position.y -= JUMPSPEED; // 위로 이동

        List<Rectangle> platforms = mainMap.getPlatforms();
    	for (Rectangle platform : platforms) {
    		if (new Rectangle(position.x, position.y - JUMPSPEED, 85, 93).intersects(platform)) {
    			 if (position.y + 50 > platform.y && position.y < platform.y + platform.height) {
    	                position.y += JUMPSPEED; // 플랫폼 위로 이동
    	                return true; // 더 이상 충돌 확인 불필요
    	            }
    	            onGround = false; // 점프 중이므로 착지 상태는 false
    	            
    	        }
    	    }

    	return false;
    }
    
    private boolean downCharacter() {
//        if (onGround) return; // 땅에 있다면 아래로 이동하지 않음

        position.y += JUMPSPEED; // 중력 효과로 아래로 이동
        List<Rectangle> platforms = mainMap.getPlatforms();

        for (Rectangle platform : platforms) {
            // 충돌 확인
            if (new Rectangle(position.x, position.y + JUMPSPEED, 85, 93).intersects(platform)) {
                position.y -= JUMPSPEED; // 캐릭터 크기 고려
                onGround = true; // 착지 상태 설정
                return true; // 충돌 후 더 이상 확인 불필요
            }
        }

        // 충돌이 없으면 계속 아래로 이동
        onGround = false;
        return false;
    }
    
	@Override
	public void up() {
		if(!up && !down) {
			onGround = false;
			up = true;
	        new Thread(() -> {
	            for (int i = 0; i < 120; i++) {
                    if (!upCharacter()) { // 호출 추가
                    	character.setIcon(direction == Direction.RIGHT ? i_rightJump.get(0) : i_leftJump.get(0));
                    	updateCharacterPosition();
                    } else 
                    	break;
	                
	                try {
	                    Thread.sleep(5);
	                } catch (Exception e) {
	                    System.out.println("위쪽 이동 중 인터럽트 발생 : " + e.getMessage());
	                }
	            }
	            up = false;
	            down(); // 점프가 끝나면 하강 호출
	        }).start();
		}
	}

	@Override
	public void down() {
		if (!down) {
			down = true;
	        new Thread(() -> {
	            while (!onGround) { // 중력 효과
	                if (downCharacter()) { // 호출 추가
	                	System.out.println("바닥도착");
	                	character.setIcon(direction == Direction.RIGHT ? i_rightIdle.get(1) : i_rightIdle.get(1));
	                	updateCharacterPosition();
	                	break;
	                }
	                updateCharacterPosition();
                	character.setIcon(direction == Direction.RIGHT ? i_rightJump.get(1) : i_leftJump.get(1));
	                try {
	                    Thread.sleep(5); // 중력 효과 간격
	                } catch (Exception e) {
	                    System.out.println("아래쪽 이동 중 인터럽트 발생 : " + e.getMessage());
	                }
	            }
	            down = false;
	            idle(); // 땅에 착지하면 기본 상태로 전환
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
			    	gameLoop();
					position.x = position.x - SPEED;
			    	if (!moveCharacter(-SPEED)) {
			    		position.x = position.x + SPEED;
			    	}
					updateCharacterPosition();
					if (!up && !down) {
						leftMoveIndex = (leftMoveIndex + 1) % i_leftMove.size();
						character.setIcon(i_leftMove.get(leftMoveIndex));
					}
					try {
						Thread.sleep(30);
					} catch (Exception e) {
						System.out.println("왼쪽 이동중 인터럽트 발생 : " + e.getMessage());
					}
				}
				left = false;
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
			    	gameLoop();
					position.x = position.x + SPEED;
			    	if (!moveCharacter(SPEED)) {
			    		position.x = position.x - SPEED;
			    	}
					updateCharacterPosition();
					if (!up && !down) {
						rightMoveIndex = (rightMoveIndex + 1) % i_rightMove.size();
						character.setIcon(i_rightMove.get(rightMoveIndex));
					}
					try {
						Thread.sleep(30);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
				right = false;
			}).start();
		}
	}
	@Override
	public void idle() {
		if (!idle) {
			idle = true;
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
	@Override
	public void dead() {
		idle = false;
		left = false;
		right = false;
		new Thread(() -> {
			if (direction == Direction.LEFT) {
				for (int i = 0; i < i_rightDead.size() * 10; i++) {
					position.x = position.x + 1;
					position.y = position.y - 1;
					if (i % 20 == 0) {
						rightDeadIndex = (rightDeadIndex + 1) % i_rightDead.size();
						character.setIcon(i_rightDead.get(rightDeadIndex));
					}
					updateCharacterPosition();
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
				for (int i = i_rightDead.size() * 10; i > 0; i--) {
					position.x = position.x + 1;
					position.y = position.y + 1;
					
					if (i % 20 == 0) {
			        	rightDeadIndex = (rightDeadIndex + 1) % i_rightDead.size();
			        	character.setIcon(i_rightDead.get(rightDeadIndex));
					}
					updateCharacterPosition();
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
			} else if (direction == Direction.RIGHT) {
				for (int i = 0; i < i_leftDead.size() * 10; i++) {
					position.x = position.x - 1;
					position.y = position.y - 1;
			        updateCharacterPosition();
			        
					if (i % 20 == 0) {
						leftDeadIndex = (leftDeadIndex + 1) % i_leftDead.size();
			        	character.setIcon(i_leftDead.get(leftDeadIndex));
					}
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
				for (int i = i_leftDead.size() * 10; i > 0; i--) {
					position.x = position.x - 1;
					position.y = position.y + 1;
			        updateCharacterPosition();
					if (i % 20 == 0) {
						leftDeadIndex = (leftDeadIndex + 1) % i_leftDead.size();
			        	character.setIcon(i_leftDead.get(leftDeadIndex));
					}
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("왼쪽 이동 중 인터럽트 발생 : " + e.getMessage());
					}
				}
			}
		}).start();

		
	}
	@Override
	public void left_released() {
    	left = false;
    	idle();
	}
	@Override
	public void right_released() {
    	right = false;
    	idle();
		
	}
	public void gameLoop() {
		if (!loop) {
			loop = true;
		    Timer timer = new Timer(16, e -> {
		        if (!up && !down) {
		            if (!downCharacter()) { // 중력 효과
		            	down();
		            }
		        }
		        updateCharacterPosition();
		    });
		    timer.start();
		}
	}
}
