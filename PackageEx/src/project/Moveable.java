package project;

import java.awt.Rectangle;

public interface Moveable {
	public abstract void up();
	public abstract void down();
	public abstract void left();
	public abstract void right();
	public abstract void idle();
	public abstract void dead();
	public abstract void push();
//	public abstract boolean throw_active();
	public abstract void initIndex();
	public abstract void left_released();
	public abstract void right_released();
	public abstract void active(int deltaX);
	public abstract Rectangle getPosition();
	public abstract void setIdle();
}
