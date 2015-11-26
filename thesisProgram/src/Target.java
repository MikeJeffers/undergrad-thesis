import java.io.PrintWriter;
import processing.core.PApplet;
import toxi.geom.*;
import toxi.geom.mesh.Vertex;

public class Target {

	Program p;
	PrintWriter OUTPUT;
	Vec3D pos, dir;
	Vec3D dirTo;
	float rad;
	int id;

	Target(int ident, Program parent) {
		p = parent;
		id = ident;

		rad = p.tRad;
		pos = p.targetPos;
		dirTo = p.targetDir;
		dir = pos.sub(dirTo);

	}

	void run(Vec3D po, Vec3D di) {
		this.pos = po;
		this.dirTo = di;
		this.dir = pos.sub(dirTo);
		update();

	}

	void draw() {

		p.strokeWeight(15);
		p.stroke(55, 255, 255);
		p.gfx.point(this.pos);

		p.stroke(255);
		p.strokeWeight(3);
		p.gfx.line(new Line3D(this.dirTo, this.pos));

	}

	void update() {

		if (pos.y > p.maxY) {
			pos.y -= (pos.y - p.maxY) + rad;
		}
		if (pos.x > p.maxX) {
			pos.x -= (pos.x - p.maxX) + rad;
		}
		if (pos.z > p.maxZ) {
			pos.z -= (pos.z - p.maxZ) + rad;
		}

		if (pos.y < p.minY) {
			pos.y += (p.minY - pos.y) + rad;
		}
		if (pos.x < p.minX) {
			pos.x += (p.minX - pos.x) + rad;
		}
		if (pos.z < p.minZ) {
			pos.z += (p.minZ - pos.z) + rad;
		}

		Vertex v = p.moved.getClosestVertexToPoint(this.pos);
		pos = new Vec3D(pos.x, pos.y, v.z);

	}

	void exportTarget() {
		OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_TargetPos.txt"));
		Vec3D p = this.pos;
		Vec3D d = this.dir.getNormalizedTo(5);
		d = d.add(p);

		OUTPUT.println("{" + p.x + ", " + p.y + ", " + p.z + "}");
		OUTPUT.println("{" + d.x + ", " + d.y + ", " + d.z + "}");
		OUTPUT.flush();
		OUTPUT.close();
	}

}
