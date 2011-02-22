package worm;

import java.awt.Dimension;
import java.awt.Point;

public class Worm {
	private Point[] parts;
	private int xdir,ydir;
	private Dimension dim;
	public boolean wrap = false;
	public Worm(int size, int x, int y, Dimension gamedim){
		parts = new Point[size];
		Point head = new Point((int)(Math.random() * (gamedim.width - gamedim.width / 2) + gamedim.width / 2), (int) (Math.random() * (gamedim.height - gamedim.height / 2) + gamedim.height / 2));
		for (int i = 0; i < parts.length; i++) {
			parts[i] = new Point(head.x + x,head.y + y);
		}
		xdir = x;
		ydir = y;
		dim = gamedim;
	}
	public void changeDir(int x,int y){
		xdir = x;
		ydir = y;
	}
	public void move(){
		//shift down
		for (int i = parts.length - 1; i > 0; i--) {
			parts[i] = parts[i - 1];
		}
		int x = parts[0].x + xdir;
		int y = parts[0].y + ydir;
		if(wrap){
			if(x < 0){
				x = dim.width;
			}
			else if(x >= dim.width){
				x = 0;
			}
			if(y < 0){
				y = dim.height;
			}
			else if(y >= dim.height){
				y = 0;
			}
		}
		else {
			if(x >= dim.width){
				x = -1;
				y = -1;
			} else if(y >= dim.height){
				x = -1;
				y = -1;
			}
		}
		parts[0] = new Point(x,y);
	}
	public void expand(int toAdd){
		Point[] temp = new Point[parts.length + toAdd];
		for (int i = 0; i < parts.length; i++) {
			temp[i] = parts[i];
		}
		int z = parts.length - 1;
		for (int i = parts.length; i < temp.length; i++) {
			temp[i] = parts[z];
		}
		parts = temp;
	}
	public Point[] getParts(){
		return parts;
	}
	public void setXDir(int x){
		xdir = x;
	}
	public void setYDir(int y){
		ydir = y;
	}
	public int getSize(){
		return parts.length;
	}
}
