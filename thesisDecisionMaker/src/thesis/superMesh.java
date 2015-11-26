package thesis;

import processing.core.PConstants;
import toxi.geom.mesh.SuperEllipsoid;
import toxi.geom.mesh.SurfaceFunction;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;

public class superMesh extends TriangleMesh {

	DecisionMaker p;
	int stroke;
	int fill;
	int res;
	int size;
	float n1, n2;
	boolean isEscape = false;

	public superMesh(DecisionMaker parent) {
		p = parent;
	}

	public superMesh(String arg0, DecisionMaker parent) {
		super(arg0);
		p = parent;
	}

	public superMesh(String arg0, int arg1, int arg2, DecisionMaker parent) {
		super(arg0, arg1, arg2);
		p = parent;
	}

	void populateValuesRndm() {
		stroke = (int) p.random(0, 255);
		fill = (int) p.random(0, 255);
		res = (int) p.random(10, 50);
		size = (int) p.random(10, 100);
		n1 = p.random(0, PConstants.PI);
		n2 = p.random(0, PConstants.PI);
	}

	void make() {
		this.clear();
		SurfaceFunction functon = new SuperEllipsoid(this.n1, this.n2);
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(functon);

		this.addMesh(b.createMesh(null, this.res, this.size));
	}

	void evolve(superMesh mOld) {
		this.stroke = (int) (mOld.stroke + p.random(-10, 10));
		this.fill = (int) (mOld.fill + p.random(-10, 10));
		this.res = (int) (mOld.res + p.random(-5, 5));
		this.size = (int) (mOld.size + p.random(-10, 10));
		this.n1 = mOld.n1 + p.random(-0.2f, 0.2f);
		this.n2 = mOld.n2 + p.random(-0.2f, 0.2f);
		this.make();

	}

	void evolveV2(superMesh m1, superMesh m2, superMesh m3) { // equal average of past 3
		this.stroke = (int) (m1.stroke + m2.stroke + m3.stroke) / 3;
		this.fill = (int) (m1.fill + m2.fill + m3.fill) / 3;
		this.res = (int) (m1.res + m2.res + m3.res) / 3;
		this.size = (int) (m1.size + m2.size + m3.size) / 3;
		this.n1 = (m1.n1 + m2.n1 + m3.n1) / 3;
		this.n2 = (m1.n2 + m2.n2 + m3.n2) / 3;

		this.jitter(10, 0.2f);

		this.make();
	}

	void evolveV3(superMesh m1, superMesh m2, superMesh m3, float w1, float w2, float w3) {
		this.stroke = (int) ((m1.stroke * w1) + (m2.stroke * w2) + (m3.stroke * w3));
		this.fill = (int) ((m1.fill * w1) + (m2.fill * w2) + (m3.fill * w3));
		this.res = (int) ((m1.res * w1) + (m2.res * w2) + (m3.res * w3));
		this.size = (int) ((m1.size * w1) + (m2.size * w2) + (m3.size * w3));
		this.n1 = (m1.n1 * w1) + (m2.n1 * w2) + (m3.n1 * w3);
		this.n2 = (m1.n2 * w1) + (m2.n2 * w2) + (m3.n2 * w3);

		this.jitter(10, 0.2f);

		this.make();

	}
	
	void evolveV4(superMesh m1, superMesh m2, superMesh m3, float w1, float w2, float w3, int id) {
		this.stroke = (int) ((m1.stroke * w1) + (m2.stroke * w2) + (m3.stroke * w3));
		this.fill = (int) ((m1.fill * w1) + (m2.fill * w2) + (m3.fill * w3));
		this.res = (int) ((m1.res * w1) + (m2.res * w2) + (m3.res * w3));
		this.size = (int) ((m1.size * w1) + (m2.size * w2) + (m3.size * w3));
		this.n1 = (m1.n1 * w1) + (m2.n1 * w2) + (m3.n1 * w3);
		this.n2 = (m1.n2 * w1) + (m2.n2 * w2) + (m3.n2 * w3);

		this.jitterAdv(10, 0.2f, 25, id);

		this.make();

	}

	void jitter(float amnt, float amnt2) {
		this.stroke += (int) p.random(-amnt, amnt);
		this.fill += (int) p.random(-amnt, amnt);
		this.res += (int) p.random(-amnt, amnt);
		this.size += (int) p.random(-amnt, amnt);
		this.n1 += (int) p.random(-amnt2, amnt2);
		this.n2 += (int) p.random(-amnt2, amnt2);

		this.stroke = this.stroke % 255;
		this.fill = this.fill % 255;
		this.res = (this.res % 95) + 5;
		this.size = (this.size % 95) + 5;
	}
	
	void jitterAdv(float amnt, float amnt2, float amnt3, int id) {
		this.stroke += (id==1) ? (int) p.random(-amnt3, amnt3) : (int) p.random(-amnt, amnt);
		this.fill += (id==2) ? (int) p.random(-amnt3, amnt3) : (int) p.random(-amnt, amnt);
		this.res += (id==3) ? (int) p.random(-amnt3, amnt3) : (int) p.random(-amnt, amnt);
		this.size += (id==4) ? (int) p.random(-amnt3, amnt3) : (int) p.random(-amnt, amnt);
		this.n1 += (int) p.random(-amnt2, amnt2);
		this.n2 += (int) p.random(-amnt2, amnt2);

		this.stroke = this.stroke % 255;
		this.fill = this.fill % 255;
		this.res = (this.res % 95) + 5;
		this.size = (this.size % 95) + 5;
	}

	public void setEscape() {
		this.isEscape = true;
	}

}
