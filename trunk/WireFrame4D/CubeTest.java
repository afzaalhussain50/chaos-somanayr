import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import wireframe.wireframe2d.Wireframe2D;
import wireframe.wireframe4d.Vector3D;
import wireframe.wireframe4d.Vector4D;
import wireframe.wireframe4d.Wire4D;
import wireframe.wireframe4d.Wireframe4D;


@SuppressWarnings("serial")
public class CubeTest extends Applet implements MouseMotionListener, MouseWheelListener, KeyListener{
	public Vector4D[] points = {
			new Vector4D(50, 50, 50, 50), new Vector4D(50, 100, 50, 50), new Vector4D(100, 50, 50, 50), new Vector4D(100, 100, 50, 50),
			new Vector4D(50, 50, 100, 50), new Vector4D(50, 100, 100, 50), new Vector4D(100, 50, 100, 50), new Vector4D(100, 100, 100, 50),
			new Vector4D(50, 50, 50, 100), new Vector4D(50, 100, 50, 100), new Vector4D(100, 50, 50, 100), new Vector4D(100, 100, 50, 100),
			new Vector4D(50, 50, 100, 100), new Vector4D(50, 100, 100, 100), new Vector4D(100, 50, 100, 100), new Vector4D(100, 100, 100, 100)
	};
	
	public void init(){
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}
	
	private byte mode;
	
	public Point p = new Point(200,200);
	
	public Point p2 = new Point(200, 200);
	
	public Point p3 = new Point(200, 200);
	
	public Point last = null;
	
	public int w = -50;
	
	public Wireframe4D tesseract;
	
	{
		Wire4D[] wires = new Wire4D[32];
		
		//c1 s1
		wires[0] = getWire(1,3);
		wires[1] = getWire(1,5);
		wires[2] = getWire(5,7);
		wires[3] = getWire(7,3);
		
		//c1 s1 con s2
		wires[4] = getWire(1,2);
		wires[5] = getWire(4,3);
		wires[6] = getWire(5,6);
		wires[7] = getWire(7,8);
		
		//c1 s2
		wires[8] = getWire(2,4);
		wires[9] = getWire(2,6);
		wires[10] = getWire(6,8);
		wires[11] = getWire(8,4);
		
		//c1 con c2
		wires[12] = getWire(1,9);
		wires[13] = getWire(2,10);
		wires[14] = getWire(3, 11);
		wires[15] = getWire(4, 12);
		wires[16] = getWire(5, 13);
		wires[17] = getWire(6,14);
		wires[18] = getWire(7,15);
		wires[19] = getWire(8,16);
		
		//c2 s1
		wires[20] = getWire(9, 11);
		wires[21] = getWire(9, 13);
		wires[22] = getWire(13, 15);
		wires[23] = getWire(11, 15);
		
		//c2 s1 con s2
		wires[24] = getWire(9, 10);
		wires[25] = getWire(13, 14);
		wires[26] = getWire(11, 12);
		wires[27] = getWire(15, 16);
		
		//c2 s2
		wires[28] = getWire(10, 12);
		wires[29] = getWire(10, 14);
		wires[30] = getWire(14, 16);
		wires[31] = getWire(12, 16);
		
		tesseract = new Wireframe4D(wires);
	}
	
	public Wire4D getWire(int i1, int i2){
		return new Wire4D(points[i1 - 1], points[i2 - 1]);
	}
	
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.GREEN);
		Wireframe2D wf = tesseract.flatten(new Vector4D(p2.x, p2.y, -p3.x, w), new Vector3D(p.x, p.y, -p3.y));
//		if(wf.hasDuplicates())
//			System.out.println("Has dupes");
		wf.render(g);
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame("Test");
		CubeTest t = new CubeTest();
		t.init();
		t.setPreferredSize(new Dimension(500,500));
		f.add(t);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void mouseDragged(MouseEvent e) {
		int dX, dY;
		if(last == null){
			dX = 0;
			dY = 0;
			last = e.getPoint();
		} else {
			dX = e.getPoint().x - last.x;
			dY = e.getPoint().y - last.y;
			last = e.getPoint();
		}
		if(mode == 1)
			p = new Point(p.x + dX, p.y + dY);
		else if (mode == 2)
			p2 = new Point(p2.x + dX, p2.y + dY);
		else if (mode == 3)
			p3 = new Point(p3.x + dX, p3.y + dY);
		else if (mode == 0){
			last = null;
			return;
		}
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		paint(g);
		g.dispose();
		g = getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
	}

	public void mouseMoved(MouseEvent e) {
		if(mode == 0 || e.getButton() == MouseEvent.NOBUTTON)
			last = null;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		w += e.getWheelRotation();
		if(w >= 0)
			w = -50;
		System.out.println(w);
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		paint(g);
		g.dispose();
		g = getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
	}

	public void keyPressed(KeyEvent e) {
		last = null;
		if(e.getKeyChar() == 'a')
			mode = 1;
		else if(e.getKeyChar() == 's')
			mode = 2;
		else if(e.getKeyChar() == 'd')
			mode = 3;
		else
			mode = 0;
	}

	public void keyReleased(KeyEvent e) {
		mode = 0;
		last = null;
	}

	public void keyTyped(KeyEvent e) {
	}
	
}
