package thesis;
import java.util.ArrayList;
import java.util.Vector;
import toxi.geom.*;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.Vertex;

public class emitter {

	thesisWindErode p;
	Vec3D loc, dir;
	int range;
	Vector<force> forces = new Vector<force>();
	ArrayList<Vertex> verts = new ArrayList<Vertex>();

	boolean add;
	int rang = 5;
	int nVerts;

	emitter(String fileName, boolean a, thesisWindErode parent) {

		add = a;
		p = parent;
		loc = new Vec3D();

		TriangleMesh windMesh = (TriangleMesh) new STLReader().loadBinary(
				p.dataPath(fileName), STLReader.TRIANGLEMESH);
		windMesh = windMesh.getScaled(10);
		verts.addAll(windMesh.getVertices());
		nVerts = verts.size();

		windMesh.clear();

		for (int i = 0; i < verts.size(); i = i + 100) {
			Vertex v = verts.get(i);
			Vec3D dir = v.normal.getInverted();

			forces.add(new force((Vec3D) v, dir.jitter(.01f), add, p));
		}

	}

	void update() {
		Vector<force> usedForces = new Vector<force>();
		for (force f : forces) {
			f.update();
			if (f.on == false)
				usedForces.add(f);
			if (f.loc.distanceTo(loc) > 10000)
				usedForces.add(f);
		}
		forces.removeAll(usedForces);
	}

	void draw() {
		p.fill(255, 0, 0);

		for (force f : forces) {
			f.draw();
		}
	}

	void addPts(int n) {

		for (int i = 0; i < n; i++) {
			int index = (int) (p.random(0, nVerts));
			Vertex v = verts.get(index);
			Vec3D dir = v.normal.getInverted();

			forces.add(new force((Vec3D) v, dir.jitter(.01f), add, p));
		}

	}

}
