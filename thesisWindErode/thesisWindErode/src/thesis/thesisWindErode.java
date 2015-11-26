package thesis;

import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Slider;
import controlP5.Toggle;
import peasy.PeasyCam;
import processing.core.*;
import toxi.geom.*;
import toxi.processing.ToxiclibsSupport;

public class thesisWindErode extends processing.core.PApplet {
	private static final long serialVersionUID = 1L;
	//TODO
	/* TO ADD */
	// Better emitter position controls
	// Better emitter locaton ctrls.
	// box size controls
	// a restart button

	/*
	 * cnWind1Add cnWind2Add cwindSpeed cwindFreq cboxX cboxY cboxZ cRAD
	 */

	// General
	public ToxiclibsSupport gfx;
	PeasyCam nav;
	PFont font;
	grid grid;
	ControlP5 controlP5;

	pointStack tree;
	emitter wind;
	emitter windAdd;
	int TREE_SIZE = 100000;

	int RAD = 30;
	float maxHealth = 10;
	int cnWind1Add = 10;
	int cnWind2Add = 5;
	int cwindSpeed = 3;
	int cwindFreq = 1;
	int cboxX = 17;
	int cboxY = 12;
	int cboxZ = 40;
	int cRAD = RAD;

	// Boolean
	boolean paused = true;
	boolean drawTree = false;
	boolean drawGrid = true;
	boolean boxMeshOn = false;
	boolean screenShot = false;
	boolean camOn = true;
	boolean drawPts = true;

	public void setup() {
		size(1600, 900, OPENGL);
		smooth();
		frameRate(30);

		font = createFont("Arial Bold", 48);
		gfx = new ToxiclibsSupport(this);
		
		

		nav = new PeasyCam(this, 500);
		nav.setMinimumDistance(-50);

		grid = new grid(10, 10, 25, 25, this);

		this.startTree();
		this.startWind();

		this.guiSetup();

	}

	public void update() {
		tree.update();
		wind.update();
		windAdd.update();
		if (millis() % cwindFreq == 0)
			wind.addPts(cnWind1Add);
		if (millis() % cwindFreq == 0)
			windAdd.addPts(cnWind2Add);


	}

	public void draw() {
		background(0);

		if (!paused)
			update();

		if (drawGrid)
			grid.drawGrid();

		if (drawTree)
			tree.draw();

		if (!boxMeshOn)
			tree.drawPoints();

		if (drawPts)wind.draw();
		if (drawPts)windAdd.draw();

		if (boxMeshOn)
			tree.drawBoxMesh();

		nav.beginHUD();
		if (screenShot)
			this.screenShot();
		fill(0);
		text(frameRate, 20, 20);
		controlP5.draw();
		nav.endHUD();
	}

	public void keyPressed() {
		switch (key) {
		case ' ':
			paused = !paused;
			break;
		case 't':
			drawTree = !drawTree;
			break;

		case 'g':
			drawGrid = !drawGrid;
			break;
			
		case 'l':
			nav.setActive(camOn);
			camOn = !camOn;
			break;

		case 'b':
			//tree.calcBoxMesh();
			//boxMeshOn = !boxMeshOn;
			break;

		case 'e':
			tree.exportPoints();
			break;

		case 'z':
			screenShot = true;
			break;

		case 's':
			//tree.calcBoxMesh();
			//tree.saveMesh();
			break;
			
		case 'p':
			drawPts = !drawPts;
			break;
		}
	}

	void reset() {
		this.startTree();
		this.startWind();
	}
	
	void export(){
		tree.calcBoxMesh();
		tree.saveMesh();
	}

	void startTree() {
		
		tree = new pointStack(new Vec3D(-1, -1, 0).scaleSelf(TREE_SIZE / 2),
				TREE_SIZE, this);
		
		tree.readFile("ptStack.txt");
	}

	void startWind() {
		wind = new emitter("summerwinds_1.stl", false, this);
		windAdd = new emitter("winterwinds_1.stl", true, this);
	}

	void guiSetup() {
		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);

		// style stuff
		controlP5.setColorForeground(200);
		controlP5.setColorBackground(50);
		controlP5.setColorValueLabel(color(255));
		controlP5.setColorActive(color(150));
		controlP5.setColorCaptionLabel(color(255));

		Group displayCtrls = controlP5.addGroup("Display Controls")
				.setPosition(10, 20);
		displayCtrls.setOpen(false);

		Button bReset = controlP5.addButton("reset", 1, 5, 5, 55, 15);
		bReset.setGroup(displayCtrls);
		
		Button bExport = controlP5.addButton("export", 1, 5, 30, 55, 15);
		bExport.setGroup(displayCtrls);
		
		Toggle tdrawGrid = controlP5.addToggle("drawGrid", false, 65, 5, 30, 10)
				.setMode(ControlP5.SWITCH).setId(0);
		tdrawGrid.setGroup(displayCtrls);
		
		Toggle tdrawTree = controlP5.addToggle("drawTree", false, 65, 30, 30, 10)
				.setMode(ControlP5.SWITCH).setId(0);
		tdrawTree.setGroup(displayCtrls);

		Group advCtrlsGrp = controlP5.addGroup("Advanced Controls")
				.setPosition(width - 250 - 5, 20);
		advCtrlsGrp.setOpen(false);

		Slider snWind1Add = controlP5
				.addSlider("cnWind1Add", 1, 20, 5, 5, 5, 150, 10).setId(1)
				.setTriggerEvent(Slider.RELEASE);
		snWind1Add.setGroup(advCtrlsGrp);

		Slider snWind2Add = controlP5
				.addSlider("cnWind2Add", 1, 20, 5, 5, 20, 150, 10).setId(1)
				.setTriggerEvent(Slider.RELEASE);
		snWind2Add.setGroup(advCtrlsGrp);

		Slider swindFreq = controlP5
				.addSlider("cwindFreq", 1f, 200f, 8f, 5, 35, 150, 10).setId(1)
				.setTriggerEvent(Slider.RELEASE);
		swindFreq.setGroup(advCtrlsGrp);

		Slider sboxX = controlP5.addSlider("cboxX", 10, 25, 15, 5, 50, 150, 10)
				.setId(1).setTriggerEvent(Slider.RELEASE);
		sboxX.setGroup(advCtrlsGrp);

		Slider sboxY = controlP5.addSlider("cboxY", 10, 25, 15, 5, 65, 150, 10)
				.setId(1).setTriggerEvent(Slider.RELEASE);
		sboxY.setGroup(advCtrlsGrp);

		Slider sboxZ = controlP5
				.addSlider("cboxZ", 10, 200, 50, 5, 80, 150, 10).setId(1)
				.setTriggerEvent(Slider.RELEASE);
		sboxZ.setGroup(advCtrlsGrp);

		Slider sRAD = controlP5.addSlider("cRAD", 2, 18, 6, 5, 95, 150, 10)
				.setId(1).setTriggerEvent(Slider.RELEASE);
		sRAD.setGroup(advCtrlsGrp);
	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/" + year() + "_" + month() + "_"
				+ day() + "_" + hour() + "_" + minute() + "_" + frameCount
				+ "_wind_screen.png"));

		screenShot = false;
	}

}
