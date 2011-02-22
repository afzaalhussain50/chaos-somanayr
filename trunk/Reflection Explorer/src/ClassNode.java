import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;


public class ClassNode implements TreeNode{
	
	private ArrayList<ClassNode> children = new ArrayList<ClassNode>();
	
	private int size;

	private Object obj;
	
	private ClassNode parent;
	
	private String name;
	
	private String type;
	
	private boolean stat1c;
	
	private boolean isArray = false;
	
	private Field f;
	
	public ClassNode(Object obj, ClassNode parent, boolean isStatic, String name, String type, Field derivedFrom){
		this.obj = obj;
		this.parent = parent;
		if(obj == null)
			size = 0;
		else{
			size = obj.getClass().getDeclaredFields().length;
		}
		try{
			size = Array.getLength(obj);
			isArray = true;
		} catch (Exception ex){
		}
		this.name = name;
		this.type = type;
		stat1c = isStatic;
		f = derivedFrom;
	}
	
	public Enumeration<? extends Object> children() {
		return new ClassNodeEnumerator(children);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		if(children.size() != size)
			generateChildren();
		return children.get(childIndex);
	}

	public int getChildCount() {
		return size;
	}

	public int getIndex(TreeNode node) {
		if(children.size() != size)
			generateChildren();
		return children.indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return Explorer.isPrimitive(obj);
	}
	
	public void generateChildren(){
		Class<? extends Object> c = obj.getClass();
		if(isArray){
			System.out.println("Is array: " + toString() + " size: " + Array.getLength(obj) + "My size: " + size);
			for(int i = 0; i < size; i++){
				try {
					System.out.println(Array.get(obj, i));
					children.add(new ClassNode(Array.get(obj, i), this, false, "" + i, type.substring(1, type.length()), null));
				} catch (Exception e) { 
					e.printStackTrace();
				}
			}
			return;
		}
		for(Field f : c.getDeclaredFields()){
			f.setAccessible(true);
			try {
				children.add(new ClassNode(f.get(obj), this, Modifier.isStatic(f.getModifiers()), f.getName(),  f.getType().getName(), f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		children.trimToSize();
	}
	
	public void dumpChildren(){
		children = new ArrayList<ClassNode>();
	}
	
	public String toString(){
		if(isLeaf() || obj instanceof String)
			return (stat1c ? "static " : "") + type + " " + name + " = "+ String.valueOf(obj);
		else
			return (stat1c ? "static " : "" ) + type + " " + name;
	}
	
	public boolean equals(Object obj){
		ClassNode node = (ClassNode) obj;
		return(node.obj == this.obj);
	}
	
	public Object getValue(){
		return obj;
	}

	public ObjectWrapper getWrapper() {
		return new ObjectWrapper(obj, f);
	}

}
