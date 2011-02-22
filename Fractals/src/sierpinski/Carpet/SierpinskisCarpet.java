package sierpinski.Carpet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import util.Fractal;
import util.FractalLoader;

public class SierpinskisCarpet extends Fractal{

	Dimension dim = new Dimension(300,300);
	
	@Override
	protected void drawFractal(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, dim.width, dim.height);
		g.setColor(Color.WHITE);
		render(new Rectangle(dim), 0, g);
	}
	
	private void render(Rectangle s, final int depth, final Graphics g){
		int newS = s.width/3;
		int[] xs = new int[3];
		int[] ys = new int[3];
		for (int i = 0; i < xs.length; i++) {
			xs[i] = newS * i + s.x;
			ys[i] = newS * i + s.y;
		}
		g.fillRect(xs[1], ys[1], newS, newS);
		if(newS == 0){
			reportFinished();
			return;
		}
		for (int i = 0; i < ys.length; i++) {
			for (int j = 0; j < xs.length; j++) {
				if(i != 1 || j != 1){
					final Rectangle rect = new Rectangle(xs[j], ys[i], newS, newS);
					render(rect, depth + 1, g);
				}
			}
		}
	}

	@Override
	protected Dimension getSize() {
		return dim;
	}

	@Override
	public boolean allowThreadDeath() {
		return false;
	}
	
	public static void main(String[] args){
		FractalLoader.loadFractal(new SierpinskisCarpet());
	}

}
