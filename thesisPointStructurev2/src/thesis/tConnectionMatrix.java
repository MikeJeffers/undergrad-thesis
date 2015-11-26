package thesis;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.geom.*;
import toxi.geom.mesh.Face;

public class tConnectionMatrix implements Runnable {

	thesisPointStructurev2 p;
	tpointCloud pc;
	Vector<connection> connections = new Vector<connection>();
	Vector<connection> connectionsHightlighted = new Vector<connection>();
	Vector<Triangle3D> faces = new Vector<Triangle3D>();
	Vector<Triangle3D> Triangles = new Vector<Triangle3D>();
	Vector<Triangle3D> BadTriangles = new Vector<Triangle3D>();
	Vector<Vec3D> isecPts = new Vector<Vec3D>();
	Vector<connection> tEdges = new Vector<connection>();

	float minAng;
	boolean intersect = true;

	public tConnectionMatrix(thesisPointStructurev2 parent, tpointCloud ptCloud) {
		p = parent;
		pc = ptCloud;
		minAng = PConstants.DEG_TO_RAD * 15;
		for (Face f : p.obstMesh.faces)
			this.faces.add(f.toTriangle());
	}

	public void run() {
		this.pc = p.ctrl.getPC();
		if (pc.getPoints().size() < 2)
			return;

		this.genConnections();
		this.checkForObstInt();
		this.removeMinNodes();
		this.checkAngles();
		this.removeMinNodes();
		this.removeWorstScore();

		while (this.getWorstScore() < 0 && this.connections.size() > 0) {
			this.resetValue();
			this.checkZValue();
			this.checkForObstInt();
			this.removeMinNodes();
			this.checkAngles();
			this.removeMinNodes();
			this.removeWorstScore();
		}
		p.gui.sendMessage("FINAL Connections: " + connections.size()
				+ " generated, Done.");

		while (intersect) {
			Triangles = this.genTriangles();
			Triangles = this.cullDupTri(Triangles);
			intersect = false;
		}
	}

	private void resetValue() {
		for (connection c : this.connections)
			c.value = 0;
	}

	private void removeWorstScore() {
		int minScore = this.getWorstScore();
		p.gui.sendMessage("-------WORST SCORE: " + minScore);
		Vector<connection> toRemove = new Vector<connection>();

		for (connection c : this.connections)
			if (c.value <= minScore)
				toRemove.add(c);

		this.connections.removeAll(toRemove);
	}

	private int getWorstScore() {
		int minScore = 1000;
		for (connection c : this.connections)
			minScore = (minScore < c.value) ? minScore : c.value;
		return minScore;
	}

	private void genConnections() {
		p.gui.sendMessage("Generate Connections: Started");
		for (int i = 0; i < pc.getPoints().size(); i++) {// 0
			Vec3D sp = pc.getPoints().get(i);
			ArrayList<Vec3D> ptsInArea = pc.getPointsWithinSphere(sp, p.SR);
			for (int j = 0; j < ptsInArea.size(); j++) {
				Vec3D ap = ptsInArea.get(j);
				connection c1 = new connection(sp.copy(), ap.copy(), i, j);
				if (this.checkCon(c1))
					connections.add(c1);
			}
		}
		p.gui.sendMessage("Generate Connections: " + connections.size()
				+ " generated, Done.");
	}

	private void checkAngles() {
		p.gui.sendMessage("Checking for angles under " + minAng
				* PConstants.RAD_TO_DEG + "*.");
		for (Vec3D pt : pc.getPoints())
			this.checkNodeAngle(pt);
		p.gui.sendMessage("Done checking angles.");
	}

	private void checkNodeAngle(Vec3D pt) {
		Vector<connection> nodeConns = new Vector<connection>();

		for (connection c : connections)
			if (c.pts[0].equals(pt) || c.pts[1].equals(pt))
				nodeConns.add(c);

		for (connection c : nodeConns) {
			Vec3D far;
			if (c.pts[0].equals(pt))
				far = c.pts[1].copy();
			else
				far = c.pts[0].copy();

			far.subSelf(pt);
			c.angle = far.headingXY();

			if (c.angle < 0)
				c.angle += PConstants.PI * 2;
		}

		Collections.sort(nodeConns, connection.conectionComparatorAngle);

		for (int i = 0; i < nodeConns.size(); i++) {
			float angNext = nodeConns.get((i + 1) % nodeConns.size()).angle;
			float angPrev = nodeConns.get((i + nodeConns.size() - 1)
					% nodeConns.size()).angle;
			float angCur = nodeConns.get(i).angle;

			float angBehind = angCur - angPrev;
			float angAhead = angNext - angCur;
			if (angBehind < 0)
				angBehind += PConstants.PI * 2;
			if (angAhead < 0)
				angAhead += PConstants.PI * 2;

			if (angBehind > minAng && angAhead > minAng)
				continue;

			if (angBehind + angAhead < minAng)
				nodeConns.get(i).hurt(2);

			if (angBehind < minAng && angAhead < minAng
					&& (angBehind + angAhead > minAng)) {
				nodeConns.get(i).hurt(2);
				i++;
				continue;
			}

			if (angBehind < minAng && angAhead > minAng) {
				nodeConns.get(i).hurt();

			}
			if (angBehind > minAng && angAhead < minAng) {
				nodeConns.get(i).hurt();

			}

		}
	}

	private void removeMinNodes() {
		int i = 0;
		p.gui.sendMessage("Removing nodes w/ less than 2  connections.");
		while (checkMinNodes() > 0)
			i++;
		p.gui.sendMessage("Nodes removed, took " + i + " runs.");
	}

	private int checkMinNodes() {
		Vector<connection> toRemove = new Vector<connection>();
		for (Vec3D pt : this.pc.getPoints()) {
			Vector<connection> attached = new Vector<connection>();
			int n = 0;
			for (connection c : connections) {
				if (c.value >= 0) {
					if (pt.equals(c.pts[0]) || pt.equals(c.pts[1])) {
						attached.add(c);
						n++;
					}
				}
			}
			if (n <= 1) {
				toRemove.addAll(attached);
			}
		}

		for (connection c : toRemove)
			c.hurt();

		connectionsHightlighted.addAll(toRemove);
		connections.removeAll(toRemove);
		// p.gui.sendMessage(toRemove.size() + " connsToRemove");
		return toRemove.size();
	}

	private void checkZValue() {
		Vector<connection> toReinforce = new Vector<connection>();
		for (Vec3D pt : this.pc.getPoints()) {
			Vector<connection> attached = new Vector<connection>();
			int n = 0;

			for (connection c : connections) {
				if (pt.equals(c.pts[0]) || pt.equals(c.pts[1])) {
					attached.add(c);
					n++;
				}
			}

			if (n > 0) {
				float[] Zvals = new float[n];
				for (int i = 0; i < attached.size(); i++) {
					Vec3D otherPt = attached.get(i).getOtherPT(pt);
					Vec3D diff = pt.sub(otherPt);
					diff = diff.getNormalized();
					Zvals[i] = diff.z;
				}
				int index = this.getMinIndex(Zvals);
				toReinforce.add(attached.get(index));
			}

		}
		for (connection c : connections) {
			for (connection r : toReinforce) {
				if (c.equals(r)) {
					c.help();
				}
			}
		}
		p.gui.sendMessage("Helped " + toReinforce.size() + " connections");
	}

	private void checkForObstInt() {
		p.gui.sendMessage("Looking for connections that intersect the enviroment.");
		Vector<connection> toRemove = new Vector<connection>();
		TriangleIntersector ti;
		for (connection c : connections) {
			for (Triangle3D t : this.faces) {
				ti = new TriangleIntersector(t);
				if (ti.intersectsRay(c.ln.toRay3D())) {
					IsectData3D hit = ti.getIntersectionData();
					if (t.containsPoint(c.ln.closestPointTo(hit.pos))) {
						c.hurt();
						toRemove.add(c);
					}
				}
			}

		}
		p.gui.sendMessage("Flagged " + toRemove.size()
				+ " for intersecting the world.");
		connections.removeAll(toRemove);
		connectionsHightlighted.addAll(toRemove);

	}

	boolean checkCon(connection c) {
		for (connection cGood : connections) {
			if (c.ln.getLength() < 8f)
				return false;

			if (cGood.compareTo(c) == 1) {
				// p.print("dup ");
				return false;
			}
		}

		return true;
	}

	Vector<Triangle3D> genTriangles() {
		p.gui.sendMessage("Making Triangles");
		Vector<Triangle3D> triangles = new Vector<Triangle3D>();
		for (connection c : connections) {
			Vec3D ptA = null;
			Vec3D ptB = null;
			Vec3D ptC = null;
			Triangle3D tri = null;
			Vec3D[] c1pts = c.pts;
			for (connection c2 : connections) {
				if (c != c2) {
					Vec3D[] c2pts = c2.pts;
					if (c1pts[0].equals(c2pts[0])) {
						ptA = c1pts[0];
						ptB = c2pts[(0 + 1) % c2pts.length];
						ptC = c1pts[(0 + 1) % c2pts.length];
						Line3D l = new Line3D(ptC, ptB);
						for (connection c3 : connections) {
							if (c != c3 && c2 != c3) {
								if (c3.ln.equals(l)) {
									tri = new Triangle3D(ptA, ptB, ptC);
									triangles.add(tri);
									// break;
								}
							}
						}
					}
				}
			}
		}

		p.gui.sendMessage(triangles.size() + " triangles made.");

		return triangles;
	}

	Vector<Triangle3D> cullDupTri(Vector<Triangle3D> tri) {
		Vector<Triangle3D> returnTri = new Vector<Triangle3D>();
		ArrayList<int[]> getIndex = new ArrayList<int[]>();
		for (int i = 0; i < tri.size(); i++) {
			Triangle3D t1 = tri.get(i);
			Vec3D cent1 = t1.computeCentroid();
			for (int j = 0; j < tri.size(); j++) {
				if (i != j) {
					Triangle3D t2 = tri.get(j);
					Vec3D cent2 = t2.computeCentroid();
					if (cent1.equalsWithTolerance(cent2, 1f)) {
						int[] pair = new int[] { i, j };
						getIndex.add(pair);
					}
				}
			}
		}

		p.println(getIndex.size() + " number of pairs found");

		int[] ids = new int[getIndex.size()];
		for (int n = 0; n < getIndex.size(); n++) {
			int[] pair = getIndex.get(n);
			int A = pair[0];
			int B = pair[1];
			int id = (A * B) / ((A ^ 2 + B ^ 2) + 1);
			ids[n] = id;
		}

		Arrays.sort(ids);

		ArrayList<Integer> index = new ArrayList<Integer>();
		ArrayList<Integer> uniqueIds = new ArrayList<Integer>();

		for (int i = 0; i < ids.length; i++) {
			if (ids[i] < ids[(i + 1) % ids.length]) {
				uniqueIds.add(ids[i]);
			}
		}

		for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < uniqueIds.size(); j++) {
				if (ids[i] == uniqueIds.get(j)) {
					int[] pair = getIndex.get(i);
					int A = pair[0];
					int B = pair[1];
					int[] newArray = new int[] { A, B };
					Arrays.sort(newArray);
					index.add(newArray[0]);
					break;
				}
			}
		}

		int[] indicies = new int[index.size()];
		for (int i = 0; i < indicies.length; i++) {
			indicies[i] = index.get(i);
		}
		Arrays.sort(indicies);

		for (int i = 0; i < indicies.length; i++) {
			if (indicies[i] < indicies[(i + 1) % indicies.length]) {

				returnTri.add(tri.get(indicies[i]));
			}
		}

		p.println(returnTri.size() + " number of triangles remaining");
		return returnTri;
	}

	float getMin(float[] numbers) {
		float min = 1000000;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
			}
		}
		return min;
	}

	int getMinIndex(float[] numbers) {
		float min = 1000000;
		int index = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
				index = i;
			}
		}
		return index;
	}

	void exportConnections() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_connections.txt"));

		for (connection c : connections) {
			p.OUTPUT.println(c.ln.a.x + ", " + c.ln.a.y + ", " + c.ln.a.z);
			p.OUTPUT.println(c.ln.b.x + ", " + c.ln.b.y + ", " + c.ln.b.z);
		}

		p.OUTPUT.flush();
		p.OUTPUT.close();
	}

	void exportForGH() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_connectionsforGH.txt"));

		for (Vec3D n : this.pc.getPoints()) {
			Vector<connection> attached = new Vector<connection>();
			Vector<Integer> cIds = new Vector<Integer>();
			for (int i = 0; i < connections.size(); i++) {
				connection c = connections.get(i);
				if (c.connectsTo(n)) {
					attached.add(c);
					cIds.add(i);
				}
			}

			Vector<Vec3D> rawPts = new Vector<Vec3D>();
			Vector<Vec3D> unique = new Vector<Vec3D>();

			for (connection c : attached) {
				rawPts.add(c.pts[0]);
				rawPts.add(c.pts[1]);
			}

			for (Vec3D v : rawPts) {
				int ptc = 0;
				for (Vec3D v2 : rawPts) {
					if (v2.equals(v))
						ptc++;
				}
				if (ptc == 1 && v.equals(n) == false)
					unique.add(v);
			}

			p.OUTPUT.println("NODE");
			p.OUTPUT.println(n.x + ", " + n.y + ", " + n.z);
			p.OUTPUT.println(unique.size());
			for (int j = 0; j < unique.size(); j++) {
				int cID = cIds.get(j);
				Vec3D up = unique.get(j);
				p.OUTPUT.println(cID);
				p.OUTPUT.println(up.x + ", " + up.y + ", " + up.z);
			}

		}

		p.OUTPUT.flush();
		p.OUTPUT.close();
	}

	void exportTriangles() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_allTriangles.txt"));

		for (Triangle3D t : Triangles) {
			p.OUTPUT.println(t.a.x + ", " + t.a.y + ", " + t.a.z);
			p.OUTPUT.println(t.b.x + ", " + t.b.y + ", " + t.b.z);
			p.OUTPUT.println(t.c.x + ", " + t.c.y + ", " + t.c.z);

		}

		p.OUTPUT.flush();
		p.OUTPUT.close();

	}

	// These two functions down here are for associating triangles with specific
	// connections in connection matrix

	int getTriEdge(Line3D edge) {
		int getId = -1;
		for (int i = 0; i < connections.size(); i++) {
			connection c = connections.get(i);
			if (c.ln.equals(edge)) {
				getId = i;
			}
		}
		return getId;
	}

	void export1() {
		p.out1 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_TrianglesExport.txt"));

		for (Triangle3D t : Triangles) {
			Line3D l1 = new Line3D(t.a, t.b);
			Line3D l2 = new Line3D(t.b, t.c);
			Line3D l3 = new Line3D(t.c, t.a);
			int hash1 = getTriEdge(l1);
			int hash2 = getTriEdge(l2);
			int hash3 = getTriEdge(l3);
			p.out1.println(hash1);
			p.out1.println(t.a.x + ", " + t.a.y + ", " + t.a.z);
			p.out1.println(hash2);
			p.out1.println(t.b.x + ", " + t.b.y + ", " + t.b.z);
			p.out1.println(hash3);
			p.out1.println(t.c.x + ", " + t.c.y + ", " + t.c.z);
		}

		p.out1.flush();
		p.out1.close();

	}

	void export2() {
		p.out2 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_JointExport.txt"));

		int count = 0;
		for (Vec3D n : this.pc.getPoints()) {
			count++;
			Vector<connection> attached = new Vector<connection>();
			Vector<Integer> cIds = new Vector<Integer>();
			for (int i = 0; i < connections.size(); i++) {
				connection c = connections.get(i);
				if (c.connectsTo(n)) {
					attached.add(c);
					cIds.add(i);
				}
			}

			Vector<Vec3D> rawPts = new Vector<Vec3D>();
			Vector<Vec3D> unique = new Vector<Vec3D>();

			for (connection c : attached) {
				rawPts.add(c.pts[0]);
				rawPts.add(c.pts[1]);
			}

			for (Vec3D v : rawPts) {
				int ptc = 0;
				for (Vec3D v2 : rawPts) {
					if (v2.equals(v))
						ptc++;
				}
				if (ptc == 1 && v.equals(n) == false)
					unique.add(v);
			}

			p.out2.println("NODE");
			p.out2.println(count);
			p.out2.println(n.x + ", " + n.y + ", " + n.z);
			for (int j = 0; j < unique.size(); j++) {
				int cID = cIds.get(j);
				Vec3D up = unique.get(j);
				p.out2.println(cID);
				p.out2.println(up.x + ", " + up.y + ", " + up.z);
			}

		}

		p.out2.flush();
		p.out2.close();

	}

	void export3() {

		p.out3 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_ConnectionsExport.txt"));

		for (int i = 0; i < connections.size(); i++) {
			connection c = connections.get(i);
			p.out3.println(i);
			p.out3.println(c.pts[0].x + ", " + c.pts[0].y + ", " + c.pts[0].z);
			p.out3.println(c.pts[1].x + ", " + c.pts[1].y + ", " + c.pts[1].z);
		}

		p.out3.flush();
		p.out3.close();

	}

	void exportEverything() {

		p.out1 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_TrianglesExport.txt"));

		for (Triangle3D t : Triangles) {
			Line3D l1 = new Line3D(t.a, t.b);
			Line3D l2 = new Line3D(t.b, t.c);
			Line3D l3 = new Line3D(t.c, t.a);
			int hash1 = getTriEdge(l1);
			int hash2 = getTriEdge(l2);
			int hash3 = getTriEdge(l3);
			p.out1.println(hash1);
			p.out1.println(t.a.x + ", " + t.a.y + ", " + t.a.z);
			p.out1.println(hash2);
			p.out1.println(t.b.x + ", " + t.b.y + ", " + t.b.z);
			p.out1.println(hash3);
			p.out1.println(t.c.x + ", " + t.c.y + ", " + t.c.z);
		}

		p.out1.flush();
		p.out1.close();

		p.out2 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_JointExport.txt"));

		int count = -1;
		for (Vec3D n : this.pc.getPoints()) {
			count++;
			Vector<connection> attached = new Vector<connection>();
			Vector<Integer> cIds = new Vector<Integer>();
			for (int i = 0; i < connections.size(); i++) {
				connection c = connections.get(i);
				if (c.connectsTo(n)) {
					attached.add(c);
					cIds.add(i);
				}
			}

			Vector<Vec3D> rawPts = new Vector<Vec3D>();
			Vector<Vec3D> unique = new Vector<Vec3D>();

			for (connection c : attached) {
				rawPts.add(c.pts[0]);
				rawPts.add(c.pts[1]);
			}

			for (Vec3D v : rawPts) {
				int ptc = 0;
				for (Vec3D v2 : rawPts) {
					if (v2.equals(v))
						ptc++;
				}
				if (ptc == 1 && v.equals(n) == false)
					unique.add(v);
			}

			p.out2.println("NODE");
			p.out2.println(count);
			p.out2.println(n.x + ", " + n.y + ", " + n.z);
			for (int j = 0; j < unique.size(); j++) {
				int cID = cIds.get(j);
				Vec3D up = unique.get(j);
				p.out2.println(cID);
				p.out2.println(up.x + ", " + up.y + ", " + up.z);
			}

		}

		p.out2.flush();
		p.out2.close();

		p.out3 = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_ConnectionsExportwithjID.txt"));

		for (int i = 0; i < connections.size(); i++) {
			connection c = connections.get(i);
			int index = -1;
			for (Vec3D n : this.pc.getPoints()) {
				index++;
				if (c.connectsTo(n)) {
					p.out3.println(index);
				}
			}
			p.out3.println(i);
			p.out3.println(c.pts[0].x + ", " + c.pts[0].y + ", " + c.pts[0].z);
			p.out3.println(c.pts[1].x + ", " + c.pts[1].y + ", " + c.pts[1].z);
			
		}

		p.out3.flush();
		p.out3.close();

		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_ptCloudwID.txt"));

		int id = 0;
		for (Vec3D e : this.pc.getPoints()) {
			id++;
			p.OUTPUT.println(id);
			p.OUTPUT.println(e.x + ", " + e.y + ", " + e.z);
		}
		p.OUTPUT.flush();
		p.OUTPUT.close();

	}

	void clear() {
		this.connections = new Vector<connection>();
		this.connectionsHightlighted = new Vector<connection>();
	}

	void clearTriangles() {
		this.Triangles = new Vector<Triangle3D>();
		this.BadTriangles = new Vector<Triangle3D>();
	}

	public Vector<connection> getConnections() {
		return this.connections;
	}

	public Vector<Vec3D> getIsecPoints() {
		return this.isecPts;
	}

	public tConnectionMatrix getCM() {
		return this;

	}

	public Vector<connection> getBadConnections() {
		return this.connectionsHightlighted;
	}

	public Vector<Triangle3D> getTriangles() {
		return this.Triangles;
	}

	public Vector<Triangle3D> getBadTriangles() {
		return this.BadTriangles;
	}

}
