package worm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class WormMain{
	static JFrame frame;
	static WormCanvas wc;
	static Color[] colors = {Color.GREEN, Color.GRAY, Color.BLACK};
	public static void main(String[] args){
		Autoupdater update = Autoupdater.update();
		if(update != null){
			while(update.downloading()){};
			update.setVisible(false);
			try {
				System.out.println("Executing: " + "java -jar " + System.getProperty("user.dir") + File.separator + "worm.jar");
				Runtime.getRuntime().exec("java -jar " + System.getProperty("user.dir") + File.separator + "worm.jar");
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		JFrame f = new JFrame("Worms! by Ryan");
		wc = new WormCanvas();
		JToolBar tb = new JToolBar();
		JButton options =new JButton("Options");
		options.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				new Options(wc).setVisible(true);
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
			
		});
		tb.add(options);
		f.add(tb, BorderLayout.NORTH);
		f.add(wc, BorderLayout.CENTER);
		f.setVisible(true);
		SettingsIO.read(wc);
		wc.init();
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = f;
	}
}

@SuppressWarnings("serial")
class Options extends JFrame{
	int trigger = -1;
	public JCheckBox hs;
	public JSlider speed;
	public JTextField min;
	public JTextField max;
	public JButton submit;
	public WormCanvas wc;
	public JTextField hsloc;
	public JLabel speednum = new JLabel("Speed:");
	public JTextField chance;
	public JTextField[] size = new JTextField[4];
	JColorChooser choose = new JColorChooser();
	JScrollPane contain;
	public JButton wormColor = new JButton("Worm Color"), bgcolor = new JButton("Background"), grid = new JButton("Grid color");
	public Options(WormCanvas worms){
		super("Worms");
		this.wc = worms;
		chance = new JTextField((wc.percent * 100) + "", 3);
		hs = new JCheckBox("Use highscores: ");
		hsloc = new JTextField(Constants.base, 20);
		hs.setSelected(wc.hs);
		min = new JTextField("" + wc.minFruit, 3);
		max = new JTextField("" + wc.maxFruit, 3);
		submit = new JButton("Submit");
		speed = new JSlider(0, 100);
		speed.setValue(wc.speed);
		JTabbedPane tabs = new JTabbedPane();
		JPanel gc = new JPanel();
		add(tabs, BorderLayout.NORTH);
		add(choose, BorderLayout.CENTER);
		tabs.addTab("Game control", gc);
		JPanel oth = new JPanel();
		contain = new JScrollPane(oth);
		contain.setViewportView(oth);
		tabs.addTab("Main", contain);
		gc.setLayout(new FlowLayout());
		hs.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				hsloc.setEnabled(hs.isSelected());
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
			
		});
		oth.add(hs);
		oth.add(hsloc);
		wormColor.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				WormMain.colors[0] = choose.getColor();
			}

			public void mouseEntered(MouseEvent e) {
				if(trigger == 0)
					return;
				choose.setColor(WormMain.colors[0]);
				trigger = 0;
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
			
		});
		oth.add(wormColor);
		bgcolor.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				WormMain.colors[1] = choose.getColor();
			}

			public void mouseEntered(MouseEvent e) {
				if(trigger == 1)
					return;
				choose.setColor(WormMain.colors[1]);
				trigger = 1;
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		oth.add(bgcolor);
		grid.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				WormMain.colors[2] = choose.getColor();
			}

			public void mouseEntered(MouseEvent e) {
				if(trigger == 2)
					return;
				choose.setColor(WormMain.colors[2]);
				trigger = 2;
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
			
		});
		oth.add(grid);
		Dimension dim = wc.size;
		int i = -1;
		size[++i] = new JTextField("" + dim.width, 4);
		size[++i] = new JTextField("" + dim.height, 4);
		dim = wc.prefSize;
		size[++i] = new JTextField("" + dim.width, 4);
		size[++i] = new JTextField("" + dim.height, 4);
		i = -1;
		oth.add(new JLabel("Grid dimensions: ("));
		oth.add(size[++i]);
		oth.add(new JLabel(", "));
		oth.add(size[++i]);
		oth.add(new JLabel(") Game dimensions: ("));
		oth.add(size[++i]);
		oth.add(new JLabel(", "));
		oth.add(size[++i]);
		oth.add(new JLabel(")"));
		gc.add(speednum);
		speed.addMouseMotionListener(new MouseMotionListener(){

			public void mouseDragged(MouseEvent e) {
				speednum.setText("Speed: " + speed.getValue());
			}

			public void mouseMoved(MouseEvent e) {
			}
			
		});
		gc.add(speed);
		gc.add(new JLabel("Max fruit:"));
		gc.add(max);
		gc.add(new JLabel("Min fruit:"));
		gc.add(min);
		addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				submit();
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
			
		});
		submit.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				submit();
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
			
		});
		gc.add(new JLabel("Regenerate at: "));
		gc.add(chance);
		gc.add(new JLabel("%"));
		contain.setPreferredSize(new Dimension(gc.getPreferredSize().width, 55));
		add(submit, BorderLayout.SOUTH);
		pack();
	}
	public void submit(){
		int fruitmin = Integer.parseInt(min.getText());
		int fruitmax = Integer.parseInt(max.getText());
		if(fruitmin > fruitmax){
			int temp = fruitmin;
			fruitmin = fruitmax;
			fruitmax = temp;
		}
		wc.minFruit = fruitmin;
		wc.maxFruit = fruitmax;
		wc.hs = hs.isSelected();
		wc.minspeed = speed.getValue();
		Constants.base = hsloc.getText() + (hsloc.getText().endsWith(File.separator) ? "" : File.separator);
		Constants.regen();
		wc.percent = Double.parseDouble(chance.getText())/100.0;
		int i = -1;
		wc.size = new Dimension(Integer.parseInt(size[++i].getText()),Integer.parseInt(size[++i].getText()));
		wc.prefSize = new Dimension(Integer.parseInt(size[++i].getText()),Integer.parseInt(size[++i].getText()));
		wc.setPreferredSize(wc.prefSize);
		WormMain.frame.remove(wc);
		WormMain.frame.add(wc, BorderLayout.CENTER);
		WormMain.frame.pack();
		SettingsIO.make(wc);
		wc.setBackground(WormMain.colors[1]);
		setVisible(false);
		wc.requestFocus();
	}
}
class SettingsIO{
	public static void read(WormCanvas wc){
		try {
			Scanner s = new Scanner(new File(Constants.settings));
			wc.minFruit = s.nextInt();
			wc.maxFruit = s.nextInt();
			wc.minspeed = s.nextInt();
			wc.percent = s.nextDouble();
		} catch (Exception e) {
			wc.minFruit = 10;
			wc.maxFruit = 20;
			wc.minspeed = 10;
			wc.percent = .1;
		}
	}
	public static void make(WormCanvas wc){
		File f = new File(Constants.settings);
		if(f.exists()){
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write("".getBytes());
			String settings = wc.minFruit + " " + wc.maxFruit + " " + wc.minspeed + " " + wc.percent;
			fos.write(settings.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static boolean exists(){
		File f = new File(Constants.settings);
		return f.exists();
	}
}
