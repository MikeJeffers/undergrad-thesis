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
		attract(p.myTarget);
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

	void collide(Agent a) {
		Vec3D other = a.pos;
		float dist = this.loc.distanceTo(other);
		if (dist < this.radius * 1.5f) {
			Vec3D diff = other.sub(this.loc);
			diff = diff.getNormalizedTo(.5f);
			p.dotTree.remove(this.loc);
			this.loc = loc.sub(diff);
			p.dotTree.addPoint(this.loc);
		}
	}

	void attract(Target t) {
		Vec3D diff = t.pos.sub(this.loc);
		float factor = diff.magnitude();
		if(factor<355){
			factor = (float) Math.sqrt(factor*0.0001f);
		//factor = factor/25;
	
		if (diff.magnitude() < t.rad) {
			diff = diff.getNormalizedTo(factor);
		} else if (this.loc.z > 200f) {
			diff = diff.getNormalizedTo(factor);
			diff = new Vec3D(diff.x, diff.y, -diff.z);
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
		Vertex v = p.terrain.getClosestVertexToPoint(this.loc);
		if (loc.z < v.z) {
			 loc = new Vec3D(loc.x, loc.y, v.z);
			for (PcPoint other : p.PC.PtList) {
				float distToOther = this.loc.distanceTo(other.loc);
				if (this.ID != other.ID) {
					if (distToOther < this.radius*1.5f) {
						if (other.loc.z <= (this.loc.z+12f)) {
							Vec3D move = new Vec3D(0,0,12);
							p.dotTree.remove(this.loc);
							this.loc = this.loc.add(move);
							p.dotTree.addPoint(this.loc);
						}
					}
				}

			}

		}
	}

	void checkBounds() {
		this.loc = new Vec3D(loc.x, loc.y, loc.z - 0.04f);

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
			p.strokeWeight(.75f);
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
				if (distToOther < this.radius * 1.5f) {
					p.gfx.line(this.loc, other.loc);
				}
			}
		}
	}
}