package wireframe.wireframe2d;

import java.awt.Point;

public class Wire2D {
	public Point p1;
	public Point p2;
	
	public Wire2D(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public boolean equals(Object obj){
		Wire2D w = (Wire2D)obj;
		return ((w.p1.equals(p1) && w.p2.equals(p2))||(w.p1.equals(p2) && w.p2.equals(p1)));
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("p1: (").append(p1.x).append(", ").append(p1.y).append("), p2: (").append(p2.x).append(", ").append(p2.y).append(")");
		return buf.toString();
	}
}
