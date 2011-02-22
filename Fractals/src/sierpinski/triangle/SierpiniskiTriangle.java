package sierpinski.triangle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import util.ExactPolygon;
import util.Fractal;
import util.FractalLoader;

public class SierpiniskiTriangle extends Fractal{
	
	public SierpiniskiTriangle(){
		
	}

	@Override
	protected void drawFractal(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 300, 300);
		ExactPolygon tri = new ExactPolygon(new double[]{150, 0, 300}, new double[]{0, 150 * Math.sqrt(3), 150 * Math.sqrt(3)} );
		g.setColor(Color.RED);
		g.fillPolygon(tri.toPolygon());
		g.setColor(Color.BLACK);
		recur(tri, g, 0);
	}
	
	protected void recur(ExactPolygon parent, final Graphics g, final int depth){
		if(depth > 5 || !running){
			reportFinished();
			return;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		double[] x = {getAverage(parent.x[0], parent.x[1]), getAverage(parent.x[2], parent.x[1]), getAverage(parent.x[0], parent.x[2])};
		double[] y = {getAverage(parent.y[0], parent.y[1]), getAverage(parent.y[2], parent.y[1]), getAverage(parent.y[0], parent.y[2])};
		ExactPolygon erase = new ExactPolygon(x, y);
		ExactPolygon[] children = {
				new ExactPolygon(new double[]{parent.x[0], x[0], x[2]}, new double[]{parent.y[0], y[0], y[2]}),
				new ExactPolygon(new double[]{parent.x[1], x[0], x[1]}, new double[]{parent.y[1], y[0], y[1]}),
				new ExactPolygon(new double[]{parent.x[2], x[1], x[2]}, new double[]{parent.y[2], y[1], y[2]})
		};
		g.fillPolygon(erase.toPolygon());
		for (final ExactPolygon e : children) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					recur(e, g, depth + 1);
				}
			}).start();
		}
	}

	@Override
	protected Dimension getSize() {
		return new Dimension(300,(int)Math.ceil(150 * Math.sqrt(3)));
	}

	@Override
	public boolean allowThreadDeath() {
		return false;
	}
	
	public static void main(String[] args){
		FractalLoader.loadFractal(new SierpiniskiTriangle());
	}

}
