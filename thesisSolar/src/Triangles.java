import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import processing.core.PApplet;
import toxi.geom.IsectData3D;
import toxi.geom.Ray3D;
import toxi.geom.Triangle3D;
import toxi.geom.TriangleIntersector;
import toxi.geom.Vec3D;

public class Triangles {
	SolarTriangles p;
	Vector<Tri> triangleList = new Vector<Tri>();
	Vector<Vec3D> points = new Vector<Vec3D>();
	float[] colRange = new float[2];
	boolean trigger = true;

	Triangles(SolarTriangles parent) {
		p = parent;

	}

	void execute() {
		while (trigger) {

			this.getRays();
			this.testIntersect();
			this.calcColorValue();
		}

	}

	void calcColorValue() {

		float[] values = new float[triangleList.size()];
		for (int i = 0; i < triangleList.size(); i++) {
			values[i] = triangleList.get(i).value;
		}
		colRange = p.getRange(values);
	}

	void draw() {
		for (Tri t : triangleList) {
			p.stroke(0);
			p.strokeWeight(1);
			p.fill(255 * ((t.value + 1) / colRange[1]),
					200 * ((t.value + 1) / colRange[1]),
					155 * ((t.value + 1) / colRange[1]), 200);
			p.gfx.triangle(t.getTriangle());
		}

		for (Vec3D v : points) {
			p.stroke(255, 0, 0);
			p.gfx.point(v);
		}

	}

	void testIntersect() {
		for (Tri t : triangleList) {
			Vector<Ray3D> rayList = t.solarRays;
			int tally = 0;
			for (Ray3D r : rayList) {
				tally += numOfIntersections(r);
			}
			t.increase(tally);
			//p.println(tally);
		}
		trigger = false;
	}

	int numOfIntersections(Ray3D rayToCheck) {
		int count = 0;

		for (Tri tt : triangleList) {
			Triangle3D t = tt.t;
			TriangleIntersector tSect = new TriangleIntersector(t);
			if (tSect.intersectsRay(rayToCheck)) {
				IsectData3D isec = tSect.getIntersectionData();
				if (t.containsPoint(isec.pos)) {
					points.add((Vec3D) isec.pos);
					count++;
				}
			}
		}

		return count;
	}

	void getRays() {
		for (Tri t : this.triangleList) {
			t.populateRays(p.Srs.getSVecs());
		}
	}

	boolean flipTriangle(Triangle3D triangle) {
		boolean flip = false;
		Vec3D norm = triangle.computeNormal();
		float testDir = norm.angleBetween(p.unitZ);
		if (testDir < (p.PI / 2)) {
			flip = true;
			norm = norm.getInverted();
		} else {
			flip = false;
		}
		return flip;
	}

	void readFile(String fPath) {
		Vector<Tri> getTri = new Vector<Tri>();
		File f = new File("");
		try {
			f = new File(fPath);
			// p.gui.sendMessage(f.getName() + " opened.");
		} catch (NullPointerException ex) {
			// p.gui.sendMessage("File: " + " could not be found.");
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());
		int count = 0;
		for (int i = 0; i < strLines.length; i += 3) {
			String line1 = strLines[i].substring(0, strLines[i].length());
			String line2 = strLines[i + 1].substring(0,
					strLines[i + 1].length());
			String line3 = strLines[i + 2].substring(0,
					strLines[i + 2].length());

			String[] split1 = line1.split(", ");
			String[] split2 = line2.split(", ");
			String[] split3 = line3.split(", ");

			float x1 = PApplet.parseFloat(split1[0]);
			float y1 = PApplet.parseFloat(split1[1]);
			float z1 = PApplet.parseFloat(split1[2]);
			Vec3D A = new Vec3D(x1 * p.SF, y1 * p.SF, z1 * p.SF);

			float x2 = PApplet.parseFloat(split2[0]);
			float y2 = PApplet.parseFloat(split2[1]);
			float z2 = PApplet.parseFloat(split2[2]);
			Vec3D B = new Vec3D(x2 * p.SF, y2 * p.SF, z2 * p.SF);

			float x3 = PApplet.parseFloat(split3[0]);
			float y3 = PApplet.parseFloat(split3[1]);
			float z3 = PApplet.parseFloat(split3[2]);
			Vec3D C = new Vec3D(x3 * p.SF, y3 * p.SF, z3 * p.SF);

			Triangle3D importTri = new Triangle3D(A, B, C);
			if(this.flipTriangle(importTri)){
				importTri = importTri.flipVertexOrder();
			}
			Tri tImport = new Tri(p, importTri, count, 0);
			getTri.add(tImport);
			count++;
		}

		triangleList = getTri;

	}

	Vector<Triangle3D> getTriangles() {
		Vector<Triangle3D> getTriangle = new Vector<Triangle3D>();
		for (Tri tr : this.triangleList) {
			getTriangle.add(tr.t);
		}
		return getTriangle;
	}

	void exportTriangles() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_allTriangles.txt"));

		for (Tri tr : this.triangleList) {

			Triangle3D t = tr.t;

			p.OUTPUT.println(tr.id);
			p.OUTPUT.println(tr.value);
			p.OUTPUT.println(t.a.x + ", " + t.a.y + ", " + t.a.z);
			p.OUTPUT.println(t.b.x + ", " + t.b.y + ", " + t.b.z);
			p.OUTPUT.println(t.c.x + ", " + t.c.y + ", " + t.c.z);
		}

		p.OUTPUT.flush();
		p.OUTPUT.close();
	}

}
