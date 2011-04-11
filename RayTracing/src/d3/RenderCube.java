package d3;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class RenderCube extends Applet implements ComponentListener{
	
	static Cube defaultCube = new Cube(new int[]{
			200, 200, 300, 300, 200, 200, 300, 300
		}, new int[]{
			200, 300, 200, 300, 200, 300, 200, 300
		}, new int[]{
			50, 50, 50, 50, 150, 150, 150, 150
		});
	
	public int maxS = 10;
	
	Cube c = Cube.getCube(new Vector3D(200, 200, 50), 100);
	
	Vector3D cam = new Vector3D(250, 250, -50); //view point
	
	int xd = 1, yd = -1, zd = 0;
	
	public RenderCube(){
		init();
	}
	
	public void init(){
		addComponentListener(this);
	}
	
	@Override
	public void paint(Graphics g2){
		Point[] p = new Point[8];
		BufferedImage db = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = db.getGraphics();
		for (int i = 0; i < c.vectors.length; i++) {
			Vector3D v = c.vectors[i];
			Vector3D dV = new Vector3D(v.x - cam.x, v.y - cam.y, v.z - cam.z);
			if(dV.z == 0)
				continue;
			double lamda = -cam.z / (double)dV.z;
			int x = (int)Math.round(cam.x + lamda * dV.x);
			int y = (int)Math.round(cam.y + lamda * dV.y);
			g.drawLine(x, y, x, y);
			p[i] = new Point(x,y);
			if(p[i].x > getWidth()){
				xd = -(int) (Math.random() * maxS + 1);
			}
			if(p[i].x < 0){
				xd = (int) (Math.random() * maxS + 1);
			}
			if(p[i].y > getHeight()){
				yd = -(int) (Math.random() * maxS + 1);
			}
			if(p[i].y < 0){
				yd = (int) (Math.random() * maxS + 1);
			}/*
			System.out.println("-------------------------Vector: " + v + "-------------------------");
			System.out.println(v + ", " + cam + " => " + cam + " + t" + dV);
			System.out.println("For z = 0, t = " + lamda + " (" + -cam.z + " / " + dV.z + ")");
			System.out.println("For t = " + lamda + ", x = " + x + " (" + cam.x + " + " + lamda + " * " + dV.x + ")");
			System.out.println("For t = " + lamda + ", y = " + x + "(" + cam.y + " + " + lamda + " * " + dV.y + ")");*/
		}
		g.setColor(Color.YELLOW);
		drawLine(p[0], p[1], g);
		drawLine(p[0], p[2], g);
		drawLine(p[2], p[3], g);
		drawLine(p[3], p[1], g);
		drawLine(p[0], p[4], g);
		drawLine(p[1], p[5], g);
		drawLine(p[2], p[6], g);
		drawLine(p[3], p[7], g);
		drawLine(p[4], p[5], g);
		drawLine(p[4], p[6], g);
		drawLine(p[6], p[7], g);
		drawLine(p[7], p[5], g);
		g.setColor(Color.BLUE);
		for (Point x : p) {
			g.fillRect(x.x - 1, x.y - 1, 4, 4);
		}
		g.dispose();
		g2.drawImage(db, 0, 0, null);
		for (Vector3D v : c.vectors) {
			v.x += xd;
			v.y += yd;
			v.z += zd;
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				paint(getGraphics());
			}
		}).start();
	}
	
	private void drawLine(Point p1, Point p2, Graphics g) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	public static void main(String[] args){
		JFrame f = new JFrame();
		f.setSize(new Dimension(500,500));
		f.add(new RenderCube());
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("Resized...");
		cam.x = getWidth() / 2;
		cam.y = getHeight() / 2;
		maxS = Math.min(cam.x, cam.y) / 25;
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
