import java.util.ArrayList;
import toxi.geom.*;
import toxi.geom.mesh.Vertex;

public class Agent {

	Program p;

	Vec3D pos, vel, begin, finish;
	boolean intersect;
	float maxSpeed = 2f;
	float cRadius;
	float seekRadius = 48f;
	Ray3D view;
	int id;

	Agent(int ident, float _cRad, Program parent) {
		p = parent;
		id = ident;
		cRadius = _cRad;

		intersect = false;
		pos = new Vec3D(p.random(p.minX, p.maxX), p.random(p.minY, p.maxY),
				p.random(p.minZ, p.maxZ));
		vel = new Vec3D(p.random(-maxSpeed, maxSpeed), p.random(-maxSpeed,
				maxSpeed), p.random(-maxSpeed, maxSpeed));

	}

	void run(ArrayList<Agent> AllAgents, Target t) {

		Vec3D direction = this.pos.sub(t.pos);
		direction = direction.getInverted();
		this.view = new Ray3D(this.pos, direction);

		intersectCheck(t);

		if (!intersect) {
			getAngle(t);
			seek(t);
		}
		obstacle();
		collide(AllAgents);
		cohesion(AllAgents);
		pushP();
		update();
	}

	void intersectCheck(Target t) {
		this.intersect = false;
		for (Triangle3D tt : p.triangles) {
			TriangleIntersector tSect = new TriangleIntersector(tt);
			IsectData3D isec = new IsectData3D();
			if (tSect.intersectsRay(this.view)) {
				isec = tSect.getIntersectionData();
				if (tt.containsPoint(isec.pos)) {
					float disToTarget = this.pos.distanceTo(t.pos);
					float dis = isec.dist;
					if (dis < disToTarget) {
						this.intersect = true;
						avoid(isec.pos, isec.normal);
						break;
					}
				}
			}
		}
	}

	void draw() {

		if (intersect) {
			p.strokeWeight(6);
			p.stroke(55, 55, 255);
		} else {
			p.strokeWeight(5);
			p.stroke(255, 155, 255);
		}
		p.gfx.point(this.pos);
	}

	void seek(Target t) {
		Vec3D dif = t.pos.sub(this.pos);
		float factor = dif.magnitude();
		factor = 0.5f / (factor + t.rad + 0.0001f);
		if (dif.magnitude() > p.tRad/1.75f) {
			dif.getNormalizedTo(maxSpeed);
			this.vel = vel.interpolateTo(dif, factor);
		} else {
			dif = dif.getInverted();
			this.vel = vel.interpolateTo(dif, factor * 2f);
		}
	}

	void getAngle(Target t) {
		Vec3D ang1 = t.pos.sub(this.pos);
		Vec3D ang2 = t.dir;
		float angle = ang1.angleBetween(ang2, true);
		if (angle > p.PI / 3.5f) {
			Vec3D loc = t.dir.getNormalizedTo(t.rad * 1.1f).sub(t.pos);
			loc = loc.invert();
			vel = vel.interpolateTo(loc.sub(this.pos), 0.1f);
		}
	}

	void avoid(ReadonlyVec3D pos2, ReadonlyVec3D normal) {
		Vec3D ang1 = this.pos.sub(p.targetPos);
		Vec3D ang2 = pos2.sub(normal);
		float angle = ang1.angleBetween(ang2, true);
		Vec3D rot = new Vec3D();
		if (angle < p.PI / 2) {
			rot = normal.getRotatedZ(-p.PI / 2);
		} else {
			rot = normal.getRotatedZ(p.PI / 2);
		}
		rot = rot.interpolateTo(p.targetPos, 0.005f);
		this.vel = vel.interpolateTo(rot, 0.9f);
	}

	void obstacle() {

		ArrayList<Vec3D> pts = p.ObstacleTree.getPointsWithinSphere(this.pos,
				cRadius * 4);
		if (pts == null)
			return;
		Vec3D other = new Vec3D();
		float minDist = 10000;
		for (Vec3D pt : pts) {
			float dist = pos.distanceTo(pt);
			if (dist < minDist) {
				minDist = dist;
				other = pt;
			}
		}

		Vec3D bounce = new Vec3D(this.pos.sub(other));
		float curDist = this.pos.distanceTo(other);
		float factor = 1 / (curDist + 1);
		this.vel = vel.interpolateTo(bounce, factor * 0.6f);

		if (curDist <= cRadius * 1f) {
			bounce = bounce.getNormalizedTo(maxSpeed * 1.1f);
			this.pos = this.pos.add(bounce);
		}
	}

	void collide(ArrayList<Agent> AllAgents) {
		ArrayList<Vec3D> getNeighbors = new ArrayList<Vec3D>();
		for (Agent other : AllAgents) {
			if (this.id != other.id) {
				float distToOther = this.pos.distanceTo(other.pos);
				if (distToOther < cRadius) {
					Vec3D reflect = this.pos.sub(other.pos);
					reflect = reflect.getNormalizedTo(this.cRadius);
					getNeighbors.add(reflect);
				}
			}
		}
		Vec3D average = new Vec3D();
		for (Vec3D v : getNeighbors) {
			average = average.add(v);
		}
		average = average.getNormalizedTo(this.cRadius);
		this.vel = vel.add(average);
	}

	void cohesion(ArrayList<Agent> AllAgents) {
		for (Agent other : AllAgents) {
			float distToOther = pos.distanceTo(other.pos);
			if (id != other.id) {
				if (distToOther < seekRadius && distToOther > cRadius * 1.1f) {
					vel = vel.interpolateTo(other.vel, .01f);
				}
			}
		}
	}

	void pushP() {
		ArrayList<Vec3D> pts = p.dotTree.getPointsWithinSphere(this.pos,
				this.cRadius * 2);
		if (pts == null)
			return;
		Vec3D other = new Vec3D();
		float minDist = 10000;
		for (Vec3D pt : pts) {
			float dist = pos.distanceTo(pt);
			if (dist < minDist) {
				minDist = dist;
				other = pt;
			}
		}

		float curDist = this.pos.distanceTo(other);
		if (curDist <= p.aRad) {
			p.PC.bump(this);
		}
	}

	void update() {

		if (pos.y > p.maxY || pos.y < p.minY) {
			vel.y = -vel.y;
		}
		if (pos.x > p.maxX || pos.x < p.minX) {
			vel.x = -vel.x;
		}
		if (pos.z > p.maxZ || pos.z < p.minZ) {
			vel.z = -vel.z;
		}

		if (pos.y > p.maxY) {
			pos.y -= (pos.y - p.maxY) + cRadius;
		}
		if (pos.x > p.maxX) {
			pos.x -= (pos.x - p.maxX) + cRadius;
		}
		if (pos.z > p.maxZ) {
			pos.z -= (pos.z - p.maxZ) + cRadius;
		}

		if (pos.y < p.minY) {
			pos.y += (p.minY - pos.y) + cRadius;
		}
		if (pos.x < p.minX) {
			pos.x += (p.minX - pos.x) + cRadius;
		}
		if (pos.z < p.minZ) {
			pos.z += (p.minZ - pos.z) + cRadius;
		}

		Vertex v = p.moved.getClosestVertexToPoint(this.pos);
		pos = new Vec3D(pos.x, pos.y, v.z);
		vel.limit(maxSpeed);
		pos = pos.add(vel);

	}

}