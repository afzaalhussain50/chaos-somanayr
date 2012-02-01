import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;


public class CartesianPlot extends JComponent
{

	
	
	double[] x;
	double[] y;
	double xMin, xMax, yMin, yMax;
	double xRange, yRange;
	int xDensity, yDensity;
	TickGenerator tg;
	
	//cached values
	private double xScale;
	private double yScale;
	private Point origin; //prescaled
	private int w;
	private int h;
	private int xBuffer = 40, xTBuffer = 2 * xBuffer;
	private int yBuffer = 40, yTBuffer = 2 * yBuffer;
	
	public CartesianPlot(double[] x, double[] y, int xDensity, int yDensity, TickGenerator tg){
		if(x.length != y.length || x.length == 0)
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.xDensity = xDensity;
		this.yDensity = yDensity;
		this.tg = tg;
		
		//cache min/max values for x,y
		xMin = x[0];
		xMax = x[0];
		yMin = y[0];
		yMax = y[0];
		
		for (int i = 1; i < y.length; i++)
		{
			xMax = Math.max(xMax, x[i]);
			xMin = Math.min(xMin, x[i]);
			
			yMax = Math.max(yMax, y[i]);
			yMin = Math.min(yMin, y[i]);
		}
		
		//cache range
		xRange = xMax - xMin;
		yRange = yMax - yMin;
	}

	@Override
	public void paint(Graphics g)
	{
		//create a double buffer
		BufferedImage db = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = db.getGraphics();
		
		g2.setColor(Color.BLACK);
		
		//caches
		initGraph();
		
		//axes
		drawAxes(g2);
		
		//points
		drawPoints(g2);
		
		//render the buffer
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(db, 0, 0, null);
		g2.dispose();
	}
	
	private void drawPoints(Graphics g)
	{
		Point last = null;
		for (int i = 0; i < x.length; i++)
		{
			Point p = graphToComponent(x[i], y[i]);
			if(last != null){
				g.drawLine(last.x, last.y, p.x, p.y);
			}
			last = p;
			g.fillRect(p.x - 2, p.y - 2, 5, 5);
		}
	}

	private void initGraph()
	{
		w = getWidth() - xTBuffer;
		h = getHeight() - yTBuffer;
		xScale = w / xRange;
		yScale = h / yRange;
		System.out.println(yRange);
		
		//FIXME: correct?
		origin = new Point((int)(-xMin * xScale), -(int) (yMin * yScale));
		
		System.out.println(origin);
		System.out.println(xScale + ", " + yScale);
	}

	private void drawAxes(Graphics g)
	{
		
		FontMetrics fm = g.getFontMetrics();
		
		//XAxis
		g.drawLine(0, yBuffer + h - (origin.y), getWidth(), yBuffer + h - (origin.y));
		
		//YAxis
		g.drawLine(xBuffer + origin.x, 0, xBuffer + origin.x, getHeight());
		
		double xJump = xRange / w * xDensity;
		
		System.out.println("xMin: " + xMin + ", xMax: " + xMax + ", xJump: " + xJump);
		
		//X ticks
		for (double i = xMin; i <= xMax; i+=xJump)
		{
			Point p = graphToComponent(i, 0);
			System.out.println("X tick at " + i + " becomes " + p);
			g.drawLine(p.x, p.y - 5, p.x, p.y + 5);
			String s = tg.xToString(i);
			g.drawString(s, p.x - fm.stringWidth(s) / 2, p.y + 5 + fm.getHeight());
		}
		
		double yJump = yRange / h * yDensity;
		
		//Y ticks
		for (double i = yMin; i <= yMax; i+=yJump)
		{
			Point p = graphToComponent(0, i);
			g.drawLine(p.x - 5, p.y, p.x + 5, p.y);
			String s = tg.xToString(i);
			g.drawString(s, p.x - fm.stringWidth(s) - 5, p.y + fm.getHeight() / 4);
		}
	}

	private Point graphToComponent(double x, double y){
		
		//FIXME does this work?
		int compX = xBuffer + (int)(x * xScale) + origin.x;
		int compY = yBuffer + (int)(h - y * yScale) - origin.y;
		
		return new Point(compX, compY);
	}
	
	public static interface TickGenerator{
		public String xToString(double x);
		public String yToString(double y);
	}
	
}
