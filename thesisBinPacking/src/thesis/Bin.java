package thesis;

import java.util.Comparator;
import java.util.Vector;

import toxi.geom.Rect;

public class Bin implements Comparable<Bin> {

	float freeSpace;
	float usedSpace;
	int sheet;
	int numOnSheet;
	Vector<Stick> sticks = new Vector<Stick>();
	public Rect r;
	float x;
	float y;
	int id;
	
	public Bin(int _sheet, int n, int _id) {
		freeSpace = 96f;
		usedSpace = 0f;
		sheet = _sheet;
		numOnSheet = n;
		id = _id;
		
		x = numOnSheet*15f+7.5f;
		y = 0;
		r = new Rect(x, y, 15, 960);
	}
	
	public boolean AddStick(Stick s){
		if(s.getLength() <= freeSpace){
			s.x = x;
			sticks.add(s);
			s.setyVal(usedSpace);
			freeSpace -= s.getLength();
			usedSpace += s.getLength();
			return true;
		}else{
			return false;
		}
	}

	public static Comparator<Bin> BinSort = new Comparator<Bin>() {
		public int compare(Bin a, Bin b) {
			return (a.freeSpace < b.freeSpace ? -1 : (a.freeSpace == b.freeSpace) ? 0 : 1);
		}
	};
	
	@Override
	public int compareTo(Bin a) {
		return (a.freeSpace < this.freeSpace ? -1 : (a.freeSpace == this.freeSpace) ? 0 : 1);
	}

}
