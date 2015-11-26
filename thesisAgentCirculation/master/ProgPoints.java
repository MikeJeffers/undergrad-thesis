import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class ProgPoints {
	ArrayList<Pdot> PtList;
	Circulation p;
	PrintWriter OUTPUT;

	ProgPoints(Circulation parent) {
		p = parent;
		PtList = new ArrayList<Pdot>();
	}

	void execute() {
		for (Pdot a : PtList) {
			a.collideSelf(PtList);
		}
	}

	void bump(int i) {
		Agent a = p.myHolder.getA(i);
		for (Pdot p : PtList) {
			p.collide(a);
			p.attract(a);
		}
	}

	void readFile(String fName) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();

		File f = new File("");
		try {

			f = new File(p.dataPath(fName));
			p.println(fName);

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
		this.populate(importPts);
		p.dotTree.addAll(importPts);
	}

	void populate(ArrayList<Vec3D> iPts) {

		for (int i = 0; i < iPts.size(); i++) {
			PtList.add(new Pdot(iPts.get(i), i, p.aRad, p));
		}
	}

	void exportPoints() {
		OUTPUT = p
				.createWriter(p.sketchPath("output/" + PApplet.second() + "_"
						+ PApplet.month() + "_" + PApplet.day() + "_"
						+ PApplet.hour() + "_" + PApplet.minute() + "_"
						+ p.frameCount + "_ptStack.txt"));

		ArrayList<Vec3D> getPos = new ArrayList<Vec3D>();
		for (Pdot p : PtList) {
			getPos.add(p.loc);
		}

		for (Vec3D v : getPos) {

			OUTPUT.println("{" + v.x + ", " + v.y + ", " + v.z + "}");

		}

		OUTPUT.flush();
		OUTPUT.close();
	}

	void draw() {
		for (Pdot a : PtList)
			a.draw();
	}

	void addPdot(Pdot a) {
		PtList.add(a);
	}

	int progPointsSize() {
		return PtList.size();
	}

	Pdot getP(int idd) {
		return (Pdot) PtList.get(idd);
	}
}