package thesis;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;
import thesis.NDTree;

public class thesisDataTreeTest extends PApplet {
	private static final long serialVersionUID = 8395091574806740282L;
	public ToxiclibsSupport gfx;

	public NDTree<String> treeTest;

	public static void main(String[] args) {

	}

	public void setup() {
		size(1280, 720, OPENGL);
		smooth();
		frameRate(30);
		background(255);

		gfx = new ToxiclibsSupport(this);

		treeTest = new NDTree<String>("The Top");

		treeTest.insert(1, "A"); // 2
		treeTest.insert(2, "ABC"); // 3
		treeTest.insert(3, "A#"); // 4
		treeTest.insert(2, "A@2");// 5
		treeTest.insert(5, "A@3");// 6


		if (treeTest.lookupAndReturn("A#") == null)
			println("No Dice");
		else
			println(treeTest.lookupAndReturn("A#").pID);//treeTest.lookupAndReturn("A#").pID
		

		println(treeTest.getTreeOutline());

		noLoop();
	}

	public void draw() {
		background(255);
	}

}
