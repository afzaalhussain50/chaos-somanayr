package circos;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

public class Circo
{

	private static class Line
	{
		private double x1, y1, x2, y2;

		public Line(Point start, Point end)
		{
			x1 = start.x;
			y1 = start.y;
			x2 = end.x;
			y2 = end.y;
		}

		public Line(PrecisePoint start, PrecisePoint end)
		{
			x1 = start.x;
			y1 = start.y;
			x2 = end.x;
			y2 = end.y;
		}

		public PrecisePoint getPointAtPosition(double percent)
		{
			double x;
			double y;
			if (x1 == x2)
				x = x1;
			else
				x = (percent * (x2 - x1)) + x1;
			if (y1 == y2)
				y = y1;
			else
				y = (percent * (y2 - y1)) + y1;
			return new PrecisePoint(x, y);
		}
	}

	/*
	 * Overrides java.awt.Point
	 */
	public static class PrecisePoint
	{
		double x;
		double y;

		public PrecisePoint(double x, double y)
		{
			this.x = x;
			this.y = y;
		}

		public int getX()
		{
			return (int) Math.round(x);
		}

		public int getY()
		{
			return (int) Math.round(y);
		}
	}

	private static Logger log = Logger.getLogger(Circo.class.getName());

	public static void drawSpline(Graphics g, Point[] p)
	{
		if (p.length > 3)
			log("Cannot use more than three points.", Level.WARNING);
		double detail = 1 / (Point2D.distance(p[0].x, p[0].y, p[1].x, p[1].y) + Point2D
				.distance(p[1].x, p[1].y, p[2].x, p[2].y));
		Line l1 = new Line(p[0], p[1]);
		Line l2 = new Line(p[1], p[2]);
		double t;
		PrecisePoint last = new PrecisePoint(p[0].x, p[0].y);
		for (t = 0.0; t < 1.0; t += detail)
		{
			PrecisePoint cur = new Line(l1.getPointAtPosition(t),
					l2.getPointAtPosition(t)).getPointAtPosition(t);
			g.drawLine(last.getX(), last.getY(), cur.getX(), cur.getY());
			last = cur;
		}
	}

	public static void expandPortion(Graphics g, Point translate,
			float xExpand, float yExpand, Circo circos)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setTransform(AffineTransform.getScaleInstance(xExpand, yExpand));
		circos.render(g2, (int) (1 / xExpand * translate.x),
				(int) (1 / xExpand * translate.y), new BasicStroke(),
				new BasicStroke(2.0f), false);
	}

	public static void log(String message)
	{
		log(message, Level.INFO);
	}

	public static void log(String message, Level level)
	{
		log.log(level, message);
	}

	/* Tests! */
	public static void main(String[] args) throws Exception
	{
		DataSet ds = DataSet.read(new File(
				"C:\\users\\ramos\\Documents\\test.circ"));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Applet app = new Applet();
		frame.add(app);
		int w = 1000, h = 800;
		app.setPreferredSize(new Dimension(w, h));
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(1000);
		Graphics g = app.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.GREEN);
		ds.dist = 200;
		Circo c = new Circo(ds);
		expandPortion(g, new Point(300, 300), 1f, 1f, c);
		// drawArc(g, new Point(100, 100), 50, 0, Math.PI / 2, "", 20);
	}

	private DataSet data;

	private double gap;

	private double[] groupSweep;

	private double[] relSweep;

	public Circo(DataSet ds)
	{
		if (!ds.isValid())
			throw new IllegalArgumentException("Data set is not valid!");
		this.data = ds;

		prepareData();
	}

	private void prepareData()
	{
		int[] relCount = new int[data.sizes.length]; // helps identify how wide
														// splines should be
		for (int i = 0; i < data.data1.length; i++)
		{
			relCount[data.data1[i]]++;
			relCount[data.data2[i]]++;
		}
		data.organizeData();

		relSweep = new double[data.sizes.length];
		for (int i = 0; i < relCount.length; i++)
		{
			relSweep[i] = data.sizes[i] / relCount[i];
		}

		gap = data.calcGap(); // cache gap for CPU efficiency

		groupSweep = new double[data.groupNames.length];
		int off = 0;
		for (int i = 0; i < groupSweep.length; i++)
		{
			double t = 0.0;
			for (; off <= data.groupEnd[i]; off++)
			{
				t += data.sizes[off] + gap;
			}
			groupSweep[i] = t - gap; // problematic if group size is 0, which is
										// why that's not allowed
		}
	}

	public void render(Graphics g, int centerX, int centerY, Stroke arc,
			Stroke relate, boolean hasDataBeenModified)
	{
		if (hasDataBeenModified)
			prepareData();
		Graphics2D g2 = (Graphics2D) g;
		drawArcs(g2, centerX, centerY);
		g2.setStroke(relate);
		drawRelationships(g2, centerX, centerY);
	}

	/**
	 * @param g
	 * @param centerX
	 * @param centerY
	 */
	private void drawArcs(Graphics g, int centerX, int centerY)
	{

		/* inner arcs */
		double t = 0.0;
		for (int i = 0; i < data.sizes.length; i++)
		{
			drawArc(g, new Point(centerX, centerY), data.dist, t,
					data.sizes[i], data.labels[i], data.innerArcThickness);
			t += data.sizes[i] + gap;
		}

		/* outer arcs */
		int maxDist = 0;
		for (int i = 0; i < data.labels.length; i++)
			maxDist = Math.max(maxDist, data.dist + data.innerTextOffset
					+ g.getFontMetrics().stringWidth(data.labels[i])
					+ data.outerArcOffset);
		t = 0.0;
		for (int i = 0; i < data.groupNames.length; i++)
		{
			drawArc(g, new Point(centerX, centerY), maxDist, t, groupSweep[i],
					data.groupNames[i], data.outerArcThickness);
			System.out.println(t + " radians, sweep is " + groupSweep[i]);
			t += groupSweep[i] + gap;
		}
	}

	private static void drawArc(Graphics g, Point center, int l, double theta,
			double sweep, String label, int thickness)
	{
		for (double i = 0; i < thickness; i += .5)
		{ // hardcoded
			Point last = pointAt(center, l + i, theta);
			for (double t = theta; t < theta + sweep; t += .01)
			{
				Point cur = pointAt(center, l + i, t);
				g.drawLine(last.x, last.y, cur.x, cur.y);
				last = cur;
			}
		}
		double t = theta + sweep / 2;
		Point p = pointAt(center, l + 15, t);
		int size = g.getFontMetrics().stringWidth(label);
		BufferedImage img = new BufferedImage(size * 2, size * 2,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.RED);
		double rot = (Math.PI / 2 - t);
		// System.out.println(rot / Math.PI + "PI");
		boolean b = true;
		if (rot < -Math.PI / 2)
		{
			rot += Math.PI;
			b = false;
		}
		g2.setTransform(AffineTransform.getRotateInstance(rot, size, size));
		g2.drawString(label, size, size);
		g2.dispose();
		if (!b)
			size *= 1.75; // magic value or hardcoded???
		g.drawImage(img, p.x - size, p.y - size, null);
	}

	private void drawRelationships(Graphics g, int centerX, int centerY)
	{
		int[] offsets = new int[data.sizes.length];
		for (int i = 0; i < data.data1.length; i++)
		{
			int d1 = data.data1[i];
			int d2 = data.data2[i];
			double s1 = 0;
			for (int j = 0; j < d1; j++)
			{
				s1 += data.sizes[j];
			}
			double s2 = 0;
			for (int j = 0; j < d2; j++)
			{
				s2 += data.sizes[j];
			}
			g.setColor(new Color((data.colorScheme[data.type[i]])
					| data.alpha << 24, true));
			drawRelationship(new Point(d1, d2), g, centerX, centerY, s1
					+ (offsets[d1]++) * relSweep[d1] + gap * d1, relSweep[d1],
					s2 + (offsets[d2]++) * relSweep[d2] + gap * d2,
					relSweep[d2], data.dist);
		}
	}

	private void drawRelationship(Point off, Graphics g, int centerX,
			int centerY, double theta1, double sweep1, double theta2,
			double sweep2, int l)
	{
		int steps = 50 * (int) Math.round(Math.PI * data.dist * // circumference
				Math.max(theta1, theta2) / (Math.PI * 2) // percent of
															// circumference
		); /* end Math.round */
		double t1 = theta1; // t1 progresses
		double t2 = theta2 + sweep2; // t2 regresses
		double jump1 = sweep1 / steps;
		double jump2 = sweep2 / steps;
		Point p1 = new Point(centerX, centerY);

		/* draw insides */
		for (int step = 0; step < steps; step++)
		{
			Point p0 = pointAt(p1, l, t1);
			Point p2 = pointAt(p1, l, t2);
			// debugPoint(g, p0);
			// debugPoint(g, p2);
			drawSpline(g, new Point[]
			{ p0, p1, p2 });
			t1 += jump1;
			t2 -= jump2;
		}

		/* draw outsides */
		g.setColor(new Color(data.bezierOutter));
		// debugPoint(g, pointAt(p1, l, theta1));
		// debugPoint(g, pointAt(p1, l, theta2 + sweep2));
		drawSpline(g, new Point[]
		{ pointAt(p1, l, theta1), p1, pointAt(p1, l, theta2 + sweep2) });
		// debugPoint(g, pointAt(p1, l, theta1 + sweep1));
		// debugPoint(g, pointAt(p1, l, theta2));
		drawSpline(g, new Point[]
		{ pointAt(p1, l, theta1 + sweep1), p1, pointAt(p1, l, theta2) });
	}

	private static Point pointAt(Point center, double l, double theta)
	{
		return new Point((int) Math.round(Math.sin(theta) * l) + center.x,
				(int) Math.round(Math.cos(theta) * l + center.y));
	}

}
