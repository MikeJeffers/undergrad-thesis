import java.io.File;
import java.util.Vector;

import processing.core.PApplet;
import toxi.geom.Ray3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;

public class SolarVectors {
	SolarTriangles p;
	Vector<Vec3D> sVectors = new Vector<Vec3D>();

	SolarVectors(SolarTriangles parent) {
		p = parent;

	}

	void readFile(String fPath) {
		Vector<Ray3D> getRays = new Vector<Ray3D>();
		Vector<Vec3D> getDirections = new Vector<Vec3D>();
		File f = new File("");
		try {
			f = new File(fPath);
			// p.gui.sendMessage(f.getName() + " opened.");
		} catch (NullPointerException ex) {
			// p.gui.sendMessage("File: " + " could not be found.");
		}

		String[] strLines = p.loadStrings(f.getAbsolutePath());
		int count = 0;
		for (int i = 0; i < strLines.length; i += 2) {
			String line1 = strLines[i].substring(1, strLines[i].length() - 1);

			String[] split1 = line1.split(",");

			float x1 = PApplet.parseFloat(split1[0]);
			float y1 = PApplet.parseFloat(split1[1]);
			float z1 = PApplet.parseFloat(split1[2]);
			Vec3D A = new Vec3D(x1 * p.raySF, y1 * p.raySF, z1 * p.raySF);
			getDirections.add(A);

			count++;
		}
		sVectors = getDirections;
	}

	Vector<Vec3D> getSVecs() {
		return this.sVectors;
	}

}
