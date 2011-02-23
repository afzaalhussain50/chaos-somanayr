package worm;

import java.io.File;

public class Constants {
	public static String base = System.getProperty("user.dir");
	public static String hs = base + "scores.txt";
	public static String settings = base + "settings.txt";
	public static String trunk = "http://chaos-somanayr.googlecode.com/svn/trunk/worm/";
	public static String update = trunk + "resources/worm.jar";
	public static String src = trunk + "Autoupdater.java";
	public static void regen(){
		hs = base + "scores.txt";
		update = trunk + "Worm.jar";
		src = trunk + "Autoupdater.java";
		settings = base + "settings.txt";
	}
//	public static File getRoot() {
//		File[] rts = File.listRoots();
//		for(File f : rts){
//			if(f.canWrite()){
//				return f;
//			}
//		}
//		return null;
//	}
}
