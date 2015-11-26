package thesis;


import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import processing.core.PApplet;
import toxi.geom.*;

//Point Cloud Octree Container
public class tpointCloud extends PointOctree {
	thesisPointStructurev2 p;
	int nPTS = 0;
	Vector<Integer> apID = new Vector<Integer>();

	public tpointCloud(Vec3D c, float s, thesisPointStructurev2 parent) {
		super(c, s);
		p = parent;
		size = (int) s;

		this.addPoint(new Vec3D(0, 0, 0));
	}

	void drawTree() {
		drawNode(this);
	}

	void drawNode(PointOctree n) {
		if (n.getNumChildren() > 0) {
			p.noFill();
			p.stroke(n.getDepth(), 10);
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

	void update() {

	}

	void drawPoints() {
		p.stroke(0);
		p.strokeWeight(3);
		for (Vec3D v : this.getPoints())
			p.gfx.point(v);

		p.strokeWeight(1);
	}

	void readFile(String fPath) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		File f = new File("");

		try {
			f = new File(fPath);
			p.gui.sendMessage(f.getName() + " opened.");
		} catch (NullPointerException ex) {
			p.gui.sendMessage("File: " + " could not be found.");
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++) {
			String clean = strLines[i].substring(1, strLines[i].length() - 1);

			String[] splitToken = clean.split(", ");

			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx*p.SF, yy*p.SF, zz*p.SF);
			importPts.add(ptt);
		}

		this.populateFromFile(importPts);
		
	p.gui.sendMessage("--"+this.getPoints().size()+" points added.");
	}

	void populateFromFile(ArrayList<Vec3D> _pts) {
		ArrayList<Vec3D> inpt = _pts;
		for (int i = 0; i < inpt.size(); i++){
				this.addPoint(inpt.get(i));
		}

		nPTS = this.getPoints().size();
	}
	
	
	public void clearPC(){
		this.empty();
		this.nPTS = 0;
	}
}
