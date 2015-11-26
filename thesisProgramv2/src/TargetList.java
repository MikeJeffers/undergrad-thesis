import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class TargetList {
	ArrayList<Target> tList;
	Program p;
	PrintWriter OUTPUT;

	TargetList(Program parent) {
		p = parent;
		tList = new ArrayList<Target>();
	}

	void execute() {
		for (Target t : tList) {
			t.update();
		}
	}

	void readFile(String fName, String uName) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		ArrayList<Vec3D> importDir = new ArrayList<Vec3D>();
		File f = new File("");
		File u = new File("");
		try {
			f = new File(p.dataPath(fName));

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

		try {
			u = new File(p.dataPath(uName));
		} catch (NullPointerException ex) {
			PApplet.println("File: " + "could not be found.");
		}

		String[] lines = p.loadStrings(u.getAbsolutePath());
		for (int i = 0; i < lines.length; i++) {
			String clean = lines[i].substring(1, lines[i].length() - 1);

			String[] splitToken = clean.split(", ");

			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx, yy, zz);
			importDir.add(ptt);
		}

		this.populate(importPts, importDir);
		

	}

	void populate(ArrayList<Vec3D> pts, ArrayList<Vec3D> dirs) {

		for (int i = 0; i < pts.size(); i++) {
			tList.add(new Target(pts.get(i), dirs.get(i), i, p));
		}
	}

	void draw() {
		for (Target t : tList)
			t.draw();
	}

	void addPdot(Target t) {
		tList.add(t);
	}

	ArrayList<Target> getAllPts() {
		return tList;
	}

	int TargetListSize() {
		return tList.size();
	}

	Target getT(int idd) {
		return (Target) tList.get(idd);
	}
}