package thesis;


import java.util.Vector;

import processing.core.PApplet;

import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

public class tPhysics extends VerletPhysics {
	thesisPointStructure p;

	

	public tPhysics(thesisPointStructure parent) {
		p = parent;
		GravityBehavior grav = new GravityBehavior(new Vec3D(0, 0, 0.1f));
		this.addBehavior(grav);
	}

	public void setup(Vector<int[]> connections, Vector<Integer> apID) {
		for(int i = 0; i < p.pc.getPoints().size(); i++){
			if(apID.contains(i))
				this.addParticle(new VerletParticle(p.pc.getPoints().get(i)).lock());
			else
				this.addParticle(new VerletParticle(p.pc.getPoints().get(i)));	
		}
		
		for(int j = 0; j < connections.size(); j++){
			int[] con = connections.get(j);
			float rLength = this.particles.get(con[1]).distanceTo(this.particles.get(con[0]));
			VerletSpring vs = new VerletSpring(this.particles.get(con[0]), this.particles.get(con[1]), rLength-1f, .99f);
			this.addSpring(vs);
		}
	
	}
	
	void passConnectionsOut(){
		PApplet.println("Passing data out of physics.");
		p.pc.clearPC();
		PApplet.println("--Point Cloud Cleared.");
		p.cm.clear();
		PApplet.println("--Connection Matrix Cleared.");
		
		p.pc.loadFromPhysics(this.particles);
		
		p.cm.loadFromPhysics(this.springs);
	
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

	void exportSprings() {
		p.OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_" + PApplet.month()
				+ "_" + PApplet.day() + "_" + PApplet.hour() + "_" + PApplet.minute() + "_"
				+ p.frameCount + "_connections.txt"));
		
		for (int i = 0; i < this.springs.size(); i++) {
			VerletSpring vs = this.springs.get(i);
			p.OUTPUT.print("cID: " +i);

			
			p.OUTPUT.println(vs.a.x + ", " + vs.a.y + ", " + vs.a.z);
			p.OUTPUT.println(vs.b.x + ", " + vs.b.y + ", " + vs.b.z);
		}
		
		p.OUTPUT.flush();
		p.OUTPUT.close();

	}

}