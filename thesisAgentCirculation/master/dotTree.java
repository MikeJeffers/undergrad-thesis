import toxi.geom.*;

public class dotTree extends PointOctree {
	Circulation p;

	dotTree(Vec3D o, float d, Circulation parent) {
		super(o, d);
		p = parent;
	}

	void draw() {
		drawNode(this);
	}

	void drawNode(PointOctree n) {
		if (n.getNumChildren() > 0) {
			p.noFill();
			p.stroke(n.getDepth(), 20);
			p.strokeWeight(.5f);
			p.stroke(255, 20);
			p.pushMatrix();
			p.translate(n.x, n.y, n.z);
			p.box(n.getNodeSize());
			p.popMatrix();
			PointOctree[] childNodes = n.getChildren();
			for (int i = 0; i < 8; i++) {
				if (childNodes[i] != null)
					drawNode(childNodes[i]);
			}
		}
	}
}