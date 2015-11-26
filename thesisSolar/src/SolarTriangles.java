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

public class SolarTriangles extends PApplet {
	private static final long serialVersionUID = 1L;

	public ToxiclibsSupport gfx;
	PeasyCam nav;
	Triangles Trs;
	SolarVectors Srs;
	PrintWriter OUTPUT;

	String tImport = "finalTriangles.txt";
	String rImport = "solarvectorslat40.txt";
	TriangleMesh surfMesh;
	TriangleMesh obstMesh;

	ControlP5 controlP5;
	// guiSetup gui;

	int SF = 1;
	int raySF = 1;
	int SR = 48;
	int CLOUD_SIZE = 1000;
	int stat;

	boolean screenShot = false;
	Vec3D unitZ = new Vec3D(0, 0, 1);

	public void setup() {
		size(1200, 800, OPENGL);
		smooth();
		frameRate(30);
		background(255);

		gfx = new ToxiclibsSupport(this);
		nav = new PeasyCam(this, 500);
		nav.setMinimumDistance(-100);
		// gui = new guiSetup(this);
		Srs = new SolarVectors(this);
		Srs.readFile(dataPath(rImport));

		Trs = new Triangles(this);
		Trs.readFile(dataPath(tImport));

	}

	public void draw() {
		background(255);
		Trs.execute();
		Trs.draw();
		
		if(screenShot){
			screenShot();
		}

	}

	float[] getRange(float[] numbers) {
		float min = 1000000000;
		float max = 0;
		float[] range = new float[2];

		for (int i = 0; i < numbers.length; i++) {
			if (max < numbers[i]) {
				max = numbers[i];
			}
			if (min > numbers[i]) {
				min = numbers[i];
			}
		}
		range[0] = min;
		range[1] = max;
		return range;
	}
	
	
	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/vid/" + year() + "_" + month()
				+ "_" + day() + "_" + hour() + "_" + minute() + "_"
				+ frameCount + "_SolarTriangles_screen.png"));

		screenShot = false;
	}

	public void keyPressed() {

		switch (key) {
		case 'e':
			Trs.exportTriangles();
			break;

		case 's':
			screenShot = true;
			break;
		}

	}

}
