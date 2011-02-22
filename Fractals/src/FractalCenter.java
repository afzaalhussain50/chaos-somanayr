import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import midpoint.Loader;
import midpoint.Midpoint;

import sierpinski.Carpet.SierpinskisCarpet;
import sierpinski.triangle.SierpiniskiTriangle;
import snowflake.Snowflake;



public class FractalCenter {
	private static JButton snowflake, midpoint, midpointRandom, siertri, siercarp;
	public static void main(String[] args){
		JFrame main = new JFrame("Ryan's Fractal Center");
		snowflake = new JButton("Snowflake");
		midpoint = new JButton("Triangles");
		midpointRandom = new JButton("Triangles - random");
		siertri = new JButton("Sierpinski's Triangle");
		siercarp = new JButton("Sierpinski's Carpet");
		main.setLayout(new FlowLayout());
		main.add(snowflake);
		main.add(midpoint);
		main.add(midpointRandom);
		main.add(siertri);
		main.add(siercarp);
		snowflake.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Snowflake.main(null);
			}
		});
		midpoint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Loader.main(null);
			}
		});
		midpointRandom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Midpoint.main(null);
			}
		});
		siertri.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SierpiniskiTriangle.main(null);
			}
		});
		siercarp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SierpinskisCarpet.main(null);
			}
		});
		main.pack();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
	}
}
