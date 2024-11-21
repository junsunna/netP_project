package project;

public interface Moveable {
	public abstract void up();
	public abstract void down();
	public abstract void left();
	public abstract void right();
	public abstract void idle();
	public abstract void dead();
	public abstract void initIndex();
	public abstract void left_released();
	public abstract void right_released();
}
