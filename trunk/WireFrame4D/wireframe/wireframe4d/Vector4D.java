package wireframe.wireframe4d;

import java.awt.Point;

public class Vector4D {
	public double x, y, z, w;

	public Vector4D(double x, double y, double z, double w) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point flatten(Vector4D cam1, Vector3D cam2){
		return flattenTo3D(cam1).flatten(cam2);
	}
	
	private Vector3D flattenTo3D(Vector4D cam){
		Vector4D dV = new Vector4D(x - cam.x, y - cam.y, z - cam.z, w - cam.z);
		double lamda = cam.w / (double)dV.w;
		int x = (int)Math.round(cam.x + lamda * dV.x);
		int y = (int)Math.round(cam.y + lamda * dV.y);
		int z = (int)Math.round(cam.z + lamda * dV.z);
		return new Vector3D(x,y,z);
	}

}