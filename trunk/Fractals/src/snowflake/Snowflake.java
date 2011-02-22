package snowflake;
import java.applet.Applet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Random;

import util.ExactPolygon;

import javax.swing.JFrame;


public class Snowflake extends Applet implements Runnable, MouseListener{

	BufferedImage buf = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
	
	Thread current;
	
	public void run() {
		Random r = new Random();
		current = Thread.currentThread();
		ExactPolygon[] lastSet = new ExactPolygon[1];
		lastSet[0] = new ExactPolygon(new double[]{300, 600, 450}, new double[]{300, 300, 300 + 150 * Math.sqrt(3)});
		Graphics g = getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 1000);
		g.setColor(/*new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256))*/getColor());
		g.fillPolygon(lastSet[0].toPolygon());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.interrupted();
		}
		long sleep = 1000;
		for(int j = 0; j < 10; j++){
			ExactPolygon[] newSet = new ExactPolygon[lastSet.length * 6];
			int count = 0;
			for (int i = 0; i < lastSet.length; i++) {
				double[] oldX = lastSet[i].x;
				double[] oldY = lastSet[i].y;
				double blastx = (2 * oldX[0] - 2 * oldX[2]) / 3 + oldX[2];
				double blasty = (2 * oldY[0] - 2 * oldY[2]) / 3 + oldY[2];
				double prevBx = blastx, prevBy = blasty;
				for(int idx = 0; idx < 3; idx++){
					double x1 = oldX[idx];
					double y1 = oldY[idx];
					//drawPoint(x1, y1, "Old");
					double x2 = oldX[(idx != 2) ? (idx + 1) : 0];
					double y2 = oldY[(idx != 2) ? (idx + 1) : 0];
					//drawPoint(x2, y2, "Old");
					double ax = (x2 - x1) / 3 + x1;
					double ay = (y2 - y1) /3 + y1;
					//drawPoint(ax, ay, "A");
					double bx = (2 * x2 - 2 * x1) / 3 + x1;
					double by = (2 * y2 - 2 * y1) / 3 + y1;
					//drawPoint(bx, by, "B");
					newSet[count++] = new ExactPolygon(new double[]{x1, ax, prevBx}, new double[]{y1, ay, prevBy});
					prevBx = bx;
					prevBy = by;
					//g.setColor(Color.RED);
					//g.fillPolygon(newSet[count - 1].toPolygon());
					//g.setColor(Color.BLUE);
					double m = -(bx - ax) / (by - ay); //slope of perp bissector\
					double mpx = (ax + bx) / 2;
					double mpy = (ay + by) / 2;
					//drawPoint(mpx, mpy, "MP");
					//double b = mpy - m * mpx; //for debugging
					//double anx = Double.isInfinite(m) ? mpx : 0;
					//drawPoint(anx, b, "B");
					//g.drawLine((int)anx, (int)b, (int)mpx, (int)mpy);
					double altitude = Math.sqrt(Math.pow((ax - bx) / 2, 2) + Math.pow((ay - by) / 2, 2)) * Math.sqrt(3);
					double run = (Math.sqrt(Math.pow(altitude,2) / (1 + Math.pow(m, 2))));
					double rise = (Math.sqrt(Math.pow(altitude, 2) / (1 + 1 / (m * m))));
					double cx, cy;
					if(Double.isInfinite(m)){
						cx = mpx;
						double cy1 = mpy + altitude;
						double cy2 = mpy - altitude;
						/*double x3 = oldX[(idx != 0) ? idx - 1 : 2];
						double y3 = oldY[(idx != 0) ? idx - 1 : 2];*/
						if(lastSet[i].toPolygon().contains(cx, cy2))
							cy = cy1;
						else
							cy = cy2;
						/*drawPoint(x3, y3, "Old3");
						drawPoint(cx, cy, "Y");
						g.setColor(Color.ORANGE);
						g.drawLine((int)cx, (int)cy1, (int)cx, (int)cy2);
						g.setColor(Color.BLUE);*/
					} else {
						double cx1 = mpx + run;
						double cx2 = mpx - run;
						double cy1 = mpy + rise;
						double cy2 = mpy - rise;
						double x3 = oldX[(idx != 0) ? idx - 1 : 2];
						double y3 = oldY[(idx != 0) ? idx - 1 : 2];
						if(Math.abs(cx1 - x3) > Math.abs(cx2 - x3))
							cx = cx1;
						else
							cx = cx2;
						if(Math.abs(cy1 - y3) > Math.abs(cy2 - y3))
							cy = cy1;
						else
							cy = cy2;
						/*double d1 = distanceTo(cx1, cy1, x3, y3);
						double d2 = distanceTo(cx1, cy2, x3, y3);
						double d3 = distanceTo(cx2, cy1, x3, y3);
						double d4 = distanceTo(cx2, cy2, x3, y3);
						if (d1 > d2 && d1 > d3 && d1 > d4){
							cx = cx1;
							cy = cy1;
						} else if (d2 > d1 && d2 > d3 && d2 > d4){
							cx = cx1;
							cy = cy2;
						} else if (d3 > d1 && d3 > d2 && d3 > d4){
							cx = cx2;
							cy = cy1;
						} else {
							cx = cx2;
							cy = cy2;
						}*/
					}
					//drawPoint(cx, cy, "C");
					//System.out.println(cx + ", " + cy);
					//g.setColor(Color.GREEN);
					//g.drawLine((int)cx, (int)cy, (int)mpx, (int)mpy);
					//g.setColor(Color.BLUE);
					newSet[count++] = new ExactPolygon(new double[]{ax, bx, cx}, new double[]{ay, by, cy});
				}
			}
			for(ExactPolygon poly : newSet){
				g.setColor(/*new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256))*/getColor());
				g.fillPolygon(poly.toPolygon());
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sleep /= 2;
			lastSet = newSet;
		}
		current = null;
		new Thread(this).start();
	}
	
	public double distanceTo(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static void main(String[] args){
		JFrame container = new JFrame("Snowflake Fractals by Ryan Amos");
		Snowflake s = new Snowflake();
		container.add(s);
		container.setSize(new Dimension(1000, 1000));
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		container.setVisible(true);
		s.init();
	}
	
	public void init(){
		addMouseListener(this);
	}
	
	public Graphics getGraphics(){
		return new MultiGraphics(super.getGraphics(), buf.getGraphics());
	}
	
	public class MultiGraphics extends Graphics{
		
		Graphics[] l;
		
		public MultiGraphics(Graphics... objects){
			l = objects;
		}

		@Override
		public void clearRect(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.clearRect(x, y, width, height);
		}

		@Override
		public void clipRect(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.clipRect(x, y, width, height);
		}

		@Override
		public void copyArea(int x, int y, int width, int height, int dx, int dy) {
			for(Graphics g : l)
				g.copyArea(x, y, width, height, dx, dy);
		}

		@Override
		public Graphics create() {
			return l[0].create();
		}

		@Override
		public void dispose() {
			for(Graphics g : l)
				g.dispose();
		}

		@Override
		public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
			for(Graphics g : l)
				g.drawArc(x, y, width, height, startAngle, arcAngle);
		}

		@Override
		public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, x, y, observer);
			return true;
		}

		@Override
		public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, x, y, bgcolor, observer);
				return true;
		}

		@Override
		public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, x, y, width, height, observer);
			return true;
		}

		@Override
		public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, x, y, width, height, bgcolor, observer);
			return true;
		}

		@Override
		public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
			return true;
		}

		@Override
		public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
			for(Graphics g : l)
				g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
			return true;
		}

		@Override
		public void drawLine(int x1, int y1, int x2, int y2) {
			for(Graphics g : l)
				g.drawLine(x1, y1, x2, y2);
		}

		@Override
		public void drawOval(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.drawOval(x, y, width, height);
		}

		@Override
		public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
			for(Graphics g : l)
				g.drawPolygon(xPoints, yPoints, nPoints);
		}

		@Override
		public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
			for(Graphics g : l)
				g.drawPolyline(xPoints, yPoints, nPoints);
		}

		@Override
		public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
			for(Graphics g : l)
				g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		}

		@Override
		public void drawString(String str, int x, int y) {
			for(Graphics g : l)
				g.drawString(str, x, y);
		}

		@Override
		public void drawString(AttributedCharacterIterator iterator, int x, int y) {
			for(Graphics g : l)
				g.drawString(iterator, x, y);
		}

		@Override
		public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
			for(Graphics g : l)
				g.fillArc(x, y, width, height, startAngle, arcAngle);
		}

		@Override
		public void fillOval(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.fillOval(x, y, width, height);
		}

		@Override
		public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
			for(Graphics g : l)
				g.fillPolygon(xPoints, yPoints, nPoints);
		}

		@Override
		public void fillRect(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.fillRect(x, y, width, height);
		}

		@Override
		public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
			for(Graphics g : l)
				g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		}

		@Override
		public Shape getClip() {
			return l[0].getClip();
		}

		@Override
		public Rectangle getClipBounds() {
			return l[0].getClipBounds();
		}

		@Override
		public Color getColor() {
			return l[0].getColor();
		}

		@Override
		public Font getFont() {
			return l[0].getFont();
		}

		@Override
		public FontMetrics getFontMetrics(Font f) {
			return l[0].getFontMetrics(f);
		}

		@Override
		public void setClip(Shape clip) {
			for(Graphics g : l)
				g.setClip(clip);
		}

		@Override
		public void setClip(int x, int y, int width, int height) {
			for(Graphics g : l)
				g.setClip(x, y, width, height);
		}

		@Override
		public void setColor(Color c) {
			for(Graphics g : l)
				g.setColor(c);
		}

		@Override
		public void setFont(Font font) {
			for(Graphics g : l)
				g.setFont(font);
		}

		@Override
		public void setPaintMode() {
			for(Graphics g : l)
				g.setPaintMode();
		}

		@Override
		public void setXORMode(Color c1) {
			for(Graphics g : l)
				g.setXORMode(c1);
		}

		@Override
		public void translate(int x, int y) {
			for(Graphics g : l)
				g.translate(x, y);
		}
		
	}
	
	public void paint(Graphics g){
		if(current == null)
			new Thread(this).start();
		else
			g.drawImage(buf, 0, 0, null);
	}
	
	public void drawPoint(double x, double y, String name){
		Graphics g = getGraphics();
		g.setColor(Color.GREEN);
		g.drawRect((int)(x - 5), (int) (y - 5), 10, 10);
		g.drawString(name, (int)x + 10, (int)y + 10);
		g.dispose();
	}

	public void mouseClicked(MouseEvent e) {
		if(current != null)
			current.stop();
		new Thread(this).start();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private static Color[] colSet = {Color.BLUE,
		/*Color.CYAN,
		new Color(100, 100, 255),
		new Color(50, 100, 255),
		new Color(100, 50, 255),
		new Color(125, 0, 255),
		new Color(230, 230, 230),
		new Color(75, 75, 255)*/};
	private int idx = (int)(Math.random() * colSet.length);
	
	public Color getColor(){
		Color c = colSet[idx++];
		if(idx >= colSet.length)
			idx = 0;
		return c;
	}

}