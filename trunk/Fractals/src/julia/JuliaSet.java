package julia;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import util.Fractal;

/**
 * Draws a Julia Set fractal
 * @author ramos
 */
public class JuliaSet extends Fractal{

	static Color[] colorSet = {
		Color.BLACK,
		Color.BLUE,
		Color.CYAN,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.GREEN,
		Color.LIGHT_GRAY,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.RED,
		Color.WHITE,
		Color.YELLOW
	};

	private Complex c;

	private Dimension dim;

	/*private double mag;

	private Point translation;*/

	//private FileWriter fw;

	private boolean verbose = false;

	public JuliaSet(Complex c, Dimension dim/*, double mag, Point translation*/){
		this.c = c;
		this.dim = dim;/*
		this.mag = mag;
		this.translation = translation;*/
		log("Initializing with c value " + c.toString());
	}

	@Override
	public boolean allowThreadDeath() {
		return true;
	}

	@Override
	protected void drawFractal(Graphics g) {
		for(int x = 0; x < dim.width; x++){
			for(int y = 0; y < dim.height; y++){
				Complex cur = new Complex((double)x / dim.width, (double)y / dim.height);
				//log("--------(" + x + ", " + y + ")--------");
				int i;
				for(i = 0; (/*x - */cur.getReal()) * (/*x - */cur.getReal()) + (/*y - */cur.getImaginary()) * (/*y - */cur.getImaginary()) < 4; i++){
					Complex next = cur.square().add(c);
					if(Double.isInfinite(next.getReal()) || Double.isInfinite(next.getImaginary()) || Double.isNaN(next.getReal()) || Double.isNaN(next.getImaginary()))
						break;
					cur = next;
					if(verbose)
						log(cur.toString());
					if(i > 100)
						break;
				}
				//log("Iterations: " + i);
				g.setColor(colorSet[i % colorSet.length]);
				if(i > 100)
					g.setColor(new Color(153, 102, 204));
				g.drawLine(x, y, x, y);
			}
		}
	}

	/*private void drawComplex(Graphics g, Complex c, Point p){
		g.drawLine(p.x, p.y, p.x, p.y);
		//g.drawRect((int)((p.x - translation.x) * mag), (int)((p.y - translation.y) * mag), 0, 0);
	}*/

	@Override
	protected Dimension getSize() {
		return dim;
	}

	public static void main(String[] args){
		Dimension dim = new Dimension(2000, 2000);
		Complex[] c = {new Complex(-0.4, 0.6),
				new Complex(0.285, 0),
				new Complex(0.285, 0.01),
				new Complex(0.123, 0.7),
				new Complex(-.6180339887, 0),
				new Complex(-0.726895347709114071439, 0.188887129043845954792),
				new Complex(-0.156844471694257101941, -0.649707745759247905171)};
		for (int i = 0; i < c.length; i++) {
			System.out.println("Doing complex " + c[i] + " (#" + i + ")");
			File imgo = new File("/home/ramos/JuliaSet/img" + i + ".jpg");
			imgo.delete();
			imgo.getParentFile().mkdirs();
			BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
			JuliaSet j = new JuliaSet(c[i], new Dimension(dim.width, dim.width));
			j.drawFractal(img.getGraphics());
			try {
				ImageIO.write(img, "jpg", imgo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void log(String str){
		System.out.println(str);
	}

}