package wireframe.wireframe4d;

import wireframe.wireframe2d.Wire2D;

public class Wire4D {
	public Vector4D p2;
	public Vector4D p1;
	public Wire4D(Vector4D p1, Vector4D p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Wire2D flatten(Vector4D cam, Vector3D cam3d){
		return new Wire2D(p1.flatten(cam, cam3d), p2.flatten(cam, cam3d));
	}
	
}
