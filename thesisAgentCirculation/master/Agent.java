import java.util.ArrayList;
import toxi.geom.*;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.Vertex;

public class Agent {

	Circulation p;

	Vec3D pos, vel, begin, finish;

	float maxSpeed = 6f;
	float cRadius;
	float seekRadius = 80;
	int id;
	int group;
	boolean state;
	float destinationRadius = 60;

	Agent(int ident, int _group, float _home, Circulation parent) {
		p = parent;
		id = ident;
		group = _group;
		cRadius = p.cRad;
		if (_home > .5f) {
			state = true;
		} else {
			state = false;
		}
		begin = p.start.get(group);
		finish = p.end.get(group);
		if(p.random(0, 10)>5){
			pos = new Vec3D((finish.x+p.random(-555f, 555f)), (finish.y+p.random(-555f, 555f)), finish.z);
		}
		else{
			pos = new Vec3D((begin.x+p.random(-555f, 555f)), (begin.y+p.random(-555f, 555f)), begin.z);
		}
		
		vel = new Vec3D(p.random(-maxSpeed, maxSpeed), p.random(-maxSpeed,
				maxSpeed), p.random(-maxSpeed, maxSpeed));

	}

	void run(ArrayList<Agent> AllAgents) {

		obstacle();

		pushP();
		cohesion(AllAgents);
		collide(AllAgents);
		getDestination();
		obstacle();
		checkTerrain();
		update();

		if (p.frameCount % 1600 == 0) {
			changeEverything();
		}
	}

	void draw() {

		float col = group+1 * (255 / p.start.size()+1);
		p.strokeWeight(8);
		p.stroke(255/col*0.9f, col*group*0.7f, 255 / col*0.3f);
		p.gfx.point(this.pos);
		float mult = this.vel.magnitude();
		p.strokeWeight(3);
		p.gfx.line(this.pos, this.pos.add(this.vel.getNormalizedTo(mult*2)));
	}

	void changeEverything() {
		int newI = (int) p.random(0, p.start.size());
		begin = p.start.get(newI);
		finish = p.end.get(newI);
	}

	void obstacle() {

		ArrayList<Vec3D> pts = p.obsTree.getPointsWithinSphere(this.pos,
				cRadius * 5);
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
		this.vel = vel.interpolateTo(bounce, factor);

		if (curDist <= cRadius * 1.1) {
			this.pos = this.pos.add(bounce);
		}

	}

	void pushP() {
		ArrayList<Vec3D> pts = p.dotTree
				.getPointsWithinSphere(this.pos, p.aRad*25);
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
		if (curDist <= p.aRad*25) {
			p.PP.bump(this.id);
		}

	}


	
	void getDestination() {

		float distToStart = pos.distanceTo(begin);
		float distToEnd = pos.distanceTo(finish);

		Vec3D interp = new Vec3D();

		if (distToStart < destinationRadius) {
			state = true;
		}

		if (distToEnd < destinationRadius) {
			state = false;
		}
		if (state) {
			interp = (finish.sub(pos));
		} else {
			interp = (begin.sub(pos));
		}
		interp.getNormalizedTo(maxSpeed);
		vel = vel.interpolateTo(interp, .17f);

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
					if (group == other.group) {
						vel = vel.interpolateTo(other.vel, .01f);
					}
				}
			}

		}

	}

	void checkTerrain(){
		
		ArrayList<Vec3D> vecs = p.tTree.getPointsWithinSphere(this.pos,
				this.cRadius * 2f);
		if (vecs != null) {
			float[] distances = new float[vecs.size()];
			for (int i = 0; i < vecs.size(); i++) {
				Vec3D ve = vecs.get(i);
				float dis = ve.distanceTo(this.pos);
				distances[i] = dis;
			}
			int getI = p.indexOfMin(distances);
			Vec3D v = vecs.get(getI);
		
			
			pos = new Vec3D(pos.x, pos.y, (v.z+36f));
			
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

		vel.limit(maxSpeed);
		pos = pos.add(vel);

	}

}
