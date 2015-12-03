package thesis;

import java.util.Comparator;
import java.util.Random;


import toxi.geom.Rect;

public class Stick implements Comparable<Stick> {

	private float length;
	public int cID;
	private float yVal;
	public Rect outline;
	public float x;
	int r,g,b;
	String[] data;

	public Stick(int _cID, float _length, String[] _data) {
		length = _length;
		cID = _cID;
		x = 0;
		setyVal(0);
		Random rnd = new Random();
		r = rnd.nextInt(255);
		g = rnd.nextInt(255);
		b = rnd.nextInt(255);
		data = _data;
	}

	public static Comparator<Stick> StickSortASC = new Comparator<Stick>() {
		public int compare(Stick a, Stick b) {
			return (a.length < b.length ? -1 : (a.length == b.length) ? 0 : 1);
		}
	};

	public static Comparator<Stick> StickSortDSC = new Comparator<Stick>() {
		public int compare(Stick a, Stick b) {
			return (a.length > b.length ? -1 : (a.length == b.length) ? 0 : 1);
		}
	};

	public int compareTo(Stick a) {
		return (a.length < this.length ? -1 : (a.length == this.length) ? 0 : 1);
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getyVal() {
		return yVal;
	}

	public void setyVal(float yVal) {
		this.yVal = yVal;
		outline = new Rect(x, yVal*10, 10, length*10);
	}

}
