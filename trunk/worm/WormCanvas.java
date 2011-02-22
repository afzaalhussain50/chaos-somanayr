package worm;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class WormCanvas extends Applet implements Runnable, KeyListener, ComponentListener{
	public boolean paused = false, countdown = true;
	Dimension prefSize = calcSize(true), size = calcSize(false);
	public Worm worm = new Worm(5,-1,0,size);
	private BufferedImage db,ob;
	ArrayList<Fruit> fruits = new ArrayList<Fruit>(0);
	int minspeed = 5, speed = minspeed;
	int curScore = 0;
	int maxFruit = 20;
	int minFruit = 10;
	int sz;
	double percent = .1;
	boolean hs = true;
	public void sleep(int ms){
		sleep((long)ms);
	}
	private Dimension calcSize(boolean type) {
		if(type){
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			return new Dimension(Math.min(dim.width, dim.height - 100), Math.min(dim.width, dim.height - 100));
		}
		if(prefSize.width != prefSize.height){
			Dimension dim = prefSize;
			prefSize = new Dimension(Math.min(dim.width, dim.height), Math.min(dim.width, dim.height));
			setPreferredSize(prefSize);
			addPackQueue();
		}
		Runtime.getRuntime().gc();
		Dimension dim = prefSize;
		int x = 10;
		for (int i = dim.width / 10; i > dim.width / 100; i--) {
			if(dim.width % i == 0){
				x = i;
				break;
			}
		}
		int y = 10;
		for (int i = dim.height / 10; i > dim.height / 100; i--) {
			if(dim.height % i == 0){
				y = i;
				break;
			}
		}
		return new Dimension(x,y);
	}
	boolean packing = false;
	private void addPackQueue() {
		if(packing)
			return;
		packing = true;
		new Thread(new Runnable(){

			public void run() {
				sleep(5000);
				WormMain.frame.pack();
				packing = false;
			}
			
		}).start();
	}
	public void init(){
		sleep(1000);
		setPreferredSize(prefSize);
		db = new BufferedImage(prefSize.width,prefSize.height,BufferedImage.TYPE_INT_RGB);
		ob = new BufferedImage(prefSize.width,prefSize.height,BufferedImage.TYPE_INT_RGB);
		addKeyListener(this);
		new Thread(this).start();
		genFruits();
		setBackground(WormMain.colors[1]);
		speed = minspeed;
		addComponentListener(this);
	}
	private void genFruits() {
		int prevsize = fruits.size();
		sz = ((int)(Math.round(Math.random() * (maxFruit - minFruit)))) + minFruit;
		fruits.ensureCapacity(sz);
		for (int i = prevsize; i < sz; i++) {
			try{
				fruits.add(Fruit.genFruit(size, new Dimension(prefSize.width / size.width,prefSize.height / size.height)));
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public void sleep(long ms){
		long to = System.currentTimeMillis() + ms;
		while(to > System.currentTimeMillis()){}
	}
	public void paint(Graphics g){
		g.drawImage(db, 0, 0, null);
	}
	public void drawDifference(Graphics g){
		for(int x = 0; x < prefSize.width; x++){
			for(int y = 0; y < prefSize.height; y++){
				int z = db.getRGB(x, y);
				if(z == ob.getRGB(x, y)){
					g.setColor(new Color(z));
					g.fillRect(x, y, 1, 1);
				}
			}
		}
	}
	boolean starting = true;
	int count = 0;
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			if(hasFocus()){
				if(countdown){
					countDown();
					countdown = false;
				}
				if(!paused){
					count++;
					if(count > 30)
						starting = false;
					drawBg();
					worm.move();
					drawWorm(WormMain.colors[0]);
					drawFruits();
				}
			}
			Graphics g = getGraphics();
			paint(g);
			g.dispose();
			sleep(1000/speed);
		}
	}
	public void drawBg(){
		Graphics g = db.getGraphics();
		g.setColor(WormMain.colors[1]);
		g.fillRect(0, 0, prefSize.width, prefSize.height);
		double xdif = (double)prefSize.width / (double)size.width;
		double ydif = (double)prefSize.height / (double)size.height;
		g.setColor(WormMain.colors[2]);
		for (double i = 0; i < prefSize.width; i+=xdif) {
			g.drawLine((int)i, 0, (int)i, prefSize.height);
		}
		for (double i = 0; i < prefSize.height; i+=ydif) {
			g.drawLine(0, (int)i, prefSize.width, (int)i);
		}
		g.dispose();
	}
	public void drawWorm(Color c){
		Graphics g = db.getGraphics();
		Point[] points = worm.getParts();
		int xdif = prefSize.width / size.width;
		int ydif = prefSize.height / size.height;
		boolean b = offscreen(points[0]);
		for (int i = 1; i < points.length; i++) {
			if((points[i].equals(points[0]) && !starting) || b){
				paused = true;
				if(hs)
					new HighScores((worm.getSize() + curScore));
				curScore = 0;
				WormMain.frame.setTitle("Worms! by Ryan - 0");
				break;
			}
		}
		fruits.trimToSize();
		if(fruits.size() == 0 || (double)fruits.size()/(double)sz < percent){
			genFruits();
			fruits.trimToSize();
		}
		for(int i = 0; i < fruits.size(); i++){
			boolean flag = false;
			for(Point p : fruits.get(i).getCells()){
				if(p.equals(points[0])){
					flag = true;
					break;
				}
			}
			if(flag){
				worm.expand(fruits.get(i).getInc());
				curScore += fruits.get(i).getInc() * speed;
				WormMain.frame.setTitle("Worms! by Ryan - " + curScore);
				if(worm.getSize() > Math.min(size.width * size.height / 10, speed * 10)){
					curScore += worm.getSize();
					worm = new Worm(5, (dir) ? -1 : 0, dir ? 0 : -1, size);
					dir = true;
					speed *= 2;
					starting = true;
					count = 0;
					startdir = true;
				}
				fruits.remove(i);
				i--;
				fruits.trimToSize();
			}
		}
		/*g.setColor(new Color(c.getGreen(), c.getBlue(), c.getRed()));
		g.fillRect(points[0].x * xdif, points[0].y * ydif, xdif, ydif);*/
		g.setColor(c);
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			g.fillOval(p.x * xdif, p.y * ydif, xdif, ydif);
		}
		g.dispose();
	}
	private boolean offscreen(Point p) {
		return (p.x >= prefSize.width || p.x < 0 || p.y >= prefSize.height || p.y < 0);
	}
	public void drawFruits(){
		double xdif = (double)prefSize.width / (double)size.width;
		double ydif = (double)prefSize.height / (double)size.height;
		Graphics g = db.getGraphics();
		try{
			for (Fruit f : fruits) {
				Point p = f.getCells()[0];
				g.drawImage(f.getImage(), (int)(p.x * xdif), (int) (p.y * ydif), null);
			}
		} catch (java.util.ConcurrentModificationException ex){
			ex.printStackTrace();
		}
		g.dispose();
	}
	boolean dir = true, startdir = true;;
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			if(dir && !startdir)
				break;
			else
				dir = true;
			startdir = false;
			worm.changeDir(-1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			if(dir  && !startdir)
				break;
			else
				dir = true;
			startdir = false;
			worm.changeDir(1, 0);
			break;
		case KeyEvent.VK_UP:
			if(!dir  && !startdir)
				break;
			else
				dir = false;
			startdir = false;
			worm.changeDir(0, -1);
			break;
		case KeyEvent.VK_DOWN:
			if(!dir  && !startdir)
				break;
			else
				dir = false;
			startdir = false;
			worm.changeDir(0, 1);
			break;
		case KeyEvent.VK_ENTER:
			countdown = true;
			while(countdown == true){sleep(10);}
			boolean dir = Math.round(Math.random()) == 1;
			worm = new Worm(5, (dir) ? -1 : 0, dir ? 0 : -1, size);
			paused = false;
			starting = true;
			count = 0;
			fruits = new ArrayList<Fruit>();
			genFruits();
			speed = minspeed;
			startdir = true;
		}
	}
	private void countDown() {
		if(true)
			return;
		System.out.println("counting...");
		Graphics g = db.getGraphics();
		String[] cd = {"3", "2", "1"};
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, prefSize.width, prefSize.height);
		g.setColor(Color.GREEN);
		for(int x = 0; x < cd.length; x++){
			for(int i = 0; i < 50; i++){
				g.setFont(new Font("Times New Roman", Font.BOLD, i));
				FontMetrics fm = g.getFontMetrics();
				int xoff = prefSize.width / 2 - fm.stringWidth(cd[x]) / 2;
				int yoff = prefSize.height / 2 - fm.getHeight() / 2;
				g.drawString(cd[x], xoff, yoff);
				repaint();
				sleep(50);
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, prefSize.width, prefSize.height);
			}
			sleep(500);
		}
		System.out.println("done counting");
		g.dispose();
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentResized(ComponentEvent e) {
		prefSize = this.getSize();
		size = calcSize(false);
		BufferedImage temp = db;
		db = new BufferedImage(prefSize.width, prefSize.height, BufferedImage.TYPE_INT_RGB);
		Graphics g = db.getGraphics();
		g.drawImage(temp, 0, 0, db.getWidth(), db.getHeight(), null);
		g.dispose();
		paused = true;
	}
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
