package thesis;

import java.io.PrintWriter;
import java.util.ArrayList;
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

public class thesisPointStructurev2 extends PApplet {
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
	int SR = 48;
	int CLOUD_SIZE = 10000;
	int stat;

	String fPath = "";
	String STLSURFPath = "C:/Users/Mike/Documents/SCRIPTS/thesisPointStructurev2/bin/data/tennis_surf.stl";
	String STLOBSTPath = "C:/Users/Mike/Documents/SCRIPTS/thesisPointStructurev2/bin/data/tennis_walls.stl";

	// Booleans
	boolean paused = false;
	boolean drawTree = false;
	boolean screenShot = false;
	boolean drawBadConns = false;
	boolean drawEnviroment = true;
	boolean drawPTS = true;

	public PrintWriter OUTPUT;
	public PrintWriter out1;
	public PrintWriter out2;
	public PrintWriter out3;

	public static void main(String[] args) {

	}

	public void setup() {
		size(1600, 900, OPENGL);
		smooth();
		frameRate(30);
		background(255);

		gfx = new ToxiclibsSupport(this);
		gui = new guiSetup(this);

		nav = new PeasyCam(this, 500);
		nav.setMinimumDistance(-100);

		surfMesh = (TriangleMesh) new STLReader().loadBinary(STLSURFPath, STLReader.TRIANGLEMESH);
		obstMesh = (TriangleMesh) new STLReader().loadBinary(STLOBSTPath, STLReader.TRIANGLEMESH);

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
		strokeWeight(1);
		// drawSurf
		if (drawEnviroment)
			gfx.mesh(surfMesh);

		
		// drawObst
		if (drawEnviroment)
			gfx.mesh(obstMesh);

		if (drawPTS) {
			strokeWeight(3);
			stroke(0);
			for (Vec3D v : drawPts)
				gfx.point(v);
			strokeWeight(1);
		}

		if (drawTree)
			ctrl.pc.drawTree();
		strokeWeight(1);
		if (ctrl.drawConnsOn())
			this.drawConns(drawConnections.clone(), drawBadConnections.clone());

		
		if(ctrl.drawBadTri){
			for (Triangle3D tt : drawBadFaces){
				strokeWeight(1);
				fill(255, 55, 55, 155);
				gfx.triangle(tt);
			}

			
		}
		
		if(ctrl.drawisec){
			for (Vec3D v : drawIsecPts){
				strokeWeight(5);
				stroke(0, 200, 0);
				gfx.point(v);
			}
		}
		
		
		fill(255);
		stroke(0);
		strokeWeight(1);
		
		
		if (ctrl.drawTri){
			for (Triangle3D t : drawFaces){
				strokeWeight(0.5f);
				fill(50,55);
				gfx.triangle(t);
			}
		}
		
		

		

		 if(ctrl.drawPhysicsOn())
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

		if (ctrl.hasNewTri){
			drawFaces = ctrl.getTri();
			drawBadFaces = ctrl.getBadTri();
			drawIsecPts = ctrl.getIsecPoints();
		}
		
		
	}

	public void keyPressed() {
		switch (key) {
		case 'r':
			this.drawConnections = new Vector<connection>();
			this.drawBadConnections  = new Vector<connection>();
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
			ctrl.drawTri =! ctrl.drawTri;
			break;
			
		case 'u':
			ctrl.drawBadTri =! ctrl.drawBadTri;
			break;
			
		case 'i':
			ctrl.drawisec =! ctrl.drawisec;
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
	void drawConns(Object object, Object badObjects) {
		for (connection c : (Vector<connection>) object) {
			if (!drawBadConns)
				if (c.value < 0)
					continue;
			switch (c.value) {
			case 0:
				strokeWeight(1);
				stroke(0, 50);
				break;
			case -1:
				strokeWeight(1);
				stroke(255, 100, 0);
				break;
			case -2:
				stroke(0, 100, 255);
				strokeWeight(2);
				break;
			case -3:
				stroke(0, 255, 0);
				break;
			default:
				stroke(255, 0, 0);
				strokeWeight(4);
				break;
			}

			if (c.value == 0) {
				strokeWeight(1);
				stroke(0, 50);
			} else {
				strokeWeight(Math.abs(c.value));
				stroke(Math.abs(c.value) * 250 / 5, 0, 0);
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
