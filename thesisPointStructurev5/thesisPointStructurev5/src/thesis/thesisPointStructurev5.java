package thesis;
/* MJ+JP
 * (Jordan Parsons and Michael Jeffers)
 * Date: 4/16/2013
 */

import java.io.PrintWriter;
import java.util.Vector;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import peasy.PeasyCam;
import processing.core.*;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class thesisPointStructurev5 extends PApplet {
	private static final long serialVersionUID = 1L;

	// Lib Classes
	public ToxiclibsSupport gfx;
	PeasyCam nav;
	ControlP5 controlP5;
	TriangleMesh surfMesh;
	TriangleMesh obstMesh;
	// My Classes
	guiSetup gui;
	thesisControlThread ctrl;
	// Drawing lists
	public Vector<connection> drawConnections = new Vector<connection>();
	public Vector<connection> drawBadConnections = new Vector<connection>();
	Vector<Vec3D> drawPts = new Vector<Vec3D>();
	private Vector<Triangle3D> drawFaces = new Vector<Triangle3D>();
	private Vector<Triangle3D> drawBadFaces = new Vector<Triangle3D>();
	private Vector<Vec3D> drawIsecPts = new Vector<Vec3D>();

	// Vars
	int SF = 1;
	int SR = 50;
	int CLOUD_SIZE = 10000;
	int stat;

	String fPath = "";

	String STLSURFPath = "final_srf.stl";
	String STLOBSTPath = "final_obs.stl";
	String ptsFile = "ptCloud_it1.txt";



	// Booleans
	boolean paused = false;
	boolean drawTree = false;
	boolean screenShot = false;
	boolean drawBadConns = true;
	boolean drawEnviroment = true;
	boolean drawPTS = true;
	boolean drawTri = true;

	public PrintWriter OUTPUT;
	public PrintWriter out1;
	public PrintWriter out2;
	public PrintWriter out3;

	public static void main(String[] args) {

	}

	public void setup() {
		size(1200, 800, OPENGL);
		smooth();
		frameRate(30);
		background(255);

		gfx = new ToxiclibsSupport(this);
		gui = new guiSetup(this);

		nav = new PeasyCam(this, 1000);
		nav.setMinimumDistance(-100);

		surfMesh = (TriangleMesh) new STLReader().loadBinary(dataPath(STLSURFPath), STLReader.TRIANGLEMESH);
		obstMesh = (TriangleMesh) new STLReader().loadBinary(dataPath(STLOBSTPath), STLReader.TRIANGLEMESH);

		ctrl = new thesisControlThread(this);
		ctrl.start();

		gui.sendMessage("Started");
	}

	void update() {

	}

	public void draw() {
		background(255);

		if (!paused)
			update();

		fill(255);
		stroke(0);
		strokeWeight(1f);
		// drawSurf
		if (drawEnviroment)
			gfx.mesh(surfMesh);

		// drawObst
		if (drawEnviroment)
			gfx.mesh(obstMesh);

		if (drawPTS) {
			strokeWeight(4);
			stroke(0);
			for (Vec3D v : drawPts)
				gfx.point(v);
			strokeWeight(1);
		}

		if (drawTree)
			ctrl.pc.drawTree();
		
		strokeWeight(3);
		if (ctrl.drawConnsOn())
			this.drawConns(drawConnections.clone());


		if (ctrl.drawisec) {
			for (Vec3D v : drawIsecPts) {
				strokeWeight(5);
				stroke(0, 200, 0);
				gfx.point(v);
			}
		}

		fill(255);
		stroke(0);
		strokeWeight(1);

		if (drawTri) {
			for (Triangle3D t : ctrl.getTri()) {
				noStroke();
				//strokeWeight(0.5f);
				fill(50, 55);
				gfx.triangle(t);
			}
		}

		if (ctrl.drawPhysicsOn())
			ctrl.physics.draw();

		if (screenShot)
			screenShot();

		nav.beginHUD();
		gui.draw();
		nav.endHUD();
		this.checkStatus();
	}

	public void checkStatus() {
		if (ctrl.getNewCon()) {
			drawConnections = ctrl.getConnections();
			drawBadConnections = ctrl.getBadConnections();
		}
		if (ctrl.getNewPts())
			drawPts = ctrl.getPts();

		if (ctrl.hasNewTri) {
			drawFaces = ctrl.getTri();
			drawBadFaces = ctrl.getBadTri();
			drawIsecPts = ctrl.getIsecPoints();
		}

	}

	public void keyPressed() {
		switch (key) {
		case 'r':
			this.drawConnections = new Vector<connection>();
			this.drawBadConnections = new Vector<connection>();
			ctrl.clearConnections();
			ctrl.genConnections();
			break;

		case 'p':
			drawPTS = !drawPTS;
			break;

		case ' ':
			paused = !paused;
			ctrl.togglePhysics();
			break;

		case 'c':
			ctrl.drawConns = !ctrl.drawConns;
			break;

		case 's':
			screenShot = true;
			break;

		case 'l':
			nav.setActive(!nav.isActive());
			break;

		case 'b':
			drawBadConns = !drawBadConns;
			break;

		case 'm':
			drawEnviroment = !drawEnviroment;
			break;

		case 'e':
			ctrl.export();
			break;

		case 'y':
			ctrl.drawTri = !ctrl.drawTri;
			break;

		case 'u':
			ctrl.drawBadTri = !ctrl.drawBadTri;
			break;

		case 'i':
			ctrl.drawisec = !ctrl.drawisec;
			break;

		case 'g':
			ctrl.startPhysics();
			ctrl.drawConns = false;
			ctrl.drawPhysics = true;
			break;

		}
	}

	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.getController().getName().equals("openPTFile")) {
			ctrl.clearPC();
			gui.openPTFile();
		}

		if (theEvent.getController().getName().equals("reLoad")) {
			ctrl.clearPC();
			ctrl.readFile(fPath);
		}

	}

	@SuppressWarnings("unchecked")
	void drawConns(Object object) {
		for (connection c : (Vector<connection>) object) {
			strokeWeight(1.5f);
			stroke(0, 125);

			if (!drawBadConns)
				break;

			switch (c.value) {
			case 0:
				strokeWeight(1.5f);
				stroke(0, 125);
				break;
			case -1:
				strokeWeight(2);
				stroke(50, 0, 0);
				break;
			case -2:
				strokeWeight(3);
				stroke(100, 0, 0);
				break;
			case -3:
				strokeWeight(4);
				stroke(150, 0, 0);
				break;
			case -4:
				strokeWeight(4);
				stroke(200, 0, 0);
				break;
			case -5:
				strokeWeight(4);
				stroke(255, 0, 0);
				break;
			case -6:
				strokeWeight(5);
				stroke(255, 0, 0);
				break;
			case 1:
				strokeWeight(2);
				stroke(0, 255, 255);
				break;
			case 2:
				strokeWeight(2);
				stroke(0, 0, 255);
				break;
			case 3:
				strokeWeight(3);
				stroke(0, 0, 255);
				break;
			}

			gfx.line(c.ln);
		}

	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/" + year() + "_" + month() + "_" + day() + "_" + hour()
				+ "_" + minute() + "_" + frameCount + "_ps.png"));

		screenShot = false;
	}
}
