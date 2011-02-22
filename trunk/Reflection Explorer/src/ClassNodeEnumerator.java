import java.util.ArrayList;
import java.util.Enumeration;


public class ClassNodeEnumerator implements Enumeration<ClassNode>{

	private ArrayList<ClassNode> classes = new ArrayList<ClassNode>();
	
	private int runningCount = 0;
	
	public ClassNodeEnumerator(ArrayList<ClassNode> classes){
		this.classes = classes;
	}
	
	public boolean hasMoreElements() {
		return runningCount < classes.size();
	}

	public ClassNode nextElement() {
		runningCount++;
		return classes.get(runningCount - 1);
	}

}
