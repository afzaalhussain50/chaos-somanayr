package midpoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;

import util.ExactPolygon;

public class Loader extends JApplet implements MouseListener, MouseMotionListener{
	
	ArrayList<Point> points = new ArrayList<Point>();
	
	Midpoint mp = null;
	
	Graphics mpg;
	
	BufferedImage buf = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);
	
	public Loader(){
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(700,700));
	}
	
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 700, 700);
		if(points.isEmpty())
			return;
		g.setColor(Color.GREEN);
		Point last = null;
		for (Point p : points) {
			if(last != null)
				g.drawLine(p.x, p.y, last.x, last.y);
			g.fillRect(p.x - 2, p.y - 2, 4, 4);
			last = p;
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(rendering())
			return;
		Graphics g = getGraphics();
		g.drawImage(buf, 0, 0, null);
		Point last = points.get(points.size() - 1);
		Point p = arg0.getPoint();
		g.drawLine(last.x, last.y, p.x, p.y);
		g.dispose();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(points.isEmpty() || rendering())
			return;
		Graphics g = buf.getGraphics();
		paint(g);
		Point last = points.get(points.size() - 1);
		Point p = arg0.getPoint();
		g.drawLine(last.x, last.y, p.x, p.y);
		g.dispose();
		g = getGraphics();
		g.drawImage(buf, 0, 0, null);
		g.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(mpg != null){
			mpg.dispose();
			mpg = null;
		}
		if(points.isEmpty()){
			points.add(arg0.getPoint());
			Graphics g = buf.getGraphics();
			paint(g);
			g.dispose();
			g = getGraphics();
			g.drawImage(buf, 0, 0, null);
			g.dispose();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(rendering())
			return;
		Point p = arg0.getPoint();
		points.add(p);
		Graphics g = getGraphics();
		paint(g);
		g.dispose();
		if(points.size() <= 2)
			return;
		Point p1 = points.get(0);
		if(Math.abs(p.x - p1.x) < 10 && Math.abs(p.y - p1.y) < 10){
			double[] x  = new double[points.size()];
			double[] y = new double[points.size()];
			for (int i = 0; i < x.length; i++) {
				x[i] = points.get(i).x;
				y[i] = points.get(i).y;
			}
			ExactPolygon poly = new ExactPolygon(x, y);
			mp = new Midpoint(poly);
			mpg = getGraphics();
			mp.render(mpg);
			points = new ArrayList<Point>();
		}
	}
	
	private boolean rendering(){
		return mp != null && mp.isRunning();
	}
	
	public static void main(String[] args){
		JFrame mainframe = new JFrame();
		mainframe.add(new Loader());
		mainframe.pack();
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainframe.setVisible(true);
	}
	
}
