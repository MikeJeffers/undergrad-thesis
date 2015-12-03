package thesis;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import gifAnimation.GifMaker;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class ImageGen extends PApplet {
	private static final long serialVersionUID = 1L;

	ToxiclibsSupport gfx, gfx2;
	PeasyCam nav;

	TriangleMesh stick;
	TriangleMesh[] plates = new TriangleMesh[2];
	TriangleMesh[] fingers = new TriangleMesh[2];

	int meshFileLine;
	float zRot;
	String[] s;

	Vector<connectionLabel> labels = new Vector<connectionLabel>();
	QRCodeWriter writer = new QRCodeWriter();
	BitMatrix codeRaw = null;

	Vector<String[]> lines = new Vector<String[]>();

	int SIZE = 600;
	int LARGE_SIZE = 800;
	int TEXT_SIZE = 125;
	String folder = year() + "_" + month() + "_" + day() + "_" + hour() + "_" + minute();

	PImage codeDraw = createImage(SIZE, SIZE, RGB);

	int SF = 2;
	int counter = 0;
	int rotSpeed = 10;
	boolean makeGif = true;
	PFont myFont;

	int nSticks;

	String meshLocaton = "C:/Users/jparsons/Dropbox/Thesis I/Final_Export_and_import_Geom/MeshExporter_Final/";
	String filePath = "C:/Users/jparsons/Dropbox/Thesis I/Final_Export_and_import_Geom/CURRENT_final_connection_export_REVISED_4_23_2013.txt";

	GifMaker gifExport;
	PGraphics gifImage;

	public void setup() {
		size(540, 700, OPENGL);

		readFile(filePath);

		myFont = createFont("Arial", 32);
		textFont(myFont);
		gfx2 = new ToxiclibsSupport(this);

		meshFileLine = 145;
		zRot = 0;

		loadNextConnection();
		updatePGraphic();
	}

	public void draw() {

		background(255);

		image(gifImage, 0, 0, width, height);

		drawText();

		if (makeGif)
			gifExport.addFrame();

		counter++;

		if (counter >= 360 / rotSpeed) {
			if (makeGif) {
				gifExport.finish();
			}
			loadNextConnection();
			counter = 0;
		}

		updatePGraphic();

	}

	void drawText() {
		DecimalFormat df = new DecimalFormat("00.0");
		fill(0);
		noStroke();
		textSize(25);
		textAlign(LEFT, TOP);
		text("connectionID: " + s[0], 5, 5);
		textSize(15);
		fill(150);
		text("nodeA: " + s[1] + " " + s[7], 5, 30);
		text("nodeB: " + s[2] + " " + s[8], 5, 50);
		text("length: " + Float.parseFloat(df.format(Float.parseFloat(s[5]))) + "\"", 5, height-20);
	}

	void loadNextConnection() {
		gifImage = createGraphics(width * SF, height * SF, P3D);
		gfx = new ToxiclibsSupport(this, gifImage);

		if (meshFileLine >= lines.size() - 1) {
			println("Last image reached, exiting.");
			exit();
		}

		s = lines.get(meshFileLine);
		if (makeGif) {
			connectionLabel e = new connectionLabel(Integer.parseInt(s[0]), s);
			labels.add(e);
		}
		println("Generating GIF for cID: " + s[0]);
		println("--nodeA: " + s[1] + " nodeB: " + s[2]);

		if (makeGif) {
			gifExport = new GifMaker(this, "/output/" + folder + "/gifs/cID_" + s[0] + ".gif");
			gifExport.setRepeat(0);
			println("--GIF ON");
		}

		Vec3D origin = new Vec3D(0, 0, 0);
		Vec3D trans = null;

		stick = (TriangleMesh) new STLReader().loadBinary(meshLocaton
				+ "MeshExportFinal_cID_" + s[0] + ".stl", STLReader.TRIANGLEMESH);
		plates[0] = (TriangleMesh) new STLReader().loadBinary(meshLocaton
				+ "MeshExportFinal_plate_nID" + s[1] + ".stl", STLReader.TRIANGLEMESH);
		fingers[0] = (TriangleMesh) new STLReader().loadBinary(meshLocaton
				+ "MeshExportFinal_Fingers_nID" + s[1] + ".stl", STLReader.TRIANGLEMESH);

		plates[1] = (TriangleMesh) new STLReader().loadBinary(meshLocaton
				+ "MeshExportFinal_plate_nID" + s[2] + ".stl", STLReader.TRIANGLEMESH);
		fingers[1] = (TriangleMesh) new STLReader().loadBinary(meshLocaton
				+ "MeshExportFinal_Fingers_nID" + s[2] + ".stl", STLReader.TRIANGLEMESH);

		println("--" + stick.vertices.size() + " vertices on the stick.");

		stick.scale(20);
		plates[0].scale(20);
		fingers[0].scale(20);
		plates[1].scale(20);
		fingers[1].scale(20);

		trans = origin.sub(stick.computeCentroid());

		stick = stick.translate(trans);
		plates[0] = plates[0].translate(trans);
		fingers[0] = fingers[0].translate(trans);
		plates[1] = plates[1].translate(trans);
		fingers[1] = fingers[1].translate(trans);
		stick.scale(0.9f);

		meshFileLine++;
	}

	void updatePGraphic() {
		gifImage.beginDraw();

		gifImage.lights();
		gifImage.smooth();
		gifImage.background(10,0);
		gifImage.textFont(myFont);

		gifImage.pushMatrix();
		gifImage.translate((width * SF) / 2, (height * SF) / 2);
		gifImage.rotateX(HALF_PI);
		gifImage.rotateZ(radians(zRot));

		gifImage.stroke(0);
		gifImage.fill(200);
		gfx.mesh(stick);
		
		

//		gifImage.pushMatrix();
//		gifImage.translate(00, 00, 50);
//		gifImage.rotateZ(radians(-zRot));
//		gifImage.rotateX(-HALF_PI);
//		gifImage.textSize(30);
//		gifImage.fill(0);
//		gifImage.text("cID: " + s[0], 0, 0);
//		gifImage.popMatrix();

		gifImage.fill(150);
		gfx.mesh(plates[0]);
		gfx.mesh(plates[1]);

		// gifImage.pushMatrix();
		// gifImage.rotateZ(radians(-zRot));
		// gifImage.translate(-width+10,0,plates[0].computeCentroid().z-100);
		// gifImage.rotateX(-HALF_PI);
		// gifImage.textSize(30);
		// gifImage.fill(0);
		// gifImage.text("nodeA: " + s[1] +" "+s[7],0,0);
		// gfx.line(new Vec3D(175,-12,0),new
		// Vec3D((plates[0].computeCentroid().x)*cos(radians(-zRot))+width-10,-12,0));
		// gifImage.popMatrix();

		// gifImage.pushMatrix();
		// gifImage.translate(plates[0].computeCentroid().x,
		// plates[0].computeCentroid().y+0,plates[0].computeCentroid().z-100);
		// gifImage.rotateZ(radians(-zRot));
		// gifImage.rotateX(-HALF_PI);
		// gifImage.textSize(30);
		// gifImage.fill(0);
		// gifImage.text("nodeA: " + s[1] +" "+s[7],0,0);
		// gifImage.popMatrix();

		// gifImage.pushMatrix();
		// gifImage.translate(plates[1].computeCentroid().x,
		// plates[1].computeCentroid().y+0,plates[1].computeCentroid().z+100);
		// gifImage.rotateZ(radians(-zRot));
		// gifImage.rotateX(-HALF_PI);
		// gifImage.textSize(30);
		// gifImage.fill(0);
		// gifImage.text("nodeB: " + s[2] +" "+s[8],0,0);
		// gifImage.popMatrix();

		gifImage.fill(175);
		gfx.mesh(fingers[0]);
		gfx.mesh(fingers[1]);

		gifImage.popMatrix();
		gifImage.endDraw();
		zRot += rotSpeed;
	}

	void readFile(String fPath) {
		File f = new File("");

		try {
			f = new File(fPath);
			println(f.getName() + " opened.");
		} catch (NullPointerException ex) {
			println("File: " + " could not be found.");
		}

		String[] strLines = loadStrings(f.getAbsolutePath());

		for (int i = 0; i < strLines.length; i++)
			lines.add(strLines[i].split(","));

	}

	public class connectionLabel {
		int cID;
		String nodeA;
		String nodeB;
		int SUB = (int) (LARGE_SIZE * 0.1f);

		PGraphics label = createGraphics((int) (SIZE * 2.625), SIZE, P2D);

		QRCodeWriter QRwriter = new QRCodeWriter();
		PImage QR = createImage(LARGE_SIZE, LARGE_SIZE, RGB);
		PImage QRS = createImage(LARGE_SIZE - SUB * 2, LARGE_SIZE - SUB * 2, ALPHA);

		BitMatrix QRRaw = null;

		connectionLabel(int cID, String[] s) {
			println("--Making QR");

			nodeA = "nodeA: " + s[1] + s[7];
			nodeB = "nodeB: " + s[2] + s[8];

			try {
				// String msg = "cID: " + cID + "\n" + nodeA + "\n" + nodeB;
				String msg = "";
				// msg += "\n" + "Length: " + s[5] + "\"";
				// msg += "\n" + "IntAng: " + s[6] + "°";
				msg = "http://qr.mj-jp.com/final/" + s[0];

				QRRaw = QRwriter.encode(msg, BarcodeFormat.QR_CODE, LARGE_SIZE, LARGE_SIZE);
				for (int i = 0; i < LARGE_SIZE; i++) {
					for (int j = 0; j < LARGE_SIZE; j++) {
						if (QRRaw.get(i, j))
							QR.set(i, j, color(0, 0, 0));
						else
							QR.set(i, j, color(255, 255, 255));
					}
				}
				QRS = QR.get(SUB, SUB, LARGE_SIZE - SUB * 2, LARGE_SIZE - SUB * 2);
				QRS.resize(SIZE, SIZE);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			label.smooth();
			label.beginDraw();
			label.background(color(255));
			label.fill(0);
			label.noStroke();
			label.textSize(TEXT_SIZE);
			label.image(QRS, 0, 0);
			label.textAlign(LEFT, TOP);
			label.text("cID: " + cID, SIZE + 25, 5);
			label.textAlign(LEFT, CENTER);
			label.text(nodeA, SIZE + 25, (SIZE / 2));
			label.textAlign(LEFT, BOTTOM);
			label.text(nodeB, SIZE + 25, SIZE - 5);
			label.endDraw();
			label.save(sketchPath("/output/" + folder + "/qrCodes/" + cID + ".jpg"));
		}
	}

}