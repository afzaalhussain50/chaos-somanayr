import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

import javax.swing.JComponent;

/**
 * Creates a Cartesian (x,y) plot. Auto-scales to fit the range of the data.
 * Note: setters will throw {@link ConcurrentModificationException} if accessed while painting.
 * @author Ryan Amos
 */
public class CartesianPlot extends JComponent
{

	private double[] x;
	private double[] y;
	private double xMin, xMax, yMin, yMax;
	private double xRange, yRange;
	private int xDensity, yDensity;

	private TickGenerator tg;

	//cached values
	private boolean isGraphPainting = false;
	private double xScale;
	private double yScale;
	private Point origin; //prescaled
	private int w;
	private int h;
	private int xBuffer = 40, xTBuffer = 2 * xBuffer;
	private int yBuffer = 40, yTBuffer = 2 * yBuffer;

	/**
	 * Initializes a {@code CartesianPlot}
	 * @param x The x coordinates of the points. Must be the same length as the y coordinates list and must be longer than 0.
	 * @param y The y coordinates of the points. Must be the same length as the x coordinates list and must be longer than 0.
	 * @param xDensity The density of ticks along the X axis
	 * @param yDensity The density of ticks along the Y axis
	 * @param generator The {@code TickGenerator} to name ticks.
	 * @see TickGenerator
	 */
	public CartesianPlot(double[] x, double[] y, int xDensity, int yDensity, TickGenerator generator){
		if(x.length != y.length || x.length == 0)
			throw new IllegalArgumentException();
		
		//save values
		this.x = x.clone();
		this.y = y.clone();
		this.xDensity = xDensity;
		this.yDensity = yDensity;
		this.tg = generator;

		precomputeData();
	}

	/**
	 * Initialize the values for: xMin, xMax, yMin, yMax, xRange, yRange. These values are to be used later and are pre-computed for easier access.
	 */
	private void precomputeData()
	{
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

	/**
	 * Paints the graph
	 */
	@Override
	public void paint(Graphics g)
	{
		//get lock
		isGraphPainting = true;

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

		//close lock
		isGraphPainting = false;
	}

	/**
	 * Draws graph points
	 */
	private void drawPoints(Graphics g)
	{
		Point last = null;
		for (int i = 0; i < x.length; i++)
		{
			Point p = graphToComponent(x[i], y[i]);
			
			//draw connector, if any
			if(last != null){
				g.drawLine(last.x, last.y, p.x, p.y);
			}
			last = p;
			
			//draw point
			g.fillRect(p.x - 2, p.y - 2, 5, 5);
		}
	}

	/**
	 * Initializes graph constants
	 */
	private void initGraph()
	{
		w = getWidth() - xTBuffer;
		h = getHeight() - yTBuffer;
		xScale = w / xRange;
		yScale = h / yRange;

		origin = new Point((int)(-xMin * xScale), -(int) (yMin * yScale));
	}

	/**
	 * Draws the axes
	 * @param g The Graphics object to draw with
	 */
	private void drawAxes(Graphics g)
	{

		FontMetrics fm = g.getFontMetrics();

		//XAxis
		g.drawLine(0, yBuffer + h - (origin.y), getWidth(), yBuffer + h - (origin.y));

		//YAxis
		g.drawLine(xBuffer + origin.x, 0, xBuffer + origin.x, getHeight());

		double xJump = xRange / w * xDensity;

		//X ticks
		for (double i = xMin; i <= xMax; i+=xJump)
		{
			Point p = graphToComponent(i, 0);
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

	/**
	 * Converts a point on the graph to the point on the screen, including the buffer
	 * @param x The x coordinate on the graph
	 * @param y The y coordinate on the graph
	 * @return The Cartesian (x, y) point on the component, after including the buffer.
	 */
	private Point graphToComponent(double x, double y){

		//FIXME does this work?
		int compX = xBuffer + (int)(x * xScale) + origin.x;
		int compY = yBuffer + (int)(h - y * yScale) - origin.y;

		return new Point(compX, compY);
	}

	public double[] getXValues()
	{
		return x.clone();
	}

	public void setXValues(double[] x)
	{
		if(isGraphPainting){
			throw new ConcurrentModificationException("Graph is painting.");
		}
		this.x = x.clone();
		precomputeData();
	}

	public double[] getYValues()
	{
		return y.clone();
	}

	public void setYValues(double[] y)
	{
		if(isGraphPainting){
			throw new ConcurrentModificationException("Graph is painting.");
		}
		this.y = y.clone();
		precomputeData();
	}

	public int getxDensity()
	{
		return xDensity;
	}

	public void setxDensity(int xDensity)
	{
		if(isGraphPainting){
			throw new ConcurrentModificationException("Graph is painting.");
		}
		this.xDensity = xDensity;
	}

	public int getyDensity()
	{
		return yDensity;
	}

	public void setyDensity(int yDensity)
	{
		if(isGraphPainting){
			throw new ConcurrentModificationException("Graph is painting.");
		}
		this.yDensity = yDensity;
	}

	public TickGenerator getTickGenerator()
	{
		return tg;
	}

	public void setTickGenerator(TickGenerator tg)
	{
		if(isGraphPainting){
			throw new ConcurrentModificationException("Graph is painting.");
		}
		this.tg = tg;
	}
	
	public boolean isLocked(){
		return isGraphPainting;
	}

	public static interface TickGenerator{
		public String xToString(double x);
		public String yToString(double y);
	}

}
