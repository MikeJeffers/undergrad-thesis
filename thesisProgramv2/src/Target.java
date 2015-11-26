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

	Target(Vec3D _pos, Vec3D _dirTo, int ident, Program parent) {
		p = parent;
		id = ident;

		rad = p.tRad;
		pos = _pos;
		dirTo = _dirTo;
		dir = pos.sub(dirTo);

	}



	void draw() {

		p.strokeWeight(10);
		p.stroke(55, 255, 255);
		p.gfx.point(this.pos);

		p.stroke(255, 222);
		p.strokeWeight(2);
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
