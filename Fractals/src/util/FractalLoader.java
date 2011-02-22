package util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class FractalLoader extends JFrame{
	private JApplet app = new JApplet();
	private Fractal fract;
	private FractalLoader(Fractal fractal){
		this.fract = fractal;
		add(app);
		app.setPreferredSize(fractal.getSize());
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		app.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fract.render(app.getGraphics());
			}
		});
	}
	public static void loadFractal(Fractal f){
		new FractalLoader(f);
	}
}
