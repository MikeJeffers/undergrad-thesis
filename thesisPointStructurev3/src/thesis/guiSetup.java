package thesis;

import processing.core.PApplet;
import controlP5.*;

public class guiSetup {

	ControlP5 controlP5;
	Textarea outputMSG;
	String messages = "";
	
	thesisPointStructurev3 p;
	
	public guiSetup(thesisPointStructurev3 parent) {
		p = parent;
		controlP5 = new ControlP5(p);
		controlP5.setAutoDraw(false);

		// style stuff
		controlP5.setColorForeground(50);
		controlP5.setColorBackground(225);
		controlP5.setColorValueLabel(p.color(0));
		controlP5.setColorActive(p.color(150));
		controlP5.setColorCaptionLabel(p.color(0));

		// Labels
		Textlabel appName = controlP5.addTextlabel("appName", "thesisPointStructurev2", 10, 10);
		appName.setVisible(true);
		appName.setColor(0x000000);

		// G1
		Group displayCtrls = controlP5.addGroup("Controls").setPosition(10, 40);

		Button advance = controlP5.addButton("paused", 0, 5, 5, 80, 20);
		advance.setGroup(displayCtrls);
		
		Bang openPTS = controlP5.addBang("openPTFile", 5, 30, 30, 10).setTriggerEvent(Bang.RELEASE);
		openPTS.setGroup(displayCtrls);
		
		Bang reLoadPts = controlP5.addBang("reLoad", 45, 30, 30, 10).setTriggerEvent(Bang.RELEASE);
		reLoadPts.setGroup(displayCtrls);
		
		
		 Slider tdSlider = controlP5.addSlider("SF", 1, 10, 1, 5, 90, 150, 10).setId(1).setTriggerEvent(Slider.RELEASE);
		  tdSlider.setGroup(displayCtrls);

		// Message output
		outputMSG = controlP5.addTextarea("Messages", messages, p.width - 5 - 250, 5, 200,
				p.height - 10);
		outputMSG.setColor(0x000000);

	}
	
	void draw(){
		controlP5.draw();
	}
	
	//===============================================================
	//Open the File browser for point selector
	//called by a control p5 bang
	void openPTFile() {
	  sendMessage("Waiting for PTS");
	  String loadPath = p.selectInput("Select a point file to Load:");  // Opens file chooser
	  if (loadPath == null) {
	    sendMessage("No file was selected...");
	  } 
	  else {
	    if (loadPath.endsWith("txt")) { 
	    	p.fPath = loadPath;
	      p.ctrl.readFile(loadPath);
	    }
	    else {
	      sendMessage("Please select a point cloud.");
	    }
	  }
	}
	
	void sendMessage(String _msg) {
		messages = messages + _msg + "\n";
		outputMSG.setText(messages);
		PApplet.println(_msg);
	}

}
