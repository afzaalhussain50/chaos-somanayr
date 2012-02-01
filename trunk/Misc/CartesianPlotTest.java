import java.text.NumberFormat;

import javax.swing.JFrame;

public class CartesianPlotTest
{
	public static void main(String[] args){
		double[] x = new double[1000];
		double[] y = new double[1000];
		for (int i = 0; i < x.length; i++)
		{
			x[i] = i / (100D);
			y[i] = Math.cos(x[i]) + .5;
		}
		
		CartesianPlot.TickGenerator tg = new CartesianPlot.TickGenerator()
		{
			
			@Override
			public String yToString(double y)
			{
				return String.valueOf(NumberFormat.getInstance().format(y));
			}
			
			@Override
			public String xToString(double x)
			{
				return String.valueOf(NumberFormat.getInstance().format(x));
			}
		};
		
		JFrame frame = new JFrame();
		CartesianPlot cp = new CartesianPlot(x, y, 50, 50, tg);
		frame.add(cp);
		frame.setSize(500, 500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
