package wireframe.wireframe4d;

import wireframe.wireframe2d.Wire2D;
import wireframe.wireframe2d.Wireframe2D;

public class Wireframe4D {
	private Wire4D[] wires;

	public Wireframe4D(Wire4D[] wires) {
		this.wires = wires;
	}
	
	public Wireframe2D flatten(Vector4D camera, Vector3D cam3d){
		Wire2D[] flattened = new Wire2D[wires.length];
		for (int i = 0; i < flattened.length; i++) {
			flattened[i] = wires[i].flatten(camera, cam3d);
			//System.out.println((i + 1) + ": " + flattened[i]);
		}
		return new Wireframe2D(flattened);
	}
}
