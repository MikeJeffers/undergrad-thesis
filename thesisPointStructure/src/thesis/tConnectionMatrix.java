package thesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;

import toxi.geom.Line3D;
import toxi.geom.Vec3D;
import toxi.physics.VerletSpring;

public class tConnectionMatrix {
	Vector<Line3D> connections = new Vector<Line3D>();
	Vector<int[]> connectionIDs = new Vector<int[]>();
	thesisPointStructure p;
	ArrayList<Line3D> deleted = new ArrayList<Line3D>();
	PrintWriter outNew;

	int PT_TO_RUN = 0;
	int SR;
	float MIN_ANG = (float) (Math.PI / 5);

	public tConnectionMatrix(thesisPointStructure parent) {
		p = parent;
		SR = p.SR;
	}

	public void checkConAngles() {
		PApplet.println("Checking for and removing acute XY angles in the connection matrix.");
		PApplet.println("--Size before: " + this.connections.size());
		for (int i = 0; i < p.pc.nPTS; i++)
			checkAng(i);

		PApplet.println("--Size after: " + this.connections.size());
	}

	void checkAng(int i) {
		Vec3D nodePt = p.pc.getPoints().get(i);
		Vector<int[]> connsAtPT = new Vector<int[]>();

		for (int[] c : connectionIDs)
			if (c[0] == i || c[1] == i)
				connsAtPT.add(c);

		int[] ptIds = new int[connsAtPT.size()];
		for (int j = 0; j < connsAtPT.size(); j++)
			if (connsAtPT.get(j)[0] != i)
				ptIds[j] = connsAtPT.get(j)[0];
			else
				ptIds[j] = connsAtPT.get(j)[1]; 

		Vector<intFloatComp> ptHeadings = new Vector<intFloatComp>();

		for (int k = 0; k < ptIds.length; k++) {
			Vec3D localPt = p.pc.getPoints().get(ptIds[k]).copy();
			localPt.subSelf(nodePt);
			float ang = localPt.headingXY();
			ptHeadings.add(new intFloatComp(ptIds[k], ang));
		}

		Collections.sort(ptHeadings, intFloatComp.intFloatComparator);

		boolean skipNext = false;
		for (int m = 0; m < ptHeadings.size(); m++) {
			intFloatComp a = ptHeadings.get(m);
			intFloatComp b = ptHeadings.get((m + 1) % ptHeadings.size());
			float diff = Math.abs(b.val - a.val);
			if(diff> Math.PI){
				if(a.val>b.val){
					a.val = (float) (a.val- Math.PI*2);
				}
				else{
					b.val = (float) (b.val - Math.PI*2);
				}
				diff = Math.abs(a.val-b.val);
			}
			
			int[] toDel = new int[2];
			if (diff < this.MIN_ANG) {
				toDel[0] = i;
				toDel[1] = a.num;
				this.delCon(toDel);
				skipNext = true;
			}
			
			if(skipNext)
				m++;
			
			skipNext = false;
		}

	}

	public void delCon(int[] c) {

		int toDel = -1;
		Vec3D testP = p.pc.getPoints().get(c[0]);
		Vec3D testP2 = p.pc.getPoints().get(c[1]);

		Line3D testLn = new Line3D(testP, testP2);
		
		

		for (int i = 0; i < this.connections.size(); i++) {
			Line3D ln = this.connections.get(i);
			if (ln.equals(testLn)) {
				toDel = i;
				break;
			}
		}

		if (toDel > -1) {
			deleted.add(connections.get(toDel));
			this.connections.remove(toDel);
			this.connectionIDs.remove(toDel);
			
		} else {
			PApplet.println("del failed------------------------------");
		}

		
		
	}

	void clear() {
		this.connectionIDs.clear();
		this.connections.clear();
		this.deleted.clear();
	}

	public void draw() {
		p.stroke(0, 50);
		for (Line3D l : connections)
			p.gfx.line(l);
	}

	void step() {
		if (PT_TO_RUN >= p.pc.nPTS)
			return;

		this.genCons(p.pc.getPoints().get(PT_TO_RUN));

		PT_TO_RUN++;
	}

	void stepAll() {
		while (PT_TO_RUN < p.pc.nPTS) {
			this.step();
		}
	}

	void genCons(Vec3D pt) {
		Vector<Vec3D> areaPts = new Vector<Vec3D>(p.pc.getPointsWithinSphere(pt, SR));
		Vector<Line3D> testCons = new Vector<Line3D>();

		if (areaPts.size() == 0) {
			return;
		}

		for (Vec3D pt2 : areaPts)
			if (pt2.distanceTo(pt) > 0.1f)
				testCons.add(new Line3D(pt, pt2));

		for (Line3D l : testCons)
			if (this.isGood(l))
				connections.add(l.copy());
	}

	void genConIDS() {
		Vector<Vec3D> points = new Vector<Vec3D>(p.pc.getPoints());

		for (Line3D ln : connections) {
			// PApplet.print("making an cID");
			int[] cID = new int[2];
			cID[0] = points.indexOf(ln.a);
			cID[1] = points.indexOf(ln.b);
			connectionIDs.add(cID);
		}
	}

	boolean isGood(Line3D conTest) {
		if (connections.size() == 0)
			return true;

		for (Line3D ln : connections) {
			if (conTest.equals(ln))
				return false;
		}

		return true;
	}
	
	void exportDeleted() {
		outNew = p.createWriter(p.sketchPath("output/"  + "_" + PApplet.month()
				+ "_" + PApplet.day() + "_" + PApplet.hour() + "_" + PApplet.minute() + "_"
				+ p.frameCount + "_con_" + p.nCyc+ "_.txt"));

		for (Line3D l : deleted) {

			outNew.println(l.a.x + ", " + l.a.y + ", " + l.a.z);
			outNew.println(l.b.x + ", " + l.b.y + ", " + l.b.z);
		}

		outNew.flush();
		outNew.close();
		this.deleted.clear();
	}

	void exportConnections() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/"  + "_" + PApplet.month()
				+ "_" + PApplet.day() + "_" + PApplet.hour() + "_" + PApplet.minute() + "_"
				+ p.frameCount + "_con_" + p.nCyc+ "_.txt"));

		for (Line3D l : connections) {

			p.OUTPUT.println(l.a.x + ", " + l.a.y + ", " + l.a.z);
			p.OUTPUT.println(l.b.x + ", " + l.b.y + ", " + l.b.z);
		}

		p.OUTPUT.flush();
		p.OUTPUT.close();
	}

	public void loadFromPhysics(List<VerletSpring> springs) {
		int bc = 0;
		PApplet.println("--Loading springs from physics.");
		for (VerletSpring vs : springs) {
			int idA = p.pc.getPoints().indexOf((Vec3D) vs.a);
			int idB = p.pc.getPoints().indexOf((Vec3D) vs.b);
			if (idA == -1 || idB == -1) {
				bc++;
				continue;
			}
			Line3D ln = new Line3D(p.pc.getPoints().get(idA), p.pc.getPoints().get(idB));
			int[] cID = new int[2];
			cID[0] = idA;
			cID[1] = idB;
			this.connectionIDs.add(cID);
			this.connections.add(ln);
		}
		PApplet.println("--Loaded springs from physics with " + bc + " bad connections.");

	}

}
