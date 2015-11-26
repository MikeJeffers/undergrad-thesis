import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import peasy.PeasyCam;
import processing.core.*;
import toxi.geom.*;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class Program extends processing.core.PApplet {
	private static final long serialVersionUID = 1L;

	public ToxiclibsSupport gfx;
	PeasyCam cam;
	

	PointCloudList PC;
	TargetList targetList;
	
	
	Tree octree;
	ObsTree ObstacleTree;
	dotTree dotTree;


	float x = 0;
	float y = 0;
	float z = 0;
	float xx = 0;
	float yy = 0;
	float zz = 0;

	ArrayList<Vec3D> importPts = new ArrayList<Vec3D>();
	ArrayList<Vec3D> allVertx = new ArrayList<Vec3D>();
	ArrayList<Triangle3D> triangles = new ArrayList<Triangle3D>();

	TriangleMesh aMesh = new TriangleMesh();
	TriangleMesh importMesh = new TriangleMesh();
	TriangleMesh terrain = new TriangleMesh();


	float cRad = 24f;
	float tRad = 90f;
	float aRad = 24f;

	float minX = -2000;
	float maxX = 2000;
	float minY = -2000;
	float maxY = 2000;
	float minZ = 0;
	float maxZ = 1000;
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
		
		PC = new PointCloudList(this);
		targetList = new TargetList(this);
		
		octree = new Tree(new Vec3D(minX, minY, minZ), range * 15, this);
		ObstacleTree = new ObsTree(new Vec3D(minX, minY, minZ), range * 15, this);
		dotTree = new dotTree(new Vec3D(minX, minY, minZ), range * 15, this);
		

		PC.readFile("ptCloud_big.txt");

		targetList.readFile("TARGET_POS_it1.txt", "TARGET_DIR_it1.txt");


		aMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("final_obs.stl"), STLReader.TRIANGLEMESH);

		allVertx.addAll(aMesh.getVertices());
		ObstacleTree.addAll(allVertx);
		allVertx.clear();

		importMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("final_srf.stl"), STLReader.TRIANGLEMESH);
		allVertx.addAll(importMesh.getVertices());
		octree.addAll(allVertx);
		terrain = importMesh;




		List<Face> faces = aMesh.getFaces();

		for (Face f : faces) {
			Triangle3D tri = f.toTriangle();
			triangles.add(tri);
		}


	}

	public void draw() {
		background(0);

		noStroke();
		PC.execute();
		PC.draw();
		targetList.execute();
		targetList.draw();
		noStroke();
		strokeWeight(5);
		stroke(100);
		for (int j = 0; j < start.size(); j++) {
			gfx.point(start.get(j));
			gfx.point(end.get(j));
		}
		//noStroke();
		strokeWeight(0.1f);
		stroke(255, 55);
		fill(222, 55);
		gfx.mesh(aMesh);
		fill(200, 55);
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

		case 'q':
			drawLines = !drawLines;
			break;

		case 'm':
			cam.setActive(camOn);
			camOn = !camOn;
			break;
			
		case 's':
			screenShot = true;
			break;

		case 'e':
			PC.exportPoints();
			background(255);
			break;

		case 'p':
			screenShot = true;
			break;

		case 'v':
			video = !video;
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
				+ frameCount + "_prog_screen.png"));

		screenShot = false;
	}
}