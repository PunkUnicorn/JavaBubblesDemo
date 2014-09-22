package com.Bubbles.Java3D;

import java.awt.Color;
import java.awt.Graphics;

/**
 * http://www3.ntu.edu.sg/home/ehchua/programming/java/J8a_GameIntro-BouncingBalls.html
 * 
 * An obstacle line-Segment with two definite end-points.
 * 
 * @author Hock-Chuan Chua
 * @version October 2010
 */
public class ObstacleLineSegment {
   int x1, y1;   // Line-segment's starting point
   int x2, y2;   // Line-segment's ending point
   Color color;  // Line-segment's drawing color
   
   /** Constructors */
   public ObstacleLineSegment(int x1, int y1, int x2, int y2, Color color) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.color = color;
   }
   /** Constructor with the default color */
   public ObstacleLineSegment(int x1, int y1, int x2, int y2) {
      this(x1, y1, x2, y2, Color.YELLOW);
   }

   /** Set or reset the bounds (for window resizing) */
   public void set(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
   }

   /** Draw itself using the given graphic context. */
   public void draw(Graphics g) {
      g.setColor(color);
      g.drawLine(x1, y1, x2, y2);
   }
}
