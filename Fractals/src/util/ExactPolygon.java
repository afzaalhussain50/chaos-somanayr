package util;
import java.awt.Polygon;



public class ExactPolygon{
	public double[] x, y;
	public ExactPolygon(double[] x, double[] y){
		if(x.length != y.length)
			throw new IllegalArgumentException("Cannot have different array sizes");
		this.x = x;
		this.y = y;
	}
	
	public int size(){
		return x.length;
	}
	
	public double[] x(){
		double[] ret = new double[size()];
		//TODO: array copy
		throw new UnsupportedOperationException();
	}
	
	public Polygon toPolygon(){
		int[] xs = new int[x.length];
		int[] ys = new int[y.length];
		for (int i = 0; i < xs.length; i++) {
			xs[i] = (int)Math.round(x[i]);
		}
		for (int i = 0; i < ys.length; i++) {
			ys[i] = (int)Math.round(y[i]);
		}
		return new Polygon(xs, ys, Math.min(xs.length, ys.length));
	}
	
	@Override
	public boolean equals(Object obj){
		ExactPolygon that = (ExactPolygon)obj;
		if(that.y.length != y.length)
			return false;
		for (int i = 0; i < that.y.length; i++) {
			if(y[i] != that.y[i] || x[i] != that.x[i])
				return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		boolean first = true;
		for (int i = 0; i < x.length; i++) {
			if(!first)
				buf.append(", ");
			else
				first = true;
			buf.append("(" + x[i] + ", " + y[i] + ")");
		}
		buf.append("}");
		return buf.toString();
	}
}