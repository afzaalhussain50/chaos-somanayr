package worm;

public class Constants {
	public static String base = "Y:\\Worm\\";
	public static String hs = base + "scores.txt";
	public static String settings = base + "settings.txt";
	public static String trunk = "http://code.google.com/p/worm-game-awty";
	public static String update = trunk + "Worm.jar";
	public static String src = trunk + "src/worm/Autoupdater.java";
	public static void regen(){
		hs = base + "scores.txt";
		update = trunk + "Worm.jar";
		src = trunk + "src/worm/Autoupdater.java";
		settings = base + "settings.txt";
	}
}
