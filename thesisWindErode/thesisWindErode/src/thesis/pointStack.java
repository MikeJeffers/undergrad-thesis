package thesis;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.PApplet;
import java.util.List;
import java.util.Vector;

import toxi.geom.*;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.TriangleMesh;

public class pointStack extends PointOctree {
	Vector<Integer> vals = new Vector<Integer>();
	Vector<Vec3D> boxPts = new Vector<Vec3D>();
	thesisWindErode p;
	int RAD;
	int size;
	int stepZ;
	int nZ;

	TriangleMesh boxMesh;
	PrintWriter OUTPUT;

	public pointStack(Vec3D c, float s, thesisWindErode parent) {
		super(c, s);
		p = parent;
		size = (int) s;
		this.RAD = p.RAD;
	}

	void update() {

	}

	void draw() {
		drawNode(this);
	}

	void calcBoxMesh() {
		boxMesh = new TriangleMesh();

		for (Vec3D v : this.getPoints()) {

			int nNeighbors = this.getPointsWithinSphere(v, RAD).size();

			if (nNeighbors <= 6) {
				boxPts.add(v.copy());
				Mesh3D box = new AABB(v, RAD / 2).toMesh();
				boxMesh.addMesh(box);
			}
		}

	}

	void exportPoints() {
		OUTPUT = p
				.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
						+ PApplet.month() + "_" + PApplet.day() + "_"
						+ PApplet.hour() + "_" + PApplet.minute() + "_"
						+ p.frameCount + "_ptStack.txt"));
		for (Vec3D v : this.getPoints()) {
		//	int nNeighbors = this.getPointsWithinSphere(v, RAD).size();

		//	if (nNeighbors <= 6) {
				OUTPUT.println("{" + v.x + ", " + v.y + ", " + v.z + "}");
		//	}
		}

		OUTPUT.flush();
		OUTPUT.close();

	}

	void drawBoxMesh() {
		p.stroke(0);
		p.fill(200);

		p.gfx.mesh(boxMesh);
	}

	void drawNode(PointOctree n) {
		if (n.getNumChildren() > 0) {
			p.noFill();
			p.stroke(255-(n.getDepth()*10), 20);
			p.pushMatrix();
			p.translate(n.x, n.y, n.z);
			p.box(n.getNodeSize());
			p.popMatrix();
			PointOctree[] childNodes = n.getChildren();
			for (int i = 0; i < 8; i++) {
				if (childNodes[i] != null)
					drawNode(childNodes[i]);
			}
		}
	}

	void populate(int nX, int nY, int nZ, int sX, int sY, int sZ) {
		float x = (sX * nX) / 2;
		float y = (sY * nY) / 2;

		this.RAD = p.cRAD;

		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				for (int k = 0; k < nZ; k++) {
					this.addPoint(new Vec3D(x - (i * sX), y - (j * sY), k * sZ));
					vals.add(2);
				}
			}
		}
	}

	void readFile(String fName) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		
		File f = new File("");
		try {

			f = new File(p.dataPath(fName));

		} catch (NullPointerException ex) {
			PApplet.println("File: " + " could not be found.");
			// return false;
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++) {
			String clean = strLines[i].substring(1, strLines[i].length() - 1);

			String[] splitToken = clean.split(", ");

			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx, yy, zz);
			importPts.add(ptt);
		}

		this.populateFromFile(importPts);
	}

	void populateFromFile(ArrayList<Vec3D> _pts) {
		ArrayList<Vec3D> inpt = _pts;

		for (int i = 0; i < inpt.size(); i++) {
			this.addPoint(inpt.get(i));
			vals.add(5);
		}
		vals.set(vals.size() - 1, 0);
	}

	void removePt(Vec3D v) {

		List<Vec3D> pts = this.getPoints();
		int ptID = 0;
		for (int i = 0; i < pts.size(); i++) {
			if (pts.get(i) == v) {
				ptID = i;
				break;
			}
		}
		this.remove(v);
		vals.remove(ptID);

	}

	void addPT(Vec3D pt, Vec3D v) {
		this.addPoint(pt.copy().addSelf(v.normalizeTo(-3)));
		vals.add(2);
	}

	void addHealth(Vec3D v) {
		List<Vec3D> pts = this.getPoints();
		int ptID = 0;
		for (int i = 0; i < pts.size(); i++) {
			if (pts.get(i) == v) {
				ptID = i;
				break;
			}
		}
		vals.set(ptID, vals.get(ptID) + 1);
	}

	void hitPT(Vec3D v) {
		List<Vec3D> pts = this.getPoints();
		int ptID = 0;
		for (int i = 0; i < pts.size(); i++) {
			if (pts.get(i) == v) {
				ptID = i;
				break;
			}
		}
		if (vals.get(ptID) <= 1) {
			this.remove(v);
			vals.remove(ptID);
		} else {
			vals.set(ptID, vals.get(ptID) - 1);
		}
	}

	void drawPoints() {
		p.stroke(0);
		int i = 0;
		for (Vec3D v : this.getPoints()) {
			int col = vals.get(i);
			p.fill(PApplet.map(col, 1, 5, 75, 225));
			p.strokeWeight(1);
			p.gfx.box(new AABB(v, RAD/2));
			i++;
		}
	}

	void saveMesh() {
		this.calcBoxMesh();
		boxMesh.saveAsSTL(
				p.sketchPath("output\\stl\\" + PApplet.year() + "_"
						+ PApplet.month() + "_" + PApplet.day() + "_"
						+ PApplet.hour() + "_" + PApplet.minute() + "_"
						+ p.frameCount + "boxMesh.stl"), false);
	}

}
