import java.util.Vector;

import toxi.geom.Ray3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;

public class Tri {
	SolarTriangles p;
	int id;
	int value;
	Triangle3D t;
	Vector<Ray3D> solarRays = new Vector<Ray3D>();

	Tri(SolarTriangles parent, Triangle3D _t, int _id, int _val) {
		p = parent;
		id = _id;
		value = _val;
		t = _t;

	}

	Triangle3D getTriangle() {
		return t;
	}

	void populateRays(Vector<Vec3D> directions) {
		Vector<Ray3D> raysOnTri = new Vector<Ray3D>();
		for (Vec3D v : directions) {
			Vec3D newTarget = this.t.computeCentroid();
			Ray3D nRay = new Ray3D(newTarget, v);
			raysOnTri.add(nRay);
		}
		this.solarRays = raysOnTri;
	}

	void increase(int inc) {
		this.value += inc;
	}

	void decrease(int inc) {
		this.value -= inc;
	}

}
