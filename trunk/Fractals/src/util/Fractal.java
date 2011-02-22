package util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.ObjectInputStream.GetField;

import javax.swing.JApplet;
import javax.swing.JFrame;


public abstract class Fractal {
	
	/**
	 * Determines if the fractal is rendering.
	 */
	protected boolean running = false;
	
	/**
	 * Used to track the active fractal
	 */
	private Thread activeFractal;
	
	/**
	 * Draws the fractal. Must stop if {@code running} is false
	 * @param g The graphics object used to render the fractal.
	 */
	protected abstract void drawFractal(Graphics g);
	
	/**
	 * @return the size of the desired canvas
	 */
	protected abstract Dimension getSize();
	
	/**
	 * Decides if the fractal can be stopped in case of an emergency
	 * @return If {@code ThreadDeath} is allowed
	 */
	public abstract boolean allowThreadDeath();
	
	/**
	 * Call when the fractal has finished rendering
	 */
	protected void reportFinished(){
		activeFractal = null;
		running = false;
	}
	
	/**
	 * Uses the distance formula to measure the distance between two points
	 */
	protected double distanceTo(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	/**
	 * Draws a point. Used for debugging
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param name The name. Should be 3 letters maximum
	 * @param g The graphics object used to render the fractal
	 */
	protected void drawPoint(double x, double y, String name, Graphics g, Color c){
		Color temp = g.getColor();
		g.setColor(c);
		g.drawRect((int)(x - 5), (int) (y - 5), 10, 10);
		g.drawString(name, (int)x + 10, (int)y + 10);
		g.setColor(temp);
	}
	
	/**
	 * Returns the slope (m) of a line
	 * @return m
	 */
	protected double getSlope(double x1, double y1, double x2, double y2){
		return (y2 - y1) / (x2 - x1);
	}
	
	/**
	 * Returns the Y intercept of a line (b)
	 * @param x The x coordinate of a point on the line
	 * @param y The y coordinate of a point on the line
	 * @param m The slope
	 * @return b
	 */
	protected double getYIntercept(double x, double y, double m){
		return y - m * x;
	}
	
	/**
	 * Use this to find the average of several numbers. To find the average point, call this
	 * with the x coordinates, then call this with the y coordinates.
	 * @param nums
	 * @return
	 */
	protected double getAverage(double... nums){
		double count = 0;
		for (double d : nums) {
			count += d;
		}
		return count / nums.length;
	}
	
	/**
	 * Renders the fractal
	 * @param g The graphics object used to render the fractal
	 */
	public void render(final Graphics g){
		if(activeFractal == null){
			activeFractal = new Thread(new Runnable() {
				
				@Override
				public void run() {
					drawFractal(g);
				}
			});
			running = true;
			activeFractal.start();
		} else {
			throw new UnsupportedOperationException("Fractal is current active");
		}
	}
	
	/**
	 * Requests that the fractal stop rendering
	 */
	public void requestStop(){
		running = false;
	}
	
	/**
	 * Determines if the fractal rendering thread is {@code null}. If it is null, then the fractal is not rendering
	 * @return if the fractal is rendering
	 */
	public boolean isRunning(){
		return activeFractal == null;
	}
	
	/**
	 * Invokes Thread.stop() on the fractal's thread. If the fractal has only one thread, then this will stop it.
	 * @throws ThreadDeath If the fractal does not allow {@code ThreadDeath}
	 * @see {@code util.Fractal.allowThreadDeath()}
	 */
	public void emergencyStop() throws ThreadDeath{
		if(!allowThreadDeath())
			throw new ThreadDeath();
		activeFractal.stop();
		activeFractal = null;
		running = false;
	}
}
