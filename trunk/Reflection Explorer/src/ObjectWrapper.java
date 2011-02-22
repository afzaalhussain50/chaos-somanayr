import java.lang.reflect.Field;


public class ObjectWrapper {
	private Object obj;
	private Field f;

	public ObjectWrapper(Object obj, Field f){
		this.obj = obj;
		this.f = f;
		if(f != null)
		f.setAccessible(true);
	}
	
	public Object get(){
		return obj;
	}
	
	public String toString(){
		if(f == null)
			return String.valueOf(obj);
		StringBuffer buf = new StringBuffer(f.getType().toString()).append(' ').append(f.getName());
		if(Explorer.isPrimitive(get()))
				buf.append(" = ").append(f.toString());
		return buf.toString();
	}
	
	public static Object[] extract(Object[] wrappers){
		Object[] obj = new Object[wrappers.length];
		for (int i = 0; i < obj.length; i++) {
			obj[i] = ((ObjectWrapper)wrappers[i]).get();
		}
		return obj;
	}
	
	public boolean equals(Object object){
		Object anObj = ((ObjectWrapper)object).obj;
		if(anObj == null)
			if(obj == null)
				return true;
			else
				return false;
		return obj.equals(anObj);
	}
	
}
