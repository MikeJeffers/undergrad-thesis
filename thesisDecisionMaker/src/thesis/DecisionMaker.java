package thesis;

import processing.core.PFont;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

//evolution w/ decreasing jitter
//new mesh based on past n meshes

public class DecisionMaker extends processing.core.PApplet {
	private static final long serialVersionUID = 1L;

	ToxiclibsSupport gfx;

	superGrid sg;
	
	PFont fBold, fReg, fSmall ;

	
	boolean screenShot = false;

	public void setup() {
		size(1280, 720, OPENGL);
		smooth();
		frameRate(20);

		gfx = new ToxiclibsSupport(this);
		
		//fBold = createFont(dataPath("fonts/HelveticaLTStd-Bold.otf"), 32);
		//fReg = createFont(dataPath("fonts/HelveticaLTStd-Bold.otf"), 18);
		//fSmall = createFont(dataPath("fonts/HelveticaLTStd-Roman.otf"), 10);
		//textFont(fReg);

		sg = new superGrid(this);
	}

	public void draw() {
		background(255);
		lights();

		sg.draw();
		sg.drawPast();

	
		
		fill(100);
		//textFont(fSmall);
		text(frameRate, 20, 50);
		
		fill(0);
		//textFont(fBold);
		text("thesisDecisionMaker", 20, 40);
		
		fill(100);
		//textFont(fReg);
		text("choiceHistory", 20, height-100);
		
		if (screenShot)
			this.screenShot();

	}

	public void mousePressed() {
		int n = sg.getMeshClicked(mouseX, mouseY);
		if( n > -1){
			superMesh m = sg.meshes.get(n);
			TriangleMesh m2 = sg.meshes.get(n).copy();
			m.clear();
			m.addMesh(m2.getScaled(.25f));
			sg.pastMeshes.add(m);
			sg.ctrlPast();
			
			sg.rePopulateGen4(n);
			
			screenShot = true;
		}
	}

	public void keyPressed() {
		switch (key) {
		case ' ':
			setup();
			break;

		case 's':
			screenShot = true;
			break;
			
		case'e':
			sg.export();
			break;
		}
	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/" + year() + "_" + month() + "_"
				+ day() + "_" + hour() + "_" + minute() + "_" + frameCount
				+ "_wind_screen.png"));

		screenShot = false;
	}

}
