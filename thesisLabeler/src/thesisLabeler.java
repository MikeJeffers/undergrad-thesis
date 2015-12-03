import java.io.File;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class thesisLabeler extends PApplet {
	private static final long serialVersionUID = 1L;

	QRCodeWriter writer = new QRCodeWriter();
	BitMatrix codeRaw = null;

	Vector<String[]> lines = new Vector<String[]>();

	int SIZE = 600;
	int LARGE_SIZE = 800;
	int TEXT_SIZE = 125;
	String folder = year() + "_" + month() + "_" + day() + "_" + hour() + "_" + minute();

	PImage codeDraw = createImage(SIZE, SIZE, RGB);
	String filePath = "C:/Users/jparsons/Dropbox/Thesis I/2013_04_03_jp_full_tests/chunk5/sticks.txt";

	connectionLabel l1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setup() {
		size(1280, 720);
		background(255);
		readFile(filePath);
		genLabels();
		exit();
	}

	private void genLabels() {
		Vector<connectionLabel> labels = new Vector<connectionLabel>();
		
		for (String[] s : lines) {
			

			connectionLabel e = new connectionLabel(Integer.parseInt(s[0]), s);

			labels.add(e);

		}

	}

	public void draw() {
		background(255);
		if (codeDraw != null)
			image(codeDraw, 5, 5);
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

		for (int i = 0; i < strLines.length; i++) {

			String[] splitToken = strLines[i].split(",");
			lines.add(splitToken);

		}

	}

	public class connectionLabel {
		int cID;
		String nodeA;
		String nodeB;
		int SUB = (int) (LARGE_SIZE * 0.1f);

		PGraphics label = createGraphics((int) (SIZE * 2.625), SIZE);

		QRCodeWriter QRwriter = new QRCodeWriter();
		PImage QR = createImage(LARGE_SIZE, LARGE_SIZE, ALPHA);
		PImage QRS = createImage(LARGE_SIZE - SUB * 2, LARGE_SIZE - SUB * 2, ALPHA);

		BitMatrix QRRaw = null;

		connectionLabel(int cID, String[] s) {
			if (Integer.parseInt(s[3]) == 0) {
				nodeA = "nodeA: " + s[1] + " -" + s[7];
				nodeB = "nodeB: " + s[2] + " +" + s[8];
			} else {
				nodeA = "nodeA: " + s[1] + " +" + s[7];
				nodeB = "nodeB: " + s[2] + " -" + s[8];
			}
			try {
				String msg = "cID: " + cID + "\n" + nodeA + "\n" + nodeB;
				msg+="\n"+"Length: "+s[5]+"\"";
				msg+="\n"+"IntAng: "+s[6]+"°";
				QRRaw = QRwriter.encode(msg, BarcodeFormat.QR_CODE, LARGE_SIZE, LARGE_SIZE);
				for (int i = 0; i < LARGE_SIZE; i++) {
					for (int j = 0; j < LARGE_SIZE; j++) {
						if (QRRaw.get(i, j))
							QR.set(i, j, color(0, 255));
						else
							QR.set(i, j, color(255, 255));
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
			label.textAlign(LEFT,TOP);
			label.text("cID: " + cID, SIZE+25, 5);
			label.textAlign(LEFT,CENTER);
			label.text(nodeA, SIZE+25, (SIZE/2));
			label.textAlign(LEFT,BOTTOM);
			label.text(nodeB, SIZE+25, SIZE-5);
			label.endDraw();
			label.save(sketchPath("/output/labelTests/" + folder + "/" + cID + ".jpg"));
		}
	}

}
