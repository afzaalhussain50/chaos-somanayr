package wireframe.wireframe2d;

import java.awt.Color;
import java.awt.Graphics;

public class Wireframe2D {
	private Wire2D[] wires;
	public Wireframe2D(Wire2D... wires){
		this.wires = wires;
	}
	
	public void render(Graphics g){
		for (int i = 0; i < wires.length; i++) {
			g.drawLine(wires[i].p1.x, wires[i].p1.y, wires[i].p2.x, wires[i].p2.y);
		}
	}
	
	public boolean hasDuplicates(){
		boolean b = false;
		for (int i = 0; i < wires.length; i++) {
			for (int j = i + 1; j < wires.length; j++) {
				if(wires[i].equals(wires[j])){
					b = true;
					System.out.println((i + 1) + ", " + (j + 1) + " are duplicates");
				}
			}
		}
		return b;
	}
	
}
