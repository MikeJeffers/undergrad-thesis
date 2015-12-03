package thesis;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

public class ThesisBinPacker extends PApplet {
	private static final long serialVersionUID = 1L;

	ToxiclibsSupport gfx;
	public Vector<String[]> lines = new Vector<String[]>();
	public Vector<Stick> sticks = new Vector<Stick>();
	public Vector<Bin> bins = new Vector<Bin>();

	//String filePath = "C:/Users/jparsons/Dropbox/Thesis I/chunk5Proto_MeshExports/Connection_export_forChunk5Proto4_17_2013.txt";
	//String filePath = "C:/Users/jparsons/Dropbox/Thesis I/Connection_export_New_Test_4_19.txt";
	String filePath = "C:/Users/jparsons/Dropbox/Thesis I/Final_Export_and_import_Geom/CURRENT_final_connection_export_REVISED_4_23_2013.txt";
	
	int nSticks;
	int nBins;
	int nSheets;
	int binsPerSheet;
	int activeSheet;
	int activeStick;
	int binPerSheetCount;
	boolean screenShot = false;

	public void setup() {
		size(480, 960, OPENGL);
		background(255);

		gfx = new ToxiclibsSupport(this);

		readFile(filePath);
		genSticks();
		Collections.sort(sticks, Stick.StickSortDSC);
		
		int c = color(0,255);

		nSticks = sticks.size();
		activeStick = 146;
		nBins = 0;
		nSheets = 1;
		binsPerSheet = 30;
		activeSheet = 1;
		binPerSheetCount = 0;

		Bin b = new Bin(activeSheet, nBins, 0);
		bins.add(b);
		nBins++;
		binPerSheetCount++;
		
		println("Ready to pack "+sticks.size()+" connections.");
	}

	public void draw() {
		background(255);

		drawBins();
		if (screenShot)
			screenShot();
	}

	public void drawBins() {
		for (int i = 0; i < nSheets; i++) {
			Vector<Bin> binsToDraw = new Vector<Bin>();
			for (Bin b : bins)
				if (b.sheet == activeSheet)
					binsToDraw.add(b);

			if (binsToDraw.size() <= 0)
				return;

			for (Bin b : binsToDraw)
				drawBin(b);
		}
	}

	public void drawBin(Bin b) {
		stroke(0);
		noFill();
		gfx.rect(b.r);

		fill(200, 150);
		for (Stick s : b.sticks){
			fill(s.r,s.g,s.b);
			gfx.rect(s.outline);
		}

	}

	public void packStick() {
		if (activeStick >= nSticks) {
			println("All connections Packed in a total of "+nSheets+" sheets and "+bins.size()+" sticks.");
			return;
		}
		Stick s = sticks.get(activeStick);

		println("Attempting to pack connection: " + s.cID + " with a legnth of: "
				+ s.getLength());
		int bPacked = 0;
		boolean packed = false;

		for (Bin b : bins) {
			packed = b.AddStick(s);
			if (packed){
				bPacked = b.id;
				break;
			}
		}
		if (!packed) {
			if(binPerSheetCount % binsPerSheet == 0){
				activeSheet++;
				binPerSheetCount = 0;
				nSheets++;
			}
			Bin newBin = new Bin(activeSheet, binPerSheetCount, nBins);
			packed = newBin.AddStick(s);
			bins.add(newBin);
			bPacked = newBin.id;
			binPerSheetCount++;
			nBins++;
		}

		if (!packed)
			println("Stick is too big");

		println("("+activeStick+") Stick " + sticks.get(activeStick).cID + " packed in stick " + bPacked);
		activeStick++;
	}

	public void genSticks() {
		for (String[] s : lines) {
			Stick stick = new Stick(Integer.parseInt(s[0]), Float.parseFloat(s[5]),s);
			sticks.add(stick);
		}
	}

	public void keyPressed() {
		if (key == ' ') {
			packStick();
		}
		
		if(key == 'e'){
			exportPackedSticks();
		}
		
		if(key == 's'){
			screenShot = true;
		}
	}
	
	void exportPackedSticks(){
		PrintWriter OUTPUT = createWriter("C:/Users/jparsons/Dropbox/Thesis I/Final_Export_and_import_Geom/final_connection_export_packed_yo_again_for_science_you_monster.txt");

		for(int i = 1; i <= nSheets; i++){
			OUTPUT.println("SHEET,"+i);
			Vector<Bin> binsToExport = new Vector<Bin>();
			for (Bin b : bins)
				if (b.sheet == i)
					binsToExport.add(b);
			for(Bin b : binsToExport){
				OUTPUT.println("STICK,"+b.id);
				for(Stick s : b.sticks){
					OUTPUT.println(s.cID+","+s.getyVal());
				}
			}
			
		}
		OUTPUT.flush();
		OUTPUT.close();
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
	
	void screenShot() {
		println("screen");

		save(sketchPath("output/screenShots/" + year() + "_" + month() + "_" + day() + "_" + hour()
				+ "_" + minute() + "_" + frameCount + "_ps.png"));

		screenShot = false;
	}

}
