import java.io.File;
import java.util.ArrayList;

import peasy.PeasyCam;
import processing.core.*;
import toxi.geom.*;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class Circulation extends processing.core.PApplet {
	private static final long serialVersionUID = 1L;

	public ToxiclibsSupport gfx;
	PeasyCam cam;
	
	holder myHolder;
	ProgPoints PP;

	dotTree dotTree; 
	ObstacleTree obsTree; 
	terrainTree tTree;
	
	

	ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
	ArrayList<Vec3D> allVertx = new ArrayList<Vec3D>();

	TriangleMesh aMesh = new TriangleMesh();
	TriangleMesh importMesh = new TriangleMesh();
	TriangleMesh terrain = new TriangleMesh();
	TriangleMesh moved = new TriangleMesh();

	float cRad = 48f;
	float aRad = 24f;
	String fName = "ptCloud_it1.txt";
	String fNameDest = "TARGET_POS_it1.txt";

	float minX = -2000;
	float maxX = 2000;
	float minY = -2000;
	float maxY = 2000;
	float minZ = 0;
	float maxZ = 1500;
	ReadonlyVec3D unitY = new Vec3D(0, 1, 0);
	ReadonlyVec3D unitX = new Vec3D(1, 0, 0);
	ReadonlyVec3D unitZ = new Vec3D(0, 0, 1);

	ArrayList<Vec3D> start = new ArrayList<Vec3D>();
	ArrayList<Vec3D> end = new ArrayList<Vec3D>();

	boolean video = false;
	boolean screenShot = false;
	boolean camOn = true;
	boolean paused = false;
	
	boolean drawLines = false;
	

	public void setup() {
		size(1200, 800, OPENGL);
		smooth();
		cam = new PeasyCam(this, 500);
		gfx = new ToxiclibsSupport(this);
		float range = maxX - minX;
		myHolder = new holder(this);
		dotTree = new dotTree(new Vec3D(minX, minY, minZ), range*5, this);
		obsTree = new ObstacleTree(new Vec3D(minX, minY, minZ), range*5,
				this);
		tTree = new terrainTree(new Vec3D(minX, minY, minZ), range*5,
				this);

		PP = new ProgPoints(this);
		PP.readFile(dataPath(fName));

		ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
		File f = new File("");
		try {
			f = new File(dataPath(fNameDest));
		} catch (NullPointerException ex) {
			PApplet.println("File: " + " could not be found.");
		}
		String[] strLines = loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++) {
			String clean = strLines[i].substring(1, strLines[i].length() - 1);
			String[] splitToken = clean.split(", ");
			float xx = PApplet.parseFloat(splitToken[0]);
			float yy = PApplet.parseFloat(splitToken[1]);
			float zz = PApplet.parseFloat(splitToken[2]);
			Vec3D ptt = new Vec3D(xx, yy, zz);
			importPts.add(ptt);
		}

		for (int i = 0; i < importPts.size(); i++) {
			if (i < importPts.size() - 1) {
				start.add(importPts.get(i));
				end.add(importPts.get(i + 1));
			} else {
				start.add(importPts.get(i));
				end.add(importPts.get(0));
			}
		}



		aMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("final_obs.stl"), STLReader.TRIANGLEMESH);

		allVertx.addAll(aMesh.getVertices());
		obsTree.addAll(allVertx);
		allVertx.clear();

		importMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("final_srf.stl"), STLReader.TRIANGLEMESH);
		allVertx.addAll(importMesh.getVertices());
		tTree.addAll(allVertx);
		terrain = importMesh;
		moved = importMesh.getTranslated(new Vec3D(0, 0, 36));

		for (int i = 0; i < 150; i++) {
			int groupId = (int) (random(0, start.size()));
			float test = random(0, 1);
			myHolder.addAgent(new Agent(i, groupId, test, this));
		}

		
	}

	public void draw() {
		background(0);
		strokeWeight(3);
		stroke(222);
		noStroke();
		if (!paused)
			myHolder.execute();
		myHolder.draw();
		noStroke();

		PP.execute();
		PP.draw();

		strokeWeight(5);
		stroke(100);
		for (int j = 0; j < start.size(); j++) {
			gfx.point(start.get(j));
			gfx.point(end.get(j));
		}

		strokeWeight(0.1f);
		stroke(255, 55);
		fill(222, 222);
		gfx.mesh(aMesh);
		fill(200, 222);
		gfx.mesh(terrain);

		if (frameCount % 4 == 0 && video)
			screenShot = true;

		if (screenShot)
			screenShot();
	}

	public void keyPressed() {
		switch (key) {
		case ' ':
			paused = !paused;
			break;

		case 'l':
			cam.setActive(camOn);
			camOn = !camOn;
			break;

		case 'e':
			PP.exportPoints();
			myHolder.exportAgents();
			break;

		case 's':
			screenShot = true;
			break;

		case 'v':
			video = !video;
			break;
		
		case 'q':
			drawLines = !drawLines;
			break;
		
		}
	}
	
	
	int indexOfMin(float[] values){
		int index = 0;
		float min = 1000000000;
		for(int i=0; i<values.length; i++){
			if(values[i]<min){
				min=values[i];
				index = i;
			}
		}	
		return index;
	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/vid/" + year() + "_" + month()
				+ "_" + day() + "_" + hour() + "_" + minute() + "_"
				+ frameCount + "_circ_screen.png"));

		screenShot = false;
	}
}
