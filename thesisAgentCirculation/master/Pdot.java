import java.util.ArrayList;

import toxi.geom.*;
import toxi.geom.mesh.Vertex;

public class Pdot {
	Vec3D loc;
	int ID;
	float radius;
	Circulation p;

	Pdot(Vec3D _loc, int _id, float _radius, Circulation parent) {
		loc = _loc;
		ID = _id;
		radius = _radius;
		p = parent;
	}

	void attract(Agent a) {

		Vec3D other = a.pos;

		p.stroke(255);
		p.strokeWeight(1);
	//	p.gfx.line(other, this.loc);

		Vec3D diff = other.sub(this.loc);

		float factor = diff.magnitude();
		factor = 1 / factor;
		if(diff.magnitude()<this.radius*2.0f){
			diff.clear();
		}
		else if (diff.magnitude() < p.cRad * 20) {
			diff = diff.getNormalizedTo(factor);
		} else {
			diff.clear();
		}
		p.dotTree.remove(this.loc);
		this.loc = this.loc.add(diff);
		p.dotTree.addPoint(this.loc);

	}

	void collideSelf(ArrayList<Pdot> AllSelf) {
		for (Pdot other : AllSelf) {
			float distToOther = this.loc.distanceTo(other.loc);
			if (this.ID != other.ID) {
				if (distToOther < this.radius) {
					Vec3D move = this.loc.sub(other.loc);
					if (move.magnitude() < 0.01f) {
						move.jitter(0.01f, 0.01f, 0.02f);
					} else {
						move.getNormalizedTo(.2f);
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
		if (dist < this.radius * 1.0f) {
			Vec3D diff = other.sub(this.loc);
			diff = diff.getNormalizedTo(.5f);
			diff = diff.invert();
			p.dotTree.remove(this.loc);
			this.loc = this.loc.add(diff);
			p.dotTree.addPoint(this.loc);
		}
	}

	void checkObs() {
		ArrayList<Vec3D> vecs = p.obsTree.getPointsWithinSphere(this.loc,
				this.radius * 2);
		if (vecs != null) {
			for (Vec3D v : vecs) {
				float getDist = this.loc.distanceTo(v);
				if (getDist < this.radius) {
					Vec3D dif = this.loc.sub(v);
					p.dotTree.remove(this.loc);
					this.loc = this.loc.add(dif);
					p.dotTree.addPoint(this.loc);
				}
			}
		}
	}

	void checkMesh() {
		ArrayList<Vec3D> vecs = p.tTree.getPointsWithinSphere(this.loc,
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
				for (Pdot other : p.PP.PtList) {
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
		this.loc = new Vec3D(loc.x, loc.y, loc.z - 0.09f);

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
		p.stroke(15, 55, 255);
		p.strokeWeight(6);
		p.gfx.point(this.loc);
		
		if (p.drawLines) {
			p.stroke(255, 55, 111);
			p.strokeWeight(1f);
			drawThemLines();
		}
		
		p.dotTree.remove(this.loc);
		checkBounds();
		checkMesh();
		checkObs();
		p.dotTree.addPoint(this.loc);

	}
	
	
	void drawThemLines() {
		for (Pdot other : p.PP.PtList) {
			float distToOther = this.loc.distanceTo(other.loc);
			if (this.ID != other.ID) {
				if (distToOther < this.radius * 2.1f) {
					p.gfx.line(this.loc, other.loc);
				}
			}
		}
	}

}
