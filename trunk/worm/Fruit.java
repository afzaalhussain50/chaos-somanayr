package worm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Fruit {
	static int fruitTypes = 3;
	private Point[] loc;
	private BufferedImage img;
	private int inc;
	public static Fruit genFruit(Dimension game, Dimension cell){
		int i = (int) (Math.random() * fruitTypes);
		if (i == fruitTypes)
			return genFruit(game,cell);
		int x = (int) (Math.random() * game.width);
		int y = (int) (Math.random() * game.height);
		BufferedImage img = null;
		int inc = 0;
		int w = 1; int h = 1;
		switch(i){
		case 0:
			img = new BufferedImage(cell.width * 3, cell.height * 3, BufferedImage.TYPE_INT_RGB);
			w = 3;
			h = 3;
			colorImage(img,Color.BLACK);
			inc = 3;
			break;
		case 1:
			img = new BufferedImage(cell.width * 2, cell.height * 2, BufferedImage.TYPE_INT_RGB);
			w = 2;
			h = 2;
			colorImage(img, Color.GREEN);
			inc = 5;
			break;
		case 2:
			img = new BufferedImage(cell.width, cell.height, BufferedImage.TYPE_INT_RGB);
			w = 1;
			h = 1;
			colorImage(img, Color.BLUE);
			inc = 7;
			break;
		}
		return new Fruit(createPoints(x, y, w, h),img,inc);
	}
	private static void colorImage(BufferedImage img, Color col){
		Graphics g = img.getGraphics();
		g.setColor(col);
		g.fillRect(0,0,img.getWidth(), img.getHeight());
		g.dispose();
	}
	private static Point[] createPoints(int x, int y, int w, int h){
		ArrayList<Point> al = new ArrayList<Point>();
		for (int i = x; i < x + w; i++) {
			for (int j = y; j < y + h; j++) {
				al.add(new Point(i,j));
			}
		}
		al.trimToSize();
		Point[] temp = new Point[al.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = al.get(i);
		}
		return temp;
	}
	private Fruit(Point[] loc, BufferedImage img, int inc){
		this.loc = loc;
		this.img = img;
		this.inc = inc;
	}
	public Point[] getCells(){
		return loc;
	}
	public BufferedImage getImage(){
		return img;
	}
	public int getInc(){
		return inc;
	}
}
