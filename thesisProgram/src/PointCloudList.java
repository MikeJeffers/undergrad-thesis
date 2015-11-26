import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class PointCloudList {
	ArrayList<PcPoint> PtList;
	Program p;
	PrintWriter OUTPUT;

	PointCloudList(Program parent) {
		p = parent;
		PtList = new ArrayList<PcPoint>();
	}

	void execute() {
		for (PcPoint a : PtList) {
			a.run();

		}
	}

	void bump(Agent a) {
		for (PcPoint p : PtList) {
			p.collide(a);
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
			Random myRandom = new Random();
			float factor = myRandom.nextFloat();
			float newRad = (p.aRad + (p.aRad * factor)) / 1.5f;
			PtList.add(new PcPoint(iPts.get(i), i, p.aRad, p));
			p.println(newRad);
		}
	}

	void exportPoints() {
		OUTPUT = p
				.createWriter(p.sketchPath("output/" + PApplet.second() + "_"
						+ PApplet.month() + "_" + PApplet.day() + "_"
						+ PApplet.hour() + "_" + PApplet.minute() + "_"
						+ p.frameCount + "_ptStack.txt"));

		ArrayList<Vec3D> getPos = new ArrayList<Vec3D>();
		for (PcPoint p : PtList) {
			getPos.add(p.loc);
		}
		for (Vec3D v : getPos) {
			OUTPUT.println("{" + v.x + ", " + v.y + ", " + v.z + "}");
		}
		OUTPUT.flush();
		OUTPUT.close();
	}

	void draw() {
		for (PcPoint a : PtList)
			a.draw();
	}

	void addPdot(PcPoint a) {
		PtList.add(a);
	}

	ArrayList<PcPoint> getAllPts() {
		return PtList;
	}

	int progPointsSize() {
		return PtList.size();
	}

	PcPoint getP(int idd) {
		return (PcPoint) PtList.get(idd);
	}
}