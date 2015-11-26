package thesis;

import java.util.Vector;

import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

public class tPhysics extends VerletPhysics implements Runnable {
	thesisPointStructurev2 p;

	public tPhysics(thesisPointStructurev2 parent) {
		p = parent;
		GravityBehavior grav = new GravityBehavior(new Vec3D(0, 0, -0.1f));
		this.addBehavior(grav);

		this.setWorldBounds(new AABB(new Vec3D(0, 0, 4990), new Vec3D(5000, 5000, 5000)));
	}

	public tPhysics(Vec3D arg0, int arg1, float arg2, float arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		this.update();
		p.gui.sendMessage("bump");
	}

	void draw() {
		for (int i = 0; i < this.springs.size(); i++) {
			p.gfx.line(this.springs.get(i).a, this.springs.get(i).b);
		}

		p.strokeWeight(5);
		for (VerletParticle v : this.particles) {
			if (v.isLocked()) {
				p.strokeWeight(10);
				p.stroke(255, 0, 255);
			} else {
				p.strokeWeight(5);
				p.stroke(0);
			}
			p.gfx.point(v);
		}
		p.strokeWeight(1);
	}

	public void setup(Vector<connection> connections) {
		for (Vec3D v : p.ctrl.pc.getPoints()) {
			if (p.surfMesh.getClosestVertexToPoint(v).distanceTo(v) < 10)
				this.addParticle(new VerletParticle(v).lock());
			else
				this.addParticle(new VerletParticle(v));
		}
		for (connection c : connections) {
			VerletParticle a = null;
			VerletParticle b = null;
			for (VerletParticle v : this.particles) {
				if (c.pts[0].distanceTo(v) < 0.1f)
					a = v;

				if (c.pts[1].distanceTo(v) < 0.1f)
					b = v;
			}

			this.addSpring(new VerletSpring(a, b, c.ln.getLength() + 1, 1f));
		}

	}

}
