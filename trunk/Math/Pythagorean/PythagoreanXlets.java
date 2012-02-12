import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class PythagoreanXlets {
	private FileWriter fw;
	private boolean run = false;
	public static void main(String[] args) throws IOException{
		while(true){
			try{
				String file = JOptionPane.showInputDialog("File location: ");
				PythagoreanXlets p = new PythagoreanXlets(new File(file));
				int count = Integer.parseInt(JOptionPane.showInputDialog("Number of sides: "));
				p.start(count);
				JOptionPane.showMessageDialog(null, "Running. Press OK to stop.", "Running", JOptionPane.INFORMATION_MESSAGE);
				p.stop();
			} catch (Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, ex.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	public PythagoreanXlets(File f) throws IOException{
		f.delete();
		f.createNewFile();
		fw = new FileWriter(f);
	}

	public void stop(){
		run = false;
	}

	public synchronized void start(final int sides){
		if(run == true)
			return;
		run = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				getPythagoreans(sides);
			}
		}).start();
	}

	public void getPythagoreans(int numSides){
		int[] sides = new int[numSides];
		for (int i = 0; i < sides.length; i++) {
			sides[i] = 1;
		}
		while(run){
			whileLoop: while(run){
				for (int i = 1; i < sides.length; i++) {
					sides[i]++;
					if(sides[i] > sides[0]){
						sides[i] = 1;
						if(i == sides.length - 1)
							break whileLoop;
					} else {
						break;
					}
				}
				pythag(sides);
			}
		for (int i = 1; i < sides.length; i++) {
			sides[i] = 1;
		}
		sides[0]++;
		}
	}
	private void print(int[] sides) {
		for (int i = sides.length - 1; i >= 0; i--) {
			log(sides[i] + " ");
		}
	}
	private void pythag(int[] sides){
		for (int i = 0; i < sides.length; i++) {
			for (int j = i + 1; j < sides.length; j++) {
				if(sides[j] > sides[i])
					return;
			}
		}
		long sum = 0;
		for (int i = 0; i < sides.length; i++) {
			sum += sides[i] * sides[i];
		}
		if(isPerfectSquare(sum)){
			print(sides);
			log("| " + (long)Math.sqrt(sum) + System.getProperty("line.separator"));
			try {
				fw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public final static boolean isPerfectSquare(long n) { if (n < 0) return false; long tst = (long)(Math.sqrt(n) + 0.5); return tst*tst == n; }
	public void log(String s){
		System.out.print(s);
		try {
			fw.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}