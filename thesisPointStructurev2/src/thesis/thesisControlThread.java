package thesis;

import java.util.ArrayList;
import java.util.Vector;

import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;

public class thesisControlThread extends Thread {
	thesisPointStructurev2 p;
	int status = 0;

	tpointCloud pc;
	tConnectionMatrix cm;
	tPhysics physics;

	boolean on = true;
	boolean cmStart = false;

	boolean physicsStart = false;
	boolean physicsOn = false;

	boolean hasNewCon = false;
	boolean hasNewPts = false;
	boolean hasNewTri = false;

	boolean drawConns = true;
	boolean drawPhysics = false;
	boolean drawTri = true;
	boolean drawBadTri = true;
	boolean drawisec = false;
	boolean pcStart = false;

	public thesisControlThread(thesisPointStructurev2 parent) {
		p = parent;
		// declare internal pc & cm
		pc = new tpointCloud(new Vec3D(-p.CLOUD_SIZE / 2, -p.CLOUD_SIZE / 2, -p.CLOUD_SIZE / 2),
				p.CLOUD_SIZE, p);
		cm = new tConnectionMatrix(p, pc);
		physics = new tPhysics(p);
	}

	public void start() {
		super.start();
		p.gui.sendMessage("Thread Started.");
		this.status = 1;
	}

	public void run() {
		while (on) {
			if (pcStart) {
				p.gui.sendMessage("BAM");
				this.clearPC();
				this.pc.readFile(p.fPath);
				this.hasNewPts = true;
				this.pcStart = false;
			}
			if (cmStart) {
				cm.run();
				cmStart = false;
				drawConns = true;
				drawTri = true;
				drawBadTri = true;
			}

			if (physicsStart) {
				physics.setup(cm.getConnections());
				drawConns = false;
				drawPhysics = true;
				physicsStart = false;
				physicsOn = true;
			}

			if (physicsOn) {
				physics.run();
			}

		}
	}

	// ===============================================================
	// Getters
	public boolean getNewCon() {
		if (hasNewCon) {
			if (!cmStart)
				hasNewCon = false;
			return true;
		}
		return false;
	}

	public boolean getNewPts() {
		if (hasNewPts) {
			hasNewPts = false;
			return true;
		}
		return false;
	}

	public boolean getNewTri() {
		if (hasNewTri) {
			hasNewTri = false;
			return true;
		}
		return false;
	}

	public boolean physicsRunning() {
		return physicsOn;
	}

	public Vector<connection> getConnections() {
		return this.cm.getConnections();
	}

	public Vector<connection> getBadConnections() {
		return this.cm.getBadConnections();
	}
	
	public Vector<Vec3D> getIsecPoints() {
		return this.cm.getIsecPoints();
	}

	public Vector<Triangle3D> getTri() {
		return this.cm.getTriangles();
	}

	public Vector<Triangle3D> getBadTri() {
		return this.cm.getBadTriangles();
	}
	
	public Vector<Vec3D> getPts() {
		return new Vector<Vec3D>(this.pc.getPoints());
	}

	public int getStatus() {
		return this.status;
	}

	public boolean drawConnsOn() {
		return drawConns;
	}

	public boolean drawPhysicsOn() {
		return drawPhysics;
	}

	public void export() {
		//cm.export1();
		//cm.export2();
		//cm.export3();
		cm.exportEverything();
		//cm.exportForGH();
		//cm.exportConnections();
		//cm.exportTriangles();
	}

	// ===============================================================
	// Setters
	public void clearPC() {
		this.pc.clearPC();
		this.hasNewPts = false;
	}

	public void readFile(String fPath) {
		// this.pc.readFile(fPath);
		pcStart = true;
	}

	public void clearConnections() {
		this.cm.clear();
		this.hasNewCon = true;
	}
	public void clearTriangles() {
		this.cm.clearTriangles();
		this.hasNewTri = true;
	}

	public void genConnections() {
		this.cmStart = true;
		this.pcStart = false;
		this.hasNewCon = true;
		this.hasNewTri = true;
	}

	public void startPhysics() {
		this.physicsStart = true;
	}

	public tConnectionMatrix getCM() {
		return this.cm.getCM();
	}

	public tpointCloud getPC() {
		return this.pc;
	}
	public void togglePhysics(){
		this.physicsOn = !this.physicsOn;
	
	}
}
