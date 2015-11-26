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
	
	holder myHolder;
	PointCloudList PC;
	
	Tree octree;
	ObsTree ObstacleTree;
	dotTree dotTree;

	Target myTarget;
	
	Vec3D targetPos;
	Vec3D targetDir;
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
	TriangleMesh moved = new TriangleMesh();

	float cRad = 18f;
	float tRad = 75f;
	float aRad = 24f;

	float minX = -450;
	float maxX = 450;
	float minY = -450;
	float maxY = 450;
	float minZ = 0;
	float maxZ = 240;
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
		size(1600, 900, OPENGL);
		smooth();
		cam = new PeasyCam(this, 500);
		gfx = new ToxiclibsSupport(this);
		
		float range = maxX - minX;
		
		myHolder = new holder(this);
		PC = new PointCloudList(this);
		
		octree = new Tree(new Vec3D(minX, minY, minZ), range * 5, this);
		ObstacleTree = new ObsTree(new Vec3D(minX, minY, minZ), range * 5, this);
		dotTree = new dotTree(new Vec3D(minX, minY, minZ), range * 5, this);

		PC.readFile("ptCloud_gates2.txt");

		targetPos = new Vec3D(x, y, z);
		targetDir = new Vec3D(xx, yy, zz);

		aMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("obs_mesh.stl"), STLReader.TRIANGLEMESH);

		allVertx.addAll(aMesh.getVertices());
		ObstacleTree.addAll(allVertx);

		importMesh = (TriangleMesh) new STLReader().loadBinary(
				dataPath("terrain.stl"), STLReader.TRIANGLEMESH);
		terrain = importMesh;
		moved = importMesh.getTranslated(new Vec3D(0, 0, 36));

		myTarget = new Target(0, this);

		List<Face> faces = aMesh.getFaces();

		for (Face f : faces) {
			Triangle3D tri = f.toTriangle();
			triangles.add(tri);
		}

		for (int i = 0; i < 15; i++) {
			myHolder.addAgent(new Agent(i, cRad, this));
		}
	}

	public void draw() {
		background(0);
		targetPos = new Vec3D(x, y, z);
		targetDir = new Vec3D(xx, yy, zz);
		strokeWeight(3);
		stroke(222);
		noStroke();
		if (!paused)
			myHolder.execute();
		myHolder.draw();
		myTarget.run(targetPos, targetDir);
		myTarget.draw();
		PC.execute();
		PC.draw();
		noStroke();
		strokeWeight(5);
		stroke(100);
		for (int j = 0; j < start.size(); j++) {
			gfx.point(start.get(j));
			gfx.point(end.get(j));
		}
		noStroke();
		fill(111, 155);
		gfx.mesh(aMesh);
		fill(55);
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

		case 'e':
			myTarget.exportTarget();
			myHolder.exportAgents();
			PC.exportPoints();
			break;

		case 'p':
			screenShot = true;
			break;

		case 'v':
			video = !video;
			break;

		case 'w':
			y -= 10;
			break;

		case 'a':
			x -= 10;
			break;

		case 's':
			y += 10;
			break;

		case 'd':
			x += 10;
			break;

		case 'i':
			yy -= 10;
			break;

		case 'j':
			xx -= 10;
			break;

		case 'k':
			yy += 10;
			break;

		case 'l':
			xx += 10;
			break;

		}
	}

	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/vid/" + year() + "_" + month()
				+ "_" + day() + "_" + hour() + "_" + minute() + "_"
				+ frameCount + "_wind_screen.png"));

		screenShot = false;
	}
}