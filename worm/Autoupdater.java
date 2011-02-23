package worm;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Autoupdater extends JFrame implements Serializable{
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
		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(Constants.src).openStream()));
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
			label.setText("Downloading...");
			File dest = download(dir.length());
			label.setText("Refreshing file...");
			dir.delete();
			dir.createNewFile();
			FileInputStream is = new FileInputStream(dest);
			FileOutputStream os = new FileOutputStream(dir);
			label.setText("Transferring file...");
			System.out.println("Transferring to: " + dir);
			int checksum = 0;
			while(true){
				int i = is.read();
				if(i == -1)
					break;
				checksum += i;
				os.write(i);
				progress.setValue((int)((double)dir.length()/(double)dest.length() * 1000));
			}
			System.out.println("Checksum: " + checksum);
			is.close();
			os.close();
			dest.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		downloading = false;
	}
	
	static String hexSet = "0123456789ABCDEF";
	
	private String toHex(int i) {
		StringBuffer buf = new StringBuffer();
		for(int x = 0; x < 2; x++){
			byte hexVal = (byte) (i % 16);
			i /= 16;
			buf.append(hexSet.charAt((int)hexVal));
		}
		return buf.toString();
	}
	
	public boolean downloading(){
		return downloading;
	}
	private File download(long len) throws Exception{
		File dest = getRoot();
		System.out.println("Downloading to: " + dest);
		BufferedInputStream is = new BufferedInputStream(new URL(Constants.update).openStream());
		try{
			dest.createNewFile();
		} catch (IOException ex){
			System.out.println("File " + dest.toString() + " could not be created. Cancelling download");
			return null;
		}
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
		long count = 0;
		while(true){
			int i = is.read();
			if(i == -1)
				break;
			count+=i;
			os.write(i);
			progress.setValue((int)((double)dest.length()/(double)len * 1000));
		}
		System.out.println("Checksum: " + count);
		is.close();
		os.close();
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
