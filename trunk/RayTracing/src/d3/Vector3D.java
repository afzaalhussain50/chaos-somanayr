package d3;

public class Vector3D {
	
	public int x, y, z;
	
	public Vector3D(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double magnitude(){
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
