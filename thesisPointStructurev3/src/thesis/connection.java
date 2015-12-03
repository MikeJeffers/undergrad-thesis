package thesis;

import java.util.Comparator;
import toxi.geom.Line3D;
import toxi.geom.Vec3D;

public class connection implements Comparable<connection> {

	int[] ptIDs;
	Vec3D[] pts;
	Line3D ln;
	
	int value;
	float angle;

	public int hashCode;

	public connection(Vec3D ptA, Vec3D ptB, int idA, int idB) {
		ptIDs = new int[2];
		ptIDs[0] = idA;
		ptIDs[1] = idB;

		pts = new Vec3D[2];
		pts[0] = ptA;
		pts[1] = ptB;

		ln = new Line3D(ptA, ptB);

		hashCode = this.genHash();
		
		value = 0;
		angle = 0;
	}

	// Collections.sort(ptHeadings, intFloatComp.intFloatComparator);
	public static Comparator<connection> conectionComparatorStart = new Comparator<connection>() {
		public int compare(connection a, connection b) {
			return (a.getIDs()[0] < b.getIDs()[0] ? -1 : (a.getIDs()[0] == b.getIDs()[0]) ? 0 : 1);
		}
	};

	public static Comparator<connection> conectionComparatorEnd = new Comparator<connection>() {
		public int compare(connection a, connection b) {
			return (a.getIDs()[1] < b.getIDs()[1] ? -1 : (a.getIDs()[1] == b.getIDs()[1]) ? 0 : 1);
		}
	};

	public static Comparator<connection> conectionComparatorLineLength = new Comparator<connection>() {
		public int compare(connection a, connection b) {
			return (a.getLN().getLength() < b.getLN().getLength() ? -1
					: (a.getLN().getLength() == b.getLN().getLength()) ? 0 : 1);
		}
	};

	public static Comparator<connection> conectionComparatorAngle = new Comparator<connection>() {
		public int compare(connection a, connection b) {
			return (a.angle < b.angle ? -1 : (a.angle == b.angle) ? 0 : 1);
		}
	};

	@Override
	public int compareTo(connection o) {
		//(this.ptIDs[0] == o.ptIDs[0] && this.ptIDs[1] == o.ptIDs[1]) || (this.ptIDs[0] == o.ptIDs[1] && this.ptIDs[1] == o.ptIDs[0])
		if (this.ln.equals(o.ln))
			return 1;
		else
			return 0;
	}
	
	int genHash(){
		int hash = 1;
		
		hash = hash*(5+this.ptIDs[0]);
		hash = hash*(6+this.ptIDs[1]);
//		hash = hash*6+this.pts[0].toString().hashCode();
//		hash = hash*6+this.pts[1].toString().hashCode();
//		hash = hash*6+this.ln.toString().hashCode();
		
		return hash;
		
	}
	
	public Vec3D getOtherPT(Vec3D v){
		if(v.equals(this.pts[0]))
			return this.pts[1];
		if(v.equals(this.pts[1]))
			return this.pts[0];
		
		return null;
	}

	public connection copy() {
		connection c = new connection(this.pts[0], this.pts[1], this.ptIDs[0], this.ptIDs[1]);
		return c;
	}

	int[] getIDs() {
		return ptIDs;
	}

	Vec3D[] getPTS() {
		return pts;
	}

	Line3D getLN() {
		return ln;
	}
	
	public void hurt(){
		this.value-=2;
	}
	
	public void help(){
		this.value+=1;
	}
	
	public void hurt(int dmg){
		this.value-=(dmg*2);
	}

	public boolean connectsTo(connection c) {
		if(this.pts[0].equals(c.pts[0]) || this.pts[0].equals(c.pts[1]) || this.pts[1].equals(c.pts[0]) || this.pts[1].equals(c.pts[1]))
			return true;
		else 
			return false;
	}
	
	public boolean connectsTo(Vec3D v) {
		if(this.pts[0].equals(v) || this.pts[1].equals(v))
			return true;
		else 
			return false;
	}
	
	public Vec3D getCommonPoint(connection c){
		if(this.pts[0].equals(c.pts[0]))
			return c.pts[0];
		
		if(this.pts[1].equals(c.pts[1]))
			return c.pts[1];
		
		if(this.pts[0].equals(c.pts[1]))
			return c.pts[1];
		
		if(this.pts[1].equals(c.pts[0]))
			return c.pts[0];
		
		return null;
	}

	public Vec3D connectsToAt(connection c) {
		if(this.pts[0].equals(c.pts[0]) || this.pts[0].equals(c.pts[1]))
				return this.pts[0];
		if(this.pts[1].equals(c.pts[0]) || this.pts[1].equals(c.pts[1]))
			return this.pts[1];
		return null;
	}
	
	

}
