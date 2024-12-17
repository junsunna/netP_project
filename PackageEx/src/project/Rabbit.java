package project;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Rabbit implements Moveable{
    public static final String LEFT_MOVE = "LEFT_MOVE";
    public static final String RIGHT_MOVE = "RIGHT_MOVE";
    public static final String LEFT_JUMP = "LEFT_JUMP";
    public static final String RIGHT_JUMP = "RIGHT_JUMP";
    public static final String ACTIVE = "ACTIVE";
    public static final String LEFT_IDLE = "LEFT_IDLE";
    public static final String RIGHT_IDLE = "RIGHT_IDLE";
    public static final String basePath = "images/move/rabbit";
    public static final String LEFT_AIR_MOVE = "LEFT_AIR_MOVE";
    public static final String RIGHT_AIR_MOVE = "RIGHT_AIR_MOVE";
    private Point position;
    MainMap mainMap;
    public static final int charWidth = 85;
    public static final int charHeight = 93;
    
	Direction direction;
	boolean left = false;
	boolean right = false;
	boolean up = false;
	boolean down = false;
	boolean idle = false;
	boolean isDead = false;
	boolean loop = false;
	boolean player = false;
	boolean push = false;

	private final int SPEED = 6;
	private final int JUMPSPEED = 1;
	private int heart;
    private OptionPane o_pane;
	private JPanel m_map;
    private JLabel character;
    private int mapOffsetX = 0;
//    private final int mapWidth = 1800;
//    private final int screenWidth = 700;
    private ArrayList<ImageIcon> i_rightMove; // 오른쪽 이동
    private ArrayList<ImageIcon> i_leftMove; // 왼쪽 이동
    private ArrayList<ImageIcon> i_leftJump; // 왼쪽 점프
    private ArrayList<ImageIcon> i_rightJump; // 오른쪽 점프
    private ArrayList<ImageIcon> i_leftThrow; // 왼쪽 공중제비
    private ArrayList<ImageIcon> i_rightThrow; // 왼쪽 공중제비
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
    private int rightThrowIndex = 0;
    private int leftThrowIndex = 0;
	private boolean isActive = false;
	private boolean onGround = false;
	private boolean endDoor = false;
	private boolean switch1 = false;
	private boolean switch2 = false;
	private boolean switch3 = false;

	private int walkToggle = 0;
	
	private Map<Integer, Rectangle> platforms;
    private Map<Integer, JLabel> bulletTiles;
    
    private SoundPlayer walkSound, bulletSound, jumpSound, deadSound, throwSound;
	
    int panelWidth = 715; 
    int mapWidth = 1800;
    int screenCenterX = panelWidth / 2;
	int mapX = 0; // 맵의 X 좌표
	
	int newWidth = 190;
    int newHeight= 190;
	
    public Rabbit(MainMap mainMap, OptionPane o_pane) {
    	this.mainMap = mainMap;
    	m_map = mainMap.getMainMap();
    	platforms = mainMap.getPlatforms();
    	this.position = new Point(140, 530);
    	character = new JLabel(); // JLabel 초기화
	    character.setBounds(position.x, position.y, newWidth, newHeight); // 초기 위치 설정
    	loadMoveImage();

    	this.o_pane = o_pane;
    	heart = 3;
    	direction = Direction.RIGHT;
    	idle();
		walkSound = new SoundPlayer();
		walkSound.loadSound("audios/walk.wav"); // 사운드 파일 경로 설정
		jumpSound = new SoundPlayer();
		jumpSound.loadSound("audios/jump.wav"); // 사운드 파일 경로 설정
		deadSound = new SoundPlayer();
		deadSound.loadSound("audios/dead2.wav"); // 사운드 파일 경로 설정
		bulletSound = new SoundPlayer();
		bulletSound.loadSound("audios/bullet.wav"); // 사운드 파일 경로 설정
		throwSound = new SoundPlayer();
		throwSound.loadSound("audios/throw.wav"); // 사운드 파일 경로 설정
    }
    public JLabel getCharacter() {
        return character;
    }

    public boolean getIsActive() {
    	return isActive;
    }
    public void setPlayer(boolean play) {
    	this.player = play;
    }
    
    @Override
    public void initIndex() {
    	rightMoveIndex = 0; // 현재 이미지 인덱스
        leftMoveIndex = 0; // 현재 이미지 인덱스
        rightIdleIndex = 0; // 오른쪽 기본 인덱스
        leftIdleIndex = 0; // 왼쪽 기본 인덱스
        rightDeadIndex = 0; // 왼쪽 기본 인덱스
        leftDeadIndex = 0; // 왼쪽 기본 인덱스
        leftThrowIndex = 0;
        rightThrowIndex = 0;
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
        i_rightThrow = new ArrayList<>();
        i_leftThrow = new ArrayList<>();
        
        ImageIcon o_icon;
        Image s_image;

        // 12개의 걷기 밑 기본 애니메이션 이미지를 로드
        for (int i = 1; i <= 8; i++) {
            String filePath = basePath + "/move_left/left" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon leftMove = new ImageIcon(s_image);

            String filePath1 = basePath + "/move_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath1);


            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon rightMove = new ImageIcon(s_image);

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
        }

        for (int i = 1; i <= 9; i++) {
            String filePath2 = basePath + "/idle_left/left" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath2);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon leftIdle = new ImageIcon(s_image);

            String filePath3 = basePath + "/idle_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath3);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon rightIdle = new ImageIcon(s_image);

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
            String filePath4 = basePath + "/jump_left/left" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath4);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon leftJump = new ImageIcon(s_image);

            String filePath5 = basePath + "/jump_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath5);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
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

        for (int i = 1; i <= 10; i++) {
            String filePath6 = basePath + "/dead_left/left" + String.format("%02d.png", i);
            String filePath7 = basePath + "/dead_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath7);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon leftDead = new ImageIcon(s_image);

            o_icon = new ImageIcon(filePath6);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
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
        
        for (int i = 1; i <= 16; i++) {
            String filePath8 = basePath + "/throw_left/left" + String.format("%02d.png", i);
            String filePath9 = basePath + "/throw_right/right" + String.format("%02d.png", i);
            o_icon = new ImageIcon(filePath8);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon leftThrow = new ImageIcon(s_image);

            o_icon = new ImageIcon(filePath9);

            // 이미지 크기 조정
            s_image = o_icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon rightThrow = new ImageIcon(s_image);

            if (leftThrow.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath8);
            } else {
                i_leftThrow.add(leftThrow);
            }
            if (rightThrow.getIconWidth() == -1) {
                System.out.println("이미지를 로드할 수 없습니다: " + filePath9);
            } else {
                i_rightThrow.add(rightThrow);
            }
        }
    }
 
    
    // 캐릭터 이동
    private boolean moveCharacter(int deltaX) {
        int nextX = position.x + deltaX;
        
        checkBullet();
        // 좌우 충돌 확인
        for (Rectangle platform : platforms.values()) {
            if (new Rectangle(nextX + 50, position.y + 25, 85, 93).intersects(platform)) {
                if (((Platform) platform).id >= 32 && ((Platform) platform).id <= 38 ) {
              	  mainMap.reachCoin(((Platform) platform).id);
              	  return true;
              }
                return false;
            }
        }

        // 충돌 없으면 이동
        return true;
    }

 // 캐릭터 위치를 업데이트하고 화면과 동기화
    private void updateCharacterPosition() {
        character.setBounds(position.x - mapOffsetX, position.y, newWidth, newHeight); // 캐릭터 위치 설정
//        gamePanel.scrollRectToVisible(new Rectangle(mapOffsetX, 0, screenWidth, 700)); // 화면 스크롤 동기화
//        gamePanel.repaint(); // 패널 다시 그리기
    }
	private void updateMap() {
	    // 캐릭터의 현재 위치
	    int characterX = position.x;
	    // mapX는 맵의 위치를 나타내므로, 화면의 중앙으로 맞추기 위해 캐릭터 위치를 기준으로 이동
	    if (characterX > screenCenterX && mapX > -(mapWidth - panelWidth) && mapX > -(mapWidth - screenCenterX - 20)) {
	        // 캐릭터가 중앙을 넘어가면 맵을 오른쪽으로 이동
	    	screenCenterX += SPEED;
	        mapX -= SPEED;  // 맵을 오른쪽으로 5픽셀 이동
	    } else if (characterX < screenCenterX && mapX < 0) {
	        // 캐릭터가 중앙을 넘어가면 맵을 왼쪽으로 이동
	        mapX += SPEED;  // 맵을 왼쪽으로 5픽셀 이동
	    	screenCenterX -= SPEED;
	    }

	    // 맵의 위치 업데이트
	    m_map.setLocation(mapX, 0);
	}
	
    private void checkBullet() {
        bulletTiles = mainMap.getBullet();
        for (Map.Entry<Integer, JLabel> entry : bulletTiles.entrySet()) {
            JLabel bulletLabel = entry.getValue();
            Rectangle bulletBounds = bulletLabel.getBounds(); // bullet의 위치와 크기

            // 충돌 여부 확인
            if (new Rectangle(position.x + 60, position.y + 15, charWidth - 20, charHeight - 10).intersects(bulletBounds)) {
            	new Thread(() -> bulletSound.playSound()).start();
            	dead();
            	return;
            }
        }
    }
    
    private boolean upCharacter() {
        position.y -= JUMPSPEED; // 위로 이동
        
        checkBullet();
    	for (Rectangle platform : platforms.values()) {
    		if (new Rectangle(position.x + 50, position.y + 25, charWidth, charHeight).intersects(platform)) {
                if (((Platform) platform).id >= 32 && ((Platform) platform).id <= 38 ) {
                	  mainMap.reachCoin(((Platform) platform).id);
                	  return false;
                  }
            	if (!switch2 && ((Platform) platform).id == 18) {
            	    mainMap.dynamicButton(17);
            	    switch2 = true; // 플래그를 즉시 갱신
            	    return true;
            	}
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
        position.y += JUMPSPEED; // 중력 효과로 아래로 이동
        
        checkBullet();
        
        for (Rectangle platform : platforms.values()) {
            // 충돌 확인
            if (new Rectangle(position.x + 50, position.y + 25, charWidth, charHeight).intersects(platform)) {
                if (((Platform) platform).id >= 32 && ((Platform) platform).id <= 38 ) {
              	  	mainMap.reachCoin(((Platform) platform).id);
              	  	onGround = false;
              	  	return false;
                }
                position.y -= JUMPSPEED; // 캐릭터 크기 고려
                onGround = true; // 착지 상태 설정

              if (((Platform) platform).id == 24 || ((Platform) platform).id == 30 ) {
               	dead();
              }
                
                return true; // 충돌 후 더 이상 확인 불필요
            }
        }
        // 충돌이 없으면 계속 아래로 이동
        onGround = false;
        return false;
    }
    
	@Override
	public void up() {
		if(!up && !down && !push) {
			onGround = false;
			up = true;
	        new Thread(() -> {
			    new Thread(() -> jumpSound.playSound()).start();
	            for (int i = 0; i < 120; i++) {
                    if (!upCharacter()) { // 호출 추가
                    	character.setIcon(direction == Direction.RIGHT ? i_rightJump.get(0) : i_leftJump.get(0));
                    	updateCharacterPosition();
                    } else 
                    	break;
	                
	                try {
	                    Thread.sleep(1);
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
	                	if (!isDead) {
	                		character.setIcon(direction == Direction.RIGHT ? i_rightIdle.get(1) : i_leftIdle.get(1));
	                	}
	                	updateCharacterPosition();
	                	break;
	                }
	                updateCharacterPosition();
	                if (!isDead) {
	                	character.setIcon(direction == Direction.RIGHT ? i_rightJump.get(1) : i_leftJump.get(1));
	                }
	                try {
	                    Thread.sleep(1); // 중력 효과 간격
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
				while (left && !push) {
			    	gameLoop();
					position.x = position.x - SPEED;
			    	if (!moveCharacter(-SPEED)) {
			    		position.x = position.x + SPEED;
			    	} else if (player){
						updateMap();
			    	}
					updateCharacterPosition();
					if (!up && !down && !push) {
						leftMoveIndex = (leftMoveIndex + 1) % i_leftMove.size();
						character.setIcon(i_leftMove.get(leftMoveIndex));
						walkToggle++;
						if (walkToggle == 4) {
						    new Thread(() -> walkSound.playSound()).start();
							walkToggle = 0;
						}
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
		if (!right && !push) {
			right = true;
			direction = Direction.RIGHT;
			new Thread(() -> {
				while (right) {
			    	gameLoop();
					position.x = position.x + SPEED;
			    	if (!moveCharacter(SPEED)) {
			    		position.x = position.x - SPEED;
			    	} else if (player){
						updateMap();
			    	}
					updateCharacterPosition();
					if (!up && !down && !push) {
						rightMoveIndex = (rightMoveIndex + 1) % i_rightMove.size();
						character.setIcon(i_rightMove.get(rightMoveIndex));
						walkToggle++;
						if (walkToggle == 4) {
						    new Thread(() -> walkSound.playSound()).start();

							walkToggle = 0;
						}
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
					checkBullet();
					if (direction == Direction.LEFT && !up && !down && !right && !left && !push && !isDead) {
						
						updateCharacterPosition();
						leftIdleIndex = (leftIdleIndex + 1) % i_leftIdle.size();
						character.setIcon(i_leftIdle.get(leftIdleIndex));
					} else if (direction == Direction.RIGHT && !up && !down && !right && !left && !push && !isDead) {
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
		if (isDead) return;
		isDead = true;
	    new Thread(() -> deadSound.playSound()).start();
		idle = true;
		left = true;
		right = true;
		up = true;
		down = true;
		heart -= 1;
		o_pane.updateHeart(heart);
		new Thread(() -> {
			if (direction == Direction.LEFT) {
				for (int i = 0; i < i_rightDead.size() + 92; i++) {
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
						System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
					}
				}
				int delay = 1;
				while (true) {
					delay++;
					if (downCharacter()) break;
//					position.x = position.x + 1;
					position.y = position.y + 1;
					if (delay % 20 == 0 && rightDeadIndex != 0) {
						rightDeadIndex = (rightDeadIndex + 1) % i_rightDead.size();
						if (rightDeadIndex == 0) {
							updateCharacterPosition();
			        		continue;
			        	}
						
			        	character.setIcon(i_rightDead.get(rightDeadIndex));
					}
					updateCharacterPosition();
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
					}
				}
			} else if (direction == Direction.RIGHT) {
				for (int i = 0; i < i_leftDead.size() + 92; i++) {
					position.x = position.x - 1;
					position.y = position.y - 1;
			        
					if (i % 20 == 0) {
						leftDeadIndex = (leftDeadIndex + 1) % i_leftDead.size();
			        	character.setIcon(i_leftDead.get(leftDeadIndex));
					}
			        updateCharacterPosition();
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
					}
				}
				int delay = 1;
				while (true) {
					delay++;
					if (downCharacter()) break;
//					position.x = position.x - 1;
					position.y = position.y + 1;
					if (delay % 20 == 0 && leftDeadIndex != 0) {
						leftDeadIndex = (leftDeadIndex + 1) % i_leftDead.size();
						
						if (leftDeadIndex == 0) {
							updateCharacterPosition();
			        		continue;
			        	}
						
			        	character.setIcon(i_leftDead.get(leftDeadIndex));
					}
					updateCharacterPosition();
					try {
						Thread.sleep(8);
					} catch (Exception e) {
						System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
					}
				}
			}
			try {
				Thread.sleep(300);
			} catch (Exception e) {
				System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
			}
			if (heart == 0) {
				return;
			}
	        
			initIndex();
			// 초기 위치로 설정
	        position.x = 140; // 초기 x 좌표
	        position.y = 530; // 초기 y 좌표
	        updateCharacterPosition(); // 위치 업데이트
	        
	    	left = false;
	    	right = false;
	    	up = false;
	    	down = false;
	    	idle = false;
	    	loop = false;
	    	isDead = false;
	        idle();
	        
	        if (player) {
	        // 맵 초기화
	        	mapX = 0; // 맵의 초기 X 위치
	        	screenCenterX = panelWidth / 2; // 화면 중앙 초기화
	        	m_map.setLocation(mapX, 0); // 맵의 위치 업데이트
	        }
		}).start();	
	}
	@Override
	public void left_released() {
    	left = false;
    	idle();
    	initIndex();
	}
	@Override
	public void right_released() {
    	right = false;
    	idle();
    	initIndex();
	}
	@Override
	public void setIdle() {
		idle = false;
	}
	public void gameLoop() {
		if (!loop) {
			loop = true;
		    Timer timer = new Timer(16, e -> {
		        if (!up && !down && !push) {
		            if (!downCharacter()) { // 중력 효과
		            	down();
		            }
		        }
		        updateCharacterPosition();
		    });
		    timer.start();
		}
	}
	
	@Override
	public void push() {
		if (!push) {
			push = true;
			onGround = false;
		    new Thread(() -> throwSound.playSound()).start();
		    new Thread(() -> {
				int tog = 0;
				if (direction == Direction.RIGHT) {
					for (int i = 0; i < i_rightThrow.size() * 12; i++) {
						position.x = position.x + 2;
						if (tog == 4) {
							tog = 0;
		                    if (!upCharacter() && moveCharacter(SPEED)) { // 호출 추가
		                    	updateCharacterPosition();
								if (player){
									updateMap();
						    	}
								rightThrowIndex = (rightThrowIndex + 1) % i_rightThrow.size();
								character.setIcon(i_rightThrow.get(rightThrowIndex));
								
		                    } else {
		                    	break;
		                    }
						}
						tog++;
						updateCharacterPosition();
						try {
							Thread.sleep(8);
						} catch (Exception e) {
							System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
						}
					}
					int delay = 0;
					character.setIcon(i_rightJump.get(1));
					while (!onGround) {
						delay++;
						position.x = position.x + 1;
						position.y = position.y + 1;
						if (downCharacter()) {
		                	updateCharacterPosition();
							break;
						}
				    	
						if (delay % 4 == 0) {
							if (player){
								updateMap();
					    	}
				        	
						}
						updateCharacterPosition();
						try {
							Thread.sleep(8);
						} catch (Exception e) {
							System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
						}
					}
				} else if (direction == Direction.LEFT) {
					for (int i = 0; i < i_leftThrow.size() * 12; i++) {
						position.x = position.x - 2;
						if (tog == 4) {
							tog = 0;
		                    if (!upCharacter()) { // 호출 추가
		                    	updateCharacterPosition();
								if (i % 4 == 0) {
									if (player){
										updateMap();
							    	}
									leftThrowIndex = (leftThrowIndex + 1) % i_leftThrow.size();
						        	character.setIcon(i_leftThrow.get(leftThrowIndex));
								}
		                    } else {
		                    	break;
		                    }
						}
						tog++;
				        
				        updateCharacterPosition();
						try {
							Thread.sleep(8);
						} catch (Exception e) {
							System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
						}
					}
					int delay = 0;
					character.setIcon(i_leftJump.get(1));
					while (!onGround) {
						delay++;
						position.x = position.x - 1;
						position.y = position.y + 1;
						if (downCharacter()) {
		                	updateCharacterPosition();
							break;
						}
						
						if (delay % 4 == 0) {
							if (player){
								updateMap();
					    	}
						}
						updateCharacterPosition();
						try {
							Thread.sleep(8);
						} catch (Exception e) {
							System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
						}
					}
				}
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					System.out.println("죽음 모션 중 인터럽트 발생 : " + e.getMessage());
				}
				if (heart == 0) {
					return;
				}
		        
				initIndex();
		        updateCharacterPosition(); // 위치 업데이트
		        
		        push = false;
		        idle();
			}).start();	
		}
		
	}

	@Override
    public void active(int deltaX) {
	    new Thread(() -> {
	        int nextX = position.x + deltaX;
	        for (Rectangle platform : platforms.values()) {
	            if (new Rectangle(nextX, position.y, charWidth, charHeight).intersects(platform)) {
	                // 충돌 발생: 이동 제한
	            	if (!endDoor && ((Platform) platform).id == 25) {
	            	    mainMap.reachDoorAnimation();
	            	    endDoor = true; // 플래그를 즉시 갱신
	            	}
	            	if (!switch1 && ((Platform) platform).id == 26) {
	            	    mainMap.dynamicSwitch();
	            	    mainMap.removePlatform(26);
	            	    switch1 = true; // 플래그를 즉시 갱신
	            	}
	            	if (!switch3 && ((Platform) platform).id == 29) {
	            	    mainMap.dynamicButton(19);
	            	    switch3 = true; // 플래그를 즉시 갱신
	            	}
	            }
	
	        }
		}).start();	
    }
	@Override
	public Rectangle getPosition() {
		 return new Rectangle(position.x, position.y, charWidth, charHeight);
	}
//	@Override
//	public boolean throw_active() {
//		// TODO Auto-generated method stub
//		return false;
//	}
}

