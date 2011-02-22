package worm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Autoupdater extends JFrame{
	private static final long serialVersionUID = 1L;
	private JLabel label = new JLabel("Updating...");
	private JProgressBar progress = new JProgressBar(0,1000);
	public static Autoupdater update(){
		try {
			if(checkForUpdates()){
				System.out.println("Found update");
				return new Autoupdater();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static boolean checkForUpdates() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(Constants.src)));
		while(true){
			String line = br.readLine();
			if(line == null)
				break;
			if(line.contains("private static final long serialVersionUID")){
				String version = line.split("private static final long serialVersionUID = ")[1];
				version = version.split("L")[0];
				System.out.println(version);
				long vers = Long.parseLong(version);
				boolean ret = false;
				if(vers != serialVersionUID)
					ret = true;
				br.close();
				return ret;
			}
		}
		br.close();
		return false;
	}
	private boolean downloading = true;
	private Autoupdater(){
		add(label, BorderLayout.NORTH);
		add(progress, BorderLayout.CENTER);
		pack();
		setVisible(true);
		try {
			File dir = new File(System.getProperty("user.dir") + File.separator + "Worms.jar");
			File dest = download(dir.length());
			label.setText("Refreshing file...");
			dir.delete();
			dir.createNewFile();
			FileReader fr = new FileReader(dest);
			FileWriter fw = new FileWriter(dir);
			label.setText("Transferring file...");
			while(true){
				int i = fr.read();
				if(i == -1)
					break;
				fw.write(i);
				progress.setValue((int)((double)dir.length()/(double)dest.length() * 1000));
			}
			fr.close();
			fw.close();
			dest.delete();
			System.out.println("Harro!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		downloading = false;
	}
	public boolean downloading(){
		return downloading;
	}
	private File download(long len) throws Exception{
		File dest = getRoot();
		//URL url = new URL(Constants.update);
		//BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
		BufferedReader br = new BufferedReader(new FileReader(new File(Constants.update)));
		try{
			dest.createNewFile();
		} catch (IOException ex){
			System.out.println("File " + dest.toString() + " could not be created. Cancelling download");
			return null;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(dest));
		while(true){
			String line = br.readLine();
			if(line == null)
				break;
			bw.write(line);
			progress.setValue((int)((double)dest.length()/(double)len * 1000));
		}
		br.close();
		bw.close();
		System.out.println("Update downloaded");
		return dest;
	}
	private File getRoot() {
		File[] rts = File.listRoots();
		for(File f : rts){
			if(f.canWrite()){
				File file = new File(f.toString() + File.separator + "wormtemp" + File.separator + "Worms.jar");
				file.getParentFile().mkdirs();
				try {
					file.createNewFile();
					return file;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
