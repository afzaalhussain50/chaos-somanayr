package worm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class HighScores extends JFrame{
	JLabel score;
	JList list;
	JScrollPane pane;
	JButton submit;
	JTextField name;
	static char sep = 250;
	public HighScores(final int score){
		super("High Scores");
		Score[] scores = loadscore();
		if(scores.length > 1)
			scores = sort(scores);
		list = new JList(scores);
		pane = new JScrollPane();
		pane.setViewportView(list);
		JPanel holder = new JPanel();
		holder.setLayout(new FlowLayout());
		this.score = new JLabel("You scored: " + score);
		name = new JTextField(10);
		submit = new JButton("Submit");
		holder.add(this.score);
		holder.add(name);
		holder.add(submit);
		add(holder, BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
		pack();
		setVisible(true);
		name.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					try {
						sendScore(score, name.getText());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setVisible(false);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
			
		});
		submit.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				try {
					sendScore(score, name.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				setVisible(false);
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
	}
	public Score[] loadscore(){
		File f = new File(Constants.hs);
		if(!f.exists())
			return new Score[0];
		Scanner s = null;
		try {
			s = new Scanner(f);
			s.useDelimiter("" + sep);
		} catch (FileNotFoundException e) {
			return new Score[0];
		}
		ArrayList<Score> al = new ArrayList<Score>();
		while(s.hasNext()){
			try{
				al.add(new Score(s.nextInt(), s.next()));
			} catch (NoSuchElementException ex){
				ex.printStackTrace();
				return new Score[0];
			}
		}
		al.trimToSize();
		Score[] i = new Score[al.size()];
		for (int j = 0; j < i.length; j++) {
			i[j] = al.get(j);
		}
		s.close();
		return i;
	}
	public static Score[] sort(Score[] x){
		while(true){
			boolean flag = false;
			for (int i = 0; i < x.length - 1; i++) {
				if(x[i].score < x[i + 1].score){
					Score temp = x[i];
					x[i] = x[i + 1];
					x[i + 1] = temp;
					flag = true;
				}
			}
			if(!flag)
				break;
		}
		return x;
	}
	public void sendScore(int score, String name) throws IOException{
		File f = new File(Constants.hs);
		if(!f.exists()){
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		FileWriter fw = new FileWriter(f, true);
		fw.write("" + sep + score + sep + name);
		fw.close();
	}
	public static void main(String[] args){
		new HighScores((int) (Math.random() * 50));
	}
}
class Score{
	int score;
	String name;
	public Score(int s, String n){
		score = s;
		name = n;
	}
	public String toString(){
		return name + ": " + score;
	}
}
