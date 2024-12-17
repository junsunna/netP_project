package project;

import java.awt.Rectangle;

public class Platform extends Rectangle {
	int id;
	
	Platform(int id, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.id = id;
	}
    public int getId() {
        return id;
    }
}
