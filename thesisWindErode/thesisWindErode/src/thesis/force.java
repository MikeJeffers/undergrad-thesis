package thesis;
import java.util.ArrayList;
import java.util.List;

import toxi.geom.*;

public class force {

	Vec3D loc;
	Vec3D vel;
	int weight;
	int hits;
	thesisWindErode p;
	boolean add;
	
	boolean on = true;
	
	force(Vec3D _loc, Vec3D _t, boolean a, thesisWindErode parent){
		loc = _loc;
		vel = _t.normalizeTo(3);
		p = parent;
		hits = 0;
		add = a;
		weight = 1;
	}
	
	void update(){
		if(add){
			this.updateAdd();
			return;
		}else{
			this.updateRemove();
		}
	}
	
	void updateAdd(){
		loc.addSelf(vel);
		ArrayList<Vec3D> pts = p.tree.getPointsWithinSphere(this.loc, p.cRAD);
		if(pts == null) return;
		Vec3D idR = new Vec3D();
		float minDist = 10000;
		for(Vec3D pt : pts){
			float dist = loc.distanceTo(pt);
			if(dist < minDist){
				minDist = dist;
				idR = pt;
			}
		}
		
		List<Vec3D> allPts = p.tree.getPoints();
		int ptID = 0;
		for (int i = 0; i < allPts.size(); i++) {
			if (pts.get(i) == idR) {
				ptID = i;
				break;
			}
		}
		
		
		if(p.maxHealth > p.tree.vals.get(ptID)){
			p.tree.addHealth(idR); 
		}
		this.on = false;
	}
	
	void updateRemove(){
		loc.addSelf(vel);
		ArrayList<Vec3D> pts = p.tree.getPointsWithinSphere(this.loc, p.cRAD);
		if(pts == null) return;
		Vec3D idR = new Vec3D();
		float minDist = 10000;
		for(Vec3D pt : pts){
			float dist = loc.distanceTo(pt);
			if(dist < minDist){
				minDist = dist;
				idR = pt;
			}
		}
		
		p.tree.hitPT(idR); 
		this.hits++;
		if(this.hits >= this.weight) this.on = false; 
	}
	
	void draw(){
		p.stroke(0);
		if(add) {
			p.stroke(255);
		}else{
			p.stroke(100);
		}
		p.strokeWeight(5);
		p.gfx.point(loc);
	}
	
	
}
