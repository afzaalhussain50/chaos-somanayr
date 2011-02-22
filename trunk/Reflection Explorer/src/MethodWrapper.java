import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodWrapper {
	private Object obj;
	private Method m;

	public MethodWrapper(Object obj, Method m){
		this.obj = obj;
		this.m = m;
		m.setAccessible(true);
	}
	
	public Object invoke(Object[] args){
		try {
			return m.invoke(obj, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer(m.getReturnType().getCanonicalName()).append(' ').append(m.getName()).append('(');
		boolean flag = false;
		for(Class<?> c : m.getParameterTypes()){
			if(!flag)
				buf.append(c.getCanonicalName());
			else
				buf.append(", ").append(c.getCanonicalName());
			flag = true;
		}
		buf.append(')');
		return buf.toString();
	}
}
