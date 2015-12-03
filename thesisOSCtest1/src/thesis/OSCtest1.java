package thesis;

import processing.core.PApplet;
import oscP5.*;
import netP5.*;

public class OSCtest1 extends PApplet {
	private static final long serialVersionUID = 1L;

	OscP5 oscP5;

	NetAddress target1;

	int listenPort = 12000;
	int sendPort = 32000;

	public void setup() {
		size(1280,768, OPENGL);

		oscP5 = new OscP5(this, listenPort);
		target1 = new NetAddress("127.0.0.1", sendPort);
	}

	public void draw() {
		background(0);
	}

	public void mousePressed() {
		OscMessage myOscMessage = new OscMessage("/test");
		myOscMessage.add(100);
		oscP5.send(myOscMessage, target1);
		println("Message Sent");
	}
	void oscEvent(OscMessage theOscMessage) {
		  /* get and print the address pattern and the typetag of the received OscMessage */
		  println("### received an osc message with addrpattern "+theOscMessage.addrPattern()+" and typetag "+theOscMessage.typetag());
		 println(theOscMessage.get(0));
	}

}
