import java.util.ArrayList;

import toxi.geom.*;
import toxi.geom.mesh.Vertex;

public class PcPoint {
	Vec3D loc;
	int ID;
	float radius;
	Program p;

	PcPoint(Vec3D _loc, int _id, float _radius, Program parent) {
		loc = _loc;
		ID = _id;
		radius = _radius;
		p = parent;
	}

	void run() {
		collideSelf();
		attract();
		checkBounds();
		checkMesh();
		checkObs();

	}

	void collideSelf() {
		for (PcPoint other : p.PC.PtList) {
			float distToOther = this.loc.distanceTo(other.loc);
			if (this.ID != other.ID) {
				if (distToOther < this.radius) {
					Vec3D move = this.loc.sub(other.loc);
					if (move.magnitude() < 0.01f) {
						move = move.jitter(0.1f * this.radius,
								0.1f * this.radius, 0.5f * this.radius);
					} else {
						move = move.getNormalizedTo(.5f * this.radius);
					}
					p.dotTree.remove(this.loc);
					this.loc = this.loc.add(move);
					p.dotTree.addPoint(this.loc);
				}
			}

		}

	}

	void viewAngle(Target t) {
		Vec3D ang1 = t.pos.sub(this.loc);
		Vec3D ang2 = t.dir;
		float angle = ang1.angleBetween(ang2, true);
		Vec3D move = new Vec3D(0, 0, 0);
		if (angle < (p.PI / 6f)) {
			Vec3D dest = t.dir.sub(t.pos);
			dest = dest.invert();
			move = move.interpolateTo(dest.sub(this.loc), 0.5f);
			p.strokeWeight(6);
			p.stroke(255, 0, 0);
			p.gfx.point(this.loc);
			move = move.getNormalizedTo(0.1f);
			this.loc = this.loc.sub(move);
		}

	}

	void attract() {
		ArrayList<Target> ts = p.targetList.getAllPts();

		float[] distances = new float[ts.size()];
		for (int i = 0; i < ts.size(); i++) {
			Vec3D v = ts.get(i).pos;
			float dis = v.distanceTo(this.loc);
			distances[i] = dis;
		}
		int getI = p.indexOfMin(distances);
		Target t = ts.get(getI);

		viewAngle(t);

		Vec3D tPos = t.pos;
		Vec3D diff = tPos.sub(this.loc);
		float factor = diff.magnitude();
		if (factor < 1000) {
			factor = (float) Math.sqrt(factor * .0005f);
			if (diff.magnitude() < p.tRad) {
				// diff = diff.getNormalizedTo(2);
				// diff = new Vec3D(diff.x, diff.y, diff.z-0.11f);
			} else {
				diff = diff.getNormalizedTo(factor);
				diff = diff.getInverted();
			}
			p.dotTree.remove(this.loc);
			this.loc = loc.sub(diff);
			p.dotTree.addPoint(this.loc);
		}

	}

	void checkObs() {
		ArrayList<Vec3D> vecs = p.ObstacleTree.getPointsWithinSphere(this.loc,
				this.radius * 1.1f);
		if (vecs != null) {
			Vec3D xyLoc = new Vec3D(this.loc.x, this.loc.y, 0);
			for (Vec3D v : vecs) {
				Vec3D xyV = new Vec3D(v.x, v.y, 0);
				float getDist = xyLoc.distanceTo(xyV);
				if (getDist < this.radius) {
					Vec3D dif = this.loc.sub(v);
					dif = dif.getNormalizedTo(this.radius);
					p.dotTree.remove(this.loc);
					this.loc.add(dif);
					p.dotTree.addPoint(this.loc);
				}
			}
		}
	}

	void checkMesh() {
		ArrayList<Vec3D> vecs = p.octree.getPointsWithinSphere(this.loc,
				this.radius * 2f);
		if (vecs != null) {
			float[] distances = new float[vecs.size()];
			for (int i = 0; i < vecs.size(); i++) {
				Vec3D ve = vecs.get(i);
				float dis = ve.distanceTo(this.loc);
				distances[i] = dis;
			}
			int getI = p.indexOfMin(distances);
			Vec3D v = vecs.get(getI);
			if (loc.z < v.z) {
				loc = new Vec3D(loc.x, loc.y, v.z);
				for (PcPoint other : p.PC.PtList) {
					float distToOther = this.loc.distanceTo(other.loc);
					if (this.ID != other.ID) {
						if (distToOther < this.radius * 1.5f) {
							if (other.loc.z <= (this.loc.z + 18f)) {
								Vec3D move = new Vec3D(0, 0, 24);
								p.dotTree.remove(this.loc);
								this.loc = this.loc.add(move);
								p.dotTree.addPoint(this.loc);
							}
						}
					}

				}
			}

		}
	}

	void checkBounds() {
		this.loc = new Vec3D(loc.x, loc.y, loc.z - 0.055f);

		if (loc.y > p.maxY) {
			loc.y -= (loc.y - p.maxY) + this.radius;
		}
		if (loc.x > p.maxX) {
			loc.x -= (loc.x - p.maxX) + this.radius;
		}
		if (loc.z > p.maxZ) {
			loc.z -= (loc.z - p.maxZ) + this.radius;
		}

		if (loc.y < p.minY) {
			loc.y += (p.minY - loc.y) + this.radius;
		}
		if (loc.x < p.minX) {
			loc.x += (p.minX - loc.x) + this.radius;
		}
		if (loc.z < p.minZ) {
			loc.z += (p.minZ - loc.z) + this.radius;
		}

	}

	void draw() {

		if (p.drawLines) {
			p.stroke(255, 55, 111);
			p.strokeWeight(1f);
			drawThemLines();
			p.stroke(5, 5, 255);
			p.strokeWeight(1);
			p.gfx.point(this.loc);
		} else {
			p.stroke(255, 255, 55);
			p.strokeWeight(5);
			p.gfx.point(this.loc);
		}
	}

	void drawThemLines() {
		for (PcPoint other : p.PC.PtList) {
			float distToOther = this.loc.distanceTo(other.loc);
			if (this.ID != other.ID) {
				if (distToOther < this.radius * 2.1f) {
					p.gfx.line(this.loc, other.loc);
				}
			}
		}
	}
}