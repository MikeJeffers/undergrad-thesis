package thesis;

import java.util.Comparator;

public class intFloatComp implements Comparable<intFloatComp> {
	int num;
	float val;

	public intFloatComp(int n, float d) {
		num = n;
		val = d;
	}

	public static Comparator<intFloatComp> intFloatComparator = new Comparator<intFloatComp>() {
		public int compare(intFloatComp a, intFloatComp b) {
			return (a.val < b.val ? -1 : (a.val == b.val) ? 0 : 1);
		}
	};

	@Override
	public int compareTo(intFloatComp o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
