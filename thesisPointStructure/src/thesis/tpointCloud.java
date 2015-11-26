package thesis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;
import toxi.geom.*;
import toxi.physics.VerletParticle;

//Point Cloud Octree Container
public class tpointCloud extends PointOctree {
	thesisPointStructure p;
	int nPTS = 0;
	Vector<Integer> apID = new Vector<Integer>();

	public tpointCloud(Vec3D c, float s, thesisPointStructure parent) {
		super(c, s);
		p = parent;
		size = (int) s;

	}

	void draw() {
		drawNode(this);
	}

	void drawNode(PointOctree n) {
		if (n.getNumChildren() > 0) {
			p.noFill();
			p.stroke(n.getDepth(), 20);
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
		p.stroke(255,50,0);
		p.strokeWeight(5);
		for (Vec3D v : this.getPoints())
			p.gfx.point(v);

		p.strokeWeight(1);
	}

	void readFile(String fName) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		File f = new File("");

		try {
			f = new File(p.dataPath(fName));
			PApplet.println(fName + " opened.");
		} catch (NullPointerException ex) {
			PApplet.println("File: " + " could not be found.");
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++) {
			String clean = strLines[i].substring(1, strLines[i].length() - 1);

			String[] splitToken = clean.split(", ");

			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = -PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx*p.SF, yy*p.SF, zz*p.SF);
			importPts.add(ptt);
		}

		this.populateFromFile(importPts);
		
	PApplet.println("--"+this.getPoints().size()+" points added.");
	}
	
	
	void readFileAP(String fName) {
		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		File f = new File("");

		try {
			f = new File(p.dataPath(fName));
			PApplet.println(fName + " opened to check ap locations.");
		} catch (NullPointerException ex) {
			PApplet.println("File: " + " could not be found.");
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++) {
			String clean = strLines[i].substring(1, strLines[i].length() - 1);

			String[] splitToken = clean.split(", ");

			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = -PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx*p.SF, yy*p.SF, zz*p.SF);
			importPts.add(ptt);
		}
		
		for(Vec3D op : this.getPoints()){
			for(Vec3D v : importPts){
				if(v.equals(op))
					apID.add(this.getPoints().indexOf(op));
			}

		}
		PApplet.println("--"+this.apID.size()+" anchor points added.");
	}

	void populateFromFile(ArrayList<Vec3D> _pts) {
		ArrayList<Vec3D> inpt = _pts;

		for (int i = 0; i < inpt.size(); i++)
			this.addPoint(inpt.get(i));

		nPTS = this.getPoints().size();
	}
	
	
	public void clearPC(){
		this.empty();
		this.apID.clear();
		this.nPTS = 0;
	}

	public void loadFromPhysics(List<VerletParticle> particles) {
		PApplet.println("--Loading pts from physics to Point Cloud.");
		for(VerletParticle p : particles){
			this.addPoint((Vec3D) p);
		}
		nPTS = this.getPoints().size();
		
		this.readFileAP(p.fNameAP);
	}
}
