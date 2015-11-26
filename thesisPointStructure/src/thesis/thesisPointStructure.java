package thesis;

import java.io.PrintWriter;

import peasy.PeasyCam;
import processing.core.PApplet;
import thesis.tPhysics;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

public class thesisPointStructure extends PApplet {
	private static final long serialVersionUID = 1L;

	public ToxiclibsSupport gfx;
	PeasyCam nav;
	grid grid;
	tpointCloud pc;
	tConnectionMatrix cm;

	tPhysics physics;

	boolean paused = true;
	PrintWriter OUTPUT;

	int CLOUD_SIZE = 5000;
	int step = 0;
	int nCyc = 1;
	
	float SF = 10;
	int SR = 45;
	String fName = "37_2_7_16_32_799_ptStack.txt";
	String fNameAP = "samplePts_structureAP.txt";

	boolean drawGrid = false;
	boolean drawTree = false;
	boolean screenShot = false;
	boolean drawCM = true;

	public void setup() {
		size(1280, 720, OPENGL);
		smooth();
		frameRate(30);
		background(255);

		gfx = new ToxiclibsSupport(this);

		nav = new PeasyCam(this, 500);
		nav.setMinimumDistance(-100);

		pc = new tpointCloud(new Vec3D(-CLOUD_SIZE/2, -CLOUD_SIZE/2, -CLOUD_SIZE/2), CLOUD_SIZE, this);
		pc.readFile(fName);
		pc.readFileAP(fNameAP);
		

		cm = new tConnectionMatrix(this);
		cm.stepAll();
		cm.genConIDS();

		grid = new grid(10, 10, 25, 25, this);

		physics = new tPhysics(this);
		

	}

	public void update() {
		physics.update();
		step++;
		print((step - 1) + " ");
		//cycleScripts();
	}

	public void draw() {
		background(255);
		
		if(!paused)
			update();

		if (drawGrid)
			grid.drawGrid();
		if (drawTree)
			pc.draw();

		if (drawCM)
			cm.draw();
		else
			physics.draw();

	}

	void cycleScripts() {

		if (this.step == 0) {
			println("Begining Cycle: "+nCyc);
			cm.exportDeleted();
			cm.checkConAngles();
			cm.draw();
			this.paused = true;
			step = 1;
			println("--Pausing for user input.");
			return;
		} else if (this.step == 1) {
			println("Starting Physics.");
		//	physics = new tPhysics(this);
			println("--Adding connections & nodes to the physics system.");
		//	physics.setup(cm.connectionIDs, pc.apID);
			this.paused = false;
			drawCM = false;
			print("--Running physics: ");
		}

		if (this.step > 5) {
			println("...Done.");
			println("Physics done");
			this.step = 0;
			this.paused = true;
		//	physics.passConnectionsOut();
			drawCM = true;
			println("Cycle "+nCyc+" done.");
			nCyc++;
		}
	}

	public void keyPressed() {
		switch (key) {
		case ' ':
			
			cycleScripts();

			break;

		case 'r':
			paused = !paused;

		case 's':
			screenShot = true;
			break;

		case 't':
			drawTree = !drawTree;
			break;

		case 'e':
			// physics.exportSprings();
			cm.exportConnections();
			break;

		}
	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/" + year() + "_" + month() + "_" + day() + "_" + hour()
				+ "_" + minute() + "_" + frameCount + "_ps.png"));

		screenShot = false;
	}
}
