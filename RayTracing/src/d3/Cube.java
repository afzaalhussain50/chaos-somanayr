package d3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Cube {
	public Vector3D[] vectors = new Vector3D[8];
	
	public Cube(Vector3D[] vectors){
		if(vectors.length != 8)
			throw new IllegalArgumentException();
		this.vectors = vectors;
	}
	
	public Cube(int[] x, int[] y, int[] z){
		for (int i = 0; i < vectors.length; i++) {
			vectors[i] = new Vector3D(x[i], y[i], z[i]);
		}
	}
	
	public void render(Graphics g, Vector3D cam){
		Point[] p = new Point[8];
		for (int i = 0; i < vectors.length; i++) {
			Vector3D v = vectors[i];
			Vector3D dV = new Vector3D(v.x - cam.x, v.y - cam.y, v.z - cam.z);
			if(dV.z == 0)
				continue;
			double lamda = cam.z / (double)dV.z;
			int x = (int)Math.round(cam.x + lamda * dV.x);
			int y = (int)Math.round(cam.y + lamda * dV.y);
			g.drawLine(x, y, x, y);
			p[i] = new Point(x,y);
			System.out.println("-------------------------Vector: " + v + "-------------------------");
			System.out.println(v + ", " + cam + " => " + cam + " + t" + dV);
			System.out.println("For z = 0, t = " + lamda + " (" + cam.z + " / " + dV.z + ")");
			System.out.println("For t = " + lamda + ", x = " + x + " (" + cam.x + " + " + lamda + " * " + dV.x + ")");
			System.out.println("For t = " + lamda + ", y = " + x + "(" + cam.y + " + " + lamda + " * " + dV.y + ")");
		}
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
	}
	
	private void drawLine(Point p1, Point p2, Graphics g) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	public Cube clone(){
		int[] x = new int[8];
		int[] y = new int[8];
		int[] z = new int[8];
		for (int i = 0; i < vectors.length; i++) {
			x[i] = vectors[i].x;
			y[i] = vectors[i].y;
			z[i] = vectors[i].z;
		}
		return new Cube(x, y, z);
	}
	
	public static Cube getCube(Vector3D offset, int l){
		int[] x = {
				offset.x, offset.x, offset.x + l, offset.x + l, offset.x, offset.x, offset.x + l, offset.x + l
		};
		int[] y = {
				offset.y, offset.y + l, offset.y, offset.y + l, offset.y, offset.y + l, offset.y, offset.y + l
		};
		int[] z = {
				offset.z, offset.z, offset.z, offset.z, offset.z + l, offset.z + l, offset.z + l, offset.z + l
		};
		return new Cube(x, y, z);
	}
}
