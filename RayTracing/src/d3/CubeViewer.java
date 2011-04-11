package d3;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class CubeViewer extends Applet implements MouseMotionListener{
	Vector3D cam = new Vector3D(250, 250, -50);

	public void init(){
		setPreferredSize(new Dimension(500, 500));
		addMouseMotionListener(this);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		cam.x = e.getPoint().x;
		cam.y = e.getPoint().y;
		repaint();
	}
	
	public void paint(Graphics g){
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g2 = img.getGraphics();
		RenderCube.defaultCube.render(g2, cam);
		g2.dispose();
		g.drawImage(img, 0, 0, null);
	}
	
	@Override
	public void repaint(){
		Graphics g = getGraphics();
		paint(g);
		g.dispose();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		f.setSize(new Dimension(500,500));
		CubeViewer cv = new CubeViewer();
		f.add(cv);
		f.setVisible(true);
		cv.init();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
