import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class holder {
	ArrayList<Agent> AgentList;
	Circulation p;
	PrintWriter OUTPUT;

	holder(Circulation parent) {
		p = parent;
		AgentList = new ArrayList<Agent>();
	}

	void execute() {
		for (int k = 0; k < AgentList.size(); k++) {
			Agent a = (Agent) AgentList.get(k);
			a.run(AgentList);
		}
	}

	void draw() {
		for (Agent a : AgentList)
			a.draw();
	}

	ArrayList<Agent> getAll() {
		return AgentList;
	}

	void addAgent(Agent a) {
		AgentList.add(a);
	}

	int holderSize() {
		return AgentList.size();
	}

	Agent getA(int idd) {
		return (Agent) AgentList.get(idd);
	}

	void exportAgents() {
		OUTPUT = p.createWriter(p.sketchPath("output/" + PApplet.year() + "_"
				+ PApplet.month() + "_" + PApplet.day() + "_" + PApplet.hour()
				+ "_" + PApplet.minute() + "_" + p.frameCount
				+ "_AgentCurrentPos.txt"));

		ArrayList<Vec3D> getPos = new ArrayList<Vec3D>();
		ArrayList<Vec3D> getVel = new ArrayList<Vec3D>();

		for (int i = 0; i < AgentList.size(); i++) {
			Agent a = AgentList.get(i);
			getPos.add(a.pos);
			getVel.add(a.pos.add(a.vel.scaleSelf(3)));

			int gID = a.group;
			Vec3D p = a.pos;
			Vec3D v = a.pos.add(a.vel.scaleSelf(3));
			OUTPUT.println(gID);
			OUTPUT.println("{" + p.x + ", " + p.y + ", " + p.z + "}");
			OUTPUT.println("{" + v.x + ", " + v.y + ", " + v.z + "}");

		}

		for (int i = 0; i < getPos.size(); i++) {

		}
		OUTPUT.flush();
		OUTPUT.close();
	}
}
