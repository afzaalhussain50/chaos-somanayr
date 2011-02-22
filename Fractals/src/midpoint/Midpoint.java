package midpoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import util.ExactPolygon;
import util.Fractal;
import util.FractalLoader;


public class Midpoint extends Fractal{
	
	ExactPolygon poly;
	
	public Midpoint(ExactPolygon p){
		poly = p;
	}
	
	public Midpoint(){
		this(createShape());
	}

	@Override
	protected void drawFractal(Graphics g) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 700, 700);
		g.setColor(Color.GREEN);
		g.drawPolygon(poly.toPolygon());
		propagate(poly, g, 0);
	}
	
	private static ExactPolygon createShape() {
		//return new ExactPolygon(new double[]{300, 600, 450}, new double[]{300, 300, 300 + 150 * Math.sqrt(3)});
		int length = (int)(Math.random() * 6 + 3);
		double[] x = new double[length];
		double[] y = new double[length];
		for (int i = 0; i < y.length; i++) {
			x[i] = (int)(Math.random() * 700);
			y[i] = (int)(Math.random() * 700);
		}
		return new ExactPolygon(x, y);
	}

	private void propagate(final ExactPolygon poly, final Graphics g, final int depth){
		if(depth > 3 || !running){
			reportFinished();
			return;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		final double mpx = getAverage(poly.x);
		final double mpy = getAverage(poly.y);
		for (int i = 0; i < poly.x.length; i++) {
			int next = (i == poly.x.length - 1) ? 0 : i + 1;
			//drawPoint(mpx, mpy, "MP", g);
			//drawPoint(poly.x[next], poly.y[next], "N", g);
			//drawPoint(poly.x[i], poly.y[i], "C", g);
			final ExactPolygon tri = new ExactPolygon(new double[]{mpx, poly.x[i], poly.x[next]}, new double[]{mpy, poly.y[i], poly.y[next]});
			g.drawPolygon(tri.toPolygon());
			new Thread(new Runnable() {
				@Override
				public void run() {
					propagate(tri, g, depth + 1);
				}
			}).start();
		}
	}

	@Override
	protected Dimension getSize() {
		return new Dimension(700, 700);
	}

	@Override
	public boolean allowThreadDeath() {
		return false;
	}
	
	public static void main(String[] args){
		FractalLoader.loadFractal(new Midpoint());
	}
	
}
