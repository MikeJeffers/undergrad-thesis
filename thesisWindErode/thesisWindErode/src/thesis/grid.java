package thesis;
import java.util.List;
import java.util.Vector;

import toxi.geom.*;

public class grid {

  List<Line2D> gridLines = new Vector<Line2D>();
  int xDist = 200;
  int yDist = 300;
  float xInc;
  float yInc;
  int xNum, yNum;
  thesisWindErode p;

  grid(int _xNum, int _yNum, int _xSpace, int _ySpace, thesisWindErode parent) {
    xNum = _xNum;
    yNum = _yNum;

    p = parent;
    
    xInc = _xSpace;
    yInc = _ySpace;

    genGrid();
  }

  void genGrid() {
    Line2D tempLine;
    for (int i = -xNum; i <= xNum; i++) {
      for (int j = -yNum; j <= yNum; j++) {
        tempLine = new Line2D(new Vec2D(-xInc*i, -yInc*j), new Vec2D(-xInc*i, yInc*j));
        gridLines.add(tempLine);
        tempLine = new Line2D(new Vec2D(xInc*i, yInc*j), new Vec2D(-xInc*i, yInc*j));
        gridLines.add(tempLine);
      }
    }
  }

  void drawGrid() {
    p.stroke(150);
    p.strokeWeight(0.5f);
    for (Line2D ln : gridLines) {
      p.gfx.line(ln);
    }
  }


  List<Line2D> getLines() {
    return gridLines;
  }
}
