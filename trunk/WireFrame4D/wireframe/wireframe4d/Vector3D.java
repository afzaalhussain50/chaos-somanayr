package wireframe.wireframe4d;

import java.awt.Point;


public class Vector3D {

	public int x, y, z;

	public Vector3D(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point flatten(Vector3D cam){
		Vector3D dV = new Vector3D(x - cam.x, y - cam.y, z - cam.z);
		double lamda = cam.z / (double)dV.z;
		int x = (int)Math.round(cam.x + lamda * dV.x);
		int y = (int)Math.round(cam.y + lamda * dV.y);
		return new Point(x,y);
	}

	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
}