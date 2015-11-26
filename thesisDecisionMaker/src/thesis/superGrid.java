package thesis;

import java.util.Vector;

import processing.core.PApplet;

import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.TriangleMesh;

public class superGrid {
	DecisionMaker p;

	float rt = 0;
	Vec2D[] cps;

	Vector<superMesh> meshes;

	Vector<superMesh> pastMeshes;

	public superGrid(DecisionMaker parent) {
		p = parent;
		pastMeshes = new Vector<superMesh>();

		cps = new Vec2D[6];
		int id = 0;
		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				cps[id] = new Vec2D((p.width / 4) * j, (p.height / 3) * i);
				id++;
			}
		}

		this.rePopulateRandom();
	}

	void rePopulateRandom() {
		superMesh mesh;
		meshes = new Vector<superMesh>();

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				mesh = new superMesh(p);
				mesh.populateValuesRndm();
				mesh.make();

				meshes.add(mesh);
			}
		}
	}

	void rePopulateGen1() { // Based on most recent click
		superMesh mesh;
		meshes = new Vector<superMesh>();
		int id = 0;

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				if (id == 0) {
					mesh = new superMesh(p);
					mesh.populateValuesRndm();
					mesh.make();
					mesh.setEscape();
					meshes.add(mesh);
				} else {
					mesh = new superMesh(p);
					mesh.evolve(this.pastMeshes.get(this.pastMeshes.size() - 1));
					mesh.make();
					meshes.add(mesh);
				}
				id++;
			}
		}
	}

	void rePopulateGen2() { // even weight of past 3
		superMesh mesh;
		meshes = new Vector<superMesh>();
		int id = 0;

		PApplet.println("Running a generation 2 population.");

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				if (id == 0) {
					mesh = new superMesh(p);
					mesh.populateValuesRndm();
					mesh.make();
					mesh.setEscape();
					meshes.add(mesh);
				} else {
					mesh = new superMesh(p);
					if (this.pastMeshes.size() > 2) {
						PApplet.println("Averaging Last 3.");
						mesh.evolveV2(this.pastMeshes.get(this.pastMeshes.size() - 1),
								this.pastMeshes.get(this.pastMeshes.size() - 2),
								this.pastMeshes.get(this.pastMeshes.size() - 3));
					} else if (this.pastMeshes.size() > 1) {
						PApplet.println("Averaging Last 2. w/ 1 random seed.");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						mesh.evolveV2(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								this.pastMeshes.get(this.pastMeshes.size() - 2));
					} else {
						PApplet.println("Based on last. w/ 2 random seeds");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						superMesh spice2 = new superMesh(p);
						spice2.populateValuesRndm();
						mesh.evolveV2(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								spice2);
					}
					mesh.make();
					meshes.add(mesh);
				}
				id++;
			}
		}
	}

	void rePopulateGen3() { // weighted of past 3
		superMesh mesh;
		meshes = new Vector<superMesh>();
		int id = 0;

		float w1 = 0.6f;
		float w2 = 0.25f;
		float w3 = 0.15f;

		PApplet.println("Running a generation 3 population with: " + w1 + " " + w2 + " " + w3);

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				if (id == 0) {
					mesh = new superMesh(p);
					mesh.populateValuesRndm();
					mesh.make();
					mesh.setEscape();
					meshes.add(mesh);
				} else {
					mesh = new superMesh(p);
					if (this.pastMeshes.size() > 2) {
						PApplet.println("Averaging Last 3.");
						mesh.evolveV3(this.pastMeshes.get(this.pastMeshes.size() - 1),
								this.pastMeshes.get(this.pastMeshes.size() - 2),
								this.pastMeshes.get(this.pastMeshes.size() - 3), w1, w2, w3);
					} else if (this.pastMeshes.size() > 1) {
						PApplet.println("Averaging Last 2. w/ 1 random seed.");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						mesh.evolveV3(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								this.pastMeshes.get(this.pastMeshes.size() - 2), w1, w2, w3);
					} else {
						PApplet.println("Based on last. w/ 2 random seeds");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						superMesh spice2 = new superMesh(p);
						spice2.populateValuesRndm();
						mesh.evolveV3(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								spice2, w1, w2, w3);
					}
					mesh.make();
					meshes.add(mesh);
				}
				id++;
			}
		}
	}

	void rePopulateGen4(int cID) { // weighted of past 3
		superMesh mesh;
		meshes = new Vector<superMesh>();
		int id = 0;

		float w1 = 0.6f;
		float w2 = 0.25f;
		float w3 = 0.15f;

		PApplet.println("Running a generation 3 population with: " + w1 + " " + w2 + " " + w3);

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				if (id == 0) {
					mesh = new superMesh(p);
					mesh.populateValuesRndm();
					mesh.make();
					mesh.setEscape();
					meshes.add(mesh);
				} else {
					mesh = new superMesh(p);
					if (this.pastMeshes.size() > 2 && cID != 0) {
						PApplet.println("Averaging Last 3.");
						mesh.evolveV4(this.pastMeshes.get(this.pastMeshes.size() - 1),
								this.pastMeshes.get(this.pastMeshes.size() - 2),
								this.pastMeshes.get(this.pastMeshes.size() - 3), w1, w2, w3, id);
					} else if (this.pastMeshes.size() > 1 && cID != 0) {
						PApplet.println("Averaging Last 2. w/ 1 random seed.");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						mesh.evolveV4(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								this.pastMeshes.get(this.pastMeshes.size() - 2), w1, w2, w3, id);
					} else {
						PApplet.println("Based on last. w/ 2 random seeds");
						superMesh spice = new superMesh(p);
						spice.populateValuesRndm();
						superMesh spice2 = new superMesh(p);
						spice2.populateValuesRndm();
						mesh.evolveV4(this.pastMeshes.get(this.pastMeshes.size() - 1), spice,
								spice2, w1, w2, w3, id);
					}
					mesh.make();
					meshes.add(mesh);
				}
				id++;
			}
		}
	}

	void update() {
		rt += 0.01f;
	}

	public void draw() {
		update();

		int id = 0;

		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 3; j++) {
				superMesh m = meshes.get(id);
				p.pushMatrix();
				p.translate((p.width / 4) * j, (p.height / 3) * i);

				// Labels
				p.fill(100);
				//p.textFont(p.fSmall);
				p.text("Stroke: " + m.stroke, 125, 0);
				p.text("Fill: " + m.fill, 125, 10);
				p.text("Res: " + m.res, 125, -10);
				p.text("Size: " + m.size, 125, -20);

				p.text("n1: " + m.n1, 125, 20);
				p.text("n2: " + m.n2, 125, 30);

				p.pushMatrix();
				p.rotateY(rt);
				p.rotateZ(rt);
				p.stroke(m.stroke);
				p.fill(m.fill);
				// p.gfx.meshNormalMapped(m, false, 2);
				p.gfx.mesh(m);
				p.popMatrix();
				p.popMatrix();
				id++;
			}
		}
	}

	void drawPast() {
		int i = 1;
		for (superMesh m : pastMeshes) {
			p.pushMatrix();
			p.translate(67 * i, p.height - 50);
			if (m.isEscape == true) {
				p.fill(255, 50, 0);
				p.noStroke();
				p.gfx.rect(new Rect(-10, m.size * 0.35f, 20, 5));
			}
			p.rotateY(rt);
			p.stroke(m.stroke);
			p.fill(m.fill);
			p.gfx.mesh(m);
			p.popMatrix();
			i++;
		}
	}

	void ctrlPast() {
		if (this.pastMeshes.size() >= 19)
			this.pastMeshes.remove(0);
	}

	public int getMeshClicked(int mouseX, int mouseY) {
		int cP = -1;
		float mDist = 10000;
		int i = 0;
		Vec2D mPos = new Vec2D(mouseX, mouseY);
		for (Vec2D v : cps) {
			if (mPos.distanceTo(v) < mDist) {
				mDist = mPos.distanceTo(v);
				cP = i;
			}
			i++;
		}

		if (mDist < 50)
			return cP;
		else
			return -1;

	}

	void export() {
		TriangleMesh megaMesh = new TriangleMesh();
		int i = 0;
		for (superMesh m : pastMeshes) {
			TriangleMesh miniMesh = m.copy();
			miniMesh.center(new Vec3D(75 * i, 0, 0));
			megaMesh.addMesh(miniMesh);
			i++;

		}
		megaMesh.saveAsSTL(
				p.sketchPath("output\\stl\\" + PApplet.year() + "_" + PApplet.month() + "_"
						+ PApplet.day() + "_" + PApplet.hour() + "_" + PApplet.minute() + "_"
						+ p.frameCount + "mega.stl"), false);
	}
}
